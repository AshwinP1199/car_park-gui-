import java.time.LocalDateTime;

public class ParkingSlot {
    private String slotIdentifier;
    private Car parkedCar;
    private LocalDateTime parkingStartTime;
    private LocalDateTime parkingEndTime;

    public ParkingSlot(String slotIdentifier) {
        this.slotIdentifier = slotIdentifier;
    }

    public String getSlotIdentifier() {
        return slotIdentifier;
    }

    public Car getParkedCar() {
        return parkedCar;
    }

    public void parkCar(Car car) {
        this.parkedCar = car;
        this.parkingStartTime = LocalDateTime.now();
        this.parkingEndTime = null;
    }

    public Car removeCar() {
        Car removedCar = this.parkedCar;
        this.parkedCar = null;
        this.parkingEndTime = LocalDateTime.now();
        return removedCar;
    }

    public LocalDateTime getParkingStartTime() {
        return parkingStartTime;
    }

    public void setParkingStartTime(LocalDateTime parkingStartTime) {
        this.parkingStartTime = parkingStartTime;
    }

    public LocalDateTime getParkingEndTime() {
        return parkingEndTime;
    }

    public void setParkingEndTime(LocalDateTime parkingEndTime) {
        this.parkingEndTime = parkingEndTime;
    }
}
