import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class CarParkGUI {
    private JFrame frame;
    private JPanel buttonPanel;
    private JLabel imageLabel;
    private ImageIcon imageIcon;
    private CarPark carPark;
    private JPanel slotPanel;
    private Map<JButton, ParkingSlot> slotButtonMap;
    private JDialog slotDialog;
    private JLabel parkingTimeLabel;

    public CarParkGUI() {
        carPark = new CarPark(5, this);
        
        // Initialize the slot dialog here
        slotDialog = new JDialog(frame, "Parking Slots", Dialog.ModalityType.MODELESS);
        slotDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        slotDialog.setSize(400, 400);
        slotDialog.setLocationRelativeTo(frame);
        slotDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Handle the dialog closed event if needed
            }
        });

        frame = new JFrame("Car Parking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 350);

        buttonPanel = new JPanel(new FlowLayout());

        JButton displayButton = new JButton("Display Parking Status");
        JButton findCarButton = new JButton("Find Car");
        JButton removeCarButton = new JButton("Remove Car");
        JButton addSlotButton = new JButton("Add Parking Slot");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(displayButton);
        buttonPanel.add(findCarButton);
        buttonPanel.add(removeCarButton);
        buttonPanel.add(addSlotButton);
        buttonPanel.add(exitButton);

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("stock.jpeg"));
            int width = 800;
            int height = 300;
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageLabel = new JLabel(imageIcon, SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.TOP);

        frame.add(imageLabel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Initialize the map of slot buttons
        slotButtonMap = new HashMap<>();

        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showParkingSlots();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Exit the application
        }
    }
});

removeCarButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        boolean validInput = false;
        String licensePlate = null;

        while (true) {
            // Display an input dialog to get the license plate from the user
            licensePlate = JOptionPane.showInputDialog(frame, "Enter License Plate:");

            if (licensePlate == null) {
                // User clicked Cancel or closed the dialog, exit the loop
                break;
            }

            if (licensePlate.isEmpty()) {
                // User pressed "OK" without entering anything, show an error message and exit the loop
                JOptionPane.showMessageDialog(frame, "Please Enter a License Plate.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            }

            if (!new Car("", "", 0, licensePlate).isLicensePlateValid()) {
                // Invalid license plate format, show an error message and allow the user to try again
                JOptionPane.showMessageDialog(frame, "Invalid license plate format. It must be an uppercase letter followed by 4 digits.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Valid input, set the flag to true to exit the loop
                validInput = true;
                break;
            }
        }

        // Proceed only if a valid license plate was provided
        if (validInput && licensePlate != null) {
            String removedFromSlot = carPark.removeCarByLicensePlate(licensePlate);

            if (removedFromSlot != null) {
                JOptionPane.showMessageDialog(frame, "Car with license plate " + licensePlate + " removed successfully from Slot " + removedFromSlot + ".");
                // You can update your GUI here as needed
            } else {
                JOptionPane.showMessageDialog(frame, "Car with license plate " + licensePlate + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
});

findCarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showFindCarOptions();
            }
        });
        
        addSlotButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        addParkingSlot();
    }
});

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CarParkGUI();
            }
        });
    }
    
    // Modify the addParkingSlot method to use the CarPark's isValidSlotIdentifier
private void addParkingSlot() {
    // Prompt the user for the slot identifier
    String slotIdentifier = JOptionPane.showInputDialog(frame, "Enter Slot Identifier (e.g., A001):");

    if (slotIdentifier != null && !slotIdentifier.isEmpty() && CarPark.isValidSlotIdentifier(slotIdentifier)) {
        // Call the addParkingSlot method from your CarPark class
        boolean added = carPark.addParkingSlot(slotIdentifier);

        if (added) {
            JOptionPane.showMessageDialog(frame, "Parking slot added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            updateParkingSlotsDialog(); // Update the parking slot display
        } else {
            JOptionPane.showMessageDialog(frame, "Parking slot with the same identifier already exists or the format is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else if (slotIdentifier != null) {
        JOptionPane.showMessageDialog(frame, "Invalid slot identifier format. It must be one alphabet followed by three numbers (e.g., A001).", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void showFindCarOptions() {
        // Create an array of options for finding a car
        String[] options = { "Find by Make", "Find by License Plate", "Cancel" };

        int choice = JOptionPane.showOptionDialog(frame,
                "Choose how to find the car:",
                "Find Car",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Find by Make
                findCarByMake();
                break;
            case 1: // Find by License Plate
                findCarByLicensePlate();
                break;
            default:
                // User canceled or closed the dialog
                break;
        }
    }
    
   private void findCarByMake() {
    String make = JOptionPane.showInputDialog(frame, "Enter Car Make:");
    if (make != null && !make.isEmpty()) {
        List<Car> cars = carPark.findCarsByMake(make);

        if (cars.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No cars found for make: " + make, "Find Car", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder result = new StringBuilder("Cars found for make: " + make + "\n");

            for (Car car : cars) {
                String slotIdentifier = carPark.getSlotIdentifierByCar(car);
                ParkingSlot slot = carPark.getSlotByIdentifier(slotIdentifier);

                // Get parking time
                String parkingTime = carPark.getSlotParkingTime(slot);

                result.append("Make: ").append(car.getMake()).append("\n");
                result.append("Model: ").append(car.getModel()).append("\n");
                result.append("Registration Number: ").append(car.getLicensePlate()).append("\n");
                result.append("Parked in Slot: ").append(slotIdentifier).append("\n");
                result.append("Parking Time: ").append(parkingTime).append("\n");
                result.append("\n");
            }

            JOptionPane.showMessageDialog(frame, result.toString(), "Find Car", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

    private void findCarByLicensePlate() {
    // Prompt the user to enter a license plate
    String licensePlate = JOptionPane.showInputDialog(frame, "Enter License Plate:");
    
    if (licensePlate != null && !licensePlate.isEmpty()) {
        Car car = carPark.findCarByLicensePlate(licensePlate);
        if (car != null) {
            // If a car with the specified license plate is found
            String slotIdentifier = carPark.getSlotIdentifierByCar(car);

            // Prepare the result message
            StringBuilder result = new StringBuilder("Car found for license plate: " + licensePlate + "\n");
            result.append("Make: ").append(car.getMake()).append("\n");
            result.append("Model: ").append(car.getModel()).append("\n");
            result.append("Year: ").append(car.getYear()).append("\n");
            
            // Display the parking slot identifier
            result.append("Parked in Slot: ").append(slotIdentifier).append("\n");

            // Display the result in a dialog box
            JOptionPane.showMessageDialog(frame, result.toString(), "Find Car", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // If no car with the specified license plate is found
            JOptionPane.showMessageDialog(frame, "No car found for license plate: " + licensePlate, "Find Car", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

    private void showParkingSlots() {
        List<ParkingSlot> parkingSlots = carPark.getAllParkingSlots();

        slotPanel = new JPanel(new GridLayout(0, 1));

        for (ParkingSlot slot : parkingSlots) {
            JButton slotButton = new JButton("Slot " + slot.getSlotIdentifier());
            slotButtonMap.put(slotButton, slot); // Add button and corresponding slot to the map

            Car parkedCar = slot.getParkedCar();
            if (parkedCar != null) {
                slotButton.setBackground(Color.RED);
                slotButton.addActionListener(e -> showSlotDetails(slot, parkedCar, slotButton));
            } else {
                slotButton.setBackground(Color.GREEN);
                slotButton.addActionListener(e -> {
                    int option = JOptionPane.showOptionDialog(
                            frame,
                            "Choose an action for Slot " + slot.getSlotIdentifier(),
                            "Slot Action",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new String[]{"Park Car", "Delete Slot", "Cancel"},
                            null);

                    if (option == 0) {
                        parkCar(slot, slotButton);
                    } else if (option == 1) {
                        int confirm = JOptionPane.showConfirmDialog(frame, "Delete this slot?", "Delete Slot", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            int deleteResult = carPark.deleteParkingSlot(slot.getSlotIdentifier());
                            if (deleteResult == 0) {
                                slotPanel.remove(slotButton);
                                slotButtonMap.remove(slotButton); // Remove the button from the map
                                frame.revalidate();
                                frame.repaint();
                            } else if (deleteResult == -1) {
                                JOptionPane.showMessageDialog(frame, "Slot does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                            } else if (deleteResult == -2) {
                                JOptionPane.showMessageDialog(frame, "Slot is occupied and cannot be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
            }

            slotPanel.add(slotButton);
        }

        JScrollPane scrollPane = new JScrollPane(slotPanel);
        slotDialog.getContentPane().removeAll();
        slotDialog.getContentPane().add(scrollPane);
        slotDialog.revalidate();
        slotDialog.repaint();
        slotDialog.setVisible(true);
    }

    private void parkCar(ParkingSlot slot, JButton slotButton) {
    while (true) {
        JTextField makeField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField licensePlateField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Car Make:"));
        panel.add(makeField);
        panel.add(new JLabel("Car Model:"));
        panel.add(modelField);
        panel.add(new JLabel("Car Year:"));
        panel.add(yearField);
        panel.add(new JLabel("License Plate:"));
        panel.add(licensePlateField);

        JDialog currentDialog = new JDialog(frame, "Park Car", Dialog.ModalityType.APPLICATION_MODAL);

        int inputResult = JOptionPane.showConfirmDialog(currentDialog, panel, "Park Car", JOptionPane.OK_CANCEL_OPTION);

        if (inputResult == JOptionPane.OK_OPTION) {
            String carMake = makeField.getText();
            String carModel = modelField.getText();
            int carYear;
            try {
                carYear = Integer.parseInt(yearField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(currentDialog, "Please Enter Data in the Fields.", "Error", JOptionPane.ERROR_MESSAGE);
                continue; // Continue the loop to re-enter data
            }
            String licensePlate = licensePlateField.getText();

            // Validate the license plate format
            if (!new Car("", "", 0, licensePlate).isLicensePlateValid()) {
                JOptionPane.showMessageDialog(currentDialog, "Invalid license plate format. It must be an uppercase letter followed by 4 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                continue; // Continue the loop to re-enter data
            }

            int parkResult = carPark.parkCarInSlot(carMake, carModel, carYear, licensePlate, slot.getSlotIdentifier());

            if (parkResult == 0) {
                slotButton.setBackground(Color.RED);
                slotButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showSlotDetails(slot, carPark.getCarByLicensePlate(licensePlate), slotButton);
                    }
                });

                currentDialog.dispose();
                showParkingSlots();
                break; // Exit the loop on successful parking
            } else if (parkResult == -1) {
                JOptionPane.showMessageDialog(currentDialog, "Invalid slot identifier format.", "Error", JOptionPane.ERROR_MESSAGE);
                continue; // Continue the loop to re-enter data
            } else if (parkResult == -2) {
                JOptionPane.showMessageDialog(currentDialog, "Slot does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                continue; // Continue the loop to re-enter data
            } else if (parkResult == -3) {
                JOptionPane.showMessageDialog(currentDialog, "Slot is already occupied.", "Error", JOptionPane.ERROR_MESSAGE);
                continue; // Continue the loop to re-enter data
            } else if (parkResult == -4) {
                JOptionPane.showMessageDialog(currentDialog, "License plate is not unique within the CarPark.", "Error", JOptionPane.ERROR_MESSAGE);
                continue; // Continue the loop to re-enter data
            }
        } else {
            break; // Exit the loop if the user cancels
        }
    }
}

    private void showSlotDetails(ParkingSlot slot, Car car, JButton slotButton) {
    JPanel panel = new JPanel(new GridLayout(5, 2));

    if (car != null) {
        panel.add(new JLabel("Make:"));
        panel.add(new JLabel(car.getMake()));
        panel.add(new JLabel("Model:"));
        panel.add(new JLabel(car.getModel()));
        panel.add(new JLabel("Year:"));
        panel.add(new JLabel(String.valueOf(car.getYear())));
        panel.add(new JLabel("License Plate:"));
        panel.add(new JLabel(car.getLicensePlate()));
        panel.add(new JLabel("Time:"));

        String parkingTime = carPark.getSlotParkingTime(slot);

        panel.add(new JLabel(parkingTime));

        int option = JOptionPane.showOptionDialog(
                slotDialog,
                panel,
                "Slot Details - Slot " + slot.getSlotIdentifier(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"Remove Car", "Close"},
                null);

        if (option == 0) {
            int confirm = JOptionPane.showConfirmDialog(slotDialog, "Remove the car from this slot?", "Remove Car", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                slot.removeCar();
                slotButton.setBackground(Color.GREEN);
                updateParkingSlotsDialog();
            }
        }
    }
}


    public void updateParkingSlotsDialog() {
    List<ParkingSlot> parkingSlots = carPark.getAllParkingSlots();

    // Clear the slotPanel and slotButtonMap
    slotPanel.removeAll();
    slotButtonMap.clear();

    for (ParkingSlot slot : parkingSlots) {
        JButton slotButton = new JButton("Slot " + slot.getSlotIdentifier());
        slotButtonMap.put(slotButton, slot);

        Car parkedCar = slot.getParkedCar();
        if (parkedCar != null) {
            slotButton.setBackground(Color.RED);
            slotButton.addActionListener(e -> showSlotDetails(slot, parkedCar, slotButton));
        } else {
            slotButton.setBackground(Color.GREEN);
            slotButton.addActionListener(e -> {
                int option = JOptionPane.showOptionDialog(
                        frame,
                        "Choose an action for Slot " + slot.getSlotIdentifier(),
                        "Slot Action",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        new String[]{"Park Car", "Delete Slot", "Cancel"},
                        null);

                if (option == 0) {
                    parkCar(slot, slotButton);
                } else if (option == 1) {
                    int confirm = JOptionPane.showConfirmDialog(frame, "Delete this slot?", "Delete Slot", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int deleteResult = carPark.deleteParkingSlot(slot.getSlotIdentifier());
                        if (deleteResult == 0) {
                            // Remove the slotButton from the panel and map
                            slotPanel.remove(slotButton);
                            slotButtonMap.remove(slotButton);
                            updateParkingSlotsDialog();
                        } else if (deleteResult == -1) {
                            JOptionPane.showMessageDialog(frame, "Slot does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else if (deleteResult == -2) {
                            JOptionPane.showMessageDialog(frame, "Slot is occupied and cannot be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }

        slotPanel.add(slotButton);
    }

    slotPanel.revalidate();
    slotPanel.repaint();
    slotDialog.revalidate();
    slotDialog.repaint();
}
}
