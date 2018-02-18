package com.mayorgraeme;

import com.mayorgraeme.occupant.Carnivore;
import com.mayorgraeme.occupant.Herbivore;
import com.mayorgraeme.occupant.Occupant;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Random;

public class WorldServices {

    static Random rand = new Random();

    public static void printWorld(Occupant[][] world){
        String separator = StringUtils.rightPad("", world.length+2, "-");

        System.out.println(separator);
        for (Occupant[] occupantRow : world) {
            System.out.print("|");
            for (Occupant occupant : occupantRow) {
                if (occupant == null) {
                    System.out.print(" ");
                } else {
                    System.out.print(occupant.getChar());
                }
            }
            System.out.println("|");
        }
        System.out.println(separator);
    }


    public static Occupant[][] cloneWorld(Occupant[][] worldToClone) {
        Occupant[][] newWorld = new Occupant[worldToClone.length][worldToClone[0].length];
        for (int i = 0; i < newWorld.length; i++) {
            newWorld[i] = Arrays.copyOf(worldToClone[i], worldToClone[i].length);
        }

        return newWorld;
    }

    public static Occupant[][] generateRandomWorld() {

        Occupant[][] world = new Occupant[25][25];
        int herbX = rand.nextInt(25);
        int herbY = rand.nextInt(25);
        world[herbX][herbY] = new Herbivore();

        boolean setCarnivore = false;
        while (!setCarnivore) {
            int carnX = rand.nextInt(25);
            int carnY = rand.nextInt(25);

            if(carnX == herbX && carnY == herbY) {
                continue;
            }

            setCarnivore=true;
            world[carnX][carnY] = new Carnivore();
        }

        return world;
    }
}
