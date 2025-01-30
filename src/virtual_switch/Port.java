package virtual_switch;

import virtual_switch.Packet;

import java.io.IOException;

public class Port {
    private int portID;

    public Port(int portId) {
        this.portID = portId;
    }

    // Send the packet from this port
    public void sendPacket(Packet packet) throws IOException {
        System.out.println("Sending packet to port " + portID + " with destination MAC " + packet.getDestMac());

    }
}