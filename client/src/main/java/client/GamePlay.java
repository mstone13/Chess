package client;

import chess.*;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import model.*;
import websocket.messages.ServerMessage;

import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GamePlay implements ServerMessageObserver {
    private final Scanner scanner = new Scanner(System.in);
    private GameData currentGameData;
    public String playerColor;
    public String boardColor;

    public GamePlay() {
        boardColor = SET_BG_COLOR_PINK;
    }

    @Override
    public void notify(ServerMessage message) {
        PrintStream out = System.out;
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                updateGameData(message.getGame());
                ui.ChessBoard.run(currentGameData, playerColor != null ? playerColor.toUpperCase() : "WHITE", null, null, boardColor);
                handleLoadGame(out, message);
            }
            case NOTIFICATION -> handleNotification(out, message);
            case ERROR -> handleError(out, message);
        }
    }

    public void handleLoadGame(PrintStream out, ServerMessage message) {
        GameData gameData = message.getGame();

        if (gameData == null) {
            out.println("No game data received.");
            return;
        }

        ChessGame game = gameData.game();
        ChessGame.TeamColor currentTurn = game.getTeamTurn();
        out.println(SET_TEXT_BOLD);

        out.println(SET_TEXT_COLOR_BLUE + "Turn: " + currentTurn);

        if (!game.canMove()) {
            out.println("The game is over!");
        }

        out.print(RESET_TEXT_COLOR);
    }

    public void handleNotification(PrintStream out, ServerMessage message) {
        out.println(SET_TEXT_COLOR_RED);
        out.println(">>> " + message.getMessage());
        out.print(RESET_TEXT_COLOR);
    }

    public void handleError(PrintStream out, ServerMessage message) {
        out.println(SET_TEXT_COLOR_RED);
        out.println(">> ERROR: " + message.getErrorMessage());
        out.print(RESET_TEXT_COLOR);
    }

    public void run(GameData game, String playerColor, boolean playing,
                      WebSocketFacade facade, String authToken) throws IOException {
        this.currentGameData = game;
        this.playerColor = playerColor;
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        var result = "";

        while (!result.equals("2")){
            printMenu(out, playing);
            result = scanner.nextLine();
            directAction(out, result, playerColor, playing, facade, authToken);
        }
    }

    public void directAction(PrintStream out, String result, String playerColor, boolean playing,
                                WebSocketFacade facade, String authToken) throws IOException {
        if (playing) {
            switch (result) {
                case "1" -> printHelp(out, playing);
                case "3" -> {
                    if (currentGameData == null) {
                        out.println("No game data available yet");
                    }
                    assert currentGameData != null;
                    ui.ChessBoard.run(currentGameData, playerColor.toUpperCase(), null, null, boardColor);
                }
                case "4" -> makeMove(out, currentGameData, facade, playerColor, authToken);
                case "5" -> resign(out, currentGameData, facade, authToken);
                case "6" -> highlightLegalMoves(out, currentGameData, playerColor);
                case "7" -> {
                    changeBoardColor(out);
                    ui.ChessBoard.run(currentGameData, playerColor.toUpperCase(), null, null, boardColor);
                }

            }
        } else {
            switch (result) {
                case "1" -> printHelp(out, playing);
                case "3" -> ui.ChessBoard.run(currentGameData, playerColor.toUpperCase(), null, null, boardColor);
                case "4" -> highlightLegalMoves(out, currentGameData, playerColor);
            }
        }
    }

    public void highlightLegalMoves(PrintStream out, GameData game, String playerColor) {
        ChessPosition pos = getPosition(out, "Please enter a piece:");
        Collection<ChessMove> legalMoves = game.game().validMoves(pos);

        if (playerColor == null) {
            playerColor = "white";
        }

        ui.ChessBoard.run(game, playerColor, legalMoves, pos, boardColor);
    }

    public void makeMove(PrintStream out, GameData gameData, WebSocketFacade wsFacade,
                         String playerColor, String authToken) {
        try {
            ChessGame.TeamColor teamColor = getTeamColor(playerColor);

            updateGameData(gameData);

            if (gameData.game().isInCheckmate(teamColor) || gameData.game().isInStalemate(teamColor) ||
            gameData.game().isFinished()) {
                out.println(SET_TEXT_COLOR_RED + ">> ERROR: The game is over!");
                out.print(RESET_TEXT_COLOR);
                return;
            } else if (!gameData.game().getTeamTurn().equals(teamColor)) {
                out.println(SET_TEXT_COLOR_RED + ">> ERROR: It's not your turn. Please wait for the other player.");
                out.print(RESET_TEXT_COLOR);
                return;
            }

            ChessPosition startPos = getPosition(out, "Please enter a piece:");
            ChessPiece piece = gameData.game().getBoard().getPiece(startPos);
            if (piece == null) {
                out.println(SET_TEXT_COLOR_RED + ">> ERROR: Please enter a piece on the board.)");
                out.print(RESET_TEXT_COLOR);
                return;
            }
            ChessPosition endPos = getPosition(out, "Please enter end position:");
            ChessPiece.PieceType promotionPiece = getPromotionPiece(out, startPos, endPos, gameData);

            ChessMove move = new ChessMove(startPos, endPos, promotionPiece);
            Collection<ChessMove> legalMoves = gameData.game().validMoves(move.getStartPosition());
            if (!legalMoves.contains(move)) {
                out.println(SET_TEXT_COLOR_RED + ">> ERROR: Illegal move. Enter a valid move.");
                out.print(RESET_TEXT_COLOR);
                return;
            }

            chess.ChessBoard board = gameData.game().getBoard();
            if (board.getPiece(startPos).getTeamColor() != teamColor) {
                out.println(SET_TEXT_COLOR_RED + ">> ERROR: Select a piece of your team color!");
                out.print(RESET_TEXT_COLOR);
                return;
            }

            wsFacade.makeMove(authToken, gameData.gameID(), move);
        }
        catch (Exception e) {
            out.println(SET_TEXT_COLOR_RED + " >> ERROR: " + e.getMessage());
            out.print(RESET_TEXT_COLOR);
        }
    }


    public ChessGame.TeamColor getTeamColor(String playerColor) {
        ChessGame.TeamColor teamColor;
        if (playerColor.equalsIgnoreCase("white")) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        return teamColor;
    }

    private ChessPosition getPosition(PrintStream out, String prompt) {
        int row, col;

        out.println(SET_TEXT_ITALIC + SET_TEXT_UNDERLINE +  prompt);
        out.print(RESET_TEXT_ITALIC + RESET_TEXT_UNDERLINE);

        while (true) {
            out.print(SET_TEXT_BOLD + "Enter the row [1-8]: ");
            try {
                row = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                out.println(RESET_TEXT_BOLD_FAINT + "Please input a valid integer for row");
                continue;
            }

            out.print(SET_TEXT_BOLD + "Enter the column [a-h]: ");
            String inputLine = scanner.nextLine().toLowerCase();

            if (inputLine.length() != 1) {
                out.println(RESET_TEXT_BOLD_FAINT + "Please input a single character for column [a-h].");
                continue;
            }

            char input = inputLine.charAt(0);
            col = input - 'a' + 1;

            if (row < 1 || row > 8 || col < 1 || col > 8) {
                out.println(RESET_TEXT_BOLD_FAINT + "Please input a valid row [1-8] and col [a-h].");
                continue;
            }

            return new ChessPosition(row, col);
        }
    }

    public ChessPiece.PieceType getPromotionPiece(PrintStream out, ChessPosition startPos, ChessPosition endPos,
                                                  GameData gameData) {
        ChessGame game = gameData.game();
        ChessPiece piece = game.getBoard().getPiece(startPos);
        ChessPiece.PieceType promotionPiece = null;


        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            boolean canPromote = piece.getTeamColor() == ChessGame.TeamColor.BLACK && endPos.getRow() == 1 ||
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE && endPos.getRow() == 8;

            if (canPromote) {
                while (promotionPiece == null) {
                    out.println("""
                    Promotion pieces:
                    >> Q = Queen
                    >> R = Rook
                    >> B = Bishop
                    >> N = Knight
                    """);
                    out.print("Enter the piece to which you'd like to promote your pawn: ");
                    String choice = scanner.nextLine().toUpperCase();

                    switch (choice) {
                        case "Q" -> promotionPiece = ChessPiece.PieceType.QUEEN;
                        case "R" -> promotionPiece = ChessPiece.PieceType.ROOK;
                        case "B" -> promotionPiece = ChessPiece.PieceType.BISHOP;
                        case "N" -> promotionPiece = ChessPiece.PieceType.KNIGHT;
                        default -> out.println("Invalid choice! Please enter Q, R, B, or N.");
                    }
                }
            }
        }
        return promotionPiece;
    }

    public void resign(PrintStream out, GameData gameData, WebSocketFacade wsFacade, String authToken) throws IOException {
        if (currentGameData.isFinished()) {
            out.println(SET_TEXT_COLOR_RED + ">> ERROR: You cannot resign from a game that is finished!");
            return;
        }

        out.println(SET_TEXT_BOLD + "Are you sure you want to resign? [Y/N]");
        var answer = scanner.nextLine();
        out.print(RESET_TEXT_BOLD_FAINT);

        if (answer.equalsIgnoreCase("N")) {
            out.println("Keep going! You got this!");
            return;
        }

        ChessGame game = gameData.game();
        game.finishGame();
        gameData = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game,
                true
        );

        wsFacade.resign(authToken, gameData.gameID());
    }

    public void changeBoardColor(PrintStream out) {
        out.println("""
                Pick a color:
                >> PINK
                >> RED
                >> ORANGE
                >> YELLOW
                >> GREEN
                >> BLUE
                >> PURPLE
                """);

        out.print("Color: ");
        String choice = scanner.nextLine().toLowerCase();
        if (!choice.equals("pink") && !choice.equals("red") && !choice.equals("orange") && !choice.equals("yellow")
        && !choice.equals("green") && !choice.equals("blue") && !choice.equals("purple")) {
            out.println("Please choose a color from the list!");
        }

        switch (choice) {
            case "pink" ->
                    {
                        if (boardColor.equals(SET_BG_COLOR_PINK)) {
                            return;
                        } else { boardColor = SET_BG_COLOR_PINK; }
                    }
            case "red" -> boardColor = SET_BG_COLOR_RED;
//            case "orange" -> //make a orange bg color
            case "yellow" -> boardColor = SET_BG_COLOR_YELLOW;
//            case "green" -> //make a light green that doesn't clash
            case "blue" -> boardColor = SET_BG_COLOR_DARK_BLUE;
            case "purple" -> boardColor = SET_BG_COLOR_MAGENTA;
            default -> boardColor = SET_BG_COLOR_PINK;
         }

        out.println("Awesome! Here is your new board: ");
    }

    public void updateGameData(GameData updatedGame) {
        this.currentGameData = updatedGame;
    }

    public void printHelp(PrintStream out, boolean playing) {
        out.println(SET_TEXT_BOLD);

        if (playing) {
            out.print("""
                >> Redraw chess board: Redraws the board of the chess game being played/observed.
                >> Leave: Exit the game and return to the menu.
                >> Make move: Input a move to make provided it is your turn.
                >> Resign: Admit defeat and forfeit the game.
                >> Highlight legal moves: Legal moves for a selected piece are highlighted on the board.
                >> Choose a different color for the board.
                """);
        } else {
            out.print("""
                    >> Leave: Pretty much you can only go back to the menu.
                    >> Join a game if you want more to do.
                    >> Redraw chess board: Redraws the board of the chess game being played/observed
                    >> Highlight legal moves: Legal moves for a selected piece are highlighted on the board.
                    """);
        }

        out.print(RESET_TEXT_BOLD_FAINT);
    }

    public void printMenu(PrintStream out, boolean playing) {
        out.println();
        if (playing) {
            out.print("""
                    >> 1. Help
                    >> 2. Leave
                    >> 3. Redraw chess board
                    >> 4. Make move
                    >> 5. Resign
                    >> 6. Highlight legal moves
                    >> 7. Change board color
                    """);
        } else {
            out.print("""
                    >> 1. Help
                    >> 2. Leave
                    >> 3. Redraw chess board
                    >> 4. Highlight legal moves
                    """);
        }
    }


}
