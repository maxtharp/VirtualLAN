import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Switch {
    private final Map<String, Port> MAC_TABLE;  // Map of MAC address to corresponding port
    private final Map<Integer, Port> PORTS;  // Map of port IDs to Port objects

    public Switch() {
        MAC_TABLE = new HashMap<>();
        PORTS = new HashMap<>();
    }

    public void addPort(int portID, String portIP) {
        PORTS.put(portID, new Port(portID, portIP));
    }

    // Handle incoming packet on a given port
    public void handleIncomingPacket(Packet packet, int portID, DatagramSocket receivingSocket) throws IOException {
        Port sourcePort = PORTS.get(portID);
        if (sourcePort == null) {
            System.err.println("Unknown source port: " + portID);
            return;
        }

        String destMac = packet.destMac();
        MAC_TABLE.put(packet.srcMac(), sourcePort);  // Update MAC table

        Port destinationPort = MAC_TABLE.get(destMac);
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
        for (Map.Entry<Integer, Port> entry : PORTS.entrySet()) {
            if (entry.getKey() != sourcePortId) {
                entry.getValue().forwardPacket(packet, receivingSocket);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Switch aSwitch = new Switch();
        String deviceMAC = args[0];
        System.out.println(deviceMAC);
        System.out.println(Parser.getPort(deviceMAC));
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

            String[] messageData = new String(message).split("`");
            Packet packet = new Packet(messageData[0],messageData[1],messageData[2], messageData[3], messageData[4], messageData[5]);

            System.out.println("Received packet with destination MAC " + packet.destMac());
            aSwitch.handleIncomingPacket(packet, receivedFrame.getPort(), receivingSocket);
        }
    }
}
