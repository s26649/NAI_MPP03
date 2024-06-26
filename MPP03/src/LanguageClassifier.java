import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LanguageClassifier {
    private final Perceptron[] perceptrons;
    private static final double LEARNING_RATE = 0.1;
    private static final double SATISFACTORY_ACCURACY = 0.95;

    public LanguageClassifier(String trainingDataDir) throws IOException { // pobiera etykiety z jezykami, tworzy tablice (warstwe) perceptronow, inicjalizije perceptrony dla kazdego z jezykow, wczytuje dane treningowe
        String[] labels = getLabels(trainingDataDir);
        perceptrons = new Perceptron[labels.length];
        for (int i = 0; i < labels.length; i++) {
            perceptrons[i] = new Perceptron(labels[i]);
        }
        loadTrainingData(trainingDataDir);
    }

    private String[] getLabels(String trainingDataDir) { // otwiera katalog z danymi, filtruje subkatalogi reprezentujace jezyki i zwraca ich nazwy
        try {
            File dir = new File(trainingDataDir);
            return Arrays.stream(Objects.requireNonNull(dir.listFiles(File::isDirectory)))
                    .map(File::getName)
                    .filter(name -> !name.equals(dir.getName()))
                    .toArray(String[]::new);
        } catch (Exception e) {
            throw new RuntimeException("Error podczas wczytywania dirow", e);
        }
    }

    public void loadTrainingData(String dataDirectory) throws IOException { // wczytuje dane treningowe, trenuje perceptrony dla każdego języka, wypisuje wektory proporcji
        File dir = new File(dataDirectory);
        File[] languageFolders = dir.listFiles();
        List<String[]> trainingData = new ArrayList<>();

        if (languageFolders != null) {
            for (File folder : languageFolders) {
                if (folder.isDirectory()) {
                    String language = folder.getName();
                    Perceptron perceptron = findPerceptronByLanguage(language);
                    if (perceptron == null) {
                        continue;
                    }
                    File[] textFiles = folder.listFiles();
                    if (textFiles != null) {
                        for (File file : textFiles) {
                            if (file.isFile() && file.getName().endsWith(".txt")) {
                                String textContent = readFileContent(file.toPath());
                                trainingData.add(new String[]{textContent, language});
                            }
                        }
                    }
                    train(trainingData, language);
                    printAverageVector(trainingData);
                }
            }
        }
    }

    private Perceptron findPerceptronByLanguage(String language) {
        for (Perceptron perceptron : perceptrons) {
            if (perceptron.getLanguage().equals(language)) {
                return perceptron;
            }
        }
        return null;
    }

    private String readFileContent(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    private void train(List<String[]> trainingData, String language) { // uczy perceptrony dostosowując ich wagi do momentu gdy dokładność klasyfikacji przekroczy ustalony próg satysfakcji, wypisuje liczbe iteracji i accuracy treningu
        Collections.shuffle(trainingData);
        boolean improved = true;
        double previousAccuracy = 0.0;
        int iterationCount = 0;

        while (improved) {
            int correct = 0;
            for (String[] data : trainingData) {
                double[] proportions = TextProcessor.calculateProportions(data[0]);
                for (Perceptron perceptron : perceptrons) {
                    int d = data[1].equals(perceptron.getLanguage()) ? 1 : 0;
                    int y = perceptron.activate(proportions) >= 0 ? 1 : 0;
                    if (y == d) correct++;
                    perceptron.learn(LEARNING_RATE, d, y, proportions);
                }
            }
            double currentAccuracy = (double) correct / (trainingData.size() * perceptrons.length);
            if (currentAccuracy > SATISFACTORY_ACCURACY) {
                improved = false;
            }
            previousAccuracy = currentAccuracy;
            iterationCount++;
        }
        System.out.println("Trening zakonczony dla jezyka: " + language);
        System.out.println("Liczba iteracji: " + iterationCount);
        System.out.println("Accuracy: " + previousAccuracy * 100 + "%");
    }

    public String classify(String text) { // przypisuje tekstowi najbardziej prawdopodobny język na podstawie odpowiedzi perceptronów, wyświetla wektor aktywacji
        double[] proportions = TextProcessor.calculateProportions(text);
        double[] outputs = new double[perceptrons.length];
        String bestLanguage = null;
        double maxActivation = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < perceptrons.length; i++) {
            outputs[i] = perceptrons[i].activate(proportions);
            if (outputs[i] > maxActivation) {
                maxActivation = outputs[i];
                bestLanguage = perceptrons[i].getLanguage();
            }
        }

        System.out.print("Wektor aktywacji: (");
        for (int i = 0; i < outputs.length; i++) {
            System.out.printf("%s: %.2f", perceptrons[i].getLanguage(), outputs[i]);
            if (i < outputs.length - 1) System.out.print(", ");
        }
        System.out.println(")");

        return bestLanguage != null ? bestLanguage : "nie wiem";
    }

    private void printAverageVector(List<String[]> trainingData) { // drukuje wektor proporcji
        System.out.println("Wektor proporcji: ");
        double[] average = new double[Perceptron.DIMENSIONS];
        int count = 0;
        for (String[] data : trainingData) {
            double[] proportions = TextProcessor.calculateProportions(data[0]);
            for (int i = 0; i < proportions.length; i++) {
                average[i] += proportions[i];
            }
            count++;
        }
        for (int i = 0; i < average.length; i++) {
            average[i] /= count;
            char letter = (char) ('a' + i);
            System.out.printf("%c: %.4f ", letter, average[i]);
        }
        System.out.println("\n");
    }
}