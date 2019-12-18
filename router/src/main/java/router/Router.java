package router;

import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Router {
    private static final int BROKER_PORT;
    private static final int MARKET_PORT;

    /**
     * Static initializers
     * 
     * @param tNum
     * @param client
     * @param port
     * @throws Exception
     */

    static {
        BROKER_PORT = 5000;
        MARKET_PORT = 5001;
    }

    public void start() {
        Executor executor = Executors.newFixedThreadPool(5);

        Runnable brokerController = () -> {
            try {
                spawnClient(5, "market", MARKET_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable marketController = () -> {
            try {
                spawnClient(5, "broker", BROKER_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        executor.execute(brokerController);
        executor.execute(marketController);
    }

    public void spawnClient( int tNum, String client, int port ) throws Exception {
        Executor executor = Executors.newFixedThreadPool( tNum );

        System.out.println("The router server is running" );
        try(ServerSocket listener = new ServerSocket( port )) {
            while (true) {
                switch(client) {
                    case "broker" :
                        Broker broker = new Broker(listener.accept());
                        Runnable br = broker::run;
                        executor.execute( br );
                        break;
                    case "market" :
                        Market market = new Market(listener.accept());
                        Runnable mkt = market::run;
                        executor.execute( mkt );
                        break;

                }
            }
        }
    }
}