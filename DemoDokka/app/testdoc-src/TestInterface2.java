package com.example.demodokka;

/**
 * test TestInterface2
 */
public interface TestInterface2 {
    /**
     * open PSAM or SIM Sam slot
     *
     * @param id to open socket
     * @throws Throwable throw exception if open failed.
     */
    void open(int id) throws Throwable;

    /**
     * close PSAM or SIM Sam slot
     *
     * @param id to close socket
     * @throws Throwable throw exception if close failed.
     *
     */
    void close(int id) throws Throwable;

}
