import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Port {
    private int portID;
    private String portName;

    public Port(int portId, String portName) {
        this.portID = portId;
        this.portName = portName;
    }

    // Send the packet from this port
    public void forwardPacket(Packet packet) throws IOException {
        System.out.println("Sending packet to port " + portID + ":" + portName + " with destination MAC " + packet.getDestMac());

        DatagramSocket sendingSocket = new DatagramSocket(portID);

        String frame = packet.getSrcMac() + "," + packet.getDestMac() + "," +
                packet.getMessage();

        DatagramPacket request = new DatagramPacket(
                frame.getBytes(),
                frame.length(),
                InetAddress.getByName(Parser.getIP(packet.getDestMac())),
                portID
        );
        sendingSocket.send(request);
    }
}