# Hotel Management CLI

Hotel management CLI built with Java.

### Demonstrating:
- OOP principles
- Design patterns like:
  - Command Pattern
  - Factory Pattern
  - Strategy Pattern
- Test implementation
- SonarQube analytics possibilities
- Own data structure implementation
- Usage of Apache Commons CSV

[Prompt](PROMPT.md) — All specifications gathered in one place.

### Features:

- **Room Management**: View room details, prices, and occupancy status
- **Guest Operations**: Check-in and check-out with billing calculation
- **Persistent Storage**: Load/save hotel state from/to CSV files
- **Extensible Command System**: Strategy pattern with command registry

## Requirements

- **Java JDK 21+**
- **Maven 3.6+**

## Quick Start

### Build
```bash
mvn clean package
```

### Run
```bash
# Default configuration
java -jar hotel-main/target/hotel-main-1.0-SNAPSHOT-jar-with-dependencies.jar

# With custom CSV file
java -jar hotel-main/target/hotel-main-1.0-SNAPSHOT-jar-with-dependencies.jar data/hotel.csv

# Using environment variable
export HOTEL_CONFIG=path/to/config.csv
java -jar hotel-main/target/hotel-main-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Available Commands

| Command    | Description                         |
|------------|-------------------------------------|
| `view`     | Display room details                |
| `list`     | Show all rooms and occupancy status |
| `prices`   | List room prices                    |
| `checkin`  | Register guests in a room           |
| `checkout` | Check out guests and calculate bill |
| `save`     | Save current state to CSV           |
| `exit`     | Exit application                    |

## Project Structure

```
hotel-project/
├── hotel-main/          # Main application
│   ├── hotel.model      # Domain model (Hotel, Room, Guest)
│   ├── hotel.commands   # CLI command implementations
│   ├── hotel.factory    # Command registry/factory
│   └── hotel.io         # CSV loading/saving
└── hotel-utils/         # Utility module
    └── hotel.utils.map  # Custom Map interface & implementation
```

## CSV Format

```csv
roomNumber,description,pricePerNight,capacity,guestData,checkinDate,plannedCheckoutDate,additionalInfo
101,Single ensuite,120.0,1,,,
202,Double room,200.0,2,John Doe;Jane Doe,2025-11-01,2025-11-05,Anniversary stay
```
