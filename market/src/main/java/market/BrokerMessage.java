package market;

public class BrokerMessage {
    public String   id;
    public String   command;
    public String   index;
    public String   amount;
    public String   checksum;

    boolean isEmpty() {
        return ( id == null  || command == null  || index == null || amount == null || checksum == null);
    }

    @Override
    public String toString() {
        return ( id + " " + command  + " " +  index  + " " +  amount  + " " + checksum );
    }
}