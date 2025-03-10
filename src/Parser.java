import java.io.*;
import java.util.*;

public class Parser {
    private static final String configFilePath = "src/config.txt";

    public static String getVirtualIP(String deviceName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device targetDevice = deviceMap.get(deviceName);
        return (targetDevice != null) ? targetDevice.getVirtualIP() : null;
    }

    public static String getGatewayIP(String deviceName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device targetDevice = deviceMap.get(deviceName);
        return (targetDevice != null) ? targetDevice.getGatewayIP() : null;
    }

    public static List<String> getNeighborsIP(String inputName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device targetDevice = deviceMap.get(inputName);
        return (targetDevice != null) ? targetDevice.getNeighborsIP() : Collections.emptyList();
    }

    public static List<Integer> getNeighborsPort(String inputName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device targetDevice = deviceMap.get(inputName);
        return (targetDevice != null) ? targetDevice.getNeighborsPort() : Collections.emptyList();
    }

    public static int getPort(String deviceName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device targetDevice = deviceMap.get(deviceName);
        return (targetDevice != null) ? targetDevice.getPort() : -1;
    }

    public static String getIP(String deviceName) {
        Map<String, Device> deviceMap = parseConfigFile();
        Device targetDevice = deviceMap.get(deviceName);
        return (targetDevice != null) ? targetDevice.getIP() : null;
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
                    case "devices": case "switches": case "routers": case "links":
                        section = line.toLowerCase();
                        continue;
                }

                if (section.equals("devices") || section.equals("routers")) {
                    String name = line;
                    String virtualIP = br.readLine().trim();
                    int port = Integer.parseInt(br.readLine().trim());
                    String realIP = br.readLine().trim();
                    String gatewayIP = br.readLine().trim();
                    deviceMap.put(name, new Device(name, virtualIP, realIP, port, gatewayIP));
                } else if (section.equals("switches")) {
                    String name = line;
                    int port = Integer.parseInt(br.readLine().trim());
                    String realIP = br.readLine().trim();
                    deviceMap.put(name, new Device(name, null, realIP, port, null));
                } else if (section.equals("links")) {
                    String[] link = line.split(":");
                    if (link.length == 2) links.add(link);
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
}
