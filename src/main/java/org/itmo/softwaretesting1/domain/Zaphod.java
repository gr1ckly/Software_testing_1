package org.itmo.softwaretesting1.domain;

/**
 * Зафод, также пытающийся открыть дверь.
 */
public class Zaphod implements Person {

    private final Door door;

    public Zaphod(Door door) {
        this.door = door;
    }


    public Door.doorState tryOpenDoor() {
        return door.attemptOpen(this);
    }

    @Override
    public String getName() {
        return "Зафод";
    }

    @Override
    public void act() {
        tryOpenDoor();
    }
}

