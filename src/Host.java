import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Host {
    public static void main(String[] args) throws Exception{
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Enter the source MAC: ");
        String sourceMAC = keyboard.nextLine();

        System.out.println("Enter the destination MAC: ");
        String destinationMAC = keyboard.nextLine();

        System.out.println("Enter the text to send: ");
        String text = keyboard.nextLine();

        //use method from parser
        InetAddress serverIP;
        int serverPort;

        //creates full virtual packet w/ comma as a split
        String message = sourceMAC + "," + destinationMAC + "," + text;

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket request = new DatagramPacket(
                message.getBytes(),
                message.getBytes().length,
                serverIP,
                serverPort
        );
        socket.send(request);
        System.out.println("Message sent successfully!");
    }
}
