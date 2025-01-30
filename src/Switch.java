import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Switch {
    private Map<String, Integer> macTable;
    private static final int NUM_PORTS = 4;

    public Switch() {
        macTable = new HashMap<>();
    }

    public void receiveFrame(String srcMac, String destMac, int inputPort) {
        System.out.println("Received frame from " + srcMac + " to " + destMac + " on port " + inputPort);
        macTable.put(srcMac, inputPort);

        Integer destPort = macTable.get(destMac);

        if (destPort != null) {
            if (destPort != inputPort) {
                System.out.println("Forwarding frame to port " + destPort);
            } else {
                System.out.println("Frame is already on the correct port, no forwarding needed.");
            }
        } else {
            // Flood the frame to all ports except the input port
            System.out.println("Destination MAC not found. Flooding frame to all other ports...");
            floodFrame(inputPort, srcMac, destMac);
        }
    }

    private void floodFrame(int inputPort, String srcMac, String destMac) {
        for (int port = 1; port <= NUM_PORTS; port++) {
            if (port != inputPort) {
                System.out.println("Flooding frame to port " + port);
            }
        }
    }

    public void printMacTable() {
        System.out.println("Current MAC Table:");
        for (Map.Entry<String, Integer> entry : macTable.entrySet()) {
            System.out.println("MAC: " + entry.getKey() + " -> Port: " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        //testing
        Parser parser = new Parser();

        String deviceName = "A"; // Example device

        List<String> neighborIPs = parser.getNeighborsIP(deviceName);
        List<Integer> neighborPorts = parser.getNeighborsPort(deviceName);

        if (!neighborIPs.isEmpty()) {
            System.out.println("Neighbors' IPs of " + deviceName + ": " + neighborIPs);
        } else {
            System.out.println("No neighbors found for " + deviceName);
        }

        if (!neighborPorts.isEmpty()) {
            System.out.println("Neighbors' Ports of " + deviceName + ": " + neighborPorts);
        } else {
            System.out.println("No neighbors found for " + deviceName);
        }
        //end testing

        Switch switch1 = new Switch();
    }
}