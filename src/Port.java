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
        System.out.println("Sending packet to port " + portName + " with destination MAC " + packet.getDestMac());

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket request = new DatagramPacket(
                packet.getMessage().getBytes(),
                packet.getMessage().getBytes().length,
                InetAddress.getByName(packet.getDestMac()),
                portID
        );
        socket.send(request);
        socket.close();
    }
}