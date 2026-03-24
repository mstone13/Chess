package ui;

import model.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoard {
    private static final String EMPTY = "   ";

    public static void run(GameData game) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawChessBoard(out, game);

        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);

    }

    private static void drawChessBoard(PrintStream out, GameData game) {
        out.println();
        out.println("White Player: " + game.whiteUsername());
        out.println("Black Player: " + game.blackUsername());

        drawHeaders(out);
        String[] upperPieces = {" R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "};

        // PRINT BLACK PIECES
        placeBlackPieces(out, upperPieces);

        for (int row = 6; row > 2; row--) {
            setLightBlue(out);
            out.print(" " + row + " ");
            if (row % 2 == 0) {
                drawLineWhiteFirst(out);
            } else {
                drawLineBlackFirst(out);
            }
            setLightBlue(out);
            out.print(" " + row + " ");

            newLine(out);
        }

       // PRINT WHITE PIECES
        placeWhitePieces(out, upperPieces);
        drawHeaders(out);
    }

    private static void drawHeaders(PrintStream out) {
        setGrey(out);

        String[] headers = {" h "," g "," f "," e "," d ", " c ", " b ", " a "};
        out.print(EMPTY);
        for (String header : headers) {
            printHeaderText(out, header);
        }
        out.print(EMPTY);
        out.println();
    }

    private static void printHeaderText(PrintStream out, String player) {
        setLightBlue(out);
        out.print(player);
        setGrey(out);
    }

    private static void drawLineWhiteFirst(PrintStream out) {
        for (int col = 0; col < 8; col++) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_WHITE);
            } else {
                out.print(SET_BG_COLOR_PINK);
            }
            out.print(EMPTY);
        }
    }

    private static void drawLineBlackFirst(PrintStream out) {
        for (int col = 0; col < 8; col++) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_PINK);
            } else {
                out.print(SET_BG_COLOR_WHITE);
            }
            out.print(EMPTY);
        }
    }

    private static void placeBlackPieces(PrintStream out, String[] pieces) {
        setLightBlue(out);
        out.print(" 8 ");
        int col = 0;
        for (String piece : pieces) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_BLUE);
                out.print(piece);
            } else {
                out.print(SET_BG_COLOR_PINK);
                out.print(SET_TEXT_COLOR_BLUE);
                out.print(piece);
            }
            col++;
        }
        setLightBlue(out);
        out.print(" 8 ");
        col = 0;
        newLine(out);

        out.print(" 7 ");
        while (col < 8) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_PINK);
                out.print(SET_TEXT_COLOR_BLUE);
            } else {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_BLUE);
            }
            out.print(" P ");
            col++;
        }
        setLightBlue(out);
        out.print(" 7 ");
        newLine(out);

    }

    private static void placeWhitePieces(PrintStream out, String[] pieces) {
        setLightBlue(out);
        out.print(" 2 ");
        int col = 0;

        while (col < 8) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_RED);
            } else {
                out.print(SET_BG_COLOR_PINK);
                out.print(SET_TEXT_COLOR_RED);
            }
            out.print(" P ");
            col++;
        }
        setLightBlue(out);
        out.print(" 2 ");
        col = 0;
        newLine(out);

        out.print(" 1 ");
        for (String piece : pieces) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_PINK);
                out.print(SET_TEXT_COLOR_RED);
                out.print(piece);
            } else {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_RED);
                out.print(piece);
            }
            col++;
        }

        setLightBlue(out);
        out.print(" 1 ");

        newLine(out);

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
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

}
