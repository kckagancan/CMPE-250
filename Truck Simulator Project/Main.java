import java.io.File;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Truck Fleet Management Simulator
 *
 * @author Kagan Can, Student ID: 2022400240
 * @since Date: 25.10.2024
 */
public class Main {
    public static void main(String[] args) throws IOException {

        // Initialize a file handle and a scanner for the input, a file writer for the output.
        File fh = new File("type5-large.txt");
        Scanner scanner = new Scanner(fh);
        FileWriter writer = new FileWriter("output.txt");
        Company company = new Company(writer);

        // Read the input file.
        while (scanner.hasNextLine()) {
            try {
                String operation = scanner.next();

                // Act accordingly to the operation.
                if (operation.equals("create_parking_lot")) {

                    int capacityConstraint = scanner.nextInt();
                    int truckLimit = scanner.nextInt();
                    company.addParkingLot(new ParkingLot(capacityConstraint, truckLimit));
                } else if (operation.equals("delete_parking_lot")) {

                    int capacityConstraint = scanner.nextInt();
                    company.deleteParkingLot(capacityConstraint);
                } else if (operation.equals("add_truck")) {

                    int ID = scanner.nextInt();
                    int maxCapacity = scanner.nextInt();
                    company.addTruck(ID, maxCapacity);
                } else if (operation.equals("ready")) {

                    int capacityConstraint = scanner.nextInt();
                    company.ready(capacityConstraint);
                } else if (operation.equals("load")) {

                    int capacityConstraint = scanner.nextInt();
                    int loadAmount = scanner.nextInt();
                    company.load(capacityConstraint, loadAmount);
                } else if (operation.equals("count")) {

                    int capacityConstraint = scanner.nextInt();
                    company.count(capacityConstraint);
                }
            }

            catch (NoSuchElementException ignored){
            }

        }

        writer.close();
    }
}


