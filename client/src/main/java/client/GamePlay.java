package client;

import chess.*;
import client.websocket.WebSocketFacade;
import facade.ServerFacade;
import model.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GamePlay {
    private final Scanner scanner = new Scanner(System.in);

    public GamePlay() {}

    public String run(GameData game, String playerColor, boolean playing,
                      WebSocketFacade facade, String authToken) throws InvalidMoveException {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        var result = "";

        while (!result.equals("2") && !result.equals("5")){
            printMenu(out, playing);
            result = scanner.nextLine();
            directAction(out, result, game, playerColor, playing, facade, authToken);
        }

        if (result.equals("5")) {
            return "RESIGN";
        } else {
            return "LEAVE";
        }
    }

    public void directAction(PrintStream out, String result, GameData game, String playerColor, boolean playing,
                                WebSocketFacade facade, String authToken) throws InvalidMoveException {
        switch (result) {
            case "1" -> printHelp(out, playing);
            case "3" -> ui.ChessBoard.run(game, playerColor.toUpperCase(), null, null);
            case "4" -> makeMove(out, game, facade, playerColor, authToken);
            case "6" -> highlightLegalMoves(out, game, playerColor);
        }
    }

    public void highlightLegalMoves(PrintStream out, GameData game, String playerColor) {
        ChessPosition pos = getStartPos(out);
        Collection<ChessMove> legalMoves = new ChessGame().validMoves(pos);

        ui.ChessBoard.run(game, playerColor, legalMoves, pos);
    }

    public void makeMove(PrintStream out, GameData gameData, WebSocketFacade facade,
                         String playerColor, String authToken) {
        try {
            if(!confirmTurn(playerColor, gameData)) {
                out.println(SET_TEXT_COLOR_RED + ">> ERROR: It's not your turn. Please wait for the other player.");
                out.print(RESET_TEXT_COLOR);
                return;
            }

            ChessPosition startPos = getStartPos(out);
            ChessPosition endPos = getEndPos(out);
            ChessPiece.PieceType promotionPiece = getPromotionPiece(out, startPos, endPos, gameData);

            ChessMove move = new ChessMove(startPos, endPos, promotionPiece);

            facade.makeMove(authToken, gameData.gameID(), move);

        } catch (Exception e) {
            out.println("Error sending move: " + e.getMessage());
        }
    }

    public boolean confirmTurn(String playerColor, GameData gameData) {
        ChessGame.TeamColor actualColor = null;
        if (playerColor.equalsIgnoreCase("white")) {
            actualColor = ChessGame.TeamColor.WHITE;
        } else {
            actualColor = ChessGame.TeamColor.BLACK;
        }

        return gameData.game().getTeamTurn().equals(actualColor);
    }

    public ChessPosition getStartPos(PrintStream out) {
        boolean needToLoop = true;
        int row = 0;
        int col = 0;

        out.println("Please enter a piece:");
        while (needToLoop) {
            out.println("Enter the row of a piece [1-8]: ");
            try {
                row = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                out.println("Please input a valid integer for row");
                continue;
            }

            out.println("Enter the column of a piece [a-h]: ");
            String inputLine = scanner.nextLine().toLowerCase();
            if (inputLine.length() != 1) {
                out.println("Please input a single character for column [a-h].");
                continue;
            }
            char input = inputLine.charAt(0);
            col = input - 'a' + 1;

            if (row < 1 || row > 8 || col < 1 || col > 8) {
                out.println("Please input a valid row [1-8] and col [a-h].");
            } else {
                needToLoop = false;
            }
        }

        return new ChessPosition(row, col);
    }

    public ChessPosition getEndPos(PrintStream out) {
        boolean needToLoop = true;
        int row = 0;
        int col = 0;

        out.println("Please enter end position:");
        while (needToLoop) {
            out.println("Enter the row of the desired end position [1-8]: ");
            try {
                row = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                out.println("Please input a valid integer for row");
                continue;
            }

            out.println("Enter the column of the desired end position [a-h]: ");
            String inputLine = scanner.nextLine().toLowerCase();
            if (inputLine.length() != 1) {
                out.println("Please input a single character for column [a-h].");
                continue;
            }
            char input = inputLine.charAt(0);
            col = input - 'a' + 1;

            if (row < 1 || row > 8 || col < 1 || col > 8) {
                out.println("Please input a valid row [1-8] and col [a-h].");
            } else {
                needToLoop = false;
            }
        }

        return new ChessPosition(row, col);
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
                    out.print("Enter what promotion piece you want: ");
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

    public void printHelp(PrintStream out, boolean playing) {
        out.println(SET_TEXT_BOLD);

        if (playing) {
            out.print("""
                >> Redraw chess board: Redraws the board of the chess game being played/observed.
                >> Leave: Exit the game and return to the menu.
                >> Make move: Input a move to make provided it is your turn.
                >> Resign: Admit defeat and forfeit the game.
                >> Highlight legal moves: Legal moves for a selected piece are highlighted on the board.
                """);
        } else {
            out.print("""
                    >> Leave: Pretty much you can only go back to the menu.
                    >> Join a game if you want more to do.
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
                    """);
        } else {
            out.print("""
                    >> 1. Help
                    >> 2. Leave
                    """);
        }
    }
}
