import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
public class Host {
    public static void main(String[] args) throws Exception{
        String ID = args[0];
        int port = 3000;
        DatagramSocket receivingSocket = new DatagramSocket(port);
        DatagramPacket receivedFrame = new DatagramPacket(
                new byte[1024], 1024);
        while(true){
            receivingSocket.receive(receivedFrame);
            byte[] message = Arrays.copyOf(
                    receivedFrame.getData(),
                    receivedFrame.getLength()
            );
            String[] messageData = new String(message).split(",");
            if(ID.equals(messageData[1])){
                String acknowledgment = "Message received.\nMAC Address of sender: " + messageData[0] + ".\n Message Content: " + messageData[2];
                System.out.println(acknowledgment);
            }
        }
    }
}