import java.util.ArrayList;
import java.util.List;

public class Device {
    private String name;
    private int port;
    private String ipAddress;
    private List<Device> neighbors;

    public Device(String name, int port, String ipAddress) {
        this.name = name;
        this.port = port;
        this.ipAddress = ipAddress;
        this.neighbors = new ArrayList<>();
    }

    public void addNeighbor(Device neighbor) {
        neighbors.add(neighbor);
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

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Device{name='" + name + "', port=" + port + ", ipAddress='" + ipAddress + "'}";
    }
}
