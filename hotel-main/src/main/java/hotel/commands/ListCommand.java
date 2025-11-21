package hotel.commands;

import java.util.Scanner;

/**
 * Lists all rooms with occupancy status.
 */
public class ListCommand extends Command {
    @Override
    public void execute(Scanner scanner) {
        if (hotel == null) {
            System.out.println("Hotel not initialized");
            return;
        }
        System.out.println(hotel.listRooms());
    }
}
