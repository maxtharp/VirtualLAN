import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
public class Host {
    public static void main(String[] args) throws Exception{
        int port = 3000;
        DatagramSocket receivingSocket = new DatagramSocket(port);
        DatagramPacket receivedFrame = new DatagramPacket(
                new byte[1024], 1024);
        while(true){
            receivingSocket.receive(receivedFrame);
            byte[] clientMessage = Arrays.copyOf(
                    receivedFrame.getData(),
                    receivedFrame.getLength()
            );
            InetAddress receivedIP = receivedFrame.getAddress();
            int receivedPort = receivedFrame.getPort();
            String messageString = new String(clientMessage);
            String acknowledgment = "Message received.\nPort Number: " + receivedPort + ".\nIP: " + receivedIP + ".\n Message Content: " + messageString;
            System.out.println(acknowledgment);
        }
    }
}