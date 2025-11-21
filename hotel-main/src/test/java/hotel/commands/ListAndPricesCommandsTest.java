package hotel.commands;

import hotel.model.Hotel;
import hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ListAndPricesCommandsTest {
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
    void listCommand_printsRoomsWithStatus() {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(101, "Single", 100.0, 1));
        hotel.addRoom(new Room(102, "Double", 150.0, 2));

        ListCommand cmd = new ListCommand();
        cmd.setHotel(hotel);
        cmd.execute(new Scanner(new ByteArrayInputStream(new byte[0])));

        String printed = out.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("101 - Single - price: 100.0 PLN/night - capacity: 1 - FREE"));
        assertTrue(printed.contains("102 - Double - price: 150.0 PLN/night - capacity: 2 - FREE"));
    }

    @Test
    void pricesCommand_printsRoomsWithPrices() {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(201, "Triple", 220.0, 3));

        PricesCommand cmd = new PricesCommand();
        cmd.setHotel(hotel);
        cmd.execute(new Scanner(new ByteArrayInputStream(new byte[0])));

        String printed = out.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("201 - Triple - price: 220.0 PLN/night - capacity: 3"));
    }
}
