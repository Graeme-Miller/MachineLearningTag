package com.mayorgraeme.world;

import com.mayorgraeme.XY;
import com.mayorgraeme.occupant.Carnivore;
import com.mayorgraeme.occupant.Herbivore;
import com.mayorgraeme.occupant.Occupant;
import org.apache.commons.lang3.StringUtils;
import org.neuroph.core.NeuralNetwork;

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

    public static World generateRandomWorld(NeuralNetwork network) {
        World world = new DefaultWorld();

        int herbX = rand.nextInt(25);
        int herbY = rand.nextInt(25);

        Herbivore herbivore = new Herbivore(network);
        world.addOccupant(herbivore, new XY(herbX, herbY));

        boolean setCarnivore = false;
        while (!setCarnivore) {
            int carnX = rand.nextInt(25);
            int carnY = rand.nextInt(25);

            if(carnX == herbX && carnY == herbY) {
                continue;
            }

            setCarnivore=true;
            world.addOccupant(new Carnivore(herbivore), new XY(carnX, carnY));
        }

        return world;
    }

    public static double distanceBetweenXY(XY one, XY two) {
        return distanceBetweenPoints(one.getX(), one.getY(), two.getX(), two.getY());
    }

    public static double distanceBetweenPoints(int x1, int y1, int x2, int y2) {
        return Math.hypot(x1-x2, y1-y2);
    }
}
