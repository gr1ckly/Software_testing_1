package org.itmo.softwaretesting1.domain;

/**
 * Общий интерфейс персонажа сцены.
 */
public interface Person {

    String getName();

    /**
     * Характерное действие персонажа.
     * Для разных реализаций может означать разное
     * (схватить за руку, пытаться открыть дверь и т.п.).
     */
    void act();
}


