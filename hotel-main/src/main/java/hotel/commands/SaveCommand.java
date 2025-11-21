package hotel.commands;

import hotel.io.CsvIO;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * Saves current state to CSV using Apache Commons CSV.
 */
public class SaveCommand extends Command {
    @Override
    public void execute(Scanner scanner) {
        System.out.print("Enter filename to save (default: hotel-state.csv): ");
        String line = scanner.nextLine();
        String file = (line == null || line.isBlank()) ? "hotel-state.csv" : line.trim();
        try {
            CsvIO.saveState(Path.of(file), hotel);
            System.out.println("State saved to: " + Path.of(file).toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Failed to save state: " + e.getMessage());
        }
    }
}
