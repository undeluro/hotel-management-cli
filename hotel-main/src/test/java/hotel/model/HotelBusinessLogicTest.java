package hotel.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelBusinessLogicTest {

    private Hotel hotel;

    @BeforeEach
    void setup() {
        hotel = new Hotel();
        hotel.addRoom(new Room(101, "Single ensuite", 120.0, 1));
        hotel.addRoom(new Room(102, "Double", 200.0, 2));
        hotel.setTodaySupplier(() -> LocalDate.of(2025, 11, 2));
    }

    @Test
    void pricesListFormat() {
        String prices = hotel.listRoomsWithPrices();
        assertTrue(prices.contains("101 - Single ensuite - price: 120.0 PLN/night - capacity: 1"));
        assertTrue(prices.contains("102 - Double - price: 200.0 PLN/night - capacity: 2"));
    }

    @Test
    void viewAndListWhenFreeThenOccupied() {
        String list1 = hotel.listRooms();
        assertTrue(list1.contains("101 - Single ensuite - price: 120.0 PLN/night - capacity: 1 - FREE"));

        boolean ok = hotel.checkIn(102, List.of(new Guest("Alice"), new Guest("Bob")), null, LocalDate.of(2025, 11, 5), "Sea view requested");
        assertTrue(ok);
        String list2 = hotel.listRooms();
        assertTrue(list2.contains("102 - Double - price: 200.0 PLN/night - capacity: 2 - OCCUPIED"));
        assertTrue(list2.contains("guests: Alice, Bob"));
        assertTrue(list2.contains("plannedCheckout: 2025-11-05"));
    }

    @Test
    void checkinValidation() {
        // invalid room
        assertFalse(hotel.checkIn(999, List.of(new Guest("X")), null, null, null));
        // capacity enforcement
        assertFalse(hotel.checkIn(101, List.of(new Guest("A"), new Guest("B")), null, null, null));
        // valid single
        assertTrue(hotel.checkIn(101, List.of(new Guest("Solo")), null, null, "note"));
        // already occupied
        assertFalse(hotel.checkIn(101, List.of(new Guest("Other")), null, null, null));
    }

    @Test
    void checkoutCalculatesNightsAndBilling() {
        assertTrue(hotel.checkIn(102, List.of(new Guest("Alice")), LocalDate.of(2025, 11, 2), null, null));
        var summaryOpt = hotel.checkOut(102, LocalDate.of(2025, 11, 2));
        assertTrue(summaryOpt.isPresent());
        var s = summaryOpt.get();
        assertEquals(1, s.nights); // same day -> 1 night
        assertEquals(200.0, s.pricePerNight);
        assertEquals(200.0, s.total);
        // room should be free now
        assertFalse(hotel.findRoom(102).isOccupied());
    }

    @Test
    void checkoutInvalidWhenRoomNotOccupied() {
        assertTrue(hotel.getRooms().stream().noneMatch(Room::isOccupied));
        assertTrue(hotel.checkOut(101, LocalDate.of(2025, 11, 3)).isEmpty());
    }
}
