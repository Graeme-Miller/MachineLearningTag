package com.mayorgraeme;

import com.mayorgraeme.occupant.Carnivore;
import com.mayorgraeme.occupant.Herbivore;
import com.mayorgraeme.occupant.Occupant;
import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.util.NeuralNetworkCODEC;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.mayorgraeme.WorldServices.cloneWorld;
import static com.mayorgraeme.WorldServices.generateRandomWorld;

/**
 * This class implements a simulated annealing learning rule for supervised
 * neural networks. It is based on the generic SimulatedAnnealing class. It is
 * used in the same manner as any other training class that implements the
 * SupervisedLearning interface.
 * <p/>
 * Simulated annealing is a common training method. It is often used in
 * conjunction with a propagation training method. Simulated annealing can be
 * very good when propagation training has reached a local minimum.
 * <p/>
 * The name and inspiration come from annealing in metallurgy, a technique
 * involving heating and controlled cooling of a material to increase the size
 * of its crystals and reduce their defects. The heat causes the atoms to become
 * unstuck from their initial positions (a local minimum of the internal energy)
 * and wander randomly through states of higher energy; the slow cooling gives
 * them more chances of finding configurations with lower internal energy than
 * the initial one.
 *
 * @author Jeff Heaton (http://www.jeffheaton.com)
 */
public class GraemeSimulatedAnnealing extends SupervisedLearning {

    static Random rand = new Random();

    /**
     * The serial id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The neural network that is to be trained.
     */
    protected NeuralNetwork network;

    /**
     * The starting temperature.
     */
    private double startTemperature;

    /**
     * The ending temperature.
     */
    private double stopTemperature;

    /**
     * The number of cycles that will be used.
     */
    private int cycles;

    /**
     * The current temperature.
     */
    protected double temperature;

    /**
     * Current weights from the neural network.
     */
    private double[] weights;

    /**
     * Best weights so far.
     */
    private double[] bestWeights;

    private Map<String, Occupant[][]> gameWorldMap;

    /**
     * Construct a simulated annleaing trainer for a feedforward neural network.
     *
     * @param network   The neural network to be trained.
     * @param startTemp The starting temperature.
     * @param stopTemp  The ending temperature.
     * @param cycles    The number of cycles in a training iteration.
     */
    public GraemeSimulatedAnnealing(final NeuralNetwork network,
                                      final double startTemp, final double stopTemp, final int cycles) {
        this.network = network;
        this.temperature = startTemp;
        this.startTemperature = startTemp;
        this.stopTemperature = stopTemp;
        this.cycles = cycles;

        this.weights = new double[NeuralNetworkCODEC
                .determineArraySize(network)];
        this.bestWeights = new double[NeuralNetworkCODEC
                .determineArraySize(network)];

        NeuralNetworkCODEC.network2array(network, this.weights);
        NeuralNetworkCODEC.network2array(network, this.bestWeights);
    }

    public GraemeSimulatedAnnealing(final NeuralNetwork network, final Map<String, Occupant[][]> gameWorldMap) {
        this(network, 10, 2, 60); //TODO:reset to 1000;
        this.setMaxIterations(40);
        this.gameWorldMap = gameWorldMap;
    }

    /**
     * Get the best network from the training.
     *
     * @return The best network.
     */
    public NeuralNetwork getNetwork() {
        return this.network;
    }

    /**
     * Randomize the weights and thresholds. This function does most of the work
     * of the class. Each call to this class will randomize the data according
     * to the current temperature. The higher the temperature the more
     * randomness.
     */
    public void randomize() {

        for (int i = 0; i < this.weights.length; i++) {
            double add = 0.5 - (Math.random());
            add /= this.startTemperature;
            add *= this.temperature;
            this.weights[i] = this.weights[i] + add;
        }

        array2network(this.weights, this.network);
    }

    /**
     * Totally ignore training set.
     * For each DataSetRow run the game witha  random starting point
     * The error for one row is 100 - ticks. That is, if the herbivore lasted 100 ticks
     * then there is an error or zero. The shorter amount of ticks, the larger the error.
     * The error for the training set is the average of the error for the rows.
     */
    private double determineError(DataSet trainingSet) {
        double result = 0d;

        Iterator<DataSetRow> iterator = trainingSet.iterator();
        while (iterator.hasNext() && !isStopped()) {
            DataSetRow trainingSetRow = iterator.next();

            GameInstance gi = new GameInstance(cloneWorld(gameWorldMap.get(trainingSetRow.getLabel())), network, 100, false, 0);
            int ticks = gi.run();
//            System.out.println(trainingSetRow.getLabel() + " "+ (100d - ticks));
            result += 100d - ticks;
        }

//        System.out.println("Final: " + result + "/" + trainingSet.size()+ " = "+ result/trainingSet.size());
        return result/trainingSet.size();
    }

    public static void array2network(double[] array, NeuralNetwork network) {
        int index = 0;

        for (Layer layer : network.getLayers()) {
            for (Neuron neuron : layer.getNeurons()) {
                for (Connection connection : neuron.getOutConnections()) {
                    connection.getWeight().setValue(array[index++]);
                    //connection.getWeight().setPreviousValue(array[index++]);
                }
            }
        }
    }

    /**
     * Perform one simulated annealing epoch.
     */
    @Override
    public void doLearningEpoch(DataSet trainingSet) {

        System.arraycopy(this.weights, 0, this.bestWeights, 0,
                this.weights.length);

        double bestError = determineError(trainingSet);
        double startError = bestError;

        this.temperature = this.startTemperature;

        for (int i = 0; i < this.cycles; i++) {

            randomize();
            double currentError = determineError(trainingSet);

            if (currentError < bestError) {
                System.arraycopy(this.weights, 0, this.bestWeights, 0,
                        this.weights.length);
                bestError = currentError;
            } else
                System.arraycopy(this.bestWeights, 0, this.weights, 0,
                        this.weights.length);

            array2network(this.bestWeights, network);

            final double ratio = Math.exp(Math.log(this.stopTemperature
                    / this.startTemperature)
                    / (this.cycles - 1));
            this.temperature *= ratio;

            System.out.println("Iteration " + getCurrentIteration() + "/"+ getMaxIterations() +" Cycle "+i+"/"+this.cycles+" Start Error "+startError + " bestError "+bestError);
        }

        this.previousEpochError = getErrorFunction().getTotalError();
        //TODO WHAT IS THIS????
//		this.totalNetworkError = bestError;

        // moved stopping condition to separate method hasReachedStopCondition()
        // so it can be overriden / customized in subclasses
        if (hasReachedStopCondition()) {
            stopLearning();
        }
    }

    /**
     * Update the total error.
     */
//    protected void updateTotalNetworkError(double[] patternError) {
//        double sqrErrorSum = 0;
//        for (double error : patternError) {
//            sqrErrorSum += (error * error);
//        }
//
//        //TODO WHAT IS THIS?????
////		this.totalNetworkError += sqrErrorSum / (2 * patternError.length);
//    }

    /**
     * Not used.
     */
    @Override
    protected void updateNetworkWeights(double[] patternError) {

    }

}
