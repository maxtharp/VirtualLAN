import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Port {
    private int portID;
    private String portIP;

    public Port(int portId, String portIP) {
        this.portID = portId;
        this.portIP = portIP;
    }

    public void forwardPacket(Packet packet, DatagramSocket receivingSocket) throws IOException {
        System.out.println("Sending packet to port " + portID + " (" + portIP + ") with destination MAC " + packet.getDestMac());

        // Convert to binary format
        byte[] srcMacBytes = packet.getSrcMac().getBytes(StandardCharsets.UTF_8);
        byte[] destMacBytes = packet.getDestMac().getBytes(StandardCharsets.UTF_8);
        byte[] messageBytes = packet.getMessage().getBytes(StandardCharsets.UTF_8);

        byte[] frame = new byte[srcMacBytes.length + destMacBytes.length + messageBytes.length];
        System.arraycopy(srcMacBytes, 0, frame, 0, srcMacBytes.length);
        System.arraycopy(destMacBytes, 0, frame, srcMacBytes.length, destMacBytes.length);
        System.arraycopy(messageBytes, 0, frame, srcMacBytes.length + destMacBytes.length, messageBytes.length);

        InetAddress destAddress = InetAddress.getByName(Parser.getIP(packet.getDestMac()));
        int destPort = Parser.getPort(packet.getDestMac());

        DatagramPacket request = new DatagramPacket(frame, frame.length, destAddress, destPort);

        try (DatagramSocket sendSocket = new DatagramSocket()) {
            sendSocket.send(request);
        }
    }
}
