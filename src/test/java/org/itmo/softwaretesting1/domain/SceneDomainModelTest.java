package org.itmo.softwaretesting1.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneDomainModelTest {

    @Test
    void trillianGrabsArthurAndPullsToDoor() {
        Trillian trillian = new Trillian();
        Arthur arthur = new Arthur();
        Door door = new Door("выход");

        trillian.grabArthur(arthur);
        trillian.pullArthurToDoor(door);

        assertSame(arthur, trillian.getHoldingArthur(), "Триллиан должна держать Артура за руку");
        assertSame(door, trillian.getTargetDoor(), "Триллиан тянет к этой двери");
        assertSame(door, arthur.getTargetDoor(), "Артур также направлен к этой же двери");
    }

    @Test
    void fordAndZaphodAttemptToOpenDoor() {
        Door door = new Door("выход");
        Ford ford = new Ford(door);
        Zaphod zaphod = new Zaphod(door);

        boolean openedByFord = ford.tryOpenDoor();
        boolean openedByZaphod = zaphod.tryOpenDoor();

        assertTrue(openedByFord, "Дверь должна открыться при попытке Форда");
        assertTrue(openedByZaphod, "После первой успешной попытки дверь остаётся открытой");
        assertEquals(2, door.getOpenAttempts(), "Должны быть зафиксированы две попытки открыть дверь");
        assertTrue(door.isOpen(), "Дверь в итоге должна быть открыта");
    }

    @Test
    void approachingFlyingRodentsHypnotizeArthur() {
        Arthur arthur = new Arthur();
        FlyingRodentGroup rodents = new FlyingRodentGroup(10);

        assertTrue(arthur.isAlive(), "Изначально Артур живой");
        assertFalse(arthur.isHypnotized(), "Изначально Артур не загипнотизирован");
        assertEquals(Arthur.State.ALIVE, arthur.getState(), "Изначальное состояние Артура — ALIVE");

        rodents.startApproach();
        rodents.hypnotize(arthur);

        assertTrue(rodents.isApproaching(), "Грызуны должны надвигаться");
        assertTrue(arthur.isHypnotized(), "Артур должен быть загипнотизирован");
        assertEquals(Arthur.State.HYPNOTIZED, arthur.getState(), "Состояние Артура — HYPNOTIZED");
    }

    @Test
    void arthurCanWakeUpFromHypnosis() {
        Arthur arthur = new Arthur();
        FlyingRodentGroup rodents = new FlyingRodentGroup(5);

        rodents.startApproach();
        rodents.hypnotize(arthur);
        assertTrue(arthur.isHypnotized(), "Артур должен быть загипнотизирован");

        arthur.changeState(Arthur.State.ALIVE);

        assertFalse(arthur.isHypnotized(), "После пробуждения Артур не должен быть гипнотизирован");
        assertTrue(arthur.isAlive(), "После пробуждения Артур снова живой");
        assertEquals(Arthur.State.ALIVE, arthur.getState(), "Состояние Артура — ALIVE");
    }

    @Test
    void fordAndZaphodUseInterfacePerson() {
        Door door = new Door("выход");
        Person ford = new Ford(door);
        Person zaphod = new Zaphod(door);

        ford.act();
        zaphod.act();

        assertTrue(door.isOpen(), "После действий персонажей дверь должна быть открыта");
        assertEquals(2, door.getOpenAttempts(), "Должно быть две попытки открыть дверь");
    }

    @Test
    void mainNarrationRunsWithoutExceptions() {
        // Проверяем, что main‑класс успешно отрабатывает историю
        SceneNarration.main(new String[0]);
    }
}

