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
    int tick = 0;
    int maxTick;
    int scanRange = 5;
    boolean print = false;

    XY carnivoreLoc, herbivoreLoc;
    Occupant carnivore, herbivore;

    boolean carnivorePause = false;

    int millisecondsToWait = 0;
    

    public GameInstance(World world, NeuralNetwork neuralNetwork, int maxTick, boolean print, int millisecondsToWait) {
        this.world = world;
        this.neuralNetwork = neuralNetwork;
        this.maxTick = maxTick;
        this.print = print;
        this.millisecondsToWait = millisecondsToWait;
    }

    public int run(){
        for (int i = 0; i < maxTick; i++) {
            tick = i;

            runHerbivore();

            if(runCarnivore()) {
                return tick;
            }

            if(print) {
                WorldServices.printWorld(world);
            }

            if(millisecondsToWait != 0) {
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


    public boolean runCarnivore() {

        if (carnivorePause) {
            carnivorePause = false;
            return false;
        } else {
            carnivorePause = true;
        }

        TreeMap<Double, XY> doubleList= new TreeMap<>();

        double currentDistance = distanceBetweenXY( herbivoreLoc, carnivoreLoc);
        if(currentDistance <= 1) {
            return true;
        }

        //up
        XY upLocation = new XY(carnivoreLoc.getX(),carnivoreLoc.getY()+1);
        double upDistance = distanceBetweenXY( herbivoreLoc, upLocation);
        if(checkCanMove(upLocation)) {
            doubleList.put(upDistance, upLocation);
        }

        //down
        XY downLocation = new XY(carnivoreLoc.getX(),carnivoreLoc.getY()-1);
        double downDistance = distanceBetweenXY( herbivoreLoc, downLocation);
        if(checkCanMove(downLocation)) {
            doubleList.put(downDistance, downLocation);
        }

        //right
        XY rightLocation = new XY(carnivoreLoc.getX() + 1,carnivoreLoc.getY());
        double rightDistance = distanceBetweenXY( herbivoreLoc, rightLocation);
        if(checkCanMove(rightLocation)) {
            doubleList.put(rightDistance, rightLocation);
        }

        //left
        XY leftLocation = new XY(carnivoreLoc.getX() - 1,carnivoreLoc.getY());
        double leftDistance = distanceBetweenXY( herbivoreLoc, leftLocation);
        if(checkCanMove(leftLocation)) {
            doubleList.put(leftDistance, leftLocation);
        }


        if(!doubleList.isEmpty()) {
            XY xy = doubleList.firstEntry().getValue();
            occupants[carnivoreLoc.getX()][carnivoreLoc.getY()] = null;
            occupants[xy.getX()][xy.getY()] = carnivore;
            carnivoreLoc = xy;
        }

        return false;
    }





    public double distanceBetweenXY(XY one, XY two) {
        return distanceBetweenPoints(one.getX(), one.getY(), two.getX(), two.getY());
    }

    public double distanceBetweenPoints(int x1, int y1, int x2, int y2) {
        return Math.hypot(x1-x2, y1-y2);
    }

}
