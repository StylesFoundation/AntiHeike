package wtf.styles.antiheike.util;

public class Stopwatch {

    private long lastMS = System.currentTimeMillis();

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public void reset(long time) {
        lastMS = time;
    }

    public void setLastMS(long time) {
        lastMS = time;
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }
}
