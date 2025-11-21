package hotel.io;

import hotel.model.Guest;
import hotel.model.Hotel;
import hotel.model.Room;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvIOTest {

    @Test
    void loadRooms_readsTypicalConfig() throws IOException {
        Path tmp = Files.createTempFile("rooms-config-", ".csv");
        String csv = String.join("\n",
                "roomNumber,description,pricePerNight,capacity",
                "101,Single,120.5,1",
                "202,Double,200.0,2");
        Files.writeString(tmp, csv, StandardCharsets.UTF_8);

        List<Room> rooms = CsvIO.loadRooms(tmp);
        assertEquals(2, rooms.size());
        Room r1 = rooms.get(0);
        assertEquals(101, r1.getRoomNumber());
        assertEquals("Single", r1.getDescription());
        assertEquals(120.5, r1.getPricePerNight());
        assertEquals(1, r1.getCapacity());
        assertFalse(r1.isOccupied());

        Room r2 = rooms.get(1);
        assertEquals(202, r2.getRoomNumber());
        assertEquals(2, r2.getCapacity());
    }

    @Test
    void saveState_and_loadRoomsOrState_roundTrip() throws IOException {
        // Build a small hotel with one free and one occupied room
        Hotel hotel = new Hotel();
        Room free = new Room(101, "Single ensuite", 99.0, 1);
        Room occ = new Room(202, "Double", 150.0, 2);
        occ.occupy(List.of(new Guest("Alice"), new Guest("Bob")),
                LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 4), "Near elevator");
        hotel.addRoom(free);
        hotel.addRoom(occ);

        Path tmp = Files.createTempFile("hotel-state-", ".csv");
        CsvIO.saveState(tmp, hotel);

        // Basic header presence
        String content = Files.readString(tmp, StandardCharsets.UTF_8);
        assertTrue(content.startsWith("roomNumber,description,pricePerNight,capacity,occupied,guests,checkinDate,plannedCheckoutDate,additionalInfo"));

        // Load back using auto-detect
        List<Room> loaded = CsvIO.loadRoomsOrState(tmp);
        assertEquals(2, loaded.size());

        Room l1 = loaded.stream().filter(r -> r.getRoomNumber() == 101).findFirst().orElseThrow();
        assertFalse(l1.isOccupied());
        assertEquals("Single ensuite", l1.getDescription());

        Room l2 = loaded.stream().filter(r -> r.getRoomNumber() == 202).findFirst().orElseThrow();
        assertTrue(l2.isOccupied());
        assertEquals(2, l2.getGuests().size());
        assertEquals("Alice", l2.getGuests().get(0).getName());
        assertEquals(LocalDate.of(2025, 11, 1), l2.getCheckinDate());
        assertEquals(LocalDate.of(2025, 11, 4), l2.getPlannedCheckoutDate());
    }
}
