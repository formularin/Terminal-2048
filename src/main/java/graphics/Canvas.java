package graphics;

import graphics.Image;


public class Canvas {
    /*
    A grid of chars to be printed onto the screen for each frame.
    */

    public int rows;
    public int columns;
    public String[][] grid;

    public Canvas(int r, int c) {
        rows = r;
        columns = c;
        grid = new String[rows][columns];
    }

    public void replace(int r, int c, String ch) {
        grid[r][c] = ch;
    }

    public String[] get_rows() {

        String[] row_list = new String[rows];
        for (int r = 0; r < grid.length; r++) {
            row_list[r] = String.join("", grid[r]).replace("null", " ");
        }

        return row_list;
    }
}