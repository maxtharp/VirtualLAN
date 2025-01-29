import java.io.*;
import java.util.*;

class Device {
    String name;
    int port;
    String ipAddress;
    List<Device> neighbors = new ArrayList<>();

    public Device(String name, int port, String ipAddress) {
        this.name = name;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public void addNeighbor(Device neighbor) {
        neighbors.add(neighbor);
    }

    public void GetNeighbors(String name) {
        if (this.name.equals(name)) {
            System.out.println("Neighbors of " + name + ":");
            for (Device neighbor : neighbors) {
                System.out.println("Port: " + neighbor.port + ", IP Address: " + neighbor.ipAddress);
            }
        }
    }

    @Override
    public String toString() {
        return "Device{name='" + name + '\'' + ", port=" + port + ", ipAddress='" + ipAddress + '\'' + '}';
    }
}

public class parser {
    public static void main(String[] args) {
        String configFilePath = "src/config.txt"; // Path to your config.txt file

        List<Device> devices = new ArrayList<>();
        List<String[]> links = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            boolean isLinkSection = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Check for the "links" section
                if (line.equalsIgnoreCase("links")) {
                    isLinkSection = true;
                    continue;
                }

                if (!isLinkSection) {
                    // Parse devices
                    String name = line;
                    int port = Integer.parseInt(br.readLine().trim());
                    String ipAddress = br.readLine().trim();

                    devices.add(new Device(name, port, ipAddress));
                } else {
                    // Parse links
                    String[] link = line.split(":");
                    if (link.length == 2) {
                        links.add(link);
                    }
                }
            }

            // Establish neighbors based on links
            for (String[] link : links) {
                Device device1 = findDeviceByName(devices, link[0]);
                Device device2 = findDeviceByName(devices, link[1]);

                if (device1 != null && device2 != null) {
                    device1.addNeighbor(device2);
                    device2.addNeighbor(device1);
                }
            }

            // Print parsed data for validation
            System.out.println("Devices:");
            for (Device device : devices) {
                System.out.println(device);
            }

            System.out.println("\nLinks:");
            for (String[] link : links) {
                System.out.println(link[0] + " <-> " + link[1]);
            }

            // Test GetNeighbors
            String testDeviceName = "A";
            for (Device device : devices) {
                device.GetNeighbors(testDeviceName);
            }

        } catch (IOException e) {
            System.err.println("Error reading the configuration file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing port number: " + e.getMessage());
        }
    }

    private static Device findDeviceByName(List<Device> devices, String name) {
        for (Device device : devices) {
            if (device.name.equals(name)) {
                return device;
            }
        }
        return null;
    }
}