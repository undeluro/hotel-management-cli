package hotel.commands;

import hotel.model.Hotel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Performs checkout and prints billing summary.
 */
public class CheckoutCommand extends Command {
    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter room number:");
        String input = scanner.nextLine();
        int roomNo;
        try {
            roomNo = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            System.out.println("Room is not occupied or invalid.");
            return;
        }
        System.out.println("Checkout date (YYYY-MM-DD) [default: today]:");
        String dateStr = scanner.nextLine().trim();
        LocalDate checkout = null;
        if (!dateStr.isEmpty()) {
            try {
                checkout = LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
                return;
            }
        }
        var result = hotel.checkOut(roomNo, checkout);
        if (result.isEmpty()) {
            System.out.println("Room is not occupied or invalid.");
            return;
        }
        var s = result.get();
        System.out.println("Guest(s) checked out from room " + s.roomNumber + ". Nights: " + s.nights +
                " Price per night: " + s.pricePerNight + " Total: " + s.total);
    }
}
