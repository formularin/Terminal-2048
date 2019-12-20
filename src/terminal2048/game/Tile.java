package terminal2048.game;

import java.util.concurrent.ThreadLocalRandom;

import com.googlecode.lanterna.screen.TerminalScreen;

import terminal2048.graphics.Image;
import terminal2048.graphics.Canvas;
import terminal2048.game.Board;


public class Tile {
    /*
    Represents a tile (square with a number) in the game of 2048.

    Doesn't have a board attribute because it is meant to be an attribute of a Board object.
    */

    public Image image;

    public int value;  // the number on the tile
    
    // to be changed by external forces
    public int x;
    public int y;

    public static String createAsciiArt(int num) {
        return String.format(" ___________ \n|           |\n|     %d     |\n|           |\n|___________|", num);
    }

    public static int randomStartValue() {
        int[] startingTiles = {2, 2, 2, 2, 2, 2, 2, 2, 2, 4};
        int valueIndex = ThreadLocalRandom.current().nextInt(0, 10);
        return startingTiles[valueIndex];
    }

    public Tile(Canvas cv, int col, int row, int startingValue) {
        x = col;
        y = row;

        value = startingValue;

        int[] canvasCoordArray = canvasCoords();
        int canvasX = canvasCoordArray[0];
        int canvasY = canvasCoordArray[1];
        image = new Image(createAsciiArt(value), cv, canvasX, canvasY);
    }

    private int[] canvasCoords() {
        int canvasX = (14 * x) + 2;
        int canvasY = (5 * y) + 1;
        int[] canvasCoordArray = {canvasX, canvasY};
        return canvasCoordArray;
    }

    public void move(String direction, TerminalScreen stdscr) {
        /*
        Always moves only one unit.

        To be run repeatedly when the tile has to move on the board - creates sliding effect.
        */

        int[] vector = {0, 0};

        if (direction == "right") {
            vector[0] = 1;
            image.move(vector);
            image.render();
        } else if (direction == "left") {
            vector[0] = -1;
            image.move(vector);
            image.render();
        } else if (direction == "up") {
            vector[1] = -1;
            image.move(vector);
            image.render();
        } else if (direction == "down") {
            vector[1] = 1;
            image.move(vector);
            image.render();
        }
    }
}