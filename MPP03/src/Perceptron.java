import java.util.Random;

public class Perceptron {
    private final double[] weights;
    private double bias;
    private final String language;

    protected static final int DIMENSIONS = 26; // alfabet A-Z

    public Perceptron(String language) {
        this.language = language;
        Random random = new Random();
        this.weights = new double[DIMENSIONS];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = random.nextDouble();
        }
        bias = random.nextDouble() + 0.1; // wartosc losowa z zakresu (0 , 1)
    }

    public double activate(double[] inputs) {
        double sum = - bias;
        for (int i = 0; i < DIMENSIONS; i++) {
            sum += weights[i] * inputs[i];
        }
        return sum;
    }

    public void learn(double learningRate, int actual, int prediction, double[] input) {
        int error = actual - prediction;
        for (int i = 0; i < weights.length; i++) {
            weights[i] += error * learningRate * input[i]; // w' = w - (d - y) * stala uczenia * wektor wejsciowy
        }
        bias -= error * learningRate;
    }

    public String getLanguage() {
        return language;
    }
}
