package com.mayorgraeme;

import com.mayorgraeme.occupant.Carnivore;
import com.mayorgraeme.occupant.Herbivore;
import com.mayorgraeme.world.DefaultWorld;
import com.mayorgraeme.world.World;
import com.mayorgraeme.world.WorldServices;
import org.apache.commons.cli.*;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.mayorgraeme.world.WorldServices.generateRandomWorld;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) throws IOException, ParseException {


        for (String arg : args) {
            System.out.println("arg: "+arg);
        }
        Options options = new Options();

        options.addOption("s", "startTemperature", true, "Start temp");
        options.addOption("e","stopTemperature", true, "Stop temp");
        options.addOption("a", "alpha", true, "alpha");
        options.addOption("t", "maxTicks", true, "Max Ticks");
        options.addOption("i", "iterationsPerTemperature", true, "Iterations Per Temperature");
        options.addOption("t", "trainingSize", true, "Training Size");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        System.out.println(cmd.getOptionValue("startTemperature"));
        double startTemperature = Double.parseDouble(cmd.getOptionValue("startTemperature"));
        double stopTemperature = Double.parseDouble(cmd.getOptionValue("stopTemperature"));
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int maxTicks = Integer.parseInt(cmd.getOptionValue("maxTicks"));
        int iterationsPerTemperature = Integer.parseInt(cmd.getOptionValue("iterationsPerTemperature"));
        int trainingSize = Integer.parseInt(cmd.getOptionValue("trainingSize"));


        MultiLayerPerceptron network = new MultiLayerPerceptron(1250,100, 10, 4);

        //Create data set
        DataSet dataSet = new DataSet(4);
        Map<String, World> gameWorldMap = new HashMap<>();
        for (int i = 0; i < trainingSize; i++) {
            DataSetRow dataSetRow = new DataSetRow(1, 1, 1, 1);
            String label = "data-"+i;
            dataSetRow.setLabel(label);
            dataSet.addRow(dataSetRow);
            gameWorldMap.put(label, generateRandomWorld(network));
        }


        World displayWorld = gameWorldMap.entrySet().iterator().next().getValue();//WorldServices.generateRandomWorld(network);


        GraemeSimulatedAnnealing gsa = new GraemeSimulatedAnnealing(network, startTemperature, stopTemperature, alpha, maxTicks, gameWorldMap, iterationsPerTemperature);

//        Scanner console = new Scanner(System.in);

//        console.nextLine();
//        GameInstance gi = new GameInstance(displayWorld.clone(), network, 2000, true, 350);
//        System.out.println("First Run Result: " + gi.run());


//        console.nextLine();

        gsa.learn(dataSet);


//        console.nextLine();
//        GameInstance gi2 = new GameInstance(displayWorld.clone(), network, 2000, true, 350);
//        System.out.println("Last Run Result: " + gi2.run());


        network.save("network_out");
    }



}
