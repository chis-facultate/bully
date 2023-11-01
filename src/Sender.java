import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class Sender extends Thread {
    private String reqtype;

    public Sender(String reqtype) {
        this.reqtype = reqtype;
    }

    @Override
    public void run() {
        switch (reqtype) {
            case "election request":
                try {
                    sendElectionRequest();
                } catch (Exception e) {
                    System.out.println("*** Process has FAILED, Election Request cannot be processed !");
                }
                break;
            case "ok":
                try {
                    sendOK();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "cood":
                try {
                    sendCoordinatorMsg();
                } catch (Exception e) {
                    System.out.println("*** Process has FAILED, Won't get the new leader !");
                }
                break;
            default:
                System.out.println("case default sender thread");
        }
    }

    /**
     * The sendCoordinatorMsg() method broadcasts the leader to all the process.
     * If the process has failed then a message is displayed to indicate that
     * the process has .failed.
     */
    public static void sendCoordinatorMsg() {
        Collection<Integer> keys = Main.processesIPs.keySet();
        for (int key : keys) {
            if (key != Main.self_id) {
                String destination_server = (String) Main.processesIPs.get(key);
                int destination_port = (int) Main.processesPorts.get(key);

                try {
                    Socket socket = new Socket(destination_server, destination_port);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    out.writeUTF("cood");
                    out.writeUTF(Main.self_id + "");
                    out.writeUTF(Main.leader_id + "");

                    System.out.println("Sent Leader ID to " + key);
                } catch (IOException e) {
                    System.out.println("*** Process " + key + " has failed, won't get the new leader !");
                }
            }
        }
    }

    /**
     * The sendOK() method sends OK message to the incoming process which has
     * requested an election request.
     */
    public static void sendOK() {
        try {
            String destination_server = (String) Main.processesIPs.get(Main.source_id);
            int destination_port = (int) Main.processesPorts.get(Main.source_id);

            Socket socket = new Socket(destination_server, destination_port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("ok");
            out.writeUTF(Main.self_id + "");

            System.out.println("Sent OK to " + Main.source_id);
        } catch (Exception e) {
            System.out.println("*** Process " + Main.source_id + " has FAILED. OK Message cannot be sent !");
        }
    }

    /**
     * The sendElectionRequest() method sends Election Request to all the higher
     * processes.
     */
    public static void sendElectionRequest() {
        System.out.println("Election Initiated...");
        int failure = 0;

        Collection<Integer> keys = Main.processesIPs.keySet();
        for (int key : keys) {
            // Se trimite election request catre toat nodurile cu id mai mare
            if (key > Main.self_id) {
                String destination_server = (String) Main.processesIPs.get(key);
                int destination_port = (int) Main.processesPorts.get(key);

                try {
                    Socket socket = new Socket(destination_server, destination_port);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    out.writeUTF("election request");
                    out.writeUTF(Main.self_id + "");

                    System.out.println("Sent Election Request to " + key);
                } catch (Exception e) {
                    System.out.println("*** Process " + key + " has FAILED, cannot send Election Request !");
                    failure++;
                }
            }
        }
        // failure == higher -> niciun nod cu id mai mare nu a raspuns
        if (failure == Main.higher) {
            // Daca niciun alt proces nu a inceput o electie intre timp
            // Se porneste un timer pentru ca s-ar putea ca un proces sa revina in scurt timp
            // Altfel in timer thread exista un caz in care procesul curent se auto intituleaza lider
            if (!Main.election_req) {
                Main.start_time = System.currentTimeMillis();
                System.out.println("Inside if of sendElectionRequest, start_time= " + Main.start_time);
                Main.election_req = true;
                Main.received_ok = false;
                new TimerThread().start();
            }
        }
    }
}
