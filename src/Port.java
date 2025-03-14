import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Port {
    private final int portID;
    private final String portName;

    public Port(int portId, String portName) {
        this.portID = portId;
        this.portName = portName;
    }

    // Send the packet from this port
    public void forwardPacket(Packet packet, DatagramSocket receivingSocket) throws IOException {
        System.out.println("Sending packet to port " + portID + ":" + portName + " with destination MAC " + packet.getDestMac());

        String frame = packet.getSrcMac() + "," + packet.getDestMac() + "," +
                packet.getMessage() + "," + packet.getSrcIP() + "," + packet.getDestIP();

        DatagramPacket request = new DatagramPacket(
                frame.getBytes(),
                frame.length(),
                InetAddress.getByName(portName),
                portID
        );
        receivingSocket.send(request);
    }
}