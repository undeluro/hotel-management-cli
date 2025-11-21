---WYMAGANIA---
- Java JDK 21+
- Maven 3.6+

---URUCHOMIENIE---
1. mvn clean package
2. Korzystając z pokojów domyślnych:
    java -jar hotel-main/target/hotel-main-1.0-SNAPSHOT-jar-with-dependencies.jar
   Z własnym plikiem CSV:
    java -jar hotel-main/target/hotel-main-1.0-SNAPSHOT-jar-with-dependencies.jar data/hotel.csv
   Ze zmienną środowiskową:
    export HOTEL_CONFIG=<ścieżka/do/pliku>.csv && java -jar hotel-main/target/hotel-main-1.0-SNAPSHOT-jar-with-dependencies.jar

---KOMENDY---
Po uruchomieniu aplikacji dostępne są następujące komendy:
- view      - Wyświetl stan pokoi
- list      - Lista zajętych pokoi
- prices    - Ceny pokoi
- checkin   - Zamelduj gości
- checkout  - Wymelduj gości
- save      - Zapisz stan hotelu do CSV
- exit      - Wyjście z programu