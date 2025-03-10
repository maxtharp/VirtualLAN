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

    public String getPortName(){
        return portName;
    }

    public int getPortID() {
        return portID;
    }

    // Send the packet from this port
    public void forwardPacket(Packet packet, DatagramSocket receivingSocket) throws IOException {
        System.out.println("Sending packet to port " + portID + ":" + portName + " with destination MAC " + packet.getDestMac());

        String frame = packet.getSrcMac() + "," + packet.getDestMac() + "," +
                packet.getMessage();

        DatagramPacket request = new DatagramPacket(
                frame.getBytes(),
                frame.length(),
                InetAddress.getByName(portName),
                portID
        );
        receivingSocket.send(request);
    }
}