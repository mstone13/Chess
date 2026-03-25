package ui;

import model.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoard {
    private static final String EMPTY = "   ";

    public static void run(GameData game, String playerColor) {
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
        String[] upperPieces = {" R ", " N ", " B ", " K ", " Q ", " B ", " N ", " R "};

        placeUpperPieces(out, upperPieces, playerColor);

        if (playerColor.equalsIgnoreCase("black")) {
            for (int row = 3; row < 7; row++) {
                setLightBlue(out);
                out.print(" " + row + " ");
                if (row % 2 == 0) {
                    drawLineBlackFirst(out);
                } else {
                    drawLineWhiteFirst(out);
                }
                setLightBlue(out);
                out.print(" " + row + " ");

                newLine(out);
            }
        } else {
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
        }

        placeLowerPieces(out, upperPieces, playerColor);
        drawHeaders(out, playerColor);
    }

    private static void drawHeaders(PrintStream out, String playerColor) {
        setGrey(out);

        String[] headers = {" h "," g "," f "," e "," d ", " c ", " b ", " a "};
        out.print(EMPTY);
        if (playerColor.equalsIgnoreCase("black")) {
            for (int i = headers.length - 1; i >= 0; i--) {
                printHeaderText(out, headers[i]);
            }
        } else {
            for (String header : headers) {
                printHeaderText(out, header);
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

    private static void placeLowerPieces(PrintStream out, String[] pieces, String playerColor) {
        setLightBlue(out);
        String color;
        String lastRow;
        String pawnRow;

        if (playerColor.equalsIgnoreCase("black")) {
            color = SET_TEXT_COLOR_BLUE;
            lastRow = " 8 ";
            pawnRow = " 7 ";
        } else {
            color = SET_TEXT_COLOR_RED;
            lastRow = " 1 ";
            pawnRow = " 2 ";
        }

        out.print(pawnRow);
        int col = 0;

        while (col < 8) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_WHITE);
                out.print(color);
            } else {
                out.print(SET_BG_COLOR_PINK);
                out.print(color);
            }
            out.print(" P ");
            col++;
        }
        setLightBlue(out);
        out.print(pawnRow);
        col = 0;
        newLine(out);

        out.print(lastRow);
        for (String piece : pieces) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_PINK);
                out.print(color);
                out.print(piece);
            } else {
                out.print(SET_BG_COLOR_WHITE);
                out.print(color);
                out.print(piece);
            }
            col++;
        }

        setLightBlue(out);
        out.print(lastRow);

        newLine(out);
    }

    private static void placeUpperPieces(PrintStream out, String[] pieces, String playerColor) {
        String color;
        String topRow;
        String pawnRow;

        setLightBlue(out);

        if (playerColor.equalsIgnoreCase("black")) {
            color = SET_TEXT_COLOR_RED;
            topRow = " 1 ";
            pawnRow = " 2 ";
        } else {
            color = SET_TEXT_COLOR_BLUE;
            topRow = " 8 ";
            pawnRow = " 7 ";
        }

        out.print(topRow);
        int col = 0;
        for (String piece : pieces) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_WHITE);
                out.print(color);
                out.print(piece);
            } else {
                out.print(SET_BG_COLOR_PINK);
                out.print(color);
                out.print(piece);
            }
            col++;
        }
        setLightBlue(out);
        out.print(topRow);
        col = 0;
        newLine(out);

        out.print(pawnRow);
        while (col < 8) {
            if (col % 2 == 0) {
                out.print(SET_BG_COLOR_PINK);
                out.print(color);
            } else {
                out.print(SET_BG_COLOR_WHITE);
                out.print(color);
            }
            out.print(" P ");
            col++;
        }
        setLightBlue(out);
        out.print(pawnRow);
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
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_BLACK);
    }

}
