package hotel;

import hotel.commands.*;
import hotel.factory.CommandRegistry;
import hotel.model.Hotel;
import hotel.model.Room;
import hotel.io.CsvIO;

import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Entry point of the app.
 */
public class Main {
    private static final String HOTEL_NAME = "Yet Another Java Hotel";

    public static void main(String[] args) {
        // Initialize hotel, optionally loading rooms from CSV via CLI arg or HOTEL_CONFIG env var
        Hotel hotel = new Hotel();
        boolean loaded = loadHotelFromConfig(hotel, args);

        if (!loaded) {
            // fallback to defaults
            hotel.addRoom(new Room(101, "Single ensuite", 120.0, 1));
            hotel.addRoom(new Room(102, "Double", 200.0, 2));
            hotel.addRoom(new Room(201, "Triple family", 300.0, 3));
            System.out.println("Using default room configuration. You can provide a CSV path as first argument or via HOTEL_CONFIG env var.");
        }

        System.out.println("==================== Welcome to " + HOTEL_NAME + "! ====================\n");

        CommandRegistry registry = new CommandRegistry();
        registry.registerCommand("view", ViewCommand.class);
        registry.registerCommand("list", ListCommand.class);
        registry.registerCommand("prices", PricesCommand.class);
        registry.registerCommand("checkin", CheckinCommand.class);
        registry.registerCommand("checkout", CheckoutCommand.class);
        registry.registerCommand("save", SaveCommand.class);
        registry.registerCommand("exit", ExitCommand.class);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("    Please enter the command - valid commands: view, list, prices, checkin, checkout, save, exit: ");
            String input = scanner.nextLine();
            if (input == null) continue; // probably not needed
            String cmdName = input.trim().toLowerCase();
            if (cmdName.isEmpty()) continue;

            Command cmd = registry.createCommand(cmdName);
            if (cmd == null) {
                System.out.println("No such command, please try again...");
                continue;
            }
            cmd.setHotel(hotel);
            cmd.execute(scanner);
            
            if (cmd instanceof ExitCommand) {
                running = false;
            }
        }
    }

    private static boolean loadHotelFromConfig(Hotel hotel, String[] args) {
        try {
            String configPath = determineConfigPath(args);
            if (configPath != null) {
                Path path = Path.of(configPath);
                if (Files.isRegularFile(path)) {
                    List<Room> rooms = CsvIO.loadRoomsOrState(path);
                    for (Room r : rooms) hotel.addRoom(r);
                    boolean loaded = !rooms.isEmpty();
                    if (loaded) {
                        System.out.println("Loaded hotel data from CSV: " + path.toAbsolutePath());
                    }
                    return loaded;
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to load hotel data from CSV: " + e.getMessage());
        }
        return false;
    }

    private static String determineConfigPath(String[] args) {
        if (args != null && args.length > 0 && args[0] != null && !args[0].isBlank()) {
            return args[0];
        }
        String env = System.getenv("HOTEL_CONFIG");
        if (env != null && !env.isBlank()) {
            return env;
        }
        return "hotel-state.csv";
    }
}
