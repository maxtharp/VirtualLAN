import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Switch {
    private Map<String, Port> macTable;  // Map of MAC address to corresponding port
    private Map<Integer, Port> ports;  // Map of port IDs to Port objects

    public Switch() {
        macTable = new HashMap<>();
        ports = new HashMap<>();
    }

    public void addPort(int portID, String portIP) {
        ports.put(portID, new Port(portID, portIP));
    }

    // Handle incoming packet on a given port
    public void handleIncomingPacket(Packet packet, int portID, DatagramSocket receivingSocket) throws IOException {
        Port sourcePort = ports.get(portID);
        if (sourcePort == null) {
            System.err.println("Unknown source port: " + portID);
            return;
        }

        String destMac = packet.getDestMac();
        macTable.put(packet.getSrcMac(), sourcePort);  // Update MAC table

        Port destinationPort = macTable.get(destMac);
        if (destinationPort != null) {
            destinationPort.forwardPacket(packet, receivingSocket);
        } else {
            flood(portID, packet, receivingSocket);
        }
    }

    // Flood the packet to all other ports except the source port
    private void flood(int sourcePortId, Packet packet, DatagramSocket receivingSocket) throws IOException {
        System.out.println("Flooding packet to all ports except " + sourcePortId);

        // Send packet to all other ports
        for (Map.Entry<Integer, Port> entry : ports.entrySet()) {
            if (entry.getKey() != sourcePortId) {
                entry.getValue().forwardPacket(packet, receivingSocket);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Switch aSwitch = new Switch();
        String deviceMAC = args[0];
        System.out.println(deviceMAC);
        final int devicePort = Parser.getPort(deviceMAC);

        for (int i = 0; i < Parser.getNeighborsPort(deviceMAC).size(); i++){
            aSwitch.addPort(Parser.getNeighborsPort(deviceMAC).get(i),
                    Parser.getNeighborsIP(deviceMAC).get(i));
        }

        DatagramSocket receivingSocket = new DatagramSocket(devicePort);
        DatagramPacket receivedFrame = new DatagramPacket(new byte[1024], 1024);

        while (true) {
            receivingSocket.receive(receivedFrame);
            byte[] message = Arrays.copyOf(
                    receivedFrame.getData(),
                    receivedFrame.getLength());

            String[] messageData = new String(message).split(",");
            Packet packet = new Packet(messageData[0],messageData[1],messageData[2]);

            System.out.println("Received packet with destination MAC " + packet.getDestMac());
            aSwitch.handleIncomingPacket(packet, receivedFrame.getPort(), receivingSocket);
        }
    }
}
