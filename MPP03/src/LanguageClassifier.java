import java.io.File;
import java.io.IOException;
import java.util.*;

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
                List<double[]> featureVectors = new ArrayList<>();
                for (File file : textFiles) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        double[] features = TextProcessor.processText(file.getAbsolutePath());
                        featureVectors.add(features);
                        train(language, features);
                    }
                }
                System.out.println("Trening zakonczony dla jezyka " + language + " z wektorem proporcji:");
                printAverageVector(featureVectors);
                System.out.println();
            }
        }
    }

    private void train(String language, double[] features) {
        Perceptron perceptron = perceptrons.computeIfAbsent(language, k -> new Perceptron());
        for (Perceptron other : perceptrons.values()) {
            int expectedOutput = (other == perceptron) ? 1 : 0;
            other.learn(LEARNING_RATE, expectedOutput, other.activate(features), features);
        }
    }

    public String classify(String textPath, boolean printDetails) throws IOException {
        double[] features = TextProcessor.processText(textPath);
        double[] normalizedFeatures = normalizeVector(features);
        String bestLanguage = null;
        double maxActivation = Double.NEGATIVE_INFINITY;
        double[] activations = new double[perceptrons.size()];
        List<String> languages = new ArrayList<>(perceptrons.keySet());

        for (int i = 0; i < languages.size(); i++) {
            Perceptron perceptron = perceptrons.get(languages.get(i));
            double[] normalizedWeights = normalizeVector(perceptron.getWeights());
            double dotProduct = dotProduct(normalizedWeights, normalizedFeatures);
            activations[i] = dotProduct;
            if (dotProduct > maxActivation) {
                maxActivation = dotProduct;
                bestLanguage = languages.get(i);
            }
        }

        if (printDetails) {
            System.out.println("Wyniki aktywacji: " + Arrays.toString(activations));
        }

        return bestLanguage;
    }

    private double[] normalizeVector(double[] vector) {
        double length = Math.sqrt(Arrays.stream(vector).map(x -> x * x).sum());
        return Arrays.stream(vector).map(x -> x / length).toArray();
    }

    private double dotProduct(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    private void printAverageVector(List<double[]> featureVectors) {
        double[] average = new double[26];
        for (double[] vector : featureVectors) {
            for (int i = 0; i < vector.length; i++) {
                average[i] += vector[i];
            }
        }
        int n = featureVectors.size();
        for (int i = 0; i < average.length; i++) {
            average[i] /= n;
        }
        for (int i = 0; i < average.length; i++) {
            char letter = (char) ('a' + i);
            System.out.printf("%c: %.4f ", letter, average[i]);
        }
        System.out.println();
    }

}
