public class Packet {
    private String srcMac;
    private String destMac;
    private String message;
    private String srcIP;
    private String destIP;

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
