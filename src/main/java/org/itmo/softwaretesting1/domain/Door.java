package org.itmo.softwaretesting1.domain;

/**
 * Дверь, которую Форд и Зафод пытаются открыть.
 */
public class Door {

    private final String id;
    private boolean open;
    private int openAttempts;

    public Door(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isOpen() {
        return open;
    }

    public int getOpenAttempts() {
        return openAttempts;
    }

    /**
     * Попытка открыть дверь.
     */
    public boolean attemptOpen(Person by) {
        openAttempts++;
        if (!open) {
            open = true;
        }
        return open;
    }
}

