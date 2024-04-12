import java.io.IOException;
import java.util.*;

public class LanguageClassifier {
    private Map<String, Perceptron> perceptrons = new HashMap<>();
    private static final double LEARNING_RATE = 0.1;

    public void train(String language, List<String> trainingPaths) throws IOException {
        Perceptron perceptron = new Perceptron();
        for (String path : trainingPaths) {
            double[] features = TextProcessor.processText(path);
            for (Map.Entry<String, Perceptron> entry : perceptrons.entrySet()) {
                int expected = entry.getKey().equals(language) ? 1 : 0;
                entry.getValue().learn(LEARNING_RATE, expected, entry.getValue().activate(features), features);
            }
        }
        perceptrons.put(language, perceptron);
    }

    public String classify(String textPath) throws IOException {
        double[] features = TextProcessor.processText(textPath);
        String bestLanguage = null;
        double maxActivation = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, Perceptron> entry : perceptrons.entrySet()) {
            double activation = entry.getValue().activate(features);
            if (activation > maxActivation) {
                maxActivation = activation;
                bestLanguage = entry.getKey();
            }
        }
        return bestLanguage;
    }
}
