import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LanguageClassifier {
    private Map<String, Perceptron> perceptrons = new HashMap<>();
    private static final double LEARNING_RATE = 0.1;

    public void loadTrainingData(String dataDirectory) throws IOException {
        File dir = new File(dataDirectory);
        File[] languageFolders = dir.listFiles();

        for (File folder : languageFolders) {
            if (folder.isDirectory()) {
                String language = folder.getName();
                File[] textFiles = folder.listFiles();
                for (File file : textFiles) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        train(language, file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private void train(String language, String filePath) throws IOException {
        double[] features = TextProcessor.processText(filePath);
        Perceptron perceptron = perceptrons.computeIfAbsent(language, k -> new Perceptron());
        for (Perceptron other : perceptrons.values()) {
            int expectedOutput = (other == perceptron) ? 1 : 0;
            other.learn(LEARNING_RATE, expectedOutput, other.activate(features), features);
        }
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
