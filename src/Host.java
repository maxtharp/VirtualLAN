import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Host {
    public static void main(String[] args) throws Exception{
        Scanner keyboard = new Scanner(System.in);

        while(true) {
            System.out.println("Enter the source MAC: ");
            String sourceMAC = keyboard.nextLine();

            while(sourceMAC.contains(",")){
                System.out.println("You can't have a ',' in your source MAC\nEnter the source MAC: ");
                sourceMAC = keyboard.nextLine();
            }

            System.out.println("Enter the text to send: ");
            String text = keyboard.nextLine();

            while(text.contains(",")){
                System.out.println("You can't have a ',' in your text\nEnter the text to send: ");
                text = keyboard.nextLine();
            }

            System.out.println("Enter the destination MAC: ");
            String destinationMAC = keyboard.nextLine();

            while(destinationMAC.contains(",")){
                System.out.println("You can't have a ',' in your destination MAC\nEnter the destination MAC: ");
                destinationMAC = keyboard.nextLine();
            }

            //use method from parser to get IP and serverPort
            InetAddress serverIP = null;
            int serverPort = 3000;

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
}
