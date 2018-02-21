package com.mayorgraeme.world;

import com.mayorgraeme.XY;
import com.mayorgraeme.occupant.Carnivore;
import com.mayorgraeme.occupant.Herbivore;
import com.mayorgraeme.occupant.Occupant;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Random;

public class WorldServices {

    static Random rand = new Random();

    public static void printWorld(World world){
        Occupant[][] worldMatrix = world.getOccupantMap();

        String separator = StringUtils.rightPad("", worldMatrix.length+2, "-");

        System.out.println(separator);
        for (Occupant[] occupantRow : worldMatrix) {
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

    public static World generateRandomWorld() {
        World world = new DefaultWorld();

        int herbX = rand.nextInt(25);
        int herbY = rand.nextInt(25);

        Herbivore herbivore = new Herbivore();
        world.addOccupant(herbivore, new XY(herbX, herbY));

        boolean setCarnivore = false;
        while (!setCarnivore) {
            int carnX = rand.nextInt(25);
            int carnY = rand.nextInt(25);

            if(carnX == herbX && carnY == herbY) {
                continue;
            }

            setCarnivore=true;
            world.addOccupant(new Carnivore(), new XY(carnX, carnY));
        }

        return world;
    }
}
