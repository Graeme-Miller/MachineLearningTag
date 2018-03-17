package com.mayorgraeme;

import com.mayorgraeme.world.World;
import org.apache.commons.cli.*;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mayorgraeme.world.WorldServices.generateRandomWorld;

public class App 
{


    public static void main( String[] args ) throws IOException, ParseException {


        for (String arg : args) {
            System.out.println("arg: "+arg);
        }
        Options options = new Options();

        options.addOption("r", "replay", false, "Will perform a replay");
        options.addOption("f", "networkFilename", true, "The name of the file to save or replay");
        options.addOption("s", "startTemperature", true, "Start temp");
        options.addOption("e","stopTemperature", true, "Stop temp");
        options.addOption("a", "alpha", true, "alpha");
        options.addOption("t", "maxTicks", true, "Max Ticks");
        options.addOption("i", "iterationsPerTemperature", true, "Iterations Per Temperature");
        options.addOption("t", "trainingSize", true, "Training Size");
        options.addOption("p", "percentNeuronsChange", true, "The percetnage of neurons to change");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("r")) {
            replay(cmd);
        } else {
            machineLearn(cmd);
        }
    }

    private static void replay(CommandLine cmd) {
        String networkFilename = cmd.getOptionValue("networkFilename");

        NeuralNetwork network  = NeuralNetwork.createFromFile(networkFilename);
        World randomWorld = generateRandomWorld(network);

        GameInstance gi = new GameInstance(randomWorld.clone(), network, 2000, true, 350);
        System.out.println("Result: " + gi.run());
    }

    private static void machineLearn(CommandLine cmd) {
        double startTemperature = Double.parseDouble(cmd.getOptionValue("startTemperature"));
        double stopTemperature = Double.parseDouble(cmd.getOptionValue("stopTemperature"));
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int maxTicks = Integer.parseInt(cmd.getOptionValue("maxTicks"));
        int iterationsPerTemperature = Integer.parseInt(cmd.getOptionValue("iterationsPerTemperature"));
        int trainingSize = Integer.parseInt(cmd.getOptionValue("trainingSize"));
        int percentNeuronsChange = Integer.parseInt(cmd.getOptionValue("percentNeuronsChange"));

        String networkFilename;
        if (cmd.hasOption("networkFilename")) {
            networkFilename = cmd.getOptionValue("networkFilename");
        } else {
            networkFilename = "network_out_default";
        }


        MultiLayerPerceptron network = new MultiLayerPerceptron(125, 30, 4);

        //Create data set
        DataSet dataSet = new DataSet(4);
        Map<String, World> gameWorldMap = new HashMap<>();
        for (int i = 0; i < trainingSize; i++) {
            DataSetRow dataSetRow = new DataSetRow(1, 1, 1, 1);
            String label = "data-" + i;
            dataSetRow.setLabel(label);
            dataSet.addRow(dataSetRow);
            gameWorldMap.put(label, generateRandomWorld(network));
        }

        GraemeSimulatedAnnealing gsa = new GraemeSimulatedAnnealing(network, startTemperature, stopTemperature, alpha, maxTicks, gameWorldMap, iterationsPerTemperature, percentNeuronsChange);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> network.save(networkFilename)));
        gsa.learn(dataSet);
    }


}
