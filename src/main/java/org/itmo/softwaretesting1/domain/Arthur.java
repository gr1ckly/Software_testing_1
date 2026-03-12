package org.itmo.softwaretesting1.domain;

/**
 * Артур, который может быть в разных состояниях.
 */
public class Arthur implements Person {

    public enum State {
        ALIVE,
        HYPNOTIZED
    }

    private State state = State.ALIVE;
    private Door targetDoor;

    @Override
    public String getName() {
        return "Артур";
    }

    public State getState() {
        return state;
    }

    public Door getTargetDoor() {
        return targetDoor;
    }

    public void setTargetDoor(Door targetDoor) {
        this.targetDoor = targetDoor;
    }

    public void changeState(State newState) {
        if (newState != null) {
            this.state = newState;
        }
    }

    public boolean isHypnotized() {
        return state == State.HYPNOTIZED;
    }

    public boolean isAlive() {
        return state == State.ALIVE;
    }

    @Override
    public void act() {}
}

