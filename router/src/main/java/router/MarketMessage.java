package router;

public class MarketMessage {
    String  id = null;
    String  order_status = null;
    String  checksum = null;

    
    void destruct() {
        id = null;
        order_status = null;
        checksum = null;
    }
    
    boolean isEmpty() {
        return ( id == null || order_status == null || checksum == null);
    }
}