package broker;

public class BrokerMessage {
    public String   id;
    public String   status;
    public String   checksum;

    void destruct() {
        id = null;
        status = null;
        checksum = null;
    }
}