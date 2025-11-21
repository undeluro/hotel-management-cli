package hotel.commands;

import java.util.Scanner;

/**
 * Prints all rooms with their price per night and capacity.
 */
public class PricesCommand extends Command {
    @Override
    public void execute(Scanner scanner) {
        if (hotel == null) {
            System.out.println("Hotel not initialized");
            return;
        }
        System.out.println(hotel.listRoomsWithPrices());
    }
}
