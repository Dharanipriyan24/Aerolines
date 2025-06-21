// File: Aerolines.java
package aeroplane;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class Flight {
    public String flightNumber;
    public String airline;
    public String destination;
    public String time;
    public String status;
    public String gate;
    public boolean isArrival;
    public String date;
    public Map<String, Integer> seatAvailability = new HashMap<>();

    public Flight(String flightNumber, String airline, String destination, String time, String gate, String status, boolean isArrival, String date) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.destination = destination;
        this.time = time;
        this.gate = gate;
        this.status = status;
        this.isArrival = isArrival;
        this.date = date;

        seatAvailability.put("Economy", 50 + new Random().nextInt(51));       // 50–100
        seatAvailability.put("Business", 10 + new Random().nextInt(11));      // 10–20
        seatAvailability.put("FirstClass", 5 + new Random().nextInt(6));      // 5–10
    }

    }


class FlightManager {
    private final List<Flight> flights = new ArrayList<>();
    private final String[] airlines = {"Air India", "IndiGo", "SpiceJet", "Emirates", "Lufthansa"};
    private final String[] destinations = {"Delhi", "Mumbai", "Dubai", "Frankfurt", "London"};
    private final String[] gates = {"A1", "A2", "A3", "B1", "B2"};
    private final Random rand = new Random();

    public List<Flight> getFlights() {
        return flights;
    }

    public void generateFlight() {
        String flightNumber = "FL" + (100 + rand.nextInt(900));
        String airline = airlines[rand.nextInt(airlines.length)];
        String destination = destinations[rand.nextInt(destinations.length)];
        String time = String.format("%02d:%02d", 8 + rand.nextInt(12), rand.nextInt(60));
        String gate = gates[rand.nextInt(gates.length)];
        String[] statuses = {"SCHEDULED", "DELAYED", "BOARDING", "CANCELLED", "LANDED"};
        String status = statuses[rand.nextInt(statuses.length)];
        boolean isArrival = rand.nextBoolean();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        flights.add(new Flight(flightNumber, airline, destination, time, gate, status, isArrival, date));
    }

    public Flight findFlight(String flightNumber) {
        for (Flight f : flights) {
            if (f.flightNumber.equalsIgnoreCase(flightNumber)) return f;
        }
        return null;
    }
}

class ConsoleDisplay {
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else System.out.print("\033[H\033[2J");
        } catch (Exception e) {
            System.out.println("Couldn't clear console.");
        }
    }

    public static void displayFlights(List<Flight> flights) {
        clearConsole();
        System.out.println("============================================================================================");
        System.out.println("| DATE       | FLIGHT | AIRLINE     | TO/FROM   | TIME   | GATE | STATUS     |");
        System.out.println("============================================================================================");
        for (Flight f : flights) {
            System.out.printf("| %-10s | %-6s | %-10s | %-9s | %-6s | %-4s | %-10s |\n",
                    f.date, f.flightNumber, f.airline, f.destination, f.time, f.gate, f.status);
        }
        System.out.println("============================================================================================");
    }
}

class User implements Serializable {
	String name;
    String email;
    List<String> bookedFlights = new ArrayList<>();

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void bookFlight(String flightNumber) {
        if (!bookedFlights.contains(flightNumber)) {
            bookedFlights.add(flightNumber);
        }
    }

    public void cancelBooking() {
        if (bookedFlights.isEmpty()) {
            System.out.println("\u274C No bookings to cancel.");
            return;
        }
        System.out.println("Your current bookings:");
        for (int i = 0; i < bookedFlights.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, bookedFlights.get(i));
        }
        System.out.print("Enter booking number to cancel: ");
        try {
        	// Converts that String (like "2") into an integer (like 2).
        	// Subtracts 1 to convert the user-friendly index (e.g., "choice #2") into a 0-based index (like 1 for arrays/lists in Java).
            int index = Integer.parseInt(new Scanner(System.in).nextLine()) - 1;
            if (index >= 0 && index < bookedFlights.size()) {
                String removed = bookedFlights.remove(index);
                System.out.println("\u274C Booking for flight " + removed + " cancelled.");
            } else {
                System.out.println("\u274C Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("\u274C Invalid input.");
        }
    }

    public void showDetails(FlightManager manager) {
        System.out.println("\nUser Details:");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        if (!bookedFlights.isEmpty()) {
            System.out.println("Booked Flights:");
            for (String flightNumber : bookedFlights) {
                Flight f = manager.findFlight(flightNumber);
                if (f != null) {
                    System.out.printf("- %s to %s at %s\n", f.flightNumber, f.destination, f.time);
                } else {
                    System.out.println("- " + flightNumber + " (Not Found)");
                }
            }
        } else {
            System.out.println("Booked Flights: None");
        }
    }
}

public class Aerolines {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String USERS_FILE = "users.ser";

    public static void main(String[] args) {
        FlightManager manager = new FlightManager();
        for (int i = 0; i < 10; i++) manager.generateFlight();

        List<User> allUsers = loadUsers();
        assignFlightsToDefaultUsers(manager, allUsers);

        while (true) {
            User currentUser = loginUser(allUsers);

            while (true) {
                System.out.println("\n========= \u2708\uFE0F AEROLINES SYSTEM \u2708\uFE0F =========");
                System.out.println("1. Display Flight Status");
                System.out.println("2. Search Flight");
                System.out.println("3. Book a Flight");
                System.out.println("4. Cancel Booking");
                System.out.println("5. Show My Details (History)");
                System.out.println("6. Show Boarding Users");
                System.out.println("7. Exit");
                System.out.print("Choose an option (1 to 7): ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> ConsoleDisplay.displayFlights(manager.getFlights());
                    case "2" -> {
                        System.out.print("Enter flight number: ");
                        String flightNumber = scanner.nextLine().trim().toUpperCase();
                        Flight f = manager.findFlight(flightNumber);
                        if (f != null) {
                            System.out.printf("\n\uD83D\uDCC5 Date: %s\n\u2708\uFE0F  Flight: %s\n\uD83D\uDCCD Airline: %s\n\uD83D\uDCCC Destination: %s\n\uD83D\uDD52 Time: %s\n\uD83D\uDEEC Gate: %s\n\uD83D\uDCCA Status: %s\n\n",
                                    f.date, f.flightNumber, f.airline, f.destination, f.time, f.gate, f.status);
                        } else {
                            System.out.println("\u274C Flight not found.");
                        }
                    }
                    case "3" -> {
                        List<Flight> availableFlights = new ArrayList<>();
                        for (Flight f : manager.getFlights()) {
                            if (f.status.equalsIgnoreCase("SCHEDULED") || f.status.equalsIgnoreCase("BOARDING")) {
                                availableFlights.add(f);
                            }
                        }

                        if (availableFlights.isEmpty()) {
                            System.out.println("\u274C No flights available for booking at the moment.");
                            break;
                        }

                        System.out.println("\n\u2708\uFE0F Available Flights for Booking:");
                        for (Flight f : availableFlights) {
                            System.out.printf("- %s to %s at %s (%s)\n", f.flightNumber, f.destination, f.time, f.status);
                        }

                        System.out.print("Enter flight number to book: ");
                        String flightNumber = scanner.nextLine().trim().toUpperCase();
                        Flight selectedFlight = manager.findFlight(flightNumber);

                        if (selectedFlight == null ||
                                !(selectedFlight.status.equalsIgnoreCase("SCHEDULED") || selectedFlight.status.equalsIgnoreCase("BOARDING"))) {
                            System.out.println("\u274C Invalid or unavailable flight number.");
                            break;
                        }

                        System.out.println("Available Seats:");
                        for (Map.Entry<String, Integer> entry : selectedFlight.seatAvailability.entrySet()) {
                            System.out.printf("  %s: %d\n", entry.getKey(), entry.getValue());
                        }

                        System.out.print("Choose ticket class (Economy/Business/FirstClass): ");
                        String ticketType = scanner.nextLine().trim();
                        if (!selectedFlight.seatAvailability.containsKey(ticketType) || selectedFlight.seatAvailability.get(ticketType) <= 0) {
                            System.out.println("\u274C Please enter the class asper in the console display:");
                            break;
                        }

                        // Deduct one seat from the selected class
                        selectedFlight.seatAvailability.put(ticketType, selectedFlight.seatAvailability.get(ticketType) - 1);

                        currentUser.bookFlight(flightNumber);
                        System.out.printf("\u2705 Successfully booked %s class ticket for flight %s to %s at %s\n",
                                ticketType, selectedFlight.flightNumber, selectedFlight.destination, selectedFlight.time);
                        saveUsers(allUsers);
                    }

                    case "4" -> currentUser.cancelBooking();
                    case "5" -> currentUser.showDetails(manager);
                    case "6" -> {
                        System.out.println("\n\uD83D\uDCCB Boarding Users:");
                        Set<String> displayed = new HashSet<>();
                        for (User u : allUsers) {
                            for (String fn : u.bookedFlights) {
                                if (!displayed.contains(u.email)) {
                                    Flight f = manager.findFlight(fn);
                                    if (f != null) {
                                        System.out.printf("- %s (%s) → %s to %s at %s\n", u.name, u.email, f.flightNumber, f.destination, f.time);
                                        displayed.add(u.email);
                                    }
                                }
                            }
                        }
                    }
                    case "7" -> {
                        saveUsers(allUsers);
                        System.out.println("\uD83D\uDC4B Exiting Aerolines. Thank you!");
                        System.exit(0);
                    }
                    default -> System.out.println("\u26A0\uFE0F Invalid option.");
                }
            }
        }
    }

    private static User loginUser(List<User> users) {
        String email;
        while (true) {
            System.out.print("Enter your email to login: ");
            email = scanner.nextLine().trim().toLowerCase();
            if (email.endsWith("@gmail.com")) break;
            else System.out.println("\u26A0\uFE0F Email must end with @gmail.com.");
        }

        for (User u : users) {
            if (u.email.equalsIgnoreCase(email)) {
                System.out.println("\u2705 Welcome back, " + u.name + "!");
                return u;
            }
        }

        System.out.print("New user! Enter your name: ");
        String name = scanner.nextLine().trim();
        User newUser = new User(name, email);
        users.add(newUser);
        System.out.println("\u2705 Registered successfully!");
        return newUser;
    }

    private static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException ignored) {}
    }
//You're working with serialization or type casting of generic collections (e.g., List, Map).
//The compiler shows a warning like "unchecked cast" when reading objects from a file, even if the programmer is sure the cast is safe.
    @SuppressWarnings("unchecked")
    private static List<User> loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            List<User> defaults = new ArrayList<>();
            defaults.add(new User("Dharani", "dharani@gmail.com"));
            defaults.add(new User("Pavi", "pavi@gmail.com"));
            defaults.add(new User("Aravind", "aravind@gmail.com"));
            defaults.add(new User("Jaiwin", "jaiwin@gmail.com"));
            return defaults;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private static void assignFlightsToDefaultUsers(FlightManager manager, List<User> users) {
        String[] flightNumbers = {"FL100", "FL101", "FL102", "FL103"};
        for (int i = 0; i < users.size(); i++) {
            if (i < flightNumbers.length) {
                if (manager.findFlight(flightNumbers[i]) == null) {
                    manager.getFlights().add(new Flight(
                            flightNumbers[i], "Air India", "Delhi", "10:00", "A1", "SCHEDULED", false,
                            new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
                }
                users.get(i).bookFlight(flightNumbers[i]);
            }
        }
    }
}