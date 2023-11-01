import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverThread extends Thread {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Main.port)) {
            System.out.println("Peer is listening on port " + Main.port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                InputStream is = clientSocket.getInputStream();
                DataInputStream in = new DataInputStream(is);

                String option = in.readUTF();
                String sender = in.readUTF();
                System.out.println("Received " + option + " from " + sender);

                switch (option) {
                    case "message":
                        String message = in.readUTF();
                        System.out.println("Content: " + message);
                        break;
                    case "heartbeat":
                        // Receptie heartbeat
                        break;
                    case "cood":
                        // Actualizam variabile
                        Main.leader_id = Integer.parseInt(in.readUTF());
                        Main.leader_flag = true;
                        Main.election_req = false;
                        Main.received_ok = false;

                        System.out.println("LEADER selected is " + Main.leader_id);
                        Main.printFlags();
                        break;
                    case "ok":
                        Main.received_ok = true;
                        Main.printFlags();
                        // Dupa ce a primit un ok de la un nod cu id mai mare
                        // Nodul curent nu mai are nimic de facut
                        break;
                    case "election request":
                        Main.source_id = Integer.parseInt(sender);

                        // Daca id-ul acestui proces e mai mare decat id-ul procesului care a initiat electia
                        // Se trimite un mesaj de tip ok ca raspuns
                        if (Main.self_id > Main.source_id) {
                            new Sender("ok").start();

                            // Dupa trimiterea raspunsului se trimite un mesaj de electie noua
                            // Pentru a verifica daca raspund nodurile cu id mai mare
                            if (!Main.election_req) {
                                Main.election_req = true;
                                Main.start_time = System.currentTimeMillis();

                                System.out.println("Start time here is " + Main.start_time);
                                Main.printFlags();

                                new Sender("election request").start();
                                new TimerThread().start();
                            }
                        }
                        break;
                    default:
                        System.out.println("case default switch thread receiver");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Eroare thread receiver");
        }
    }
}
