import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TaskOperations {
    class Task {
        private String taskId;
        private String title;
        private String description;
        private int priority; // (Low - 0, Medium - 1, High - 2);
        private LocalDateTime dateAndTime;
        private LocalDateTime deadLineDateAndTime;
        private String status = "Pending";
        private String category;

        Task() {
        }

        public String toString() {
            return taskId + "%" + title + "%" + description + "%" + priority + "%" + dateAndTime + "%"
                    + deadLineDateAndTime + "%" + status + "%" + category;
        }
    }

    private Task t;
    private Utility u;
    private final Scanner sc = Main.getScanner();
    private Set<String> allocatedTaskIds = new HashSet<>();
    private List<Task> allTasks = new ArrayList<>();

    TaskOperations() {
        u = new Utility();
    }

    private File getTasksFile() {
        final File dir = new File("./data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File tasks = new File(dir, "Tasks.txt");
        try {
            if (!tasks.exists()) {
                tasks.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private boolean loadExistingIds() {
        allocatedTaskIds.clear();
        File tasks = this.getTasksFile();
        try (FileReader fr = new FileReader(tasks);) {
            BufferedReader br = new BufferedReader(fr);
            String task;
            while ((task = br.readLine()) != null) {
                String[] taskFields = task.split("%");
                allocatedTaskIds.add(taskFields[0]);
            }
            return true;
        } catch (IOException e) {
            // e.printStackTrace();
            return false;
        }
    }

    private int idExists(String id) {
        if (this.loadExistingIds() == false) {
            return -1;
        }
        if (this.allocatedTaskIds.contains(id)) {
            return 1;
        }
        return 0;
    }

    private Supplier<String> generateRandomNumber = () -> {
        StringBuilder sb = new StringBuilder();
        sb.append("TI");
        for (int i = 0; i < 8; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    };

    private LocalDateTime setDateAndTime(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time);
    }

    private Function<File, List<Task>> getTasks = (tasksFile) -> {
        List<Task> taskList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(tasksFile));) {
            String data;
            while ((data = br.readLine()) != null) {
                String[] taskFields = data.split("%");
                Task obj = new Task();
                obj.taskId = taskFields[0];
                obj.title = taskFields[1];
                obj.description = taskFields[2];
                obj.priority = Integer.parseInt(taskFields[3]);
                obj.dateAndTime = LocalDateTime.parse(taskFields[4]);
                obj.deadLineDateAndTime = LocalDateTime.parse(taskFields[5]);
                obj.status = taskFields[6];
                obj.category = taskFields[7];
                taskList.add(obj);
            }
            return taskList;
        } catch (IOException e) {
            System.out.println("Something went wrong while accessing your tasks. Please try again.");
            return new ArrayList<>();
        }
    };

    private Consumer<List<Task>> printTasks = (taskList) -> {
        taskList.stream().forEach(
                e -> {
                    System.out.println("\n--------------------------\nTask : " + e.taskId);
                    System.out.println("Title: " + e.title + "\nDescription: " + e.description + "\nPriority: "
                            + e.priority + "\nCreated Date and time : " + e.dateAndTime + "\nDeadLine: "
                            + e.deadLineDateAndTime + "\nStatus: " + e.status + "\nCategory: " + e.category);
                    System.out.println("--------------------------\n");
                });
    };

    private BiFunction<List<Task>, Integer, List<Task>> filterByPriority = (taskList, val) -> {
        List<Task> filteredListByPriority = taskList.stream()
                .filter(e -> e.priority == val)
                .collect(Collectors.toList());
        return filteredListByPriority;
    };

    private BiFunction<List<Task>, LocalDate, List<Task>> filterByDate = (taskList, date) -> {
        List<Task> filteredListByPriority = taskList.stream()
                .filter(e -> ((e.deadLineDateAndTime.toLocalDate().compareTo(date) != 1)
                        && (e.deadLineDateAndTime.toLocalDate().compareTo(LocalDate.now()) != -1)))
                .collect(Collectors.toList());
        return filteredListByPriority;
    };

    private BiFunction<List<Task>, String, List<Task>> filterByStatus = (taskList, statusString) -> {
        List<Task> filteredListByPriority = taskList.stream()
                .filter(e -> e.status.toLowerCase().equals(statusString))
                .collect(Collectors.toList());
        return filteredListByPriority;
    };

    private BiFunction<List<Task>, String, List<Task>> filterByCategory = (taskList, categoryString) -> {
        return taskList.stream()
                .filter(e -> e.category.toLowerCase().equals(categoryString.toLowerCase()))
                .collect(Collectors.toList());
    };

    private BiFunction<List<Task>, String, Task> getTask = (taskList, id) -> {
        return taskList.stream().filter(t -> t.taskId.equals(id)).findFirst().orElse(null);
    };

    public void createTask() {
        this.t = new Task();
        String data;
        this.t.taskId = generateRandomNumber.get();
        int value = this.idExists(this.t.taskId);
        if (value == -1) {
            System.out.println("Something went wrong...");
            return;
        }
        while (value == 1) {
            this.t.taskId = generateRandomNumber.get();
            value = this.idExists(this.t.taskId);
            if (value == -1) {
                System.out.println("Something went wrong...");
                return;
            }
        }
        allocatedTaskIds.add(this.t.taskId);
        System.out.println("Task Id : " + this.t.taskId);
        System.out.print("Enter the task title (short name, e.g., Prepare Report): ");
        this.t.title = sc.nextLine().trim();
        System.out.print("Enter task description (details about the task): ");
        this.t.description = sc.nextLine();
        while (true) {
            System.out.print("Set task priority (0=Low, 1=Medium, 2=High): ");
            data = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(data);
                this.t.priority = val;
                break;
            } catch (NumberFormatException e) {
                System.out.println("You have entered incorrect format, please enter integer");
            }
        }
        this.t.dateAndTime = LocalDateTime.now().withNano(0);
        while (true) {
            System.out.print("Enter the deadline date to complete this task (yyyy-mm-dd): ");
            String dateString = sc.nextLine().trim();
            LocalDate date = u.parseDate(dateString);
            if (date != null) {
                System.out.print("Do you want to specify a time as well? (Y/N): ");
                String checkString = sc.nextLine().trim().toLowerCase();
                if (checkString.equals("y")) {
                    while (true) {
                        System.out.println("Enter the deadline time to complete this task (hh:mm:ss): ");
                        String timeString = sc.nextLine().trim();
                        LocalTime time = u.parseTime(timeString);
                        if (time != null) {
                            this.t.deadLineDateAndTime = this.setDateAndTime(date, time);
                            break;
                        } else {
                            System.out.println("Invalid time! Please enter in hh:mm:ss format.");
                        }
                    }
                } else if (checkString.equals("n")) {
                    LocalTime time = u.parseTime("23:59:59");
                    this.t.deadLineDateAndTime = this.setDateAndTime(date, time);
                } else {
                    System.out.println("Invalid choice! Only 'Y' or 'N' are accepted.");
                }
                break;
            } else {
                System.out.println("Invalid date! Please enter in yyyy-mm-dd format.");
            }
        }
        while (true) {
            System.out.print("Do you want to specify a category? (Y/N): ");
            data = sc.nextLine().trim().toLowerCase();
            if (data.equals("y")) {
                System.out.print("Enter the category: ");
                this.t.category = sc.nextLine();
                break;
            } else if (data.equals("n")) {
                this.t.category = "General";
                break;
            } else {
                System.out.println("Invalid choice! Only 'Y' or 'N' are accepted.");
            }
        }
    }

    public void addTask() {
        File tasks = this.getTasksFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(tasks, true))) {
            createTask();
            pw.println(t);
        } catch (IOException e) {
            System.out.println("Something went wrong while accessing your tasks. Please try again.");
        }
    }

    public void viewTask() {
        System.out.println("---------- View Tasks ----------");
        allTasks = getTasks.apply(this.getTasksFile());
        if (allTasks.size() > 0) {
            System.out.println("Here are all your tasks:");
            printTasks.accept(allTasks);
            System.out.println("\nDo you want to filter the tasks?");
            System.out.println("1. By Priority");
            System.out.println("2. By Date");
            System.out.println("3. By Status");
            System.out.println("4. By Category");
            System.out.println("5. No, show all (Go Back)");
            System.out.print("Please choose an option (1-5): ");
            String option = sc.nextLine().trim();
            switch (option) {
                case "1":
                    System.out.print("\nEnter the priority to filter by (0 = Low, 1 = Medium, 2 = High): ");
                    String priorityString = sc.nextLine().trim();
                    int value;
                    try {
                        value = Integer.parseInt(priorityString);
                        if (value >= 0 && value <= 2) {
                            this.allTasks = this.filterByPriority.apply(this.getTasks.apply(this.getTasksFile()),
                                    value);
                            if (this.allTasks.size() > 0) {
                                System.out.println("Here are all your tasks:");
                                printTasks.accept(this.allTasks);
                            } else {
                                System.out.println("No Tasks Found");
                            }
                        } else {
                            System.out.println("Invalid input! Please enter a valid priority number (0, 1, or 2).");
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid format! Please enter Integer");
                    }
                    break;
                case "2":
                    System.out.print("\nEnter the deadline date to filter by (yyyy-mm-dd): ");
                    String dateString = sc.nextLine().trim();
                    LocalDate date = u.parseDate(dateString);
                    if (date == null) {
                        System.out.println("Invalid date format! Please enter the date in (yyyy-mm-dd) format.");
                    } else {
                        this.allTasks = this.filterByDate.apply(this.getTasks.apply(this.getTasksFile()), date);
                        if (this.allTasks.size() > 0) {
                            System.out.println("Here are all your tasks:");
                            printTasks.accept(this.allTasks);
                        } else {
                            System.out.println("No Tasks Found");
                        }
                    }
                    break;
                case "3":
                    System.out.print("\nEnter the status to filter by (Pending or Completed): ");
                    String checkString = sc.nextLine().trim().toLowerCase();
                    if (checkString.equals("pending") || checkString.equals("completed")) {
                        String statusString = checkString.equalsIgnoreCase("completed") ? "Completed" : "Pending";
                        this.allTasks = this.filterByStatus.apply(this.getTasks.apply(this.getTasksFile()),
                                statusString);
                        if (this.allTasks.size() > 0) {
                            System.out.println("Here are all your tasks:");
                            printTasks.accept(this.allTasks);
                        } else {
                            System.out.println("No Tasks Found");
                        }
                    } else {
                        System.out.println("Invalid status! Please enter 'Pending' or 'Completed'.");
                    }
                    break;
                case "4":
                    System.out.print("\nEnter the status to filter by Category: ");
                    String categoryString = sc.nextLine().trim();
                    this.allTasks = this.filterByCategory.apply(this.getTasks.apply(this.getTasksFile()),
                            categoryString);
                    if (this.allTasks.size() > 0) {
                        System.out.println("Here are all your tasks:");
                        printTasks.accept(this.allTasks);
                    } else {
                        System.out.println("No Tasks found with that category");
                    }
                    break;
                case "5":
                    break;
                default:
                    System.out.println("Invalid input! Please enter a valid option (0, 1, 2, or 4).");
                    break;
            }
        } else {
            System.out.println("No Tasks Found");
        }
    }

    private BiFunction<List<Task>, String, List<Task>> deleteTask = (tasks, tId) -> {
        return tasks.stream().filter(task -> !task.taskId.equals(tId)).collect(Collectors.toList());
    };
    private BiConsumer<List<Task>, File> rewriteFile = (tasks, file) -> {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            tasks.stream().forEach(task -> pw.println(task));
        } catch (IOException ioe) {
            System.out.println("Something went wrong while accessing your tasks. Please try again.");
        }
    };

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
    public void editTask() {
        if (this.askYesOrNo("Do you want to view tasks?")) {
            viewTask();
        }
        System.out.print("Please enter the Task ID of the task you would like to edit: ");
        String id = sc.nextLine().trim();
        this.loadExistingIds();
        int idCheck = idExists(id);
        if (idCheck == 1) {
            Task task = getTask.apply(getTasks.apply(getTasksFile()), id);
            String titleString = task.title;
            String descriptionString = task.description;
            int priorityString = task.priority;
            LocalDateTime deadLineString = task.deadLineDateAndTime;
            String statusString = task.status;
            String categoryString = task.category;
            if(this.askYesOrNo("Do you want to edit title?")){
                System.out.print("Enter the new title: ");
                titleString = sc.nextLine().trim();
            }
            if (this.askYesOrNo("Do you want to edit description?")) {
                System.out.print("Enter the new description: ");
                descriptionString = sc.nextLine().trim();
            } 
            if (this.askYesOrNo("Do you want to edit priority?")) {
                while (true) {
                    System.out.print("Enter the new priority: ");
                    String pString = sc.nextLine().trim();
                    try {
                        priorityString = Integer.parseInt(pString);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid format! Please enter integer");
                    }
                }
            }
            if (this.askYesOrNo("Do you want to edit deadLine?")) {
                while(true){
                    System.out.print("Enter the new deadLine (yyyy-mm-ddTHH:mm:ss): ");
                    String dLString = sc.nextLine().trim();
                    deadLineString = u.parseDateTime(dLString);
                    if(deadLineString == null){
                        System.out.println("Invalid date and time format");
                    }
                    else{
                        break;
                    }
                }
            } 
            if (this.askYesOrNo("Do you want to edit status?")) {
                while (true) {
                    System.out.print("Enter the new status (Pending/Completed): ");
                    String checkString = sc.nextLine().trim();
                    if (checkString.toLowerCase().equals("completed")
                            || checkString.toLowerCase().equals("pending")) {
                        statusString = checkString.equalsIgnoreCase("completed") ? "Completed" : "Pending";
                        break;
                    } else {
                        System.out.println("Invalid choice! Only 'Completed' or 'Pending' are accepted.");
                        continue;
                    }
                }
            }
            if (this.askYesOrNo("Do you want to edit category?")) {
                System.out.print("Enter the new category: ");
                categoryString = sc.nextLine().trim();
            }
            if (this.askYesOrNo("Do want to save changes?")) {
                task.title = titleString;
                task.description = descriptionString;
                task.priority = priorityString;
                task.deadLineDateAndTime = deadLineString;
                task.status = statusString;
                task.category = categoryString;
                this.allTasks = deleteTask.apply(getTasks.apply(getTasksFile()), task.taskId);
                allTasks.add(task);
                rewriteFile.accept(this.allTasks, this.getTasksFile());
                System.out.println("Successfully edited and saved.");
            } else{
                System.out.println("Task not saved....");
            }
        } else if (idCheck == 0) {
            System.out.println("Invalid Task ID. Please enter a valid one.");
        } else {
            System.out.println("File could not be loaded. Try again later.");
        }
    }

    public void deleteTask() {
        if (this.askYesOrNo("Do you want to view tasks?")) {
            viewTask();
        }
        System.out.print("Please enter the Task ID of the task you would like to delete: ");
        String id = sc.nextLine().trim();
        this.loadExistingIds();
        if (idExists(id) == 1) {
            System.out.println("Are you sure you want to delete this task? (Y/N): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            if (confirm.equals("y")) {
                this.allTasks = deleteTask.apply(getTasks.apply(getTasksFile()), id);
                rewriteFile.accept(this.allTasks, this.getTasksFile());
                System.out.println("Successfully deleted task.");
            } else if (confirm.equals("n")) {
                System.out.println("Task not deleted....");
            } else {
                System.out.println("Invalid choice! Only 'Y' or 'N' are accepted.");
            }
            return;
        } else if (idExists(id) == 0) {
            System.out.println("Invalid Task ID. Please enter a valid one.");
        } else {
            System.out.println("File could not be loaded. Try again later.");
        }
    }
}
