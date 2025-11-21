package hotel.commands;

import java.util.Scanner;

/**
 * Exits the application.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(Scanner scanner) {
        System.out.println("Exiting application.");
        System.exit(0);
    }
}
