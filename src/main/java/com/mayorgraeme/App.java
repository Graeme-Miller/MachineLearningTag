package com.mayorgraeme;

import com.mayorgraeme.occupant.Carnivore;
import com.mayorgraeme.occupant.Herbivore;
import com.mayorgraeme.occupant.Occupant;
import com.mayorgraeme.world.World;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.mayorgraeme.world.WorldServices.cloneWorld;
import static com.mayorgraeme.world.WorldServices.generateRandomWorld;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) throws IOException {
        //Create data set
        DataSet dataSet = new DataSet(4);
        Map<String, World> gameWorldMap = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            DataSetRow dataSetRow = new DataSetRow(1, 1, 1, 1);
            String label = "data-"+i;
            dataSetRow.setLabel(label);
            dataSet.addRow(dataSetRow);
            gameWorldMap.put(label, generateRandomWorld());
        }


        Occupant[][] displayWorld = new Occupant[25][25];
        displayWorld[12][12] = new Herbivore();
        displayWorld[15][15] = new Carnivore();

//        Perceptron network = new Perceptron(4, 4);
        MultiLayerPerceptron network = new MultiLayerPerceptron(1250, 4);

        GraemeSimulatedAnnealing gsa = new GraemeSimulatedAnnealing(network, gameWorldMap);

        Scanner console = new Scanner(System.in);

        // console.nextLine();
        // GameInstance gi = new GameInstance(cloneWorld(displayWorld), network, 2000, true, 350);
        // System.out.println("First Run Result: " + gi.run());


//        console.nextLine();

        gsa.learn(dataSet);


        console.nextLine();
        GameInstance gi2 = new GameInstance(cloneWorld(displayWorld), network, 2000, true, 350);
        System.out.println("Last Run Result: " + gi2.run());


    }



}
