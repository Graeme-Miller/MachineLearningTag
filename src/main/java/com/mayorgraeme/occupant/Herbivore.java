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

        XY herbivoreLoc = world.getOccupantLocation(this);
        int scanRange = 5;


        int inputInt = 0;
        //Scan Up
        XY upTopLeft = new XY(herbivoreLoc.getX() - scanRange, herbivoreLoc.getY() - scanRange);
        XY upBottomRight = new XY(herbivoreLoc.getX() + scanRange, herbivoreLoc.getY());
        inputInt = scanBox(upTopLeft, upBottomRight, world, neuralNetwork, inputInt);

        //Scan Down
        XY downTopLeft = new XY(herbivoreLoc.getX() - scanRange, herbivoreLoc.getY());
        XY downBottomRight = new XY(herbivoreLoc.getX() + scanRange, herbivoreLoc.getY() + scanRange);
        inputInt = scanBox(downTopLeft, downBottomRight, world, neuralNetwork, inputInt);

        //Scan Left
        XY leftTopLeft = new XY(herbivoreLoc.getX() - scanRange, herbivoreLoc.getY() - scanRange);
        XY leftBottomRight = new XY(herbivoreLoc.getX(), herbivoreLoc.getY() + scanRange);
        inputInt = scanBox(leftTopLeft, leftBottomRight, world, neuralNetwork, inputInt);

        //Scan Right
        XY rightTopLeft = new XY(herbivoreLoc.getX(), herbivoreLoc.getY() - scanRange);
        XY rightBottomRight = new XY(herbivoreLoc.getX() + scanRange, herbivoreLoc.getY() + scanRange);
        inputInt = scanBox(rightTopLeft, rightBottomRight, world, neuralNetwork, inputInt);

        Neuron[] inputNeurons = neuralNetwork.getInputNeurons();
        inputNeurons[inputInt++].setInput(herbivoreLoc.getX());
        inputNeurons[inputInt++].setInput(occupants.length - herbivoreLoc.getX());
        inputNeurons[inputInt++].setInput(herbivoreLoc.getY());
        inputNeurons[inputInt++].setInput(occupants[0].length - herbivoreLoc.getY());

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

        for (Vector vector : list) {
            XY newLoc = null;

            switch (vector.getDirection()) {
                case NORTH: newLoc = new XY(herbivoreLoc.getX(), herbivoreLoc.getY() - 1) ; break;
                case SOUTH: newLoc = new XY(herbivoreLoc.getX(), herbivoreLoc.getY() + 1) ; break;
                case EAST: newLoc = new XY(herbivoreLoc.getX() + 1, herbivoreLoc.getY()) ; break;
                case WEST: newLoc = new XY(herbivoreLoc.getX() - 1, herbivoreLoc.getY() - 1) ; break;
            }

            if(world.checkCanMove(newLoc)) {

                world.moveOccupant(this, herbivoreLoc, newLoc);
                herbivoreLoc = newLoc;

                return false;
            }
        }

        return false;

    }

    public int scanBox(XY topLeft, XY bottomRight, World world, NeuralNetwork network, int startInt) {
        Neuron[] inputNeurons = neuralNetwork.getInputNeurons();

        int returnInt = startInt;
        for (int x = topLeft.getX(); x <= bottomRight.getX(); x++) {
            for (int y = topLeft.getY(); y <= bottomRight.getY(); y++) {
                if(world.inBounds(new XY(x, y))) {
                    Occupant occupant = world.getOccupantMap()[x][y];
                    if(occupant instanceof Carnivore) {
                        inputNeurons[returnInt].setInput(1);
                    }
                }

                returnInt++;
            }
        }

        return  returnInt;

    }

    public char getChar() {
        return 'h';
    }
}
