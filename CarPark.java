import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarPark {
    private List<ParkingSlot> parkingSlots;
    private Map<String, ParkingSlot> licensePlateToSlot;
    private Map<String, List<ParkingSlot>> makeToSlots;
    private CarParkGUI carParkGUI;

    public CarPark(int totalSlots, CarParkGUI carParkGUI) {
        this.carParkGUI = carParkGUI; 
        parkingSlots = new ArrayList<>();
        licensePlateToSlot = new HashMap<>();
        makeToSlots = new HashMap<>();

        for (int i = 1; i <= totalSlots; i++) {
            String slotIdentifier = generateSlotIdentifier(i);
            ParkingSlot slot = new ParkingSlot(slotIdentifier);
            parkingSlots.add(slot);
        }
    }

    public boolean addParkingSlot(String slotIdentifier) {
        if (!isSlotIdentifierUnique(slotIdentifier) || !isValidSlotIdentifier(slotIdentifier)) {
            return false;
        }

        ParkingSlot slot = new ParkingSlot(slotIdentifier);
        parkingSlots.add(slot);
        return true;
    }

    public List<ParkingSlot> getAllParkingSlots() {
        return parkingSlots;
    }

    public int deleteParkingSlot(String slotIdentifier) {
    ParkingSlot slot = getSlotByIdentifier(slotIdentifier);

    if (slot == null) {
        return -1; // Slot does not exist
    }

    if (slot.getParkedCar() != null) {
        return -2; // Slot is occupied by a car and cannot be deleted
    }

    parkingSlots.remove(slot);
    carParkGUI.updateParkingSlotsDialog(); // Call the method to update the dialogue box
    return 0; // Slot deleted successfully
}

    public int parkCarInSlot(String make, String model, int year, String licensePlate, String slotIdentifier) {
        // Check if the car with the same license plate already exists in the car park
        if (getCarByLicensePlate(licensePlate) != null) {
            return -4; // Car with the same license plate already exists
        }

        // Check if the slot identifier is valid
        if (!isValidSlotIdentifier(slotIdentifier)) {
            return -1; // Invalid slot identifier format
        }

        // Find the slot with the specified identifier
        ParkingSlot slot = getSlotByIdentifier(slotIdentifier);

        // Check if the slot exists
        if (slot == null) {
            return -2; // Slot does not exist
        }

        // Create a new car and park it in the slot
        Car car = new Car(make, model, year, licensePlate);
        slot.parkCar(car);
        licensePlateToSlot.put(licensePlate, slot);
        makeToSlots.computeIfAbsent(car.getMake().toLowerCase(), k -> new ArrayList<>()).add(slot);

        return 0; // Car parked successfully
    }

    public Car getCarByLicensePlate(String licensePlate) {
        ParkingSlot slot = licensePlateToSlot.get(licensePlate);
        if (slot != null) {
            return slot.getParkedCar();
        }
        return null;
    }

    public String removeCarByLicensePlate(String licensePlate) {
    ParkingSlot slot = licensePlateToSlot.get(licensePlate);
    if (slot != null) {
        Car removedCar = slot.getParkedCar();
        slot.removeCar();
        licensePlateToSlot.remove(licensePlate);
        List<ParkingSlot> slotsForMake = makeToSlots.get(removedCar.getMake());
        if (slotsForMake != null) {
            slotsForMake.remove(slot);
        }
        return slot.getSlotIdentifier(); // Return the Slot ID
    }
    return null;
}

    public Car findCarByLicensePlate(String licensePlate) {
    return getCarByLicensePlate(licensePlate);
}


   public List<Car> findCarsByMake(String make) {
    List<Car> cars = new ArrayList<>();

    for (Map.Entry<String, ParkingSlot> entry : licensePlateToSlot.entrySet()) {
        ParkingSlot slot = entry.getValue();
        Car car = slot.getParkedCar();

        if (car != null && car.getMake().equalsIgnoreCase(make)) {
            cars.add(car);
        }
    }

    return cars;
}

    public String getSlotIdentifierByCar(Car car) {
    String licensePlate = car.getLicensePlate();
    ParkingSlot slot = licensePlateToSlot.get(licensePlate);

    if (slot != null) {
        return slot.getSlotIdentifier();
    }

    return "Not found";
}

    public String getSlotParkingTime(ParkingSlot slot) {
    if (slot != null && slot.getParkingStartTime() != null) {
        LocalDateTime endTime = slot.getParkingEndTime();
        LocalDateTime startTime = slot.getParkingStartTime();

        if (endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();
            return hours + " hours " + minutes + " minutes " + seconds + " seconds";
        } else {
            // Car is still parked
            LocalDateTime currentTime = LocalDateTime.now();
            Duration duration = Duration.between(startTime, currentTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();
            return hours + " hours " + minutes + " minutes " + seconds + " seconds";
        }
    }
    return "N/A";
}



    public boolean isSlotIdentifierUnique(String slotIdentifier) {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.getSlotIdentifier().equals(slotIdentifier)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSlotEmpty(String slotIdentifier) {
        ParkingSlot slot = getSlotByIdentifier(slotIdentifier);
        return slot != null && slot.getParkedCar() == null;
    }

    public int removeCarBySlot(String slotIdentifier) {
        ParkingSlot slot = getSlotByIdentifier(slotIdentifier);

        if (slot == null) {
            return -1;
        }

        Car car = slot.getParkedCar();

        if (car == null) {
            return -2;
        }

        String licensePlate = car.getLicensePlate();
        licensePlateToSlot.remove(licensePlate);
        slot.removeCar();

        List<ParkingSlot> slotsForMake = makeToSlots.get(car.getMake());
        if (slotsForMake != null) {
            slotsForMake.remove(slot);
        }

        return 0;
    }

    public String getCarDetailsInSlot(String slotIdentifier) {
        ParkingSlot slot = getSlotByIdentifier(slotIdentifier);
        if (slot != null && slot.getParkedCar() != null) {
            Car car = slot.getParkedCar();
            return "Make: " + car.getMake() + "\n"
                    + "Model: " + car.getModel() + "\n"
                    + "Year: " + car.getYear() + "\n"
                    + "Parking Time: " + car.getParkingTime() + "\n";
        }
        return null;
    }

    private String generateSlotIdentifier(int slotNumber) {
        char zone = (char) ('A' + (slotNumber - 1) / 26);
        int slotInZone = (slotNumber - 1) % 26 + 1;
        return zone + String.format("%03d", slotInZone);
    }

    public static boolean isValidSlotIdentifier(String slotIdentifier) {
        return slotIdentifier.matches("[A-Z]\\d{3}");
    }

    private boolean isLicensePlateUnique(String licensePlate) {
        return !licensePlateToSlot.containsKey(licensePlate);
    }

    public ParkingSlot getSlotByIdentifier(String slotIdentifier) {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.getSlotIdentifier().equals(slotIdentifier)) {
                return slot;
            }
        }
        return null;
    }
}
