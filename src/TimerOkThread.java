public class TimerOkThread extends Thread {
    @Override
    public void run() {
        System.out.println("Inside timerOK thread");
        while (true) {
            if ((!Main.leader_flag) && System.currentTimeMillis() - Main.start_time_ok > (5000 + (5000 * (5 - Main.self_id)))) {
                System.out.println("Higher Process Sent OK but Failed, so Start a new Election process");
                new Sender("election request").start();
                break;
            }
        }
        System.out.println("Thread timer ok oprit");
    }
}
