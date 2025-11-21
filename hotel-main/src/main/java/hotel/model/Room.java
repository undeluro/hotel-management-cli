package hotel.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a hotel room and its current state.
 */
public class Room {
    private final int roomNumber;
    private String description;
    private double pricePerNight;
    private int capacity;

    private boolean occupied;
    private final List<Guest> guests = new ArrayList<>();
    private LocalDate checkinDate;
    private LocalDate plannedCheckoutDate;
    private String additionalInfo;

    public Room(int roomNumber, String description, double pricePerNight, int capacity) {
        this.roomNumber = roomNumber;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isOccupied() { return occupied; }

    public List<Guest> getGuests() { return Collections.unmodifiableList(guests); }

    public LocalDate getCheckinDate() { return checkinDate; }

    public LocalDate getPlannedCheckoutDate() { return plannedCheckoutDate; }

    public String getAdditionalInfo() { return additionalInfo; }

    public void occupy(List<Guest> guests, LocalDate checkinDate, LocalDate plannedCheckoutDate, String additionalInfo) {
        this.occupied = true;
        this.guests.clear();
        if (guests != null) this.guests.addAll(guests);
        this.checkinDate = checkinDate;
        this.plannedCheckoutDate = plannedCheckoutDate;
        this.additionalInfo = additionalInfo;
    }

    public void freeUp() {
        this.occupied = false;
        this.guests.clear();
        this.checkinDate = null;
        this.plannedCheckoutDate = null;
        this.additionalInfo = null;
    }

    @Override
    public String toString() {
        return roomNumber + " - " + description + " - price: " + pricePerNight + " PLN/night - capacity: " + capacity +
                (occupied ? " - OCCUPIED" : " - FREE");
    }
}
