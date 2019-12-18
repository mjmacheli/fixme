package router;

import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

class Broker {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String id;
    private BrokerMessage brokerMessage;

    {
        brokerMessage = new BrokerMessage();
        id = String.valueOf(new Random().nextInt(42_000));
    }

    public Broker(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        /**
         * No Try with resources coz thread spawned from main thread
         */

        try {
            in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("ID: " + id);

            Main.brokers.put(id, out);
            
            // Awaits for transactions
            while ( true ) {
                String line = in.readLine();
                brokerMessage.destruct();
                try {
                    parseMsg(line);
                    for (Map.Entry market : Main.markets.entrySet()) {
                        ((PrintWriter)market.getValue()).println(line);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invslid message" );
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            if (out != null) {
                Main.brokers.remove(id);
            }

            /** We can't add throws on run coz it is extended */
            try {
                socket.close();
            }
            catch (IOException ee) {
                System.out.println(ee.getMessage());
            }
        }
    }

    private void parseMsg(String msg) {
        String[] fields = msg.split("\\|");

        for (String field : fields) {
            String[] data = field.split("=");
            if (data.length != 2) {
                throw new IllegalArgumentException("Invalid message");
            } else {
                switch (data[0]) {
                    case ("49"): {
                        brokerMessage.id = data[1];
                        break;
                    }
                    case ("COMMAND"): {
                        brokerMessage.command = data[1];
                        break;
                    }
                    case ("INDEX"): {
                        brokerMessage.index = data[1];
                        break;
                    }
                    case ("AMOUNT"): {
                        brokerMessage.amount = data[1];
                        break;
                    }
                    case ("13"): {
                        brokerMessage.checksum = data[1];
                        break;
                    }
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
        if (brokerMessage.isEmpty()) {
            throw new IllegalArgumentException("Invalid message");
        } else {
            //display message
            System.out.println(brokerMessage.toString());
        }
        int len = msg.length() - ("13=" + brokerMessage.checksum).length();
        if (len != Integer.parseInt(brokerMessage.checksum)) {
            throw new IllegalArgumentException("Checksum Error" );
        }
    }
}