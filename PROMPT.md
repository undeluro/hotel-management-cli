# prompt

Project brief — Hotel management console application (Java)

Purpose:

Implement a text-based hotel-management application in Java to teach object-orientation, inheritance, interfaces, generics and a custom Map implementation. Provide automated unit tests that cover business logic and the custom map. Allow future extension (adding commands) without changing existing code (use Command base class / Strategy pattern and a Command registry / factory).

High-level requirements:

1. Application name and modules:
    - Root project with two Maven modules:
        - hotel-main: main application, CLI and domain model.
        - hotel-utils: utility module with custom Map interface and MyMap implementation plus any shared helpers.
    - hotel-main must depend on hotel-utils.
2. Package / class organization (suggested):
    - hotel-main:
        - package hotel.model:
            - Hotel (represents the whole hotel, floors, rooms)
            - Room (room number, description, pricePerNight, capacity, occupancy status, current Guest(s), checkinDate, plannedCheckoutDate, additionalInfo)
            - Guest (name, optional contact details)
        - package hotel.commands:
            - abstract class Command { void setHotel(Hotel); abstract void execute(Scanner scanner); } // or similar
            - class ViewCommand extends Command
            - class ListCommand extends Command
            - class CheckinCommand extends Command
            - class CheckoutCommand extends Command
            - class SaveCommand extends Command
            - class ExitCommand extends Command
            - other commands as needed
        - package hotel.factory:
            - class CommandRegistry { registerCommand(String name, Class<? extends Command> cls); Command createCommand(String name); }
        - package hotel.io:
            - CsvLoader (optional, used for bonus 1)
            - CsvSaver (optional, used for bonus 2)
        - main entry point: public class Main (contains main method, builds Hotel, CommandRegistry, main loop)
    - hotel-utils:
        - package hotel.utils.map:
            - public interface Map<K, V> { boolean put(K key, V value); boolean remove(K key); V get(K key); List keys(); boolean contains(K key); }
            - public class MyMap<K, V> implements Map<K, V> { /* implemented using two lists: keys List and values List */ }
        - other utility classes as needed.
3. CLI commands (case-insensitive). For each command define expected interactions and error handling:
    - prices
        - Lists all rooms with their price per night. Display format: roomNumber - description - price - capacity.
    - view
        - Prompt: “Enter room number:”
        - If room number invalid => print error: “Invalid room number: ”
        - Otherwise print all information about the room: roomNumber, description, price, capacity, occupied? If occupied: print guest(s) data, checkinDate, plannedCheckoutDate, additional info if present.
    - checkin
        - Prompt: “Enter room number:”
        - If invalid room => error “Invalid room number”.
        - If room already occupied => error “Room is already occupied.”
        - Otherwise prompt for guest data:
            - At minimum ask for main guest name.
            - If room capacity > 1 allow entering additional guest names up to capacity.
            - Prompt for check-in date (optional). If user provides empty input treat it as current date (system local date).
                - Use ISO format yyyy-MM-dd for input and for storing/display.
            - Prompt for planned number of nights or planned checkout date (either is acceptable; pick one and document in prompt).
            - Prompt for optional additional info (free text).
            - After collecting data mark room as occupied and record checkinDate and plannedCheckoutDate (calculated from nights or from provided date).
    - checkout
        - Prompt: “Enter room number:”
        - If invalid room or room not occupied => error “Room is not occupied or invalid.”
        - Otherwise compute number of nights between recorded checkinDate and current date (if checkin and checkout on same day count as 1 night? Document rule — use nights = max(1, ceil(days difference))). Use inclusive/exclusive rule: recommended: nights = max(1, (checkoutDate - checkinDate).days); if checkoutDate is same as checkinDate -> 1 night.
        - Calculate total amount = nights * room.pricePerNight. Print summary: guest(s), checkinDate, checkoutDate, nights, price per night, total.
        - Mark room as free (clear guest data, checkinDate, plannedCheckoutDate).
    - list
        - Lists all rooms with occupancy status.
        - For occupied rooms include guest data, checkinDate and plannedCheckoutDate.
    - save (bonus 2)
        - Save current hotel state to CSV or XLSX with structure: roomNumber, description, price, capacity, guestData(serialized), checkinDate(optional), plannedCheckoutDate(optional), additionalInfo(optional).
        - Confirm success or print error.
    - exit
        - Exit loop and terminate program.
4. Room numbering and configuration:
    - Room numbers are integers where the first digit(s) indicate the floor. Example: 101 is floor 1 room 01. Validate input as integers and as existing in hotel’s configured set.
    - Hotel configuration can be either:
        - Hardcoded in program (no bonus), or
        - Read from a CSV or XLSX file with columns: roomNumber, description, pricePerNight, capacity, optional guestData, optional checkinDate (YYYY-MM-DD). Reading configuration from file -> award Bonus 1 (+0.5 grade).
    - Provide a sample CSV format specification:
        - Header row optional. Example rows:
            - 101,“Single room - sea view”,120.0,1, ,
            - 202,“Double room”,200.0,2,“John Doe;Jane Doe”,“2025-11-01”,“2025-11-05”,“note text”
        - Define guestData format when present: multiple guests separated by semicolon, each guest is name optionally with contact in parentheses or comma-separated values. Document parser’s expected format.
5. MyMap (hotel-utils) specification:
    - Provide interface exactly:
        - public interface Map<K, V> {
            
            boolean put(K key, V value);
            
            boolean remove(K key);
            
            V get(K key);
            
            List keys();
            
            boolean contains(K key);
            
            }
            
    - Implement class MyMap<K, V> using two internal lists:
        - private final List keys = new ArrayList<>();
        - private final List values = new ArrayList<>();
        - Semantics: put(K, V) adds new key/value if key not present, or replaces value if key exists (maintain index correspondence). Return true if put succeeded, false if arguments invalid (null key/value) or operation failed.
        - get(K) returns V or null if not present.
        - remove(K) returns true if removed, false if key not found.
        - keys() returns an unmodifiable List or a copy of keys.
        - contains(K) true if key exists.
    - Use generics correctly and type-safety.
    - Allowed to use java.util.List, java.util.ArrayList, java.util.LinkedList only (no built-in Map).
    - Include JavaDoc comments for each method.
6. Tests:
    - Add JUnit dependency to hotel-utils and hotel-main pom.xml. Use JUnit 5 (jupiter) recommended.
    - Write unit tests before writing full implementations for MyMap methods (test-first approach encouraged).
    - Required test coverage:
        - Unit tests for MyMap: put, get, remove, keys, contains, edge cases (null args, duplicate puts, remove non-existent key, concurrency not required).
        - Unit tests for domain logic in hotel-main: checkin (valid and invalid flows), checkout (calculation of nights and billing), view (invalid room number), list, prices.
        - Tests must assert correctness of date handling, billing calculation, capacity enforcement.
    - Aim for high coverage; Sonar analysis and coverage will be considered for bonus up to +0.5 grade if project is cleaned of blocker/critical/major issues.
7. Build and dependencies:
    - Use Maven. Add JUnit 5 dependency in pom.xml for both modules as needed:
        - junit-jupiter etc.
    - hotel-main pom.xml must include dependency on hotel-utils module.
    - Use standard project layout: src/main/java, src/test/java.
8. Design constraints and extensibility requirements:
    - Commands must extend abstract Command class. Adding a new command should not require modifying existing commands or core engine code beyond registering the new command in CommandRegistry.
    - Use Factory/Registry pattern for creating command instances. Example pattern: CommandRegistry maps lowercased command name -> Command class, createCommand instantiates via reflection and returns Command instance. If command not found return null or throw IllegalArgumentException (main loop should handle missing).
    - Main loop example pseudocode (documented to implement similarly):
        - CommandRegistry registry = new CommandRegistry();
            
            registry.register(“checkin”, CheckinCommand.class);
            
            registry.register(“checkout”, CheckoutCommand.class);
            
            registry.register(“view”, ViewCommand.class);
            
            registry.register(“list”, ListCommand.class);
            
            registry.register(“save”, SaveCommand.class);
            
            registry.register(“exit”, ExitCommand.class);
            
        - while (true) {
            
            print prompt with valid commands;
            
            read user input (Scanner.nextLine());
            
            Command cmd = registry.createCommand(inputLower);
            
            if (cmd == null) print “No such command…”; continue;
            
            cmd.setHotel(hotel); cmd.execute(scanner);
            
            }
            
    - Commands should accept Scanner or an input abstraction to allow unit testing (inject a mock or StringReader for tests).
9. Date and time rules:
    - Use java.time.LocalDate for dates. For current date use LocalDate.now().
    - Input/Output date format: ISO yyyy-MM-dd.
    - Default checkin date if omitted = LocalDate.now().
    - Default planned checkout must be explicitly provided by user or computed from numberOfNights input. If neither provided, plannedCheckoutDate may remain null or set to checkinDate + 1 day - document chosen approach.
10. Save/load CSV specifics (bonus 1 & 2):
    - Read CSV for initial hotel configuration: columns: roomNumber, description, pricePerNight, capacity, guestData(optional), checkinDate(optional), plannedCheckoutDate(optional), additionalInfo(optional).
    - Save current state to CSV using same columns and quoting rules.
    - For XLSX support optional — if implemented, ensure dependency (Apache POI or similar) and document in build. CSV is acceptable and simpler.
    - Bonus 1 (0.5 grade): hotel reads configuration from CSV/XLSX.
    - Bonus 2 (0.5 grade): application saves current state to CSV/XLSX via “save” command.
11. Error handling and logging:
    - Avoid System.out.print for internal logging. Prefer using a logger (SLF4J + Simple or java.util.logging). Sonar will flag System.out usage; cleaning these flags helps bonus.
    - User-facing messages print to console (System.out). Use logger for internal debug info.
12. Acceptance criteria (grading rubric; max grade 4.0):
    - Correctness of program runtime behavior: 3.0
        - CLI commands operate as specified, checkin/checkout flow correct, price calculation correct.
    - Design and modularity: up to 3.5
        - Use of OO principles for commands, clear package/module separation, ability to add new commands without code changes.
    - Unit tests: up to 4.0
        - Tests cover business logic and MyMap implementation thoroughly.
    - SonarQube analysis and test coverage: additional +0.5 if project cleaned of blocker/critical/major issues (and tested).
    - Bonus marks:
        - +0.5 for reading configuration from CSV/XLSX (Bonus 1).
        - +0.5 for saving current state to CSV/XLSX (Bonus 2).
    - Note: The baseline passing grade (3.0) requires correct functioning. Higher marks require tests, good design, and Sonar cleanup.
13. Example interactions (user-visible flows):
    - prices
        - Output:
            - “101 - Single ensuite - price: 120.0 PLN/night - capacity: 1”
            - “102 - Double - price: 200.0 PLN/night - capacity: 2”
    - checkin
        - Prompt: “Enter room number:”
        - User: 102
        - If free: prompt “Main guest name:” -> “Alice Kowalska”
        - If capacity>1 prompt repeatedly for additional guests or accept a single semicolon-separated list.
        - Prompt “Check-in date (YYYY-MM-DD) [default: today]:” -> if user empty use today.
        - Prompt “Planned length of stay (nights) or planned checkout date (YYYY-MM-DD) [optional]:” -> accept nights or date.
        - On success: “Guest(s) registered in room 102. Check-in: 2025-11-17 Planned checkout: 2025-11-20”
    - checkout
        - Prompt: “Enter room number:”
        - If occupied compute nights and price: “Guest Alice Kowalska checked out. Nights: 3 Price per night: 200.0 Total: 600.0”
    - view
        - Prompt: “Enter room number:”
        - Output room details including guest(s) if occupied.
    - list
        - Print table or lines for all rooms with occupancy and guest summary.
14. MyMap test cases (must implement):
    - testPutNewKeyReturnsTrueAndGetReturnsValue
    - testPutExistingKeyReplacesValue
    - testGetNonExistentKeyReturnsNull
    - testKeysReturnsAllKeysInInsertionOrder
    - testRemoveExistingKeyReturnsTrueAndKeyRemoved
    - testRemoveNonExistentKeyReturnsFalse
    - testContainsForExistingAndNonExisting
    - testPutNullKeyOrValueHandledAppropriately (either throw IllegalArgumentException or return false — document chosen behavior in code and tests)
15. Implementation guidance and hints:
    - Keep commands small and single-responsibility: each Command class performs I/O and delegates to Hotel/Room model for state changes.
    - Hotel should manage a collection of Room objects and provide utility methods: findRoom(int roomNumber), listRooms(), listRoomsWithPrices(), checkIn(room, guests, checkinDate, plannedCheckoutDate), checkOut(room, checkoutDate).
    - Use dependency injection where feasible to make tests easier (e.g., inject Clock or a LocalDate supplier to control “today” in tests).
    - For CSV parsing/writing, prefer simple splitting with handling for quoted fields; unit-test parsing and serialization.
    - Keep user prompts and parsing in one layer to separate UI from business logic.
16. Deliverables:
    - Maven multi-module project with hotel-main and hotel-utils.
    - Complete Java source code following the specified package layout.
    - Unit tests covering MyMap and hotel business logic.
    - pom.xml with JUnit dependencies and any library used for CSV/XLSX (if XLSX implemented).
    - Optional sample CSV configuration file(s).
    - README (plain text) describing how to build and run, how to run tests, and how to prepare CSV for bonus.
17. Non-functional requirements:
    - Code must compile with Java 11+ (document chosen Java version in pom).
    - Unit tests must be runnable via mvn test.
    - Provide clear error messages for invalid inputs.
    - Keep CLI UX straightforward and user-friendly.
18. Edge cases and clarifications (decisions to be implemented and documented in code/README):
    - Night calculation rule: nights = max(1, DAYS.between(checkinDate, checkoutDate)). If user checks out on the day after checkin, nights=1.
    - How to treat partial days: treat all stays in whole nights only; do not implement fractional nights.
    - Guest identity format: store as just name string; contact fields optional.
    - Null handling policy for MyMap: specify explicitly in implementation and tests (either disallow null keys/values and throw IllegalArgumentException, or return false on put null).
19. Example command registry pseudocode to implement (documented explicitly so a machine can parse/execute):
- CommandRegistry:
    - registerCommand(name:string, className:string)
    - createCommand(name:string) -> new instance of className using no-arg constructor
- main:
    - registry.registerCommand(“checkin”,“hotel.commands.CheckinCommand”)
    - … register other commands …
    - loop:
        - print “Please enter the command - valid commands: view,list,checkin,checkout,save,exit”
        - read input
        - cmd = registry.createCommand(input.toLowerCase().trim())
        - if cmd==null -> print “No such command” and continue
        - cmd.setHotel(hotel)
        - cmd.execute(scanner)
1. Automated grading checklist (for teacher/auto-grader):
    - Compiles without errors.
    - Tests run and pass.
    - MyMap implementation exists in hotel-utils and passes its tests.
    - CLI commands work manually: prices, view, list, checkin, checkout, exit.
    - Bonus detection: if program accepts –config path/to/config.csv and loads hotel configuration award bonus 0.5.
    - Bonus detection: if save command writes CSV with required fields award bonus 0.5.
    - Sonar: if Sonar reports no blocker/critical/major issues for project award additional 0.5.

End of specification.