package org.itmo.softwaretesting1.domain;

/**
 * Триллиан, которая в отчаянии хватает Артура за руку и тянет к двери.
 */
public class Trillian implements Person {

    private Arthur holdingArthur;
    private Door targetDoor;

    @Override
    public String getName() {
        return "Триллиан";
    }

    public Arthur getHoldingArthur() {
        return holdingArthur;
    }

    public Door getTargetDoor() {
        return targetDoor;
    }

    /**
     * Схватить Артура за руку.
     */
    public void grabArthur(Arthur arthur) {
        this.holdingArthur = arthur;
    }

    /**
     * Потянуть Артура к двери.
     */
    public void pullArthurToDoor(Door door) {
        this.targetDoor = door;
        if (holdingArthur != null) {
            holdingArthur.setTargetDoor(door);
        }
    }

    @Override
    public void act() {
    }
}

