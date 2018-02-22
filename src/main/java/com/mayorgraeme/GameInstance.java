package com.mayorgraeme;

import com.mayorgraeme.occupant.Carnivore;
import com.mayorgraeme.occupant.Herbivore;
import com.mayorgraeme.occupant.Occupant;
import com.mayorgraeme.world.World;
import com.mayorgraeme.world.WorldServices;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;


import java.util.*;


public class GameInstance {

    World world;
    NeuralNetwork neuralNetwork;
    int maxTick;
    boolean print = false;

    int millisecondsToWait = 0;


    public GameInstance(World world, NeuralNetwork neuralNetwork, int maxTick, boolean print, int millisecondsToWait) {
        this.world = world;
        this.neuralNetwork = neuralNetwork;
        this.maxTick = maxTick;
        this.print = print;
        this.millisecondsToWait = millisecondsToWait;
    }

    public int run() {
        for (int i = 0; i < maxTick; i++) {

            if (world.tick()) {
                return i;
            }


            if (print) {
                WorldServices.printWorld(world);
            }

            if (millisecondsToWait != 0) {
                try {
                    Thread.sleep(millisecondsToWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return maxTick;
    }


    public boolean inBounds(XY topleft, XY bottomRight, XY currentLoc) {
        return
                topleft.getX() <= currentLoc.getX() &&
                        topleft.getY() <= currentLoc.getY() &&
                        bottomRight.getX() >= currentLoc.getX() &&
                        bottomRight.getY() >= currentLoc.getY();
    }

}