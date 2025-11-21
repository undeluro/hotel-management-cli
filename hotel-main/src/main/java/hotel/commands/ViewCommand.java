package hotel.commands;

import hotel.model.Guest;
import hotel.model.Room;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * View detailed information about a single room.
 */
public class ViewCommand extends Command {
    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter room number:");
        String input = scanner.nextLine();
        int roomNo;
        try {
            roomNo = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number: " + input);
            return;
        }
        Room room = hotel != null ? hotel.findRoom(roomNo) : null;
        if (room == null) {
            System.out.println("Invalid room number: " + roomNo);
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        System.out.println("Room: " + room.getRoomNumber());
        System.out.println("Description: " + room.getDescription());
        System.out.println("Price per night: " + room.getPricePerNight());
        System.out.println("Capacity: " + room.getCapacity());
        System.out.println("Occupied: " + (room.isOccupied() ? "Yes" : "No"));
        if (room.isOccupied()) {
            List<String> names = new ArrayList<>();
            for (Guest g : room.getGuests()) names.add(g.getName());
            System.out.println("Guests: " + String.join(", ", names));
            if (room.getCheckinDate() != null) System.out.println("Check-in: " + fmt.format(room.getCheckinDate()));
            if (room.getPlannedCheckoutDate() != null) System.out.println("Planned checkout: " + fmt.format(room.getPlannedCheckoutDate()));
            if (room.getAdditionalInfo() != null && !room.getAdditionalInfo().isBlank()) System.out.println("Additional info: " + room.getAdditionalInfo());
        }
    }
}
