import java.util.Scanner;
public class Main {
    private final static Scanner sc = new Scanner(System.in);
    public static Scanner getScanner(){
        return sc;
    }
    public static void main(String[] args) {
        UI console = new UI();
        console.accessUI();
        sc.close();
    }
}
