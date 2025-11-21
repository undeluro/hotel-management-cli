package hotel.commands;

import hotel.model.Guest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import hotel.model.Room;

/**
 * Handles check-in flow with prompts as specified.
 */
public class CheckinCommand extends Command {
    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter room number:");
        String input = scanner.nextLine();
        int roomNo;
        try {
            roomNo = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number");
            return;
        }
        var room = hotel.findRoom(roomNo);
        if (room == null) {
            System.out.println("Invalid room number");
            return;
        }
        if (room.isOccupied()) {
            System.out.println("Room is already occupied.");
            return;
        }
        System.out.println("Main guest name:");
        String mainName = scanner.nextLine().trim();
        if (mainName.isEmpty()) {
            System.out.println("Main guest name is required.");
            return;
        }
        List<Guest> guests = new ArrayList<>();
        guests.add(new Guest(mainName));
        if (!addAdditionalGuests(scanner, room, guests)) {
            return;
        }
        System.out.println("Check-in date (YYYY-MM-DD) [default: today]:");
        String ciStr = scanner.nextLine().trim();
        LocalDate checkinDate = parseCheckinDate(ciStr);
        if (checkinDate == null && !ciStr.isEmpty()) {
            return;
        }
        System.out.println("Planned length of stay (nights) [optional]:");
        String nightsStr = scanner.nextLine().trim();
        LocalDate plannedCheckout = calculatePlannedCheckout(nightsStr, checkinDate);
        if (plannedCheckout == null && !nightsStr.isEmpty()) {
            return;
        }
        System.out.println("Additional info [optional]:");
        String info = scanner.nextLine();
        boolean ok = hotel.checkIn(roomNo, guests, checkinDate, plannedCheckout, info);
        if (ok) {
            System.out.println("Guest(s) registered in room " + roomNo + ". Check-in: " + (checkinDate == null ? hotel.today() : checkinDate)
                    + (plannedCheckout != null ? " Planned checkout: " + plannedCheckout : ""));
        } else {
            System.out.println("Could not check in.");
        }
    }

    private boolean addAdditionalGuests(Scanner scanner, Room room, List<Guest> guests) {
        if (room.getCapacity() > 1) {
            System.out.println("Additional guests (semicolon-separated) [optional]:");
            String extra = scanner.nextLine().trim();
            if (!extra.isEmpty()) {
                String[] parts = extra.split(";");
                for (String p : parts) {
                    String n = p.trim();
                    if (!n.isEmpty()) guests.add(new Guest(n));
                }
            }
            if (guests.size() > room.getCapacity()) {
                System.out.println("Too many guests for room capacity.");
                return false;
            }
        }
        return true;
    }

    private LocalDate parseCheckinDate(String ciStr) {
        if (!ciStr.isEmpty()) {
            try {
                return LocalDate.parse(ciStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
                return null;
            }
        }
        return null;
    }

    private LocalDate calculatePlannedCheckout(String nightsStr, LocalDate checkinDate) {
        if (!nightsStr.isEmpty()) {
            try {
                int nights = Integer.parseInt(nightsStr);
                if (nights < 1) nights = 1;
                LocalDate base = checkinDate != null ? checkinDate : hotel.today();
                return base.plusDays(nights);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of nights.");
                return null;
            }
        }
        return null;
    }
}
