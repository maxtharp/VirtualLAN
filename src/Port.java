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
    public void forwardPacket(Packet packet, DatagramSocket receivingSocket) throws IOException {
        System.out.println("Sending packet to port " + portID + ":" + portName + " with destination MAC " + packet.getDestMac());

        DatagramPacket request = new DatagramPacket(
                packet.getMessage().getBytes(),
                packet.getMessage().getBytes().length,
                //InetAddress.getByName(Parser.getIP(packet.getDestMac())),
                InetAddress.getByName("localhost"),
                portID
        );
        receivingSocket.send(request);
        receivingSocket.close();
    }
}