package router;

import java.io.PrintWriter;
import java.util.HashMap;

public class Main {
    
    public static HashMap<String, PrintWriter> brokers;
    public static HashMap<String, PrintWriter> markets;

    static {
        brokers = new HashMap<>();
        markets = new HashMap<>();
    }
    
    public static void main( String[] args ) {
        Router router = new Router();
        router.start();
    }    
}