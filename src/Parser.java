import java.io.*;
import java.util.*;

public class Parser {
    private static final String configFilePath = "src/config.txt"; // Path to your config.txt file

    public static List<String> getNeighborsIP(String inputName) {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getNeighborsIP();
        }

        return Collections.emptyList();
    }

    public static List<Integer> getNeighborsPort(String inputName) {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getNeighborsPort();
        }

        return Collections.emptyList();
    }

    public static int getPort(String inputName) {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getPort();
        }
        return -1;
    }

    public static int getExitPort(String inputName) {
        Map<String, Device> deviceMap = parseConfigFile();

        Device targetDevice = deviceMap.get(inputName);
        if (targetDevice != null) {
            return targetDevice.getExitPort();
        }
        return -1;
    }


    public static String getGateIP(String deviceName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device device = deviceMap.get(deviceName);
        if (device != null) {
            return device.getGatewayIP();
        }
        return null;
    }

    public static String getVirtualIP(String deviceName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device device = deviceMap.get(deviceName);
        if (device != null) {
            return device.getVirtualIP();
        }
        return null;
    }

    public static List<String> getConnectedNets(String deviceName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device device = deviceMap.get(deviceName);
        if (device != null) {
            return device.getConnectedNets();
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
                        int exitPort = Integer.parseInt(br.readLine().trim());
                        List<String> connectedNets = new ArrayList<>();

                        // Read each entry until you encounter a null or empty line
                        String entry = br.readLine().trim();
                        while (entry != null && entry.contains("net")) {
                            connectedNets.add(entry);
                            entry = br.readLine();
                        }

                        deviceMap.put(line, new Device(line, port, realIP, exitPort, connectedNets));
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

    public static void main(String[] args) {
        parseConfigFile();
    }
}