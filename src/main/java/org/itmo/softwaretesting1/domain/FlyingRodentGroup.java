package org.itmo.softwaretesting1.domain;

/**
 * Надвигающиеся воздухоплавающие грызуны,
 * которые способны загипнотизировать персонажей.
 */
public class FlyingRodentGroup {

    private final int count;
    private boolean approaching;

    public FlyingRodentGroup(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public boolean isApproaching() {
        return approaching;
    }

    public void startApproach() {
        this.approaching = true;
    }

    /**
     * Загипнотизировать Артура, если группа надвигается.
     */
    public void hypnotize(Arthur target) {
        if (approaching && target != null) {
            target.changeState(Arthur.State.HYPNOTIZED);
        }
    }
}

