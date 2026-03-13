package org.itmo.softwaretesting1.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneDomainModelTest {

    @Test
    void trillianGrabsArthurAndPullsToDoor() {
        Trillian trillian = new Trillian();
        Arthur arthur = new Arthur();
//        Door door = new Door();
        assertEquals(trillian.getState(), Trillian.trillianState.DONT_HOLDING_ARTHUR);
        assertEquals(trillian.grabArthur(arthur), Trillian.trillianState.HOLDING_ARTHUR);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> trillian.grabArthur(arthur));

        assertEquals("Она уже держит Артура", exception.getMessage());

        trillian.pullArthurToDoor();
        assertEquals(arthur.getState(), Arthur.State.BE_AT_DOOR);

        RuntimeException exception1 = assertThrows(RuntimeException.class, () -> trillian.pullArthurToDoor());

        assertEquals("Артур уже у двери", exception1.getMessage());


        assertSame(arthur, trillian.getHoldingArthur(), "Триллиан должна держать Артура за руку");

    }
//разбить тесты
    @Test
    void fordAndZaphodAttemptToOpenDoor() {
        Door door = new Door();
        Ford ford = new Ford(door);
        Zaphod zaphod = new Zaphod(door);

        RuntimeException exceptionFord = assertThrows(RuntimeException.class, () -> door.attemptOpen(ford));

        assertEquals("Дверь в истории откроется только с 3-го раза", exceptionFord.getMessage());

        RuntimeException exceptionZap = assertThrows(RuntimeException.class, () -> door.attemptOpen(zaphod));

        assertEquals("Дверь в истории откроется только с 3-го раза", exceptionZap.getMessage());

        assertEquals(ford.tryOpenDoor(), Door.doorState.OPEN, "Дверь должна открыться с 3-й попытки");

        RuntimeException exception = assertThrows(RuntimeException.class, ford::tryOpenDoor);

        assertEquals("Дверь уже открыта", exception.getMessage());
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

        assertEquals(rodents.isApproaching(), FlyingRodentGroup.rodentState.APPROACHING, "Грызуны должны надвигаться");
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
}

