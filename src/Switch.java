import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Switch {
    private Map<String, Port> macTable;  // Map of MAC address to corresponding port
    private Map<Integer, Port> ports;  // Map of port IDs to Port objects

    public Switch() {
        macTable = new HashMap<>();
        ports = new HashMap<>();
    }

    public void addPort(int portID, String portName) {
        ports.put(portID, new Port(portID, portName));
    }

    // Handle incoming packet on a given port
    public void handleIncomingPacket(Packet packet, int portID) throws IOException {
        // Check if the MAC address is already in the table
        String destMac = packet.getDestMac();
        if (macTable.containsKey(destMac)) {
            Port foundPort = macTable.get(destMac);
            foundPort.forwardPacket(packet);
        } else {
            flood(portID, packet);
        }

        // Update the MAC address table with the port where the packet came from
        macTable.put(packet.getSrcMac(), ports.get(portID));
    }

    // Flood the packet to all other ports except the source port
    private void flood(int sourcePortId, Packet packet) throws IOException {
        System.out.println("Flooding packet to all ports except " + sourcePortId);

        // Send packet to all other ports
        for (Map.Entry<Integer, Port> entry : ports.entrySet()) {
            if (entry.getKey() != sourcePortId) {
                entry.getValue().forwardPacket(packet);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Switch aSwitch = new Switch();
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the source MAC: ");
        String inputMAC = keyboard.nextLine();

        while (inputMAC.contains(",")) {
            System.out.println("You can't have a ',' in your source MAC\nEnter the source MAC: ");
            inputMAC = keyboard.nextLine();
        }

        final int devicePort = Parser.getPort(inputMAC);

        for (int i = 0; i < Parser.getNeighborsIP(inputMAC).size(); i++){
            aSwitch.addPort(Parser.getNeighborsPort(inputMAC).get(i),
                    Parser.getNeighborsIP(inputMAC).get(i));
        }


        DatagramSocket receivingSocket = new DatagramSocket(devicePort);
        DatagramPacket receivedFrame = new DatagramPacket(new byte[1024], 1024);

        while (true) {
            receivingSocket.receive(receivedFrame);
            byte[] message = Arrays.copyOf(
                    receivedFrame.getData(),
                    receivedFrame.getLength());
            String[] messageData = new String(message).split(",");
            Packet packet = new Packet(
                    messageData[0],
                    messageData[1],
                    messageData[2]);

            System.out.println("Received packet on port " + devicePort + " with destination MAC " + packet.getDestMac());
            aSwitch.handleIncomingPacket(packet, receivedFrame.getPort());
        }
    }
}
