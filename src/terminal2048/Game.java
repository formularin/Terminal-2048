/*
If the x and y attributes of the Tile class are not used by the end of the game,
remove them and edit all code (especially Board.mergeRow) accordingly.
*/

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
import java.lang.NullPointerException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

import terminal2048.graphics.Canvas;
import terminal2048.graphics.Image;
import terminal2048.game.Board;
import terminal2048.game.Tile;


public class Game {

    private static void printBoard(Board board) {
        for ( Tile[] row : board.tiles ) {
            for ( Tile tile : row ) {
                try {
                    System.out.print(tile.value);   
                } catch (NullPointerException e) {
                    System.out.print(".");
                }
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // create small animation to test graphics capabilities

        Logger logger = Game.createLoggger();

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen stdscr = new TerminalScreen(terminal);
        // stdscr.startScreen();

        TerminalSize terminal_size = terminal.getTerminalSize();
        TextGraphics graphics = stdscr.newTextGraphics();

        // update virtual screen
        Canvas canvas = new Canvas(25, 70);
        Board board = new Board(canvas);
        printBoard(board);
        board.makeMove("right", stdscr, graphics);
        printBoard(board);
        board.makeMove("left", stdscr, graphics);
        printBoard(board);
        board.makeMove("up", stdscr, graphics);
        printBoard(board);
        board.makeMove("down", stdscr, graphics);
        
        // update real screen
        // stdscr.clear();
        // String[] canvas_rows = canvas.get_rows();
        // for ( int i = 0; i < canvas.grid.length; i++) {
        //     graphics.putString(new TerminalPosition(0, i), canvas_rows[i]);
        // }
        // stdscr.refresh();

        // Thread.sleep(100);

        // stdscr.close();
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