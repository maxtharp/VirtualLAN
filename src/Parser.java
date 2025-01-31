import java.io.*;
import java.util.*;

public class Parser {

    private static final String configFilePath = "src/config.txt"; // Path to your config.txt file

    // Get neighbors' IP addresses
    public static List<String> getNeighborsIP(String inputName) {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getNeighborsIP();
        }

        return Collections.emptyList();
    }

    // Get neighbors' ports
    public static List<Integer> getNeighborsPort(String inputName) {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getNeighborsPort();
        }

        return Collections.emptyList();
    }

    // Helper method to parse the config file
    private static Map<String, Device> parseConfigFile() {
        List<String[]> links = new ArrayList<>();
        Map<String, Device> deviceMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            boolean isLinkSection = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase("links")) {
                    isLinkSection = true;
                    continue;
                }

                if (!isLinkSection) {
                    // Parse devices
                    String name = line;
                    int port = Integer.parseInt(br.readLine().trim());
                    String ipAddress = br.readLine().trim();
                    Device device = new Device(name, port, ipAddress);
                    deviceMap.put(name, device);
                } else {
                    // Parse links
                    String[] link = line.split(":");
                    if (link.length == 2) {
                        links.add(link);
                    }
                }
            }

            // Establish neighbor relationships
            for (String[] link : links) {
                Device device1 = deviceMap.get(link[0]);
                Device device2 = deviceMap.get(link[1]);

                if (device1 != null && device2 != null) {
                    device1.addNeighbor(device2);
                    device2.addNeighbor(device1);
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading the configuration file: " + e.getMessage());
        }

        return deviceMap;
    }
}
