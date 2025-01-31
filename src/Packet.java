public class Packet {
    private String srcMac;
    private String destMac;
    private String message;

    public Packet(String srcMac, String destMac, String message) {
        this.srcMac = srcMac;
        this.destMac = destMac;
        this.message = message;
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
}
