package graphics;

import graphics.Canvas;

import java.util.*;


public class Image {
    /*
    A 2-d arrangement of characters that appears on a canvas.
    */

    private class Char {
        public int x;
        public int y;
        public String ch;
    
        public Char(int c, int r, String character) {
            x = c;
            y = r;
            ch = character;
        }
    }

    private Set<Char> chars = new HashSet<Char>();
    private Set<Char> previousChars = new HashSet<Char>();
    private int x;
    private int previousX;
    private int y;
    private int previousY;
    private Canvas canvas;
    
    public Image(String string, Canvas cv, int col, int row) {
        canvas = cv;
        x = col;
        y = row;

        String[] rows = string.split("\n");
        for ( int r = 0; r < rows.length; r++ ) {
            String s = rows[r];
            for ( int c = 0; c < s.length(); c++ ) {
                chars.add(new Char(c, r, String.valueOf(s.charAt(c))));
            }
        }
        previousChars = chars;
    }

    public void move(int[] vector) {
        previousX = x;
        previousY = y;
        
        x += vector[0];
        y += vector[1];
    }

    public void setChars(HashSet<Char> newChars) {
        previousChars = chars;
        chars = newChars;
    }

    public void render() {
        /*
        Alter canvas such that it displays the current version of this image.
        */

        // cover up previous chars
        Iterator<Char> previousCharsItr = previousChars.iterator();
        while (previousCharsItr.hasNext()) {
            Char c = previousCharsItr.next();
            canvas.replace(c.y + previousY, c.x + previousX, " ");
        }

        // add new chars
        Iterator<Char> charsItr = chars.iterator();
        while (charsItr.hasNext()) {
            Char c = charsItr.next();
            canvas.replace(c.y + y, c.x + x, c.ch);
        }
    }
}