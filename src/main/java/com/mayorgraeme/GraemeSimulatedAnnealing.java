package com.mayorgraeme;

import com.mayorgraeme.occupant.Occupant;
import com.mayorgraeme.world.World;
import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.util.NeuralNetworkCODEC;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.mayorgraeme.world.WorldServices.cloneWorld;

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
    private double startTemperature = 0.5;

    /**
     * The ending temperature.
     */
    private double stopTemperature = 0.001;

    private double alpha = 0.8;

    /**
     * Current weights from the neural network.
     */
    private double[] weights;

    /**
     * Best weights so far.
     */
    private double[] bestWeights;

    private Map<String, World> gameWorldMap;


    public GraemeSimulatedAnnealing(final NeuralNetwork network, final Map<String, World> gameWorldMap) {
        this.setMaxIterations(1);
        this.gameWorldMap = gameWorldMap;

        this.network = network;

        this.weights = new double[NeuralNetworkCODEC
                .determineArraySize(network)];
        this.bestWeights = new double[NeuralNetworkCODEC
                .determineArraySize(network)];

        NeuralNetworkCODEC.network2array(network, this.weights);
        NeuralNetworkCODEC.network2array(network, this.bestWeights);
    }

    /**
     * Get the best network from the training.
     *
     * @return The best network.
     */
    public NeuralNetwork getNetwork() {
        return this.network;
    }


    public void randomizeNeuron() {
        double myDouble = rand.nextInt(100) + 1;
        this.weights[rand.nextInt(this.weights.length)] = myDouble/100d;
    }

    public void randomize() {

        for (int i = 0; i < Math.floor(this.weights.length/100) * 5; i++) {
            randomizeNeuron();
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


            World world = gameWorldMap.get(trainingSetRow.getLabel());
            World clonedWorld = (World)world.clone();

            GameInstance gi = new GameInstance(clonedWorld, network, 100, false, 0);
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

        double temperature = this.startTemperature;

        while (temperature > stopTemperature) {
            for (int i = 0; i < 50; i++) { //TODO: Should be 100?
                randomize();
                double currentError = determineError(trainingSet);

                //Calc acceptance prob
                double acceptanceProbability = Math.pow(2.71828, (bestError - currentError) / temperature);
                double acceptanceProbabilityTimes100 = acceptanceProbability * 100;


                if(acceptanceProbabilityTimes100 >= rand.nextInt(100)+1) {
                    System.arraycopy(this.weights, 0, this.bestWeights, 0,
                            this.weights.length);
                    bestError = currentError;
                } else {
                    System.arraycopy(this.bestWeights, 0, this.weights, 0,
                            this.weights.length);
                }

                array2network(this.bestWeights, network);

                System.out.println("Iteration " + getCurrentIteration() + "/"+ getMaxIterations() + " Start Error "+startError + " bestError "+bestError + " current error "+ currentError+ " temp "+temperature + " acceptanceProbabilityTimes: "+acceptanceProbabilityTimes100);
            }

            temperature *= alpha;


            if (bestError < 7) {
                break;
            }
        }

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
