import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
public class Host {
    public static void main(String[] args) throws Exception{

        int port = 3000;
        DatagramSocket serverSocket = new DatagramSocket(port);
        DatagramPacket clientRequest = new DatagramPacket(
                new byte[1024], 1024);
        while(true){
            serverSocket.receive(clientRequest);
            byte[] clientMessage = Arrays.copyOf(
                    clientRequest.getData(),
                    clientRequest.getLength()
            );
            String replyMessage = new String(clientMessage).toUpperCase();
            InetAddress clientIP = clientRequest.getAddress();
            int clientPort = clientRequest.getPort();
            DatagramPacket serverReply = new DatagramPacket(
                    replyMessage.getBytes(),
                    replyMessage.getBytes().length,
                    clientIP,
                    clientPort
            );
            serverSocket.send(serverReply);
        }
    }
}