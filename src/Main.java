import core.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Main application entry point for Five Minutes In The Bathroom.
 */
public class Main {
    public static void main(String[] args) {
        // Run Swing initialization on Event Dispatch Thread (EDT) using anonymous inner class
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("=================================================");
                System.out.println("           Five Minutes In The Bathroom          ");
                System.out.println("  Object-Oriented Programming (OOP) Game Engine  ");
                System.out.println("=================================================");

                GameWindow window = new GameWindow();
                window.showWindow();
            }
        });
    }
}
