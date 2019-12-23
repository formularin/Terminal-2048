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
    public Canvas cv;

    public static Boolean containsNull(Tile[] row) {
        for ( Tile tile : row ) {
            if (tile == null) {
                return true;
            }
        }
        return false;
    }

    public static Tile[] noneInARowFunc(String direction, Tile[] oldNewTileRow) {
        Tile[] newTileRow = oldNewTileRow.clone();
        if (direction == "forward") {
            for ( int i = 3; i >= 0; i-- ) {
                if (newTileRow[i] != null) {
                    for ( int j = 1; (i + j < 4) && (newTileRow[i + j] == null); j++ ) {
                        // move tile over one spot
                        newTileRow[i + j] = newTileRow[i + j - 1];
                        newTileRow[i + j - 1] = null;
                    }
                }
            }
        } else if (direction == "backward") {
            for ( int i = 0; i < 4; i++ ) {
                if (newTileRow[i] != null) {
                    for ( int j = 1; (i - j >= 0) && (newTileRow[i - j] == null); j++ ) {
                        // move tile backwards one spot
                        newTileRow[i - j] = newTileRow[i - j + 1];
                        newTileRow[i - j + 1] = null;
                    }
                }
            }
        }
        return newTileRow;
    }

    public static Tile[] twoInARow(int tile1, int tile2, Tile[] tileRow, String direction, String typeRow, Canvas canvas, int coordRow) {
        /*
        tiles one and two are the indices of the equal tiles in the row

        tile2 has to be greater than tile1
        */
        // remove equal tiles and replace left (or right if backwards) with new tile of double value
        Tile[] newTileRow = tileRow.clone();
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
        return noneInARowFunc(direction, newTileRow);
    }

    public static Tile[] threeInARow(int lastIndex, Tile[] tileRow, Canvas canvas, String direction, String typeRow, int coordRow) {
        // lastindex is the index of the tile in the group of three 
        // that is furthest to the right
        // (the to-be-created greater value tile will be in its place)
        Tile[] newTileRow = tileRow.clone();
        int ogValue = newTileRow[lastIndex].value;
        int newValue = ogValue * 2;
        
        if (direction == "forward") {
            if (typeRow == "horizontal") {
                newTileRow[lastIndex] = new Tile(canvas, lastIndex, coordRow, newValue);
                newTileRow[lastIndex - 1] = new Tile(canvas, lastIndex - 1, coordRow, ogValue);
                newTileRow[lastIndex - 2] = null;
            } else if (typeRow == "vertical") {
                newTileRow[lastIndex] = new Tile(canvas, coordRow, lastIndex, newValue);
                newTileRow[lastIndex - 1] = new Tile(canvas, coordRow, lastIndex - 1, ogValue);
                newTileRow[lastIndex - 2] = null;
            }
            if (tileRow[lastIndex - 1] == null || tileRow[lastIndex - 2] == null) {
                newTileRow[0] = null;
            }
        } else if (direction == "backward") {
            newTileRow[lastIndex] = null;
            if (typeRow == "horizontal") {
                if (containsNull(tileRow)) {
                    newTileRow[0] = new Tile(canvas, 0, coordRow, newValue);
                    newTileRow[1] = new Tile(canvas, 1, coordRow, ogValue);
                } else {
                    newTileRow[0] = new Tile(canvas, 0, coordRow, newValue);
                    newTileRow[1] = new Tile(canvas, 1, coordRow, ogValue);
                }
            } else if (typeRow == "vertical") {
                if (containsNull(tileRow)) {
                    newTileRow[0] = new Tile(canvas, coordRow, 0, newValue);
                    newTileRow[1] = new Tile(canvas, coordRow, 1, ogValue);
                } else {
                    newTileRow[0] = new Tile(canvas, coordRow, 0, newValue);
                    newTileRow[1] = new Tile(canvas, coordRow, 1, ogValue);
                }
            }
            if (tileRow[lastIndex - 1] == null || tileRow[lastIndex - 2] == null) {
                newTileRow[3] = null;
            }
        }
        return noneInARowFunc(direction, newTileRow);
    }

    public static Tile[] mergeRow(Tile[] tileRow, String direction, String typeRow, int coordRow, Canvas canvas) {
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

        if (!containsNull(tileRow)) /* if all 4 are equal */ {
            if (tileRow[0].value == tileRow[1].value && tileRow[0].value == tileRow[2].value && tileRow[0].value == tileRow[3].value) {
                int newValue = tileRow[0].value * 2;
                if (direction == "forward") {
                    if (typeRow == "horizontal") {
                        newTileRow[2] = new Tile(canvas, 2, coordRow, newValue);
                        newTileRow[3] = new Tile(canvas, 3, coordRow, newValue);
                    } else if (typeRow == "vertical") {
                        newTileRow[2] = new Tile(canvas, coordRow, 2, newValue);
                        newTileRow[3] = new Tile(canvas, coordRow, 3, newValue);
                    }
                } else if (direction == "backward") {
                    if (typeRow == "horizontal") {
                        newTileRow[1] = new Tile(canvas, 1, coordRow, newValue);
                        newTileRow[0] = new Tile(canvas, 0, coordRow, newValue);
                    } else if (typeRow == "vertical") {
                        newTileRow[1] = new Tile(canvas, coordRow, 1, newValue);
                        newTileRow[0] = new Tile(canvas, coordRow, 0, newValue);
                    }
                }
            }
            return newTileRow;
        }

        // if 3 in a row are equal
        for ( int i = 0; i < 2; i++ ) {
            if (tileRow[i] != null) {
                ArrayList<Tile> equalTiles = new ArrayList<Tile>(0);
                int lastIndex = 0;
                for ( int j = 0; i + j < 4 && (tileRow[i + j] == null || tileRow[i].value == tileRow[i + j].value); j++ ) {
                    // current tile is either null or has the same value as the first tile
                    if (tileRow[i + j] != null) {
                        equalTiles.add(tileRow[i + j]);
                    }
                    lastIndex = i + j;
                }
                if (equalTiles.size() == 3) {
                    newTileRow = threeInARow(lastIndex, tileRow, canvas, direction, typeRow, coordRow);
                    return newTileRow;
                }
            }
        }

        // if 2 in a row are equal
        for ( int t = 0; t < 3; t++ ) {
            if (tileRow[t] != null && tileRow[t + 1] != null) {
                if ( tileRow[t].value == tileRow[t + 1].value ) {
                    try {
                        if (tileRow[t + 2].value != tileRow[t].value) {
                            newTileRow = twoInARow(t, t + 1, tileRow, direction, typeRow, canvas, coordRow);
                            return newTileRow;
                        }
                    } catch (java.lang.ArrayIndexOutOfBoundsException | java.lang.NullPointerException e) {
                        newTileRow = twoInARow(t, t + 1, tileRow, direction, typeRow, canvas, coordRow);
                        return newTileRow;
                    }
                }
            }
        }

        // if none in a row are equal
        Boolean noneInARow = true;
        for ( int t = 0; t < 3; t++ ) {
            try {
                if (tileRow[t].value == tileRow[t + 1].value) {
                    noneInARow = false;
                }
            } catch (NullPointerException e) {}
        }
        if (noneInARow) {
            newTileRow = noneInARowFunc(direction, tileRow);
            return newTileRow;
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
            directionParam = "backward";
        } else {
            directionParam = "forward";
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