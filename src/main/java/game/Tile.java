package game;

import java.util.Random;

import com.googlecode.lanterna.screen.TerminalScreen;

import graphics.Image;
import graphics.Canvas;
import game.Board;


public class Tile {
    /*
    Represents a tile (square with a number) in the game of 2048.

    Doesn't have a board attribute because it is meant to be an attribute of a Board object.
    */

    public Image image;
    int[] startingTiles = {2, 4};
    int valueIndex = new Random().nextInt(startingTiles.length);
    int value = startingTiles[valueIndex];
    
    // to be changed by external forces
    public int x;
    public int y;


    public Tile(Canvas cv, int col, int row, Board bd) {
        x = col;
        y = row;

        int[] canvasCoordArray = canvasCoords();
        int canvasX = canvasCoordArray[0];
        int canvasY = canvasCoordArray[1];
        image = new Image(createAsciiArt(value), cv, canvasX, canvasY);
    }

    public static String createAsciiArt(int num) {
        return String.format(" ___________ \n|           |\n|     %d     |\n|           |\n|___________|", num);
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

        int[] vector = new int[2];

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