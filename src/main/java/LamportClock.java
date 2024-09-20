public class LamportClock {
    private int clock = 0;

    public synchronized void increment() {
        clock++;
    }

    public synchronized void update(int receivedTimestamp) {
        clock = Math.max(clock, receivedTimestamp) + 1;
    }

    public synchronized int getClock() {
        return clock;
    }
}