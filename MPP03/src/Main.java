import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static LanguageClassifier classifier = new LanguageClassifier();

    public static void main(String[] args) throws IOException {
        classifier.loadTrainingData("data");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Czy chcialbys podac sciezke do pliku czy wprowadzic samodzielnie tekst do klasyfikacji? (file/text/exit)");
            String choice = scanner.nextLine();
            if (choice.equals("exit")) break;

            switch (choice) {
                case "file":
                    System.out.println("Wprowadz sciezke do pliku:");
                    String filePath = scanner.nextLine();
                    String predictedLanguage = classifier.classify(filePath, true);
                    System.out.println("Przewidywany język: " + predictedLanguage);
                    break;
                case "text":
                    System.out.println("Wpisz tekst oraz wcisnij enter gdy skonczysz:");
                    String text = scanner.nextLine();
                    predictedLanguage = classifyText(text);
                    System.out.println("Przewidywany język: " + predictedLanguage);
                    break;
                default:
                    System.out.println("Nieprawidlowa opcja. Wpisz 'file', 'text', lub 'exit'.");
            }
        }
        scanner.close();
    }

    private static String classifyText(String text) throws IOException {
        File tempFile = File.createTempFile("temp", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(text);
        }
        String result = classifier.classify(tempFile.getAbsolutePath(), true);
        tempFile.delete();
        return result;
    }
}
