package org.itmo.softwaretesting1.domain;

public class SceneNarration {

    public static void main(String[] args) {
        Door door = new Door("выход");
        Arthur arthur = new Arthur();
        Trillian trillian = new Trillian();
        Ford ford = new Ford(door);
        Zaphod zaphod = new Zaphod(door);
        FlyingRodentGroup rodents = new FlyingRodentGroup(10);

        System.out.println("Надвигающиеся воздухоплавающие грызуны появляются на сцене.");
        rodents.startApproach();
        rodents.hypnotize(arthur);
        System.out.printf("Артур в состоянии: %s%n", arthur.getState());

        System.out.println("Триллиан в отчаянии схватила Артура за руку.");
        trillian.grabArthur(arthur);

        System.out.println("Триллиан тянет Артура к двери, которую Форд и Зафод пытаются открыть.");
        trillian.pullArthurToDoor(door);

        System.out.println("Форд пытается открыть дверь.");
        ford.tryOpenDoor();

        System.out.println("Зафод также пытается открыть дверь.");
        zaphod.tryOpenDoor();

        System.out.printf("Дверь открыта: %s, попыток: %d%n", door.isOpen(), door.getOpenAttempts());

        System.out.println("Артур приходит в себя и выходит из состояния гипноза.");
        arthur.changeState(Arthur.State.ALIVE);
        System.out.printf("Артур в состоянии: %s%n", arthur.getState());
    }
}

