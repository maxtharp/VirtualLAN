public class Packet {
    private final String srcMac;
    private final String destMac;
    private final String message;
    private final String srcIP;
    private final String destIP;

    public Packet(String srcMac, String destMac, String message, String srcIP, String destIP) {
        this.srcMac = srcMac;
        this.destMac = destMac;
        this.message = message;
        this.srcIP = srcIP;
        this.destIP = destIP;
    }
    public String getSrcMac() {
        return srcMac;
    }
    public String getDestMac() {
        return destMac;
    }
    public String getMessage() {
        return message;
    }
    public String getSrcIP() {
        return srcIP;
    }
    public String getDestIP() {
        return destIP;
    }
}
