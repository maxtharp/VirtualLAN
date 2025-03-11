import java.io.*;
import java.util.*;

public class Parser {
    private static Map<String, Device> deviceMap;
    private Map<String, List<String>> links = new HashMap<>();
    private static final String configFilePath = "src/config.txt"; // Path to your config.txt file


    // Get neighbors' IP addresses
    public static List<String> getNeighborsIP(String inputName) throws IOException {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getNeighborsIP();
        }

        return Collections.emptyList();
    }

    // Get neighbors' ports
    public static List<Integer> getNeighborsPort(String inputName) throws IOException {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getNeighborsPort();
        }

        return Collections.emptyList();
    }

    // Get the port of a specific device
    public static int getPort(String inputName) throws IOException {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getPort();
        }

        // Return -1 if device not found (indicating error)
        return -1;
    }

    public static String getGateIP(String deviceName) throws IOException {
        Map<String, Device> deviceMap = parseConfigFile();
        Device device = deviceMap.get(deviceName);
        if (device != null) {
            return device.getGatewayIP();
        }
        return null;
    }

    public static String getVirtualIP(String deviceName) throws IOException {
        Map<String, Device> deviceMap = parseConfigFile();
        Device device = deviceMap.get(deviceName);
        if (device != null) {
            return device.getVirtualIP();
        }
        return null;
    }

    private static Map<String, Device> parseConfigFile() {
        Map<String, Device> deviceMap = new HashMap<>();
        List<String[]> links = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            String section = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                switch (line.toLowerCase()) {
                    case "hosts":
                    case "switches":
                    case "routers":
                    case "links":
                        section = line.toLowerCase();
                        continue;
                }

                switch (section) {
                    case "hosts" -> {
                        int port = Integer.parseInt(br.readLine().trim());
                        String realIP = br.readLine().trim();
                        String gatewayIP = br.readLine().trim();
                        String virtualIP = br.readLine().trim();
                        deviceMap.put(line, new Device(line, port, realIP, gatewayIP, virtualIP));

                    }
                    case "switches" -> {
                        int port = Integer.parseInt(br.readLine().trim());
                        String realIP = br.readLine().trim();
                        deviceMap.put(line, new Device(line, port, realIP));

                    }
                    case "routers" -> {
                        int port = Integer.parseInt(br.readLine().trim());
                        String realIP = br.readLine().trim();
                        Map<String, String> routerTable = new HashMap<>();

                        // Read each entry until you encounter a null or empty line
                        String entry = br.readLine().trim();
                        while (entry != null && entry.contains(":")) {
                            String[] tableEntry = entry.split(":");
                            if (tableEntry.length == 2) {
                                routerTable.put(tableEntry[0], tableEntry[1]);
                            }
                            entry = br.readLine();
                        }

                        deviceMap.put(line, new Device(line, port, realIP, routerTable));
                    }
                    case "links" -> {
                        String[] link = line.split(":");
                        if (link.length == 2) links.add(link);
                    }
                }
            }

            for (String[] link : links) {
                Device device1 = deviceMap.get(link[0]);
                Device device2 = deviceMap.get(link[1]);
                if (device1 != null && device2 != null) {
                    device1.addNeighbor(device2);
                    device2.addNeighbor(device1);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        }
        return deviceMap;
    }

    public static void main(String[] args) throws IOException {
        Parser.parseConfigFile();
        System.out.println(Parser.getVirtualIP("A"));
    }
}