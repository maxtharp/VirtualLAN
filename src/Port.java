
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Port {
    private final int portID;
    private final String portName;

    public Port(int portId, String portName) {
        this.portID = portId;
        this.portName = portName;
    }

    // Send the packet from this port
    public void forwardPacket(Packet packet, DatagramSocket receivingSocket) throws IOException {
        System.out.println("Sending packet to port " + portID + ":" + portName + " with destination MAC " + packet.destMac());

        String frame = packet.srcMac() + "`" + packet.destMac() + "`" +
                packet.message() + "`" + packet.srcIP() + "`" + packet.destIP() + "`" + packet.deviceType();

        DatagramPacket request = new DatagramPacket(
                frame.getBytes(),
                frame.length(),
                InetAddress.getByName(portName),
                portID
        );
        receivingSocket.send(request);
    }

    public void forwardDvTable(String deviceMAC, Map<String, DistanceVector> dvTable, DatagramSocket receivingSocket, String deviceType) throws IOException {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        System.out.println("Sending distance vector to port " + portID + ":" + portName);

        for (Map.Entry<String, DistanceVector> entry : dvTable.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue().nextHop() + ":" + entry.getValue().cost());
        }

        String frame = deviceMAC + "`#`" + keys + ";" + values + "`#`#`" + deviceType;

        DatagramPacket request = new DatagramPacket(
                frame.getBytes(),
                frame.length(),
                InetAddress.getByName(portName),
                portID
        );
        receivingSocket.send(request);
    }
}