import java.io.DataOutputStream;
import java.net.Socket;

public class HeartbeatSender extends Thread {

    @Override
    public void run() {
        while (true) {
            sendHeartbeat(Main.leader_id);

            try {
                Thread.sleep(Main.heartbeatInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendHeartbeat(int remote_node_id) {
        System.out.println("Sending heartbeat to " + remote_node_id);

        try {
            String remote_node_address = (String) Main.processesIPs.get(remote_node_id);
            int remote_node_port = (int) Main.processesPorts.get(remote_node_id);

            Socket socket = new Socket(remote_node_address, remote_node_port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("heartbeat");
            out.writeUTF(Main.self_id + "");
        } catch (Exception e) {
            // Daca stabilirea conexiunii cu liderul esueaza
            // Se incepe procesul de electie

            Main.leader_flag = false;

            System.out.println("*** Leader has FAILED!");
            Main.printFlags();

            new Sender("election request").start();
        }
    }
}
