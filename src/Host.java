import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Host {
    public static void main(String[] args) throws Exception {

        Scanner keyboard = new Scanner(System.in);

        // allows for the source MAC to be initialized
        final String sourceMAC = args[0];

        System.out.println(sourceMAC);

        final int devicePort = Parser.getPort(sourceMAC);

        DatagramSocket socket = new DatagramSocket(devicePort); // sending socket

        List<String> stringDestinationIPs = Parser.getNeighborsIP(sourceMAC);
        List<Integer> destinationPorts = Parser.getNeighborsPort(sourceMAC);

        // get the 1st IP (should only be one for project 1)
        InetAddress destinationIP = InetAddress.getByName(stringDestinationIPs.get(0));

        // get the 1st port (should only be one for project 1)
        int destinationPort = destinationPorts.get(0);

        // sender thread
        Thread senderThread = new Thread(() -> {
            try {
                sendMessages(keyboard, socket, sourceMAC, destinationIP, destinationPort);
            } catch (Exception e) {
                System.err.println("Error in sender thread: " + e.getMessage());
            }
        });

        // receiver thread
        Thread receiverThread = new Thread(() -> {
            try {
                receiveMessages(sourceMAC, socket);
            } catch (Exception e) {
                System.err.println("Error in receiver thread: " + e.getMessage());
            }
        });

        senderThread.start();
        receiverThread.start();
    }

    private static void sendMessages(Scanner keyboard, DatagramSocket socket, String sourceMAC, InetAddress destinationIP, int destinationPort) throws Exception {
        while (true) {

            System.out.println("Enter the text to send: ");
            String text = keyboard.nextLine();

            // making sure the input doesn't have a comma
            while (text.contains(",")) {
                System.out.println("You can't have a ',' in your text\nEnter the text to send: ");
                text = keyboard.nextLine();
            }

            System.out.println("Enter the destination MAC: ");
            String destinationMAC = keyboard.nextLine();

            // making sure the input doesn't have a comma
            while (destinationMAC.contains(",")) {
                System.out.println("You can't have a ',' in your destination MAC\nEnter the destination MAC: ");
                destinationMAC = keyboard.nextLine();
            }

            // create message
            String message = sourceMAC + "," + destinationMAC + "," + text;

            // send message
            DatagramPacket request = new DatagramPacket(
                    message.getBytes(),
                    message.length(),
                    destinationIP,
                    destinationPort
            );

            socket.send(request);
            System.out.println("Message sent successfully!");
        }
    }

    private static void receiveMessages(String MAC, DatagramSocket socket) throws Exception {

        while (true) {
            DatagramPacket receivedFrame = new DatagramPacket(new byte[1024], 1024);
            socket.receive(receivedFrame);

            byte[] receivedMessage = Arrays.copyOf(receivedFrame.getData(), receivedFrame.getLength());
            String[] messageData = new String(receivedMessage).split(",");

            if (messageData.length == 3) {
                String acknowledgment = "Message received.\nMAC Address of sender: " + messageData[0] + ".\nMessage Content: " + messageData[2];
                System.out.println(acknowledgment);
            }
        }
    }
}
