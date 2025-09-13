import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DiaryOperations {
    private Utility u;
    DiaryOperations() {
        u = new Utility();
        this.loadDiaryContent();
    }

    private final Scanner sc = Main.getScanner();
    private Map<LocalDate, String> diaryContent = new HashMap<>();

    private File getdiary() {
        final File dir = new File("./data");
        if (!dir.exists()) {
            dir.mkdir();
        }
        final File diary = new File(dir, "diary.txt");
        try {
            if (!diary.exists()) {
                diary.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while accessing your diary. Please try again.");
        }
        return diary;
    }

    private void loadDiaryContent() {
        File diary = this.getdiary();
        try (FileReader fr = new FileReader(diary)) {
            BufferedReader br = new BufferedReader(fr);
            LocalDate key = null;
            StringBuilder value = new StringBuilder("");
            String line;
            while ((line = br.readLine()) != null) {
                LocalDate date = u.parseDate(line);
                if (date != null) {
                    if (key != null) {
                        diaryContent.put(key, value.toString());
                        value = new StringBuilder();
                    }
                    key = date;
                } else {
                    value.append(line);
                    value.append("\n");
                }
            }
            if (key != null) {
                diaryContent.put(key, value.toString());
                value = new StringBuilder();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while accessing your diary. Please try again.");
        }
    }

    public void addDiaryEntry() {
        File diary = this.getdiary();
        StringBuilder sb = new StringBuilder();
        System.out.print("Enter the date in yyyy-mm-dd format (e.g., 2025-09-12): ");
        String dateString = sc.nextLine().trim();
        LocalDate date = u.parseDate(dateString);
        if (date == null) {
            System.out.println("Invalid date! Please enter in yyyy-mm-dd format.");
            return;
        }
        if (diaryContent.containsKey(date)) {
            System.out.println("Entry already exists for this date. Please use Edit.");
            return;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(diary, true))) {
            if (!diaryContent.containsKey(date)) {
                pw.println(date);
            }
            System.out.print("Enter the content of your diary entry: ");
            while (true) {
                String data = sc.nextLine();
                if (data.trim().equals("")) {
                    if (sb.length() > 0) {
                        pw.print(sb.toString());
                    }
                    pw.flush();
                    diaryContent.put(date, sb.toString());
                    break;
                }
                sb.append(data);
                sb.append("\n");
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while accessing your diary. Please try again.");
        }
    }

    public void viewDiaryEntry() {
        System.out.print("Enter the date in yyyy-mm-dd format (e.g., 2025-09-12): ");
        String dateString = sc.nextLine().trim();
        LocalDate date = u.parseDate(dateString);
        if (date == null) {
            System.out.println("Invalid date! Please enter in yyyy-mm-dd format.");
            return;
        }
        String content = diaryContent.get(date);
        if (content != null) {
            System.out.println(content);
        } else {
            System.out.println("No data found");
        }
    }

    private void reWriteDiary() {
        File diary = this.getdiary();
        diaryContent = diaryContent.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
        try (PrintWriter pw = new PrintWriter(diary)) {
            for (Map.Entry<LocalDate, String> entry : diaryContent.entrySet()) {
                pw.println(entry.getKey());
                pw.print(entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while accessing your diary. Please try again.");
        }

    }

    private boolean askYesOrNo(String s){
        while(true){
            System.out.print(s+" (Y/N): ");
            String ans = sc.nextLine().trim().toLowerCase();
            if(ans.equals("y")){
                return true;
            }
            else if(ans.equals("n")){
                return false;
            }
            else{
                System.out.println("Invalid choice! Only 'Y' or 'N' are accepted.");
            }
        }
    }
    public void editDiaryEntry() {
        LocalDate date = LocalDate.now();
        if (!diaryContent.containsKey(date)) {
            System.out.println("No entry found for today, please use Add option.");
            return;
        }
        String content = diaryContent.get(date);
        System.out.println("~~~~~~~~~~~ Current Entry ~~~~~~~~~~~");
        System.out.println("Date: " + date);
        System.out.println(content);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("\n-------------------");
        System.out.println("      ACTION MENU      ");
        System.out.println("--------------------");
        System.out.println("1. Overwrite");
        System.out.println("2. Append");
        System.out.println("3. Cancel");
        System.out.println("--------------------");
        System.out.print("Enter your choice: ");
        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1":
                StringBuilder sb = new StringBuilder();
                    if(this.askYesOrNo("This will erase your old entry. Continue?")){
                        diaryContent.remove(date);

                        System.out.print("Enter the content of your diary entry to overwrite: ");
                        while (true) {
                            String data = sc.nextLine();
                            if (data.trim().equals("")) {
                                diaryContent.put(date, sb.toString());
                                break;
                            }
                            sb.append(data);
                            sb.append("\n");
                        }
                        reWriteDiary();
                    }
                break;
            case "2":
                sb = new StringBuilder();
                sb.append(content);
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                System.out.print("Enter the content of your diary entry to append: ");
                while (true) {
                    String data = sc.nextLine();
                    if (data.trim().equals("")) {
                        diaryContent.put(date, sb.toString());
                        break;
                    }
                    sb.append("\n");
                    sb.append(data);
                }
                reWriteDiary();
                break;
            case "3":
                break;
            default:
                System.out.println("Invalid selection. Please enter a valid menu option.");
                break;
        }
    }

    public void deleteDiaryEntry() {
        System.out.print("Enter the date of the entry you want to delete (yyyy-mm-dd): ");
        String dateString = sc.nextLine().trim();
        LocalDate date = u.parseDate(dateString);
        if (date == null) {
            System.out.println("Invalid date! Please enter in yyyy-mm-dd format.");
            return;
        }
        loadDiaryContent();
        if(diaryContent.containsKey(date)){
            if (this.askYesOrNo("Are you sure ?")) {
                diaryContent.remove(date);
                reWriteDiary();
                System.out.println("Successfully Deleted Diary Entry");
            } 
        }
        else{
            System.out.println("No Diary entry found on that date");
        }
    }
}