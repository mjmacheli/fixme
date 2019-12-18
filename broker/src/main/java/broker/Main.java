package broker;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

import static java.lang.Integer.parseInt;

public class Main {

    private static final int PORT;
    private static final String ADDRESS;

    String id;
    PrintWriter out;
    BufferedReader in;
    BrokerMessage brokerMessage;

    String[] stonks = { "GOLD", "SILVER", "PLAT"};

    JComboBox<String> stnks = new JComboBox<>(stonks);

    JFrame frame = new JFrame("Broker");
    JTextField txtIndex   = new JTextField(40);
    JTextField txtAmount = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 50);
    JButton btnBuy = new JButton("Buy");
    JButton btnSell = new JButton("Sell");

    static {
        ADDRESS = "127.0.0.1";
        PORT = 5000;
    }

    {
        brokerMessage = new BrokerMessage();

    }

    public static void main(String[] args) throws Exception {
        Main client = new Main();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

    String sign(String s) {
        return s + "13=" + s.length();
    }
    
    public Main() {

        txtIndex.setEditable(true);
        messageArea.setEditable(false);
        frame.getContentPane().add(stnks, "North");
        frame.getContentPane().add(txtAmount, "Center");
        frame.getContentPane().add(new JScrollPane(messageArea), "South");
        frame.getContentPane().add(btnBuy, "West");
        frame.getContentPane().add(btnSell, "East");
        frame.pack();

        btnBuy.addActionListener( e -> {
            String message ="";
            // System.out.println("item= "+( String )stnks.getSelectedItem());
            message = "49=" + this.id + "|"+ "COMMAND=BUY|" + "INDEX=" + ( String )stnks.getSelectedItem()  + "|" +  "AMOUNT=" + txtAmount.getText() + "|";
            message = sign(message);
            out.println(message);
        });

        btnSell.addActionListener( e -> {
            String message ="";

            message = "49=" + this.id + "|"+ "COMMAND=SELL|" + "INDEX=" + ( String )stnks.getSelectedItem() + "|" +  "AMOUNT=" + txtAmount.getText() + "|";
            message = sign(message);
            out.println(message);
        });
    }

    private void run() {
        try( Socket socket = new Socket(ADDRESS, PORT) ){
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
    
            while (true) {
                String line = in.readLine();
                if (line.startsWith("ID")) {
                    this.id = line.substring(3);
                    messageArea.append("ID: " + this.id + '\n');
                } else {
                    brokerMessage.destruct();
                    parseMsg(line);
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void parseMsg(String input) {
        String[] fields = input.split("\\|");

        for (String field : fields) {
            String[] data = field.split("=");
            if (data.length != 2) {
                throw new IllegalArgumentException("Invalid message");
            } else {
                // System.out.println("tst " + data[0]);
                switch (data[0]) {
                    case "49": 
                        brokerMessage.id = data[1];
                        break;
                    
                    case "COMMAND": 
                        brokerMessage.status = data[1];
                        break;
                    
                    case "STATUS": 
                        brokerMessage.status = data[1];
                        break;
                    
                    case "13": 
                        brokerMessage.checksum = data[1];
                        break;
                    
                    default:
                        throw new IllegalArgumentException("Invalid entry ");
                }
            }
        }

        /**Check if msg length is equals checksum w/out the checksm */
        int len = input.length() - ("13=" + brokerMessage.checksum).length();
        if (len != parseInt(brokerMessage.checksum)) {
            throw new IllegalArgumentException("checksum error");
        }
    }
}