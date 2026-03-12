package org.itmo.softwaretesting1.domain;

/**
 * Форд, пытающийся открыть дверь.
 */
public class Ford implements Person {

    private final Door door;
    private int attempts;

    public Ford(Door door) {
        this.door = door;
    }

    public int getAttempts() {
        return attempts;
    }

    public boolean tryOpenDoor() {
        attempts++;
        return door.attemptOpen(this);
    }

    @Override
    public String getName() {
        return "Форд";
    }

    @Override
    public void act() {
        tryOpenDoor();
    }
}

