package com.example.demowificonnectivity;

public class ThreadUtil {
    private ThreadUtil() {
        // nothing
    }

    public static void safeThreadSleep5000MS() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            if (Thread.interrupted())  {
                // Clears interrupted status!
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void safeThreadSleepMS(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            if (Thread.interrupted())  {
                // Clears interrupted status!
                Thread.currentThread().interrupt();
            }
        }
    }
}
