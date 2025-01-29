import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
public class Host {
    public static void main(String[] args) throws Exception{
        int port = 3000;
        DatagramSocket receivingSocket = new DatagramSocket(port);
        DatagramPacket receivedFrame = new DatagramPacket(
                new byte[1024], 1024);
        while(true){
            receivingSocket.receive(receivedFrame);
            byte[] Message = Arrays.copyOf(
                    receivedFrame.getData(),
                    receivedFrame.getLength()
            );
            String[] messageData = new String(Message).split(",");
            String acknowledgment = "Message received.\nMAC Address of sender: " + messageData[0] + ".\n Message Content: " + messageData[2];
            System.out.println(acknowledgment);
        }
    }
}