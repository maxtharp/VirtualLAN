import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Device {
    private final String name;
    private final int port;
    private final String ipAddress;
    private String gatewayIP;
    private String virtualIP;
    private final List<Device> neighbors;
    private List<String> connectedNets;

    Device(String name, int port, String ipAddress, String gatewayIP, String virtualIP) {
        this.name = name;
        this.port = port;
        this.ipAddress = ipAddress;
        this.gatewayIP = gatewayIP;
        this.virtualIP = virtualIP;
        this.neighbors = new ArrayList<>();
    }

    Device (String name, int port, String ipAddress, List<String> connectedNets) {
        this.name = name;
        this.port = port;
        this.ipAddress = ipAddress;
        this.connectedNets = connectedNets;
        this.neighbors = new ArrayList<>();
    }
    Device (String name, int port, String ipAddress) {
        this.name = name;
        this.port = port;
        this.ipAddress = ipAddress;
        this.neighbors = new ArrayList<>();
    }

    public void addNeighbor(Device neighbor) {
        neighbors.add(neighbor);
    }

    public List<String> getConnectedNets() {
        return connectedNets;
    }

    public List<String> getNeighborsIP() {
        List<String> neighborIPs = new ArrayList<>();
        for (Device neighbor : neighbors) {
            neighborIPs.add(neighbor.ipAddress);
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

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getGatewayIP() {
        return gatewayIP;
    }

    public String getVirtualIP() {
        return virtualIP;
    }

    @Override
    public String toString() {
        return "Device{name='" + name + "', port=" + port + ", ipAddress='" + ipAddress + "'}";
    }
}