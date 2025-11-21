package hotel.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomAndGuestTest {

    @Test
    void room_toStringReflectsOccupiedState_and_freeUpResets() {
        Room r = new Room(301, "Junior Suite", 350.0, 2);
        assertTrue(r.toString().contains("FREE"));

        r.occupy(List.of(new Guest("Alice")), LocalDate.of(2025, 11, 2), LocalDate.of(2025, 11, 5), "Note");
        assertTrue(r.toString().contains("OCCUPIED"));
        assertTrue(r.isOccupied());
        assertEquals(1, r.getGuests().size());

        r.freeUp();
        assertFalse(r.isOccupied());
        assertEquals(0, r.getGuests().size());
        assertNull(r.getCheckinDate());
        assertNull(r.getPlannedCheckoutDate());
        assertNull(r.getAdditionalInfo());
    }

    @Test
    void guest_constructorRejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Guest(" "));
        assertThrows(IllegalArgumentException.class, () -> new Guest(null));
    }
}
