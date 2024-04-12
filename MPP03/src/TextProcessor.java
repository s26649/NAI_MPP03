import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextProcessor {

    public static double[] processText(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return convertToFeatureVector(content);
    }

    private static double[] convertToFeatureVector(String text) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        double[] features = new double[Perceptron.DIMENSIONS];
        for (char c : text.toCharArray()) {
            int index = c - 'A';
            if (index >= 0 && index < Perceptron.DIMENSIONS) {
                features[index]++;
            }
        }
        int total = text.length();
        for (int i = 0; i < features.length; i++) {
            features[i] /= total;
        }
        return features;
    }
}
