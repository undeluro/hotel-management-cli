package hotel.model;

/**
 * Represents a guest with minimal information: just the name for simplicity.
 */
public class Guest {
    private final String name;

    public Guest(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Guest name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
