package terminal2048;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Board {
    /*
    Represents a board in the game of 2048.

    Contains and moves tiles (ints).
    */

    public int[][] tiles = new int[4][4];  // tiles[row][column]

    private static Boolean containsNull(int[] row) {
        for ( int tile : row ) {
            if (tile == 0) {
                return true;
            }
        }
        return false;
    }

    private static int[] noneInARowFunc(String direction, int[] oldNewTileRow) {
        int[] newTileRow = oldNewTileRow.clone();
        if (direction == "forward") {
            for ( int i = 3; i >= 0; i-- ) {
                if (newTileRow[i] != 0) {
                    for ( int j = 1; (i + j < 4) && (newTileRow[i + j] == 0); j++ ) {
                        // move tile over one spot
                        newTileRow[i + j] = newTileRow[i + j - 1];
                        newTileRow[i + j - 1] = 0;
                    }
                }
            }
        } else if (direction == "backward") {
            for ( int i = 0; i < 4; i++ ) {
                if (newTileRow[i] != 0) {
                    for ( int j = 1; (i - j >= 0) && (newTileRow[i - j] == 0); j++ ) {
                        // move tile backwards one spot
                        newTileRow[i - j] = newTileRow[i - j + 1];
                        newTileRow[i - j + 1] = 0;
                    }
                }
            }
        }
        return newTileRow;
    }

    private static int[] twoInARow(int tile1, int tile2, int[] tileRow, String direction) {
        /*
        tiles one and two are the indices of the equal tiles in the row

        tile2 has to be greater than tile1
        */
        // remove equal tiles and replace left (or right if backwards) with new tile of double value
        int[] newTileRow = tileRow.clone();
        int newValue = newTileRow[tile1] * 2;
        if (direction == "forward") {
            newTileRow[tile1] = 0;
            newTileRow[tile2] = newValue;
        } else if (direction == "backward") {
            newTileRow[tile2] = 0;
            newTileRow[tile1] = newValue;
        }
        return noneInARowFunc(direction, newTileRow);
    }

    private static int[] threeInARow(int lastIndex, int[] tileRow, String direction) {
        // lastindex is the index of the tile in the group of three 
        // that is furthest to the right
        // (the to-be-created greater value tile will be in its place)
        int[] newTileRow = tileRow.clone();
        int ogValue = newTileRow[lastIndex];
        int newValue = ogValue * 2;
        
        if (direction == "forward") {
            newTileRow[lastIndex] = newValue;
            newTileRow[lastIndex - 1] = ogValue;
            newTileRow[lastIndex - 2] = 0;
            if (tileRow[lastIndex - 1] == 0 || tileRow[lastIndex - 2] == 0) {
                newTileRow[0] = 0;
            }
        } else if (direction == "backward") {
            newTileRow[lastIndex] = 0;
            if (containsNull(tileRow)) {
                newTileRow[0] = newValue;
                newTileRow[1] = ogValue;
            } else {
                newTileRow[0] = newValue;
                newTileRow[1] = ogValue;
            }
            if (tileRow[lastIndex - 1] == 0 || tileRow[lastIndex - 2] == 0) {
                newTileRow[3] = 0;
            }
        }
        return noneInARowFunc(direction, newTileRow);
    }

    private static int randomStartValue() {
        int[] startingTiles = {2, 2, 2, 2, 2, 2, 2, 2, 2, 4};
        int valueIndex = ThreadLocalRandom.current().nextInt(0, 10);
        return startingTiles[valueIndex];
    }

    private static int[] mergeRow(int[] tileRow, String direction) {
        /*
        Takes array of tiles (representing a row or column)
        and returns what that row should look like after a
        move in `move` direction

        direction "forward" means right or down,
        "backward" means left or up

        `typeRow` - "horizontal" or "vertical"
        `coordRow` - either x or y coord of row depending on `typeRow`
        */

        int[] newTileRow = new int[4];
        
        // if all 4 are equal
        if (!containsNull(tileRow)) {
            if (tileRow[0] == tileRow[1] && tileRow[0] == tileRow[2] && tileRow[0] == tileRow[3]) {
                int newValue = tileRow[0] * 2;
                if (direction == "forward") {
                        newTileRow[2] = newValue;
                        newTileRow[3] = newValue;
                } else if (direction == "backward") {
                    newTileRow[1] = newValue;
                    newTileRow[0] = newValue;
                }
                return newTileRow;
            }
        }

        // if 3 in a row are equal
        for ( int i = 0; i < 2; i++ ) {
            if (tileRow[i] != 0) {
                ArrayList<Integer> equalTiles = new ArrayList<Integer>(0);
                for ( int j = 0; i + j < 4 && (tileRow[i + j] == 0 || tileRow[i] == tileRow[i + j]); j++ ) {
                    // current tile is either null or has the same value as the first tile
                    if (tileRow[i + j] != 0) {
                        equalTiles.add(i + j);
                    }
                }
                if (equalTiles.size() == 3) {
                    newTileRow = threeInARow(equalTiles.get(2), tileRow, direction);
                    return newTileRow;
                }
            }
        }

        // if 2 in a row are equal
        for ( int i = 0; i < 3; i++ ) {
            if (tileRow[i] != 0) {
                ArrayList<Integer> equalTiles = new ArrayList<Integer>(0);
                for ( int j = 0; i + j < 4 && (tileRow[i + j] == 0 || tileRow[i] == tileRow[i + j]); j++ ) {
                    // current tile is either null or has the same value as the first tile
                    if (tileRow[i + j] != 0) {
                        equalTiles.add(i + j);
                    }
                }
                if (equalTiles.size() == 2) {
                    newTileRow = twoInARow(equalTiles.get(0), equalTiles.get(1), tileRow, direction);
                    return newTileRow;
                }
            }
        }

        // if none in a row are equal
        Boolean noneInARow = true;
        for ( int t = 0; t < 3; t++ ) {
            if (tileRow[t] != 0) {
                if (tileRow[t] == tileRow[t + 1]) {
                    noneInARow = false;
                }
            }
        }
        if (noneInARow) {
            newTileRow = noneInARowFunc(direction, tileRow);
            return newTileRow;
        }

        return newTileRow;
    }

    private int[][] transposeTiles() {
        int[][] columns = new int[4][4];
        for ( int c = 0; c < 4; c++ ) {
            for ( int r = 0; r < 4; r++ ) {
                columns[c][r] = tiles[r][c];
            }
        }
        return columns;
    }

    public Board() {

        int[] startingTileCoords = new int[4];  // two for each tile
        for ( int i = 0; i < 4; i++ ) {
            startingTileCoords[i] = ThreadLocalRandom.current().nextInt(0, 4);
        }

        tiles[startingTileCoords[0]][startingTileCoords[1]] = Board.randomStartValue();
        tiles[startingTileCoords[2]][startingTileCoords[3]] = Board.randomStartValue();
    }

    public void makeMove(String direction, Screen stdscr, TextGraphics graphics) {
        /*
         - after each move a new 2 or 4 tile is placed
         - 10% chance of getting a 4
         - location is completely random
         - if 3 in a row are same and move is in direction of line, last two are merged

        how ill do it:
         - run mergeRow() on each row (or column)
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
                tiles[r] = mergeRow(tiles[r], "forward");
            }
        } else if (direction == "left") {
            for ( int r = 0; r < 4; r++ ) {
                tiles[r] = mergeRow(tiles[r], "backward");
            }
        } else if (direction == "up" || direction == "down") {
            int[][] transposedTiles = transposeTiles();
            for ( int c = 0; c < 4; c++ ) {
                transposedTiles[c] = mergeRow(transposedTiles[c], directionParam);
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
                if (tiles[r][c] == 0) {
                    newTilePotentialXs.add(c);
                    newTilePotentialYs.add(r);
                }
            }
        }

        newTileX = newTilePotentialXs.get(ThreadLocalRandom.current().nextInt(0, newTilePotentialXs.size()));
        newTileY = newTilePotentialYs.get(ThreadLocalRandom.current().nextInt(0, newTilePotentialYs.size()));
        tiles[newTilePotentialXs.get(newTileX)][newTilePotentialYs.get(newTileY)] = Board.randomStartValue();

        // display this to screen
    }

    public String display() {
        String display = "";
        for ( int[] row : tiles ) {
            for ( int tile : row ) {
                if (tile != 0) {
                    display += Integer.toString(tile);
                } else {
                    display += ".";
                }
                display += "\t";
            }
            display += "\n";
        }
        display += "\n";
        return display;
    }
}