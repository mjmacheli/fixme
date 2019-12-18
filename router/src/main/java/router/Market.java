package router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

class Market {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String id;
    MarketMessage marketMessage;

    {
        marketMessage = new MarketMessage();
        id = String.valueOf(new Random().nextInt(42_000));
    }

    public Market(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("ID " + id);

            Main.markets.put(id, out);

            while ( true ) {
                String line = in.readLine();
                marketMessage.destruct();
               
                try {
                    parseMsg(line);
                    for (Map.Entry broker: Main.brokers.entrySet()) {
                        System.out.println(broker.getKey() + " " + marketMessage.id);
                        if (broker.getKey().equals(marketMessage.id)) {
                            String message = "STATUS=" + marketMessage.order_status + "|";
                            ((PrintWriter)broker.getValue()).println(message + "13=" + message.length());
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("invalid message");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            if (out != null) {
                Main.brokers.remove(id);
            }
            try {
                socket.close();
            } catch (IOException ignored) {
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
                    case "49": 
                        marketMessage.id = data[1];
                        break;
                    
                    case "STATUS": 
                        marketMessage.order_status = data[1];
                        break;
                    
                    case "13":
                        marketMessage.checksum = data[1];
                        break;
                    
                    default:
                        throw new IllegalArgumentException("invalid mesage");
                }
            }
        }
        if (marketMessage.isEmpty()) {
            throw new IllegalArgumentException("Invalid message");
        }
        int length = line.length() - ("13=" + marketMessage.checksum).length();
        if (length != Integer.parseInt(marketMessage.checksum)) {
            throw new IllegalArgumentException("Invalid message" );
        }
    }
}