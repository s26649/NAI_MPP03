import java.util.Random;

public class Perceptron {
    private double[] weights;
    protected static final int DIMENSIONS = 26; // alfabet A-Z

    public Perceptron() {
        Random random = new Random();
        this.weights = new double[DIMENSIONS];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = -5 + 10 * random.nextDouble(); //wartosc losowa z zakresu (-5 , 5) dla wagi i-tego atrybutu
        }
    }

    public int activate(double[] inputs) {
        double sum = 0.0;
        for (int i = 0; i < DIMENSIONS; i++) {
            sum += weights[i] * inputs[i];
        }
        return sum >= 0 ? 1 : 0;
    }

    public void learn(double learningRate, int actual, int prediction, double[] input) {
        int error = actual - prediction;
        for (int i = 0; i < weights.length; i++) {
            weights[i] += error * learningRate * input[i]; //w' = w - (d - y) * stala uczenia * wektor wejsciowy
        }
    }

    public double[] getWeights() {
        return weights;
    }
}
