import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    private static LanguageClassifier classifier;

    static {
        try {
            classifier = new LanguageClassifier("data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Czy chcialbys podac sciezke do pliku czy wprowadzic samodzielnie tekst do klasyfikacji? (file/text/exit)");
            String choice = scanner.nextLine();
            if (choice.equals("exit")) break;

            switch (choice) {
                case "file" -> handleFileInput(scanner);
                case "text" -> handleTextInput(scanner);
                default -> System.out.println("Nieprawidlowa opcja. Wpisz 'file', 'text', lub 'exit'.");
            }
        }
        scanner.close();
    }

    private static void handleFileInput(Scanner scanner) {
        System.out.println("Wprowadz sciezke do pliku:");
        String filePath = scanner.nextLine();
        try {
            String text = Files.readString(Path.of(filePath));
            String predictedLanguage = classifier.classify(text);
            System.out.println("\nPrzewidywany język: " + predictedLanguage);
        } catch (IOException e) {
            System.out.println("Nie można odczytać pliku. Upewnij się, że ścieżka jest poprawna.");
        }
    }

    private static void handleTextInput(Scanner scanner) {
        System.out.println("Wpisz tekst oraz wcisnij enter gdy skonczysz:");
        String text = scanner.nextLine();
        String predictedLanguage = classifier.classify(text);
        System.out.println("\nPrzewidywany jezyk: " + predictedLanguage);
    }
}
