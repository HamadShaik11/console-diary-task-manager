import java.util.Scanner;

public class UI {
    Scanner sc = Main.getScanner();

    public void accessUI() {
        boolean canAccess = true;
        System.out.println("*****************************************");
        System.out.println("   Welcome to Personal Diary & Planner     ");
        System.out.println("*****************************************");

        while (canAccess) {
            System.out.println("\n============== MAIN MENU ==============");
            System.out.println("1. Diary\n2. Tasks\n3. Exit");
            System.out.println("=======================================");
            System.out.print("Choose one option from the menu: ");
            String option = sc.nextLine().trim();
            switch (option) {
                case "1":
                    this.diaryMenu();
                    break;
                case "2":
                    this.taskMenu();
                    break;
                case "3":
                    canAccess = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please enter a valid menu option.");
                    break;
            }
        }
        System.out.println("*****************************************");
        System.out.println("   Thank you! Exiting the application... ");
        System.out.println("*****************************************");
    }

    public void diaryMenu() {
        boolean canAccess = true;
        DiaryOperations diaryOperation = new DiaryOperations();
        while (canAccess) {
            System.out.println("\n-------------------\nDiary Operations\n-------------------");
            System.out.println(
                    "1. Add Diary Entry\n2. View Diary Entry\n3. Edit Diary Entry\n4. Delete Diary Entry\n5. Back to Main Menu");
            System.out.println("-------------------\n");
            System.out.print("Choose one option from the menu: ");
            String option = sc.nextLine().trim();
            switch (option) {
                case "1":
                    diaryOperation.addDiaryEntry();
                    break;
                case "2":
                    diaryOperation.viewDiaryEntry();
                    break;
                case "3":
                    diaryOperation.editDiaryEntry();
                    break;
                case "4":
                    diaryOperation.deleteDiaryEntry();
                    break;
                case "5":
                    canAccess = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please enter a valid menu option.");
                    break;
            }
        }
    }

    public void taskMenu() {
        TaskOperations taskOperation = new TaskOperations();
        boolean canAccess = true;
        while (canAccess) {
            System.out.println("\n-------------------\nTasks Operations\n-------------------");
            System.out.println(
                    "1. Add Task\n2. View Tasks\n3. Edit Task\n4. Delete Task\n5. Back to Main Menu");
            System.out.println("-------------------\n");
            System.out.print("Choose one option from the menu: ");
            String option = sc.nextLine().trim();
            switch (option) {
                case "1":
                    taskOperation.addTask();
                    break;
                case "2":
                    taskOperation.viewTask();
                    break;
                case "3":
                    taskOperation.editTask();
                    break;
                case "4": 
                    taskOperation.deleteTask();
                    break;
                case "5":
                    canAccess = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please enter a valid menu option.");
                    break;
            }
        }
    }
}
