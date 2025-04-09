
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Port {
    private final int PORT_ID;
    private final String PORT_NAME;

    public Port(int portId, String portName) {
        this.PORT_ID = portId;
        this.PORT_NAME = portName;
    }

    // Send the packet from this port
    public void forwardPacket(Packet packet, DatagramSocket receivingSocket) throws IOException {
        System.out.println("Sending packet to port " + PORT_ID + ":" + PORT_NAME + " with destination MAC " + packet.destMac());

        String frame = packet.srcMac() + "`" + packet.destMac() + "`" +
                packet.message() + "`" + packet.srcIP() + "`" + packet.destIP() + "`" + packet.deviceType();

        DatagramPacket request = new DatagramPacket(
                frame.getBytes(),
                frame.length(),
                InetAddress.getByName(PORT_NAME),
                PORT_ID
        );
        receivingSocket.send(request);
    }

    public void forwardDvTable(String deviceMAC, Map<String, DistanceVector> dvTable, DatagramSocket receivingSocket, String deviceType) throws IOException {
        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        System.out.println("Sending distance vector to port " + PORT_ID + ":" + PORT_NAME);

        for (Map.Entry<String, DistanceVector> entry : dvTable.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue().nextHop() + ":" + entry.getValue().cost());
        }

        String frame = deviceMAC + "`#`" + keys + ";" + values + "`#`#`" + deviceType;

        DatagramPacket request = new DatagramPacket(
                frame.getBytes(),
                frame.length(),
                InetAddress.getByName(PORT_NAME),
                PORT_ID
        );
        receivingSocket.send(request);
    }
}