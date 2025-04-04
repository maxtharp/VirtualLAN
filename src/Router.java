import java.net.*;
import java.io.*;
import java.util.*;

public class Router{
    private final Map<Integer, Port> PORTS;  // Map of port IDs to Port objects
    private final Map<String, DistanceVector> DV_TABLE;
    private final int DEFAULT_COST = 1;
    private final String DEVICE_TYPE = "R";
    private final List<String> HAVE_RECEIVED_FROM;

    public Router() {
        DV_TABLE = new HashMap<>();
        PORTS = new HashMap<>();
        HAVE_RECEIVED_FROM = new ArrayList<>();
    }

    private void buildTable(String deviceMAC) {
        for (String net : Objects.requireNonNull(Parser.getConnectedNets(deviceMAC))) {
            DV_TABLE.put(net, new DistanceVector(deviceMAC, DEFAULT_COST));
        }
    }

    private void updateTable(Map<String, DistanceVector> receivedTable,
                             String deviceMAC, Router router,
                             DatagramSocket receivingSocket) throws IOException {
        List<String> connectedNets = Parser.getConnectedNets(deviceMAC);
        boolean tableUpdated = false;

        for (Map.Entry<String, DistanceVector> entry : receivedTable.entrySet()) {
            int plusCost = entry.getValue().cost() + DEFAULT_COST;
            DistanceVector dvReplacement = new DistanceVector(entry.getValue().nextHop(), plusCost);

            assert connectedNets != null;
            for (String net : connectedNets) {
                /* For each connected subnet, check if there is an uncommon key and add it
                 to DV_TABLE + check if there is a common key between tables
                + check the cost to determine if it should be used to update DV_TABLE */
                if (Objects.equals(entry.getKey(), net)
                        && plusCost < DV_TABLE.get(net).cost()) {

                    DV_TABLE.replace(net, dvReplacement);
                    tableUpdated = true;
                }
                else if (!DV_TABLE.containsKey(entry.getKey())) {
                    DV_TABLE.put(entry.getKey(), dvReplacement);
                    tableUpdated = true;
                }
            }
        }
        if (tableUpdated) {
            router.sendDvTable(deviceMAC, DV_TABLE, receivingSocket);
        }
    }

    private void sendDvTable(String deviceMAC, Map<String, DistanceVector> dvTable, DatagramSocket socket) throws IOException {
        for (Map.Entry<Integer, Port> entry : PORTS.entrySet()) {
            entry.getValue().forwardDvTable(deviceMAC, dvTable, socket, DEVICE_TYPE);
        }
    }

    private void addPort(int portID, String portIP) {
        PORTS.put(portID, new Port(portID, portIP));
    }

    // Handle incoming packet on a given port
    private void handleIncomingPacket(Packet packet, int portID, DatagramSocket receivingSocket) throws IOException {
        Port sourcePort = PORTS.get(portID);

        if (sourcePort == null) {
            System.err.println("Unknown source port: " + portID);
            return;
        }

        String destIP = packet.destIP();
        String sourceIP = packet.srcIP();

        //find destination port based on IP routing table (change if net1 ignore)
        String destinationSubnet = getSubnetFromIp(destIP);
        String sourceSubnet = getSubnetFromIp(sourceIP);

        if (!(destinationSubnet.equals(sourceSubnet))){
            Port destinationPort = PORTS.get(Parser.getPort(String.valueOf(DV_TABLE.get(destinationSubnet))));

            if (destinationPort != null ) {
                destinationPort.forwardPacket(packet, receivingSocket);
            } else {
                System.out.println("Destination subnet not found. Dropping packet.");
            }
        } else {
            System.out.println("Packet ignored");
        }
    }
    private String getSubnetFromIp(String ipAddress) {
        String[] splitVirtualSourceIP = ipAddress.split("\\.");
        return splitVirtualSourceIP[0];
    }
    private String getMacFromIp(String ipAddress) {
        String[] splitVirtualSourceIP = ipAddress.split("\\.");
        return splitVirtualSourceIP[1];
    }

    public static void main(String[] args) throws IOException {
        String deviceMAC = args[0];
        Router router = new Router();
        int devicePort = Parser.getPort(deviceMAC);

        System.out.println(deviceMAC);

        for (int i = 0; i < Parser.getNeighborsPort(deviceMAC).size(); i++) {
            router.addPort(Parser.getNeighborsPort(deviceMAC).get(i),
                    Parser.getNeighborsIP(deviceMAC).get(i));
        }

        DatagramSocket receivingSocket = new DatagramSocket(devicePort);
        DatagramPacket receivedFrame = new DatagramPacket(new byte[1024], 1024);

        router.buildTable(deviceMAC);
        router.sendDvTable(deviceMAC, router.DV_TABLE, receivingSocket);

        while (true) {
            receivingSocket.receive(receivedFrame);
            Packet packet = getPacket(receivedFrame, router, deviceMAC, receivingSocket);

            System.out.println("Received packet with destination IP " + packet.destIP());
            router.handleIncomingPacket(packet, receivedFrame.getPort(), receivingSocket);
        }
    }

    private static Packet getPacket(DatagramPacket receivedFrame,
                                    Router router, String deviceMAC,
                                    DatagramSocket receivingSocket) throws IOException {

        byte[] message = Arrays.copyOf(
                receivedFrame.getData(),
                receivedFrame.getLength());

        String[] messageData = new String(message).split("`");
        if (messageData[5].equals(router.DEVICE_TYPE)) {
            System.out.println("Received DV table from " + messageData[0]);
            for(String device : router.HAVE_RECEIVED_FROM) {
                if (!device.equals(messageData[0])) {
                    router.PORTS.get(Parser.getPort(messageData[0])).forwardDvTable
                            (deviceMAC,
                            router.DV_TABLE,
                            receivingSocket,
                            router.DEVICE_TYPE);

                    router.HAVE_RECEIVED_FROM.add(messageData[0]);
                }
            }
            Map<String, DistanceVector> receivedDvTable = router.DeserializeDvTable(messageData[3]);
            router.updateTable(receivedDvTable, deviceMAC, router, receivingSocket);
        }
        return new Packet(deviceMAC,
                router.getMacFromIp(messageData[4]),
                messageData[2],
                messageData[3],
                messageData[4],
                router.DEVICE_TYPE);
    }

        private Map<String, DistanceVector> DeserializeDvTable(String input) {
            String[] parts = input.split(";");
            String[] subnets = parts[0].replaceAll("[\\[\\]]", "").split(",\\s*");
            String[] nextHopsWithCost = parts[1].replaceAll("[\\[\\]]", "").split(",\\s*");
            Map<String, DistanceVector> map = new HashMap<>();

            for (int i = 0; i < subnets.length; i++) {
                String subnet = subnets[i];
                String[] nextHopCost = nextHopsWithCost[i].split(":");
                DistanceVector distanceVector = new DistanceVector(nextHopCost[0], Integer.parseInt(nextHopCost[1]));

                map.put(subnet, distanceVector);
            }
            return map;
        }
    }
