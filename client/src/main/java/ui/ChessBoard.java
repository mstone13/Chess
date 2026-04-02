package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoard {
    private static final String EMPTY = "   ";
    private static chess.ChessBoard board;

    public static void run(GameData game, String playerColor) {
        board = game.game().getBoard();

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        if (playerColor == null) {
            playerColor = "WHITE";
        }
        drawChessBoard(out, game, playerColor);
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawChessBoard(PrintStream out, GameData game, String playerColor) {
        out.println(SET_TEXT_BOLD + SET_TEXT_UNDERLINE);
        out.println("GAME: " + game.gameName());
        out.print(RESET_TEXT_UNDERLINE);
        out.println("White Player: " + game.whiteUsername());
        out.println("Black Player: " + game.blackUsername());
        out.print(RESET_TEXT_BOLD_FAINT);

        drawHeaders(out, playerColor);

        printPieces(out);

        drawHeaders(out, playerColor);
    }

    private static void printPieces(PrintStream out) {
        for (int row = 8; row >= 1; row--) {
            setLightBlue(out);
            out.print(" " + row + " ");

            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                drawSquare(out, piece, row, col);
            }

            setLightBlue(out);
            out.print(" " + row + " ");
            newLine(out);
        }
    }

    private static void drawSquare(PrintStream out, ChessPiece piece, int row, int col) {
        boolean isLight = (row + col) % 2 == 1;
        if (isLight) {
            setWhite(out);
        } else {
            setPink(out);
        }

        if (piece == null) {
            out.print("   ");
            return;
        }

        String symbol = switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            out.print(SET_TEXT_COLOR_BLUE);
        } else {
            out.print(SET_TEXT_COLOR_RED);
        }

        out.print(" " + symbol + " ");
    }

    private static void drawHeaders(PrintStream out, String playerColor) {
        setGrey(out);

        String[] headers = {" h "," g "," f "," e "," d ", " c ", " b ", " a "};
        out.print(EMPTY);
        if (playerColor.equalsIgnoreCase("black")) {
            for (String header : headers) {
                printHeaderText(out, header);
            }
        } else {
            for (int i = headers.length - 1; i >= 0; i--) {
                printHeaderText(out, headers[i]);
            }
        }

        out.print(EMPTY);
        out.println();
    }

    private static void printHeaderText(PrintStream out, String player) {
        setLightBlue(out);
        out.print(player);
        setGrey(out);
    }

    private static void newLine(PrintStream out) {
        setGrey(out);
        out.print(EMPTY);
        out.println();
        setLightBlue(out);
    }

    private static void setLightBlue(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_BLUE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setGrey(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
    }

    private static void setPink(PrintStream out) {
        out.print(SET_BG_COLOR_PINK);
    }

}
