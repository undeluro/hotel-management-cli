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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SaveCommandTest {
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
    void savesToProvidedPathAndPrintsMessage() throws Exception {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(101, "Single", 100.0, 1));

        Path tmp = Files.createTempFile("save-cmd-", ".csv");
        // We'll pass the absolute path so the command writes there
        String input = tmp.toAbsolutePath().toString() + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        SaveCommand cmd = new SaveCommand();
        cmd.setHotel(hotel);
        cmd.execute(scanner);

        String printed = out.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("State saved to:"));
        assertTrue(Files.size(tmp) > 0);
        // basic header presence
        String content = Files.readString(tmp, StandardCharsets.UTF_8);
        assertTrue(content.startsWith("roomNumber,description,pricePerNight,capacity"));
    }
}
