package hotel.io;

import hotel.model.Guest;
import hotel.model.Hotel;
import hotel.model.Room;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV I/O utilities for loading initial room configuration and saving current hotel state.
 * Uses Apache Commons CSV.
 */
public final class CsvIO {
    private CsvIO() {}

    // Column name constants (avoid duplicating string literals)
    private static final String COL_ROOM_NUMBER = "roomNumber";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_PRICE_PER_NIGHT = "pricePerNight";
    private static final String COL_CAPACITY = "capacity";
    private static final String COL_OCCUPIED = "occupied";
    private static final String COL_GUESTS = "guests";
    private static final String COL_CHECKIN_DATE = "checkinDate";
    private static final String COL_PLANNED_CHECKOUT_DATE = "plannedCheckoutDate";
    private static final String COL_ADDITIONAL_INFO = "additionalInfo";

    // Header for loading initial room configuration
    private static final String[] CONFIG_HEADERS = new String[]{
            COL_ROOM_NUMBER, COL_DESCRIPTION, COL_PRICE_PER_NIGHT, COL_CAPACITY
    };

    // Header for saving current state (includes occupancy/guests and dates)
    private static final String[] STATE_HEADERS = new String[]{
            COL_ROOM_NUMBER, COL_DESCRIPTION, COL_PRICE_PER_NIGHT, COL_CAPACITY,
            COL_OCCUPIED, COL_GUESTS, COL_CHECKIN_DATE, COL_PLANNED_CHECKOUT_DATE, COL_ADDITIONAL_INFO
    };

    /**
     * Load either simple room configuration or full saved state depending on the CSV header.
     * If the header contains the "occupied" column, full state is loaded; otherwise only room config.
     */
    public static List<Room> loadRoomsOrState(Path path) throws IOException {
        if (path == null) throw new IllegalArgumentException("path is null");
        String firstLine;
        try (var lines = Files.lines(path, StandardCharsets.UTF_8)) {
            firstLine = lines.findFirst().orElse("");
        }
        boolean looksLikeState = firstLine.toLowerCase().contains(COL_OCCUPIED)
                || firstLine.toLowerCase().contains(COL_GUESTS);
        return looksLikeState ? loadState(path) : loadRooms(path);
    }

    public static List<Room> loadRooms(Path path) throws IOException {
        if (path == null) throw new IllegalArgumentException("path is null");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader(CONFIG_HEADERS)
                     .setSkipHeaderRecord(true)
                     .setIgnoreSurroundingSpaces(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {
            List<Room> rooms = new ArrayList<>();
            for (CSVRecord rec : parser) {
                if (rec.size() == 0) continue;
                String rnStr = rec.get(COL_ROOM_NUMBER);
                if (rnStr == null || rnStr.isBlank()) continue;
                int rn = Integer.parseInt(rnStr.trim());
                String desc = rec.get(COL_DESCRIPTION);
                String priceStr = rec.get(COL_PRICE_PER_NIGHT);
                double price = Double.parseDouble(priceStr);
                String capStr = rec.get(COL_CAPACITY);
                int cap = Integer.parseInt(capStr);
                rooms.add(new Room(rn, desc == null ? "" : desc, price, cap));
            }
            return rooms;
        }
    }

    /**
     * Load full hotel state (rooms plus occupancy) from a CSV saved by {@link #saveState(Path, Hotel)}.
     */
    public static List<Room> loadState(Path path) throws IOException {
        if (path == null) throw new IllegalArgumentException("path is null");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader(STATE_HEADERS)
                     .setSkipHeaderRecord(true)
                     .setIgnoreSurroundingSpaces(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {
            List<Room> rooms = new ArrayList<>();
            for (CSVRecord rec : parser) {
                Room r = parseBasicRoom(rec);
                if (r == null) continue;
                populateOccupancyIfAny(r, rec);
                rooms.add(r);
            }
            return rooms;
        }
    }

    public static void saveState(Path path, Hotel hotel) throws IOException {
        if (path == null) throw new IllegalArgumentException("path is null");
        if (hotel == null) throw new IllegalArgumentException("hotel is null");
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader(STATE_HEADERS)
                     .build())) {
            for (Room r : hotel.getRooms()) {
                String guests = String.join(";", r.getGuests().stream().map(Guest::getName).toList());
                String checkin = r.getCheckinDate() == null ? "" : r.getCheckinDate().toString();
                String planned = r.getPlannedCheckoutDate() == null ? "" : r.getPlannedCheckoutDate().toString();
                String info = r.getAdditionalInfo() == null ? "" : r.getAdditionalInfo();
                printer.printRecord(
                        r.getRoomNumber(),
                        r.getDescription(),
                        r.getPricePerNight(),
                        r.getCapacity(),
                        r.isOccupied(),
                        guests,
                        checkin,
                        planned,
                        info
                );
            }
        }
    }

    private static String getSafe(CSVRecord rec, String name) {
        try { return rec.get(name); } catch (IllegalArgumentException ex) { return null; }
    }

    private static LocalDate parseDate(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        try { return LocalDate.parse(s); } catch (Exception e) { return null; }
    }

    // --- Helpers used to reduce cognitive complexity in loadState ---
    private static Room parseBasicRoom(CSVRecord rec) {
        if (rec == null || rec.size() == 0) return null;
        String rnStr = rec.get(COL_ROOM_NUMBER);
        if (rnStr == null || rnStr.isBlank()) return null;
        int rn = Integer.parseInt(rnStr.trim());
        String desc = rec.get(COL_DESCRIPTION);
        String priceStr = rec.get(COL_PRICE_PER_NIGHT);
        double price = Double.parseDouble(priceStr);
        String capStr = rec.get(COL_CAPACITY);
        int cap = Integer.parseInt(capStr);
        return new Room(rn, desc == null ? "" : desc, price, cap);
    }

    private static List<Guest> parseGuests(String guestsStr) {
        List<Guest> guests = new ArrayList<>();
        if (guestsStr == null || guestsStr.isBlank()) return guests;
        String[] parts = guestsStr.split(";");
        for (String p : parts) {
            String n = p.trim();
            if (n.isEmpty()) continue;
            try {
                guests.add(new Guest(n));
            } catch (IllegalArgumentException ignored) {
                // skip bad names
            }
        }
        return guests;
    }

    private static void populateOccupancyIfAny(Room r, CSVRecord rec) {
        String occupiedStr = getSafe(rec, COL_OCCUPIED);
        boolean occ = occupiedStr != null && Boolean.parseBoolean(occupiedStr.trim());
        if (!occ) return;

        List<Guest> guests = parseGuests(getSafe(rec, COL_GUESTS));
        LocalDate checkin = parseDate(getSafe(rec, COL_CHECKIN_DATE));
        LocalDate planned = parseDate(getSafe(rec, COL_PLANNED_CHECKOUT_DATE));
        String info = getSafe(rec, COL_ADDITIONAL_INFO);
        r.occupy(guests, checkin, planned, (info == null || info.isBlank()) ? null : info);
    }
}
