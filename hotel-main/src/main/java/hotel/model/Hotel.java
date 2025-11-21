package hotel.model;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents a Hotel consisting of rooms and provides operations for check-in and check-out.
 */
public class Hotel {
    private final List<Room> rooms = new ArrayList<>();
    private Supplier<LocalDate> todaySupplier = () -> LocalDate.now(Clock.systemDefaultZone());

    public Hotel() {}

    public Hotel(List<Room> initialRooms) {
        if (initialRooms != null) {
            rooms.addAll(initialRooms);
        }
    }

    public void setTodaySupplier(Supplier<LocalDate> supplier) {
        if (supplier != null) this.todaySupplier = supplier;
    }

    public LocalDate today() { return todaySupplier.get(); }

    public void addRoom(Room room) { if (room != null) rooms.add(room); }

    public List<Room> getRooms() { return Collections.unmodifiableList(rooms); }

    public Room findRoom(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) return r;
        }
        return null;
    }

    public String listRoomsWithPrices() {
        StringBuilder sb = new StringBuilder();
        for (Room r : rooms) {
            sb.append(r.getRoomNumber())
              .append(" - ")
              .append(r.getDescription())
              .append(" - price: ")
              .append(r.getPricePerNight())
              .append(" PLN/night - capacity: ")
              .append(r.getCapacity())
              .append(System.lineSeparator());
        }
        return sb.toString().trim();
    }

    public String listRooms() {
        StringBuilder sb = new StringBuilder();
        for (Room r : rooms) {
            sb.append(r.toString());
            if (r.isOccupied()) {
                sb.append(" - guests: ");
                List<String> names = new ArrayList<>();
                for (Guest g : r.getGuests()) names.add(g.getName());
                sb.append(String.join(", ", names));
                sb.append(" - checkin: ").append(r.getCheckinDate());
                if (r.getPlannedCheckoutDate() != null) {
                    sb.append(" - plannedCheckout: ").append(r.getPlannedCheckoutDate());
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString().trim();
    }

    public boolean checkIn(int roomNumber, List<Guest> guests, LocalDate checkinDate, LocalDate plannedCheckoutDate, String additionalInfo) {
        Room room = findRoom(roomNumber);
        if (room == null) return false;
        if (room.isOccupied()) return false;
        if (guests == null || guests.isEmpty()) return false;
        if (room.getCapacity() < guests.size()) return false;
        LocalDate ci = (checkinDate == null) ? today() : checkinDate;
        room.occupy(guests, ci, plannedCheckoutDate, additionalInfo);
        return true;
    }

    /**
     * Check out a room and compute billing summary.
     * Nights rule: nights = max(1, DAYS.between(checkinDate, checkoutDate)).
     */
    public Optional<CheckoutSummary> checkOut(int roomNumber, LocalDate checkoutDate) {
        Room room = findRoom(roomNumber);
        if (room == null || !room.isOccupied() || room.getCheckinDate() == null) return Optional.empty();
        LocalDate co = (checkoutDate == null) ? today() : checkoutDate;
        long days = ChronoUnit.DAYS.between(room.getCheckinDate(), co);
        long nights = Math.max(1, days);
        double total = nights * room.getPricePerNight();
        CheckoutSummary summary = new CheckoutSummary(new ArrayList<>(room.getGuests()), room.getCheckinDate(), co, nights, room.getPricePerNight(), total, room.getRoomNumber());
        room.freeUp();
        return Optional.of(summary);
    }

    public static class CheckoutSummary {
        public final List<Guest> guests;
        public final LocalDate checkinDate;
        public final LocalDate checkoutDate;
        public final long nights;
        public final double pricePerNight;
        public final double total;
        public final int roomNumber;

        public CheckoutSummary(List<Guest> guests, LocalDate checkinDate, LocalDate checkoutDate, long nights, double pricePerNight, double total, int roomNumber) {
            this.guests = guests;
            this.checkinDate = checkinDate;
            this.checkoutDate = checkoutDate;
            this.nights = nights;
            this.pricePerNight = pricePerNight;
            this.total = total;
            this.roomNumber = roomNumber;
        }
    }
}
