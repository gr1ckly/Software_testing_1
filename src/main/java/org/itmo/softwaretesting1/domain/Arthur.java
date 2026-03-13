package org.itmo.softwaretesting1.domain;

/**
 * Артур, который может быть в разных состояниях.
 */
public class Arthur implements Person {

    public enum State {
        ALIVE,
        HYPNOTIZED,
        BE_AT_DOOR
    }

    private State state = State.ALIVE;

    @Override
    public String getName() {
        return "Артур";
    }

    public State getState() {
        return state;
    }


    public void changeState(State newState) {
        if(newState == null) throw new RuntimeException("Состояние не может быть нул");
        if(newState.equals(state)) throw new RuntimeException("Артур уже находится в этом состоянии");
        this.state = newState;
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

