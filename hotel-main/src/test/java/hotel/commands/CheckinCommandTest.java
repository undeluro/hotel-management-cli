package hotel.commands;

import hotel.model.Guest;
import hotel.model.Hotel;
import hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CheckinCommandTest {
    private final PrintStream origOut = System.out;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() {
        System.setOut(origOut);
    }

    @Test
    void happyPath_withAdditionalGuests_andPlannedStay() {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(102, "Double", 200.0, 2));
        hotel.setTodaySupplier(() -> LocalDate.of(2025, 11, 2));

        String input = String.join("\n",
                "102",            // room number
                "Alice",          // main guest
                "Bob",            // additional guests
                "2025-11-02",     // check-in date
                "2",              // nights
                "Near elevator"   // info
        ) + "\n"; // final newline
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        CheckinCommand cmd = new CheckinCommand();
        cmd.setHotel(hotel);
        cmd.execute(scanner);

        String printed = out.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("Guest(s) registered in room 102"));
        assertNotNull(hotel.findRoom(102));
        assertTrue(hotel.findRoom(102).isOccupied());
        assertEquals(2, hotel.findRoom(102).getGuests().size());
        assertEquals(LocalDate.of(2025, 11, 4), hotel.findRoom(102).getPlannedCheckoutDate());
    }

    @Test
    void validation_invalidRoomNumberFormat() {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(101, "Single", 100.0, 1));
        String input = String.join("\n",
                "not-a-number"
        ) + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        CheckinCommand cmd = new CheckinCommand();
        cmd.setHotel(hotel);
        cmd.execute(scanner);
        String printed = out.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("Invalid room number"));
    }

    @Test
    void validation_tooManyGuests() {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(201, "Double", 150.0, 2));

        String input = String.join("\n",
                "201",        // room number
                "Alice",      // main
                "Bob;Carol",  // two extra -> total 3 > capacity 2
                "",           // checkin today
                "",           // nights
                ""            // info
        ) + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        CheckinCommand cmd = new CheckinCommand();
        cmd.setHotel(hotel);
        cmd.execute(scanner);
        String printed = out.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("Too many guests for room capacity."));
        assertFalse(hotel.findRoom(201).isOccupied());
    }
}
