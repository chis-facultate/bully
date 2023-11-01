import java.io.*;
import java.net.Socket;
import java.util.Collection;

public class SendMessageFromKeyboardThread extends Thread {
    @Override
    public void run() {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        String line = "";
        while (!line.equals("bye")) {
            try {
                System.out.print("> ");
                line = consoleReader.readLine();

                Collection<Integer> keys = Main.processesIPs.keySet();
                for (Integer key : keys) {
                    sendMessage(line, key);
                }
            } catch (IOException i) {
                System.out.println(i);
            }
        }

        try {
            consoleReader.close();
            System.out.println("Thread trimitere msg tastatura oprit");
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    private void sendMessage(String message, int remote_node_id) {
        System.out.println("Sending message to " + remote_node_id);

        String remote_node_address = (String) Main.processesIPs.get(remote_node_id);
        int remote_node_port = (int) Main.processesPorts.get(remote_node_id);

        try {
            Socket socket = new Socket(remote_node_address, remote_node_port);
            System.out.println("Connection established with " + remote_node_id);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("message");
            out.writeUTF(Main.self_id + "");
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Eroare trimitere mesaj");
        }
    }
}
