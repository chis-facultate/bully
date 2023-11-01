public class TimerThread extends Thread {

    @Override
    public void run() {
        System.out.println("Inside timer thread");
        try {
            Thread.sleep(7000);
            System.out.println("Timer thread Awake");

            // Daca nu se primeste raspuns ok de la niciun nod dupa un anumit timp
            if (!Main.received_ok && !Main.leader_flag) {
                // Nodul curent se auto proclama lider
                Main.leader_id = Main.self_id;
                // Setam election_req pentru a putea incepe o noua electie cand va fi cazul
                Main.election_req = false;
                Main.leader_flag = true;
                System.out.println("I am the selected LEADER ! " + Main.self_id);
                // Trimitem mesaje spre celelalte noduri pentru a actualiza variabilele
                new Sender("cood").start();
            }
            // Daca s-a primit raspuns ok, dar nu exista lider se incepe electie noua
            else if (Main.received_ok && !Main.leader_flag) {
                Main.election_req = false;
                Main.received_ok = false;

                System.out.println("Received OK but  LEADER HAS FAILED!");
                Main.printFlags();

                new Sender("election request").start();
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Interrupted in Timer Thread");
        }
    }
}
