package org.itmo.softwaretesting1.domain;

/**
 * Форд, пытающийся открыть дверь.
 */
public class Ford implements Person {

    private final Door door;

    public Ford(Door door) {
        this.door = door;
    }

    public Door.doorState tryOpenDoor() {
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

