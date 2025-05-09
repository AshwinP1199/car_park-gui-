import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Car {
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private LocalDateTime parkingTime;

    public Car(String make, String model, int year, String licensePlate) {
        this.make = make;
        this.model = model;
        this.year = year; // Make sure to set the year correctly
        this.licensePlate = licensePlate;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public boolean isLicensePlateValid() {
        String licensePlatePattern = "^[A-Z]\\d{4}$"; // Uppercase letter followed by 4 digits
        return Pattern.matches(licensePlatePattern, licensePlate);
    }

    public void setParkingTime() {
        this.parkingTime = LocalDateTime.now();
    }

    public LocalDateTime getParkingTime() {
        return parkingTime;
    }
}
