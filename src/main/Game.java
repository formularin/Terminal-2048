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
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        TextGraphics graphics = screen.newTextGraphics();

        TerminalSize terminal_size = new TerminalSize(2, 2);
        TextCharacter text_character = new TextCharacter('#');
        BasicTextImage image = new BasicTextImage(terminal_size, text_character);

        graphics.drawImage(new TerminalPosition(0, 0), image);
        screen.refresh();

        Thread.sleep(5000);
    }
}