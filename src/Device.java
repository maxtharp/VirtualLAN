import java.util.ArrayList;
import java.util.List;

public class Device {
    private String name;
    private String virtualIP;
    private String realIP;
    private int port;
    private String gatewayIP;
    private List<Device> neighbors;

    public Device(String name, String virtualIP, String realIP, int port, String gatewayIP) {
        this.name = name;
        this.virtualIP = virtualIP;
        this.realIP = realIP;
        this.port = port;
        this.gatewayIP = gatewayIP;
        this.neighbors = new ArrayList<>();
    }

    public void addNeighbor(Device neighbor) {
        neighbors.add(neighbor);
    }

    public List<String> getNeighborsIP() {
        List<String> neighborIPs = new ArrayList<>();
        for (Device neighbor : neighbors) {
            neighborIPs.add(neighbor.realIP);
        }
        return neighborIPs;
    }

    public List<Integer> getNeighborsPort() {
        List<Integer> neighborPorts = new ArrayList<>();
        for (Device neighbor : neighbors) {
            neighborPorts.add(neighbor.port);
        }
        return neighborPorts;
    }

    public String getVirtualIP() {
        return virtualIP;
    }

    public String getGatewayIP() {
        return gatewayIP;
    }

    public int getPort() {
        return port;
    }

    public String getIP() {
        return realIP;
    }

    @Override
    public String toString() {
        return "Device{name='" + name + "', virtualIP='" + virtualIP + "', realIP='" + realIP + "', port=" + port + ", gatewayIP='" + gatewayIP + "'}";
    }
}

