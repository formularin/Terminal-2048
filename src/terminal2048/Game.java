package terminal2048;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.screen.VirtualScreen.FrameRenderer;

import java.io.IOException;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

import terminal2048.graphics.Canvas;
import terminal2048.graphics.Image;
import terminal2048.game.Board;


public class Game {

    public static void main(String[] args) throws IOException, InterruptedException {
        // create small animation to test graphics capabilities

        Logger logger = Game.createLoggger();

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen stdscr = new TerminalScreen(terminal);
        stdscr.startScreen();

        TerminalSize terminal_size = terminal.getTerminalSize();
        TextGraphics graphics = stdscr.newTextGraphics();
        
        Canvas canvas = new Canvas(10, 10);
        Image octothorpe = new Image("#", canvas, 0, 0);

        for (int f = 0; f < (canvas.rows * canvas.columns); f++) {
            // update virtual screen
            if ((f + 1) % 10 == 0 && f != 0) {
                octothorpe.move(new int[] {-9, 1});
            } else {
                octothorpe.move(new int[] {1, 0});
            }
            octothorpe.render();

            // update real screen
            stdscr.clear();
            String[] canvas_rows = canvas.get_rows();
            for ( int i = 0; i < canvas.grid.length; i++) {
                graphics.putString(new TerminalPosition(0, i), canvas_rows[i]);
            }
            stdscr.refresh();

            Thread.sleep(100);
        }

        stdscr.close();
    }

    private static Logger createLoggger() throws IOException {
        /*
        Creates a Logger and has it write to terminal-2048.log, but not to console.
        */

        FileHandler handler = new FileHandler("terminal-2048.log", true);
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);

        return logger;
    }
}