package hotel.commands;

import hotel.model.Hotel;

import java.util.Scanner;

/**
 * Base class for all CLI commands. Concrete commands should implement execute.
 */
public abstract class Command {
    protected Hotel hotel;

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public abstract void execute(Scanner scanner);
}
