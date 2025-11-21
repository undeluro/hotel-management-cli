package hotel.factory;

import hotel.commands.Command;
import hotel.commands.ListCommand;
import hotel.commands.PricesCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {

    @Test
    void createsRegisteredCommandsCaseInsensitively() {
        CommandRegistry reg = new CommandRegistry();
        reg.registerCommand("list", ListCommand.class);

        Command c1 = reg.createCommand("list");
        Command c2 = reg.createCommand("LiSt");

        assertNotNull(c1);
        assertNotNull(c2);
        assertEquals(ListCommand.class, c1.getClass());
        assertEquals(ListCommand.class, c2.getClass());
    }

    @Test
    void returnsNullForUnknown() {
        CommandRegistry reg = new CommandRegistry();
        assertNull(reg.createCommand("unknown"));
    }

    @Test
    void listNamesPreservesInsertionOrder() {
        CommandRegistry reg = new CommandRegistry();
        reg.registerCommand("list", ListCommand.class);
        reg.registerCommand("prices", PricesCommand.class);
        assertEquals("list,prices", reg.listNames());
    }
}
