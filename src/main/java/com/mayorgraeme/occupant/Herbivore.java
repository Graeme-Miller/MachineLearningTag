package com.mayorgraeme.occupant;

import com.mayorgraeme.Direction;
import com.mayorgraeme.GameInstance;
import com.mayorgraeme.Vector;
import com.mayorgraeme.XY;
import com.mayorgraeme.world.World;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Herbivore implements Occupant {

    private final NeuralNetwork neuralNetwork;

    public Herbivore(NeuralNetwork network) {
        this.neuralNetwork = network;
    }

    public boolean process(World world) {

        Occupant[][] occupants = world.getOccupantMap();

        int i = 0;
        Neuron[] inputNeurons = neuralNetwork.getInputNeurons();
        for (int x = 0; x < occupants.length; x++) {
            for (int y = 0; y < occupants[0].length; y++) {
                Occupant occupant = occupants[x][y];
                if (occupant instanceof Carnivore) {
                    inputNeurons[i].setInput(1);
                } else {
                    inputNeurons[i].setInput(0);
                }
                i++;
            }
        }
        for (int x = 0; x < occupants.length; x++) {
            for (int y = 0; y < occupants[0].length; y++) {
                Occupant occupant = occupants[x][y];
                if (occupant instanceof  Herbivore) {
                    inputNeurons[i].setInput(1);
                } else {
                    inputNeurons[i].setInput(0);
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
                return Double.compare(o2.getMagnitude(), o1.getMagnitude());
            }
        });

        XY herbivoreLoc = world.getOccupantLocation(this);

        for (Vector vector : list) {
            XY newLoc = null;

            switch (vector.getDirection()) {
                case NORTH: newLoc = new XY(herbivoreLoc.getX(), herbivoreLoc.getY() - 1) ; break;
                case SOUTH: newLoc = new XY(herbivoreLoc.getX(), herbivoreLoc.getY() + 1) ; break;
                case EAST: newLoc = new XY(herbivoreLoc.getX() + 1, herbivoreLoc.getY()) ; break;
                case WEST: newLoc = new XY(herbivoreLoc.getX() - 1, herbivoreLoc.getY() - 1) ; break;
            }

            if(world.checkCanMove(newLoc)) {
//                if(print) {
//                    System.out.println("Moving "+ vector.getDirection());
//                }

                world.moveOccupant(this, herbivoreLoc, newLoc);
                herbivoreLoc = newLoc;

                return false;
            }
//            else if (print) {
//                System.out.println("Not moving");
//            }
        }

        return false;

    }


    public char getChar() {
        return 'h';
    }
}
