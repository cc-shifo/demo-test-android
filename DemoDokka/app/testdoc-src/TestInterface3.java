package com.example.demodokka;

/**
 * test TestInterface3
 */
public interface TestInterface3 {
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
