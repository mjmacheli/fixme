package router;

public class BrokerMessage {
    public String   id = null;
    public String   command = null;
    public String   index = null;
    public String   amount = null;
    public String   checksum = null;
    
    void destruct() {
        id = null;
        command = null;
        index = null;
        amount = null;
        checksum = null;
    }

    boolean isEmpty() {
        return ( id == null  || command == null  || index == null || amount == null || checksum == null);
    }

    @Override
    public String toString() {
        return ( id + " " + command  + " " +  index  + " " +  amount  + " " + checksum );
    }
}