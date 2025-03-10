import java.net.*;
import java.io.*;
import java.util.*;

public class Router {
    private Map<String, Port> ipRoutingTable;  // Map of destination subnet to corresponding port
    private Map<Integer, Port> ports;  // Map of port IDs to Port objects

    public Router() {
        ipRoutingTable = new HashMap<>();
        ports = new HashMap<>();
    }

    public void addPort(int portID, String portIP) {
        ports.put(portID, new Port(portID, portIP));
    }

    public void buildRouterTable(String subnet, Port port) {
        ipRoutingTable.put(subnet, port);
    }

    // Handle incoming packet on a given port
    public void handleIncomingPacket(Packet packet, int portID, DatagramSocket receivingSocket) throws IOException {
        Port sourcePort = ports.get(portID);

        if (sourcePort == null) {
            System.err.println("Unknown source port: " + portID);
            return;
        }

        String destIP = packet.getDestIP();

        // Find destination port based on IP routing table
        String destinationSubnet = getSubnetFromIp(destIP);
        Port destinationPort = ipRoutingTable.get(destinationSubnet);

        if (destinationPort != null ) {
            // Forward packet based on destination subnet
            destinationPort.forwardPacket(packet, receivingSocket);
        } else {
            // If the destination subnet is not found, drop the packet or perform another action
            System.out.println("Destination subnet not found. Dropping packet.");
        }
    }

    // Get subnet from IP address (simplified for illustration)
    private String getSubnetFromIp(String ipAddress) {
        String[] splitVirtualSourceIP = ipAddress.split("\\.");
        return splitVirtualSourceIP[0];
    }

    public static void main(String[] args) throws IOException {
        Router router = new Router();
        String deviceMAC = args[0];
        int devicePort = Parser.getPort(deviceMAC);

        System.out.println(deviceMAC);


        for (int i = 0; i < Parser.getNeighborsPort(deviceMAC).size(); i++) {
            router.addPort(Parser.getNeighborsPort(deviceMAC).get(i),
                    Parser.getNeighborsIP(deviceMAC).get(i));
        }

        for (int i = 0; i < Parser.getSubnets(deviceMAC).size(); i++) {
            //router.buildRouterTable(Parser.getSubnets(deviceMAC).get(i), Parser.getNextHop(deviceMAC).get(i));
        }

        DatagramSocket receivingSocket = new DatagramSocket(devicePort);
        DatagramPacket receivedFrame = new DatagramPacket(new byte[1024], 1024);

        while (true) {
            receivingSocket.receive(receivedFrame);
            byte[] message = Arrays.copyOf(
                    receivedFrame.getData(),
                    receivedFrame.getLength());

            String[] messageData = new String(message).split(",");
            Packet packet = new Packet(messageData[0], messageData[1], messageData[2], messageData[3], messageData[4]);

            System.out.println("Received packet with destination IP " + packet.getDestIP());
            router.handleIncomingPacket(packet, receivedFrame.getPort(), receivingSocket);
        }
    }
}
