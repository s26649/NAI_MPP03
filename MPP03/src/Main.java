import java.io.IOException;
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
            System.out.println("Czy chcialbys wprowadzic samodzielnie tekst do klasyfikacji czy wyjsc z programu? (text/exit)");
            String choice = scanner.nextLine();
            if (choice.equals("exit"))
                break;
            else if (choice.equals("text"))
                handleTextInput(scanner);
            else System.out.println("Nieprawidlowa opcja. Wpisz 'file', 'text', lub 'exit'.");
        }
        scanner.close();
    }

    private static void handleTextInput(Scanner scanner) {
        System.out.println("Wpisz tekst oraz wcisnij enter gdy skonczysz:");
        String text = scanner.nextLine();
        String predictedLanguage = classifier.classify(text);
        System.out.println("\nPrzewidywany jezyk: " + predictedLanguage);
    }
}
