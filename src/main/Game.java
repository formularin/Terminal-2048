import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;

import java.io.IOException;


public class Game {
    public static void main(String[] args) throws IOException, InterruptedException {
        
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen stdscr = new TerminalScreen(terminal);
        stdscr.startScreen();

        TextGraphics graphics = stdscr.newTextGraphics();
        String screen_image = "";  // What is to be displayed on the screen

        for (int f = 0; f < 500; f++) {
            // main loop

            // update virtual screen
            screen_image = Integer.toString(f);

            // update real screen
            stdscr.clear();
            graphics.putString(new TerminalPosition(0, 0), screen_image);
            stdscr.refresh();

            Thread.sleep(10);
        }

        stdscr.close();
    }
}