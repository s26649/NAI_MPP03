import java.util.Arrays;

public class TextProcessor {

    public static double[] calculateProportions(String text) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");
        double[] proportions = new double[Perceptron.DIMENSIONS];

        for (char c : text.toCharArray()) {
            int index = c - 'A';
            if (index >= 0 && index < Perceptron.DIMENSIONS) {
                proportions[index]++;
            }
        }

        double total = Arrays.stream(proportions).sum();
        for (int i = 0; i < proportions.length; i++) {
            proportions[i] /= total;
        }
        return proportions;
    }
}
