package com.mayorgraeme;

import com.mayorgraeme.occupant.Carnivore;
import com.mayorgraeme.occupant.Herbivore;
import com.mayorgraeme.occupant.Occupant;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;


import java.util.*;


public class GameInstance {

    Occupant[][] occupants;
    NeuralNetwork neuralNetwork;
    int tick = 0;
    int maxTick;
    int scanRange = 5;
    boolean print = false;

    XY carnivoreLoc, herbivoreLoc;
    Occupant carnivore, herbivore;

    boolean carnivorePause = false;

    int millisecondsToWait = 0;
    

    public GameInstance(Occupant[][] occupants, NeuralNetwork neuralNetwork, int maxTick, boolean print, int millisecondsToWait) {
        this.occupants = occupants;
        this.neuralNetwork = neuralNetwork;
        this.maxTick = maxTick;
        this.print = print;
        this.millisecondsToWait = millisecondsToWait;


        //Grab the locs of carnivore/herbivore
        for (int x = 0; x < occupants.length; x++) {
            Occupant[] occupantRow = occupants[x];

            for (int y = 0; y < occupantRow.length; y++) {
                Occupant occupant = occupantRow[y];

                if (occupant != null) {
                    if (occupant instanceof Carnivore) {
                        carnivore = occupant;
                        carnivoreLoc = new XY(x, y);
                    } else if (occupant instanceof Herbivore) {
                        herbivore = occupant;
                        herbivoreLoc = new XY(x, y);
                    }
                }
            }
        }
    }

    public int run(){
        for (int i = 0; i < maxTick; i++) {
            tick = i;

            runHerbivore();

            if(runCarnivore()) {
                return tick;
            }

            if(print) {
                WorldServices.printWorld(occupants);
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

    private class Vector {
        double magnitude;
        Direction direction;

        public Vector(double magnitude, Direction direction) {
            this.magnitude = magnitude;
            this.direction = direction;
        }

        public double getMagnitude() {
            return magnitude;
        }

        public Direction getDirection() {
            return direction;
        }

        @Override
        public String toString() {
            return "Vector{" +
                    "magnitude=" + magnitude +
                    ", direction=" + direction +
                    '}';
        }
    }

    public void runHerbivore() {

        int i = 0;
        Neuron[] inputNeurons = neuralNetwork.getInputNeurons();
        for (int x = 0; x < occupants.length; x++) {
            for (int y = 0; y < occupants[0].length; y++) {
                Occupant occupant = occupants[x][y];
                if (occupant == null) {
                    inputNeurons[i].setInput(0);
                } else {
                    inputNeurons[i].setInput(1);
                }
                i++;
            }
        }

         neuralNetwork.calculate();
        double[] output = neuralNetwork.getOutput();

        List<Vector> list = new ArrayList<>();

        list.add(new Vector(output[0], Direction.NORTH));
        list.add(new Vector(output[1], Direction.SOUTH));
        list.add(new Vector(output[2], Direction.WEST));
        list.add(new Vector(output[3], Direction.EAST));

        Collections.sort(list, new Comparator<Vector>() {
            @Override
            public int compare(Vector o1, Vector o2) {
                return Double.compare(o2.magnitude, o1.magnitude);
            }
        });

        for (Vector vector : list) {
            XY newLoc = null;

            switch (vector.getDirection()) {
                case NORTH: newLoc = new XY(herbivoreLoc.getX(), herbivoreLoc.getY() - 1) ; break;
                case SOUTH: newLoc = new XY(herbivoreLoc.getX(), herbivoreLoc.getY() + 1) ; break;
                case EAST: newLoc = new XY(herbivoreLoc.getX() + 1, herbivoreLoc.getY()) ; break;
                case WEST: newLoc = new XY(herbivoreLoc.getX() - 1, herbivoreLoc.getY() - 1) ; break;
            }

            if(checkCanMove(newLoc)) {
                if(print) {
                    System.out.println("Moving "+ vector.getDirection());
                }

                occupants[herbivoreLoc.getX()][herbivoreLoc.getY()] = null;
                occupants[newLoc.getX()][newLoc.getY()] = herbivore;
                herbivoreLoc = newLoc;

                return;
            } else if (print) {
                System.out.println("Not moving");
            }
        }

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



    public boolean checkCanMove(XY xy) {
        if(xy.getX() < 0 || xy.getY() < 0) {
            return false;
        } else if (xy.getX() >= occupants[0].length) {
            return false;
        } else if (xy.getY() >= occupants.length) {
            return false;
        }

        return occupants[xy.getX()][xy.getY()] == null;
    }

    public double distanceBetweenXY(XY one, XY two) {
        return distanceBetweenPoints(one.getX(), one.getY(), two.getX(), two.getY());
    }

    public double distanceBetweenPoints(int x1, int y1, int x2, int y2) {
        return Math.hypot(x1-x2, y1-y2);
    }

}
