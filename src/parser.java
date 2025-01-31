import java.io.*;
import java.util.*;

class Device {
    String name;
    int port;
    String ipAddress;

    public Device(String name, int port, String ipAddress) {
        this.name = name;
        this.port = port;
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Device{name='" + name + '\'' + ", port=" + port + ", ipAddress='" + ipAddress + '\'' + '}';
    }
}

public class parser {

    public static void main(String[] args) {
        String configFilePath = "config.txt"; // Path to your config.txt file

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

            // Print parsed data for validation
            System.out.println("Devices:");
            for (Device device : devices) {
                System.out.println(device);
            }

            System.out.println("\nLinks:");
            for (String[] link : links) {
                System.out.println(link[0] + " <-> " + link[1]);
            }

        } catch (IOException e) {
            System.err.println("Error reading the configuration file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing port number: " + e.getMessage());
        }
    }
}
