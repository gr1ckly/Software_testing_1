package org.itmo.softwaretesting1.domain;


public class FlyingRodentGroup {

    private final int count;
    private rodentState state = rodentState.DONT_MOVE;

    public enum rodentState{
        APPROACHING,
        DONT_MOVE
    }

    public FlyingRodentGroup(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public rodentState isApproaching() {
        return state;
    }

    public void startApproach() {
        this.state = rodentState.APPROACHING;
    }


    public void hypnotize(Arthur target) {
        if (state == rodentState.APPROACHING && target != null) {
            target.changeState(Arthur.State.HYPNOTIZED);
        }
    }
}

