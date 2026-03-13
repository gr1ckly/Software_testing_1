package org.itmo.softwaretesting1.domain;

/**
 * Дверь, которую Форд и Зафод пытаются открыть.
 */
public class Door {

    public enum doorState{
        OPEN,
        CLOSE
    }

    private Person lastAttemptBy;
    private int openAttempts;
    private doorState state = doorState.CLOSE;

    public Door() {
        this.openAttempts = 0;
    }

    public Person getLastAttemptBy() {
        return lastAttemptBy;
    }

    public doorState isOpen() {
        return state;
    }

    public int getOpenAttempts() {
        return openAttempts;
    }

    /**
     * Попытка открыть дверь.
     */
    public doorState attemptOpen(Person by) {
        openAttempts++;
        this.lastAttemptBy = by;
        if (openAttempts > 2 && this.state.equals(doorState.CLOSE)) {
            state = doorState.OPEN;
        }else if(!this.state.equals(doorState.CLOSE)){
            throw new RuntimeException("Дверь уже открыта");
        }else {
            throw new RuntimeException("Дверь в истории откроется только с 3-го раза");
        }
        return state;
    }
}

