package terminal2048.game;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.NullPointerException;

import terminal2048.graphics.Image;
import terminal2048.graphics.Canvas;
import terminal2048.game.Tile;

public class Board {
    /*
    Represents a board in the game of 2048.

    Contains and moves tiles (Tile objects).
    */

    Image image;

    public Tile[][] tiles = new Tile[4][4];  // tiles[row][column]
    Canvas cv;

    private static Boolean containsNull(Tile[] row) {
        for ( Tile tile : row ) {
            if (tile == null) {
                return true;
            }
        }
        return false;
    }

    private static void noneInARowFunc(String direction, Tile[] newTileRow) {
        if (direction == "forward") {
            for ( int i = 3; i > -1; i-- ) {
                if (newTileRow[i] != null) {
                    for ( int j = 0; (i + j < 4) && (newTileRow[i + j] != null); j++ ) {
                        // move tile over one spot
                        newTileRow[i + j] = newTileRow[i + (j - 1)];
                        newTileRow[i + (j - 1)] = null;
                    }
                }
            }
        } else if (direction == "backward") {
            for ( int i = 0; i < 4; i++ ) {
                if (newTileRow[i] != null) {
                    for ( int j = 0; (newTileRow[i - j] != null) && (newTileRow[i - j].value > -1); j++ ) {
                        // move tile backwards one spot
                        newTileRow[i + j] = newTileRow[i + (j - 1)];
                        newTileRow[i + (j - 1)] = null;
                    }
                }
            }
        }
    }

    private static void twoInARow(int tile1, int tile2, Tile[] newTileRow, Tile[] tileRow, String direction, String typeRow, Canvas canvas, int coordRow) {
        /*
        tiles one and two are the indices of the equal tiles in the row

        tile2 has to be greater than tile1
        */
        
        // move all tiles to farthest to the left (or right if backwards) until hit by other tile
        // above step done left to right when forwards, and right to left when backwards (in iterating over tiles)

        // remove equal tiles and replace left (or right if backwards) with new tile of double value
        newTileRow = tileRow;
        int newValue = newTileRow[tile1].value * 2;
        if (direction == "forward") {
            newTileRow[tile1] = null;
            if (typeRow == "horizontal") {
                newTileRow[tile2] = new Tile(canvas, tile2, coordRow, newValue);
            } else if (typeRow == "vertical") {
                newTileRow[tile2] = new Tile(canvas, coordRow, tile2, newValue);
            }
        } else if (direction == "backward") {
            newTileRow[tile2] = null;
            if (typeRow == "horizontal") {
                newTileRow[tile1] = new Tile(canvas, tile1, coordRow, newValue);
            } else if (typeRow == "vertical") {
                newTileRow[tile1] = new Tile(canvas, coordRow, tile1, newValue);
            }
        }
        noneInARowFunc(direction, newTileRow);
    }

    private static void threeInARow(int firstIndex, Tile[] newTileRow, Tile[] tileRow, Canvas canvas, String direction, String typeRow, int coordRow) {
        newTileRow = tileRow;
        int ogValue = newTileRow[firstIndex].value;
        int newValue = ogValue * 2;
        
        if (direction == "forward") {
            newTileRow[firstIndex] = null;
            if (typeRow == "horizontal") {
                newTileRow[firstIndex + 2] = new Tile(canvas, firstIndex + 2, coordRow, newValue);
                newTileRow[firstIndex + 1] = new Tile(canvas, firstIndex + 1, coordRow, ogValue);
            } else if (typeRow == "vertical") {
                newTileRow[firstIndex + 2] = new Tile(canvas, coordRow, firstIndex + 2, newValue);
                newTileRow[firstIndex + 1] = new Tile(canvas, coordRow, firstIndex + 1, ogValue);
            }
        } else if (direction == "backward") {
            newTileRow[firstIndex + 2] = null;
            if (typeRow == "horizontal") {
                newTileRow[firstIndex] = new Tile(canvas, firstIndex, coordRow, newValue);
                newTileRow[firstIndex + 1] = new Tile(canvas, firstIndex + 1, coordRow, ogValue);
            } else if (typeRow == "vertical") {
                newTileRow[firstIndex] = new Tile(canvas, coordRow, firstIndex, newValue);
                newTileRow[firstIndex + 1] = new Tile(canvas, coordRow, firstIndex + 1, ogValue);
            }
        }
        noneInARowFunc(direction, newTileRow);
    }

    private static Tile[] mergeRow(Tile[] tileRow, String direction, String typeRow, int coordRow, Canvas canvas) {
        /*
        Takes array of tiles (representing a row or column)
        and returns what that row should look like after a
        move in `move` direction

        direction "forward" means right or down,
        "backward" means left or up

        `typeRow` - "horizontal" or "vertical"
        `coordRow` - either x or y coord of row depending on `typeRow`
        */

        Tile[] newTileRow = new Tile[4];

        // if all 4 are equal
        if (!containsNull(tileRow)) /* check if full */ {
            if (tileRow[0].value == tileRow[1].value && tileRow[0].value == tileRow[2].value && tileRow[0].value == tileRow[3].value) {
                int newValue = tileRow[0].value * 2;
                int[] newXs = new int[2];
                int[] newYs = new int[2];
                
                if (typeRow == "horizontal") {
                    newYs[0] = coordRow;
                    newYs[1] = coordRow;
                    if (direction == "forward") {
                        newXs[0] = 3;
                        newXs[1] = 4;
                    } else if (direction == "backward") {
                        newXs[0] = 0;
                        newXs[1] = 1;
                    }
                } else if (typeRow == "vertical") /*EXPLICIT IS BETTER THAN IMPLICIT*/ {
                    newXs[0] = coordRow;
                    newXs[1] = coordRow;
                    if (direction == "forward") {
                        newYs[0] = 3;
                        newYs[1] = 4;
                    } else if (direction == "backward") {
                        newYs[0] = 0;
                        newYs[1] = 1;
                    }
                }
                newTileRow[3] = new Tile(canvas, newXs[0], newYs[0], newValue);
                newTileRow[4] = new Tile(canvas, newXs[1], newYs[1], newValue);
            }
        }

        // if none in a row are equal
        Boolean noneInARow = true;
        for ( int t = 0; t < 3; t++ ) {
            try {
                if (tileRow[t].value == tileRow[t + 1].value) {
                    noneInARow = false;
                }
            } catch (NullPointerException e) {
                if (tileRow[t] == tileRow[t + 1]) {
                    noneInARow = false;
                }
            }
        }
        if (noneInARow) {
            newTileRow = tileRow;
            noneInARowFunc(direction, newTileRow);
        }

        // if 2 in a row are equal
        for ( int t = 0; t < 3; t++ ) {
            if (tileRow[t] != null && tileRow[t + 1] != null) {
                if ( tileRow[t].value == tileRow[t + 1].value ) {
                    try {
                        if (tileRow[t + 2].value != tileRow[t].value) {
                            twoInARow(t, t + 1, newTileRow, tileRow, direction, typeRow, canvas, coordRow);
                        }
                    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                        twoInARow(t, t + 1, newTileRow, tileRow, direction, typeRow, canvas, coordRow);
                    }
                }
            }
        }

        // if 3 in a row are equal
        for ( int t = 0; t < 2; t++ ) {
            if (tileRow[t] != null && tileRow[t + 1] != null && tileRow[t + 2] != null) {
                if (tileRow[t].value == tileRow[t + 1].value && tileRow[t].value == tileRow[t + 2].value) {
                    try {
                        if (tileRow[t + 3].value != tileRow[1].value) {
                            threeInARow(t, newTileRow, tileRow, canvas, direction, typeRow, coordRow);
                        }
                    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                        threeInARow(t, newTileRow, tileRow, canvas, direction, typeRow, coordRow);
                    }
                }
            }
        }

        return newTileRow;
    }

    private Tile[][] transposeTiles() {
        Tile[][] columns = new Tile[4][4];
        for ( int c = 0; c < 4; c++ ) {
            for ( int r = 0; r < 4; r++ ) {
                columns[c][r] = tiles[r][c];
            }
        }
        return columns;
    }

    public Board(Canvas canvas) throws FileNotFoundException {

        cv = canvas;
        ArrayList<String> boardStringLines = new ArrayList<String>(0);

        File file = new File("resources/board");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            boardStringLines.add(sc.nextLine());
        }

        image = new Image(String.join("\n", boardStringLines), canvas, 0, 0);

        int[] startingTileCoords = new int[4];  // two for each tile
        for ( int i = 0; i < 4; i++ ) {
            startingTileCoords[i] = ThreadLocalRandom.current().nextInt(0, 4);
        }

        tiles[startingTileCoords[0]][startingTileCoords[1]] = 
            new Tile(canvas, startingTileCoords[0], startingTileCoords[1], Tile.randomStartValue());
        tiles[startingTileCoords[2]][startingTileCoords[3]] = 
            new Tile(canvas, startingTileCoords[2], startingTileCoords[2], Tile.randomStartValue());
    }

    public void makeMove(String direction, Screen stdscr, TextGraphics graphics) {
        /*
         - after each move a new 2 or 4 tile is placed
         - 10% chance of getting a 4
         - location is completely random
         - if 3 in a row are same and move is in direction of line, last two are merged

        how ill do it:
         - run mergeRow() on each row (or column).
         - add new tile
         - calculate where each tile has to go and move them all one place at a time each frame

        `direction` can be "right", "left", "up" or "down"
        */
        String directionParam;
        if (direction == "up") {
            directionParam = "forward";
        } else {
            directionParam = "backward";
        }

        // calculate new locations of every tile on board
        if (direction == "right") {
            for ( int r = 0; r < 4; r++ ) {
                tiles[r] = mergeRow(tiles[r], "forward", "horizontal", r, cv);
            }
        } else if (direction == "left") {
            for ( int r = 0; r < 4; r++ ) {
                tiles[r] = mergeRow(tiles[r], "backward", "horizontal", r, cv);
            }
        } else if (direction == "up" || direction == "down") {
            Tile[][] transposedTiles = transposeTiles();
            for ( int c = 0; c < 4; c++ ) {
                transposedTiles[c] = mergeRow(transposedTiles[c], directionParam, "vertical", c, cv);
            }
            for ( int r = 0; r < 4; r++ ) {
                for ( int c = 0; c < 4; c++ ) {
                    tiles[r][c] = transposedTiles[c][r];
                }
            }
        }

        // create new tile
        int newTileX;
        int newTileY;

        ArrayList<Integer> newTilePotentialXs = new ArrayList<Integer>(0);
        ArrayList<Integer> newTilePotentialYs = new ArrayList<Integer>(0);
        for ( int r = 0; r < 4; r++ ) {
            for ( int c = 0; c < 4; c++ ) {
                if (tiles[r][c] == null) {
                    newTilePotentialXs.add(c);
                    newTilePotentialYs.add(r);
                }
            }
        }

        newTileX = newTilePotentialXs.get(ThreadLocalRandom.current().nextInt(0, newTilePotentialXs.size()));
        newTileY = newTilePotentialYs.get(ThreadLocalRandom.current().nextInt(0, newTilePotentialYs.size()));
        tiles[newTileX][newTileY] = new Tile(cv, newTileX, newTileY, Tile.randomStartValue());
    }
}