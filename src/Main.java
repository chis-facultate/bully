import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class Main {

    static int self_id = -1;
    static int higher = 0;
    static int leader_id = -1;
    static int source_id = -1;
    static int port = 3000;
    static HashMap processesIPs = new HashMap();
    static HashMap processesPorts = new HashMap();
    static int heartbeatInterval = 2000;
    static boolean leader_flag = false;
    static boolean election_req = false;
    static boolean received_ok = false;
    static long start_time = -1;
    static long start_time_ok = -1;

    public static void main(String[] args) {

        try {
            initialize();
        } catch (IOException e) {
            System.out.println("Eraore initializare");
            throw new RuntimeException(e);
        }

        new ReceiverThread().start();
        new HeartbeatSender().start();

        // La pornirea programului heartbeat-ul catre lider va esua deoarece leader_id = -1 la initializare
        // Urmeaza trimiterea unui election request in blocul catch din HeartbeatSender
        // TimerThread va fi pornit de receiverThread al altui proces atunci cand se primeste election request
        // Iar daca liderul se modifica atunci acesta va fi anuntat oricum
        //new TimerThread().start();
        // Programul functioneaza fara timerOkThread
    }

    private static void initialize() throws IOException {

        // Memoram ip-urile
        processesIPs.put(1, "127.0.0.1");
        processesIPs.put(2, "127.0.0.1");
        processesIPs.put(3, "127.0.0.1");

        // Memoram porturile
        processesPorts.put(1, 3000);
        processesPorts.put(2, 3001);
        processesPorts.put(3, 3002);

        // Asignam id in functie de port
        switch (port) {
            case 3000:
                self_id = 1;
                break;
            case 3001:
                self_id = 2;
                break;
            case 3002:
                self_id = 3;
                break;
            default:
                System.out.println("Case default init");
        }

        System.out.println("Server id here is " + self_id);

        // Numaram cate noduri au id mai mare
        Collection<Integer> keys = processesIPs.keySet();
        for (Integer key : keys) {
            if (key > self_id) {
                higher++;
            }
        }
    }

    public static void printFlags() {
        System.out.println("### leader_flag = " + Main.leader_flag + ", election_req = "
                + Main.election_req + ", received_ok = " + Main.received_ok);
    }
}

