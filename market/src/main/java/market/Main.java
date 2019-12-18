package market;

import java.io.*;
import java.net.Socket;

public class Main {

    private static final int PORT;
    private static final String ADDRESS;

    String  id;
    BufferedReader in;
    PrintWriter out;
    BrokerMessage brokerMessage;
    Market market;         

    //Initializers
    static {
        PORT = 5001;
        ADDRESS = "127.0.0.1";

    }

    {
        brokerMessage = new BrokerMessage();
        market = new Market();
        /**Display initial Market indices */
        System.out.println(market.getMarket());
    }

    public static void main(String[] args) throws Exception {
        Main client = new Main();
       
        client.run();
    }

    boolean validate(String str) {
        return ( str + "13=" + str.length()).equalsIgnoreCase(brokerMessage.checksum);
    }

    private void run() throws IOException {
        Socket socket = new Socket(ADDRESS, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        String req = "";

        while (true) {
            String line = in.readLine();
            if (line.startsWith("ID")) {
                //update ui
                this.id = line.substring(3);
                // System.out.println("tst " + line + " " + this.id);
            } else {
                try {
                    parseMsg(line);
                    switch ( brokerMessage.command ) {
                        case "BUY":
                            req = market.buy(brokerMessage.index, Integer.parseInt(brokerMessage.amount));
                            System.out.println("BUY:" + req + "\n Indices \n" + market.getMarket());
                            break;
                        
                        case "SELL":
                            req = market.sell(brokerMessage.index, Integer.parseInt(brokerMessage.amount));
                            System.out.println("SELL:" + req + "\n Indices \n" + market.getMarket());
                            break;
                        default :
                            throw new IllegalArgumentException();
                    
                    }
                    String message = "49=" + brokerMessage.id + "|13="+ brokerMessage.checksum + "|STATUS=" + req;
                    if (validate(message)) out.println(validate(message));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void parseMsg(String line) {
        String[] fields = line.split("\\|");

        for (String field : fields) {
            String[] data = field.split("=");
            if (data.length != 2) {
                throw new IllegalArgumentException("Invalid message");
            } else {
                switch (data[0]) {
                    case "49" : 
                        brokerMessage.id = data[1];
                        break;
                    
                    case "COMMAND" : 
                        brokerMessage.command = data[1];
                        break;
                    
                    case "INDEX" : 
                        brokerMessage.index = data[1];
                        break;
                    
                    case "AMOUNT" : 
                        brokerMessage.amount = data[1];
                        break;
                    
                    case "13" : 
                        brokerMessage.checksum = data[1];
                        break;
                    
                    default:
                        throw new IllegalArgumentException("invalid info");
                }
            }
        }
        if (brokerMessage.isEmpty()) {
            throw new IllegalArgumentException("Invalid message");
        }
    }

}