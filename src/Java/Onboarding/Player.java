package Java.Onboarding;

import java.util.Scanner;

public class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            Enemy[] enemies = {
                    new Enemy(in.next(), in.nextInt()),
                    new Enemy(in.next(), in.nextInt())};

            if (enemies[0].dist < enemies[1].dist)  System.out.println(enemies[0].name);
            else                                    System.out.println(enemies[1].name);
        }
    }
}

class Enemy {
    final int dist;
    final String name;

    public Enemy(String name, int dist) {
        this.dist = dist;
        this.name = name;
    }
}