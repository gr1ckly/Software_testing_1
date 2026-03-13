package org.itmo.softwaretesting1.domain;

/**
 * Триллиан, которая в отчаянии хватает Артура за руку и тянет к двери.
 */
public class Trillian implements Person {

    public enum trillianState{
        HOLDING_ARTHUR,
        DONT_HOLDING_ARTHUR
    }

    private trillianState state = trillianState.DONT_HOLDING_ARTHUR;
    private Arthur holdingArthur;

    @Override
    public String getName() {
        return "Триллиан";
    }

    public Arthur getHoldingArthur() {
        return holdingArthur;
    }

    public trillianState getState() {
        return state;
    }

    public trillianState grabArthur(Arthur arthur) {
        this.holdingArthur = arthur;
        if(!state.equals(trillianState.HOLDING_ARTHUR)) {
            state = trillianState.HOLDING_ARTHUR;
            return state;
        }
        else{
            throw new RuntimeException("Она уже держит Артура");
        }
    }

    public void pullArthurToDoor() {
        if (holdingArthur != null && !holdingArthur.getState().equals(Arthur.State.BE_AT_DOOR)) {
            holdingArthur.changeState(Arthur.State.BE_AT_DOOR);
        }else{
            throw new RuntimeException("Артур уже у двери");
        }
    }

    @Override
    public void act() {
    }
}

