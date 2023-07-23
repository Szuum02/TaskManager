package pl.coderslab.taskmanager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import pl.coderslab.ConsoleColors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TaskManager {

    static final String[] OPTIONS = {"add", "remove", "list", "exit"};
    static final String FILENAME = "tasks.csv";
    static String[][] tasks;
    static boolean running = true;

    public static void main(String[] args) {
        tasks = getTasksFromFile();
        while (running) {
            showOptions();
            getOptions();
        }
    }

    public static String[][] getTasksFromFile() {
        String stringFromFile = readFile();
        if (stringFromFile == null) {
            return new String[0][3];
        }
        String[] tmpTasks = stringFromFile.split("\n");
        String[][] tasks = new String[tmpTasks.length][3];
        for (int i = 0; i < tmpTasks.length; i++) {
            String[] taskData  = tmpTasks[i].split(",");
            for (int j = 0; j < 3; j++) {
                tasks[i][j] = taskData[j].strip();
            }
        }
        return tasks;
    }

    public static String readFile() {
        Path path = Paths.get(FILENAME);
        if (!Files.exists(path)) {
            System.err.println("File '" + path.toAbsolutePath() + "' doesn't exist");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        try {
            for (String line : Files.readAllLines(path)) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Cannot read tasks from file '" + path.toAbsolutePath() + "' " + e.getMessage());
            return null;
        }
        return sb.toString();
    }

    public static void showOptions() {
        System.out.println(ConsoleColors.BLUE + "Please select an option:");
        for (String option : OPTIONS) {
            System.out.println(ConsoleColors.RESET + option);
        }
    }

    public static void getOptions() {
        Scanner scanner = new Scanner(System.in);
        String option = scanner.next();

        switch (option) {
            case "add":
                addTask();
                break;
            case "remove":
                removeTask();
                break;
            case "list":
                list();
                break;
            case "exit":
                exit();
                break;
            default:
                System.out.println("Select correct option");
        }
    }

    public static void addTask() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please add task description");
        String description = scanner.nextLine();
        String date = checkAndGetDate();
        String importance = checkAndGetImportance();

        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        tasks[tasks.length - 1] = new String[3];
        tasks[tasks.length - 1][0] = description;
        tasks[tasks.length - 1][1] = date;
        tasks[tasks.length - 1][2] = importance;
    }

    public static String checkAndGetDate() {
        System.out.println("Please add task due data: yyyy-mm-dd");
        Scanner scanner = new Scanner(System.in);
        boolean correctDate = false;
        String date = null;
        while (!correctDate) {
            date = scanner.nextLine();
            String[] dateDatas = date.split("-");
            if (dateDatas.length == 3) {
                String year = dateDatas[0].strip();
                String month = dateDatas[1].strip();
                String day = dateDatas[2].strip();
                boolean correctYear = NumberUtils.isParsable(year) && Integer.parseInt(year) >= 0;
                boolean correctMonth = NumberUtils.isParsable(month) && Integer.parseInt(month) >= 1 && Integer.parseInt(month) <= 12;
                boolean correctDay = NumberUtils.isParsable(day) && Integer.parseInt(day) >= 1 && Integer.parseInt(day) <= 31;
                correctDate = correctYear && correctMonth && correctDay;
            }

            if (!correctDate) {
                System.out.println("Invalid argument passed. Please give date in format: yyyy-mm-dd");
            }
        }
        return date;
    }

    public static String checkAndGetImportance() {
        System.out.println("Is your task is important: true/false");
        Scanner scanner = new Scanner(System.in);
        boolean correctImportance = false;
        String importance = null;
        while (!correctImportance) {
            importance = scanner.nextLine().strip();
            correctImportance = importance.equalsIgnoreCase("true") || importance.equalsIgnoreCase("false");

            if (!correctImportance) {
                System.out.println("Invalid argument passed. Please give importance: true/false");
            }
        }
        return importance;
    }

    public static void list() {
        for (int i = 0; i < tasks.length; i++) {
            System.out.print(i + " : ");
            for (String element : tasks[i]) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    public static void removeTask() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select number to remove (-1 exit)");
        boolean correctNumber = false;
        int deleteIdx = 0;
        while (!correctNumber) {
            if (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Incorrect argument passed. Please give number greater or equal 0 and less than " + tasks.length + " (-1 exit)");
            }
            deleteIdx = scanner.nextInt();

            if (deleteIdx == -1) {
                System.out.println("Exit remove section");
                return;
            }

            correctNumber = deleteIdx >= 0 && deleteIdx < tasks.length;

            if (!correctNumber) {
                System.out.println("Incorrect argument passed. Please give number greater or equal 0 and less than " + tasks.length + " (-1 exit)");
            }
        }
        tasks = ArrayUtils.remove(tasks, deleteIdx);
        System.out.println("Task was successfully deleted");
    }

    public static void exit() {
        safeToFile();
        System.out.println(ConsoleColors.RED + "Bye, bye");
        running = false;
    }

    public static void safeToFile() {
        Path path = Paths.get(FILENAME);
        List<String> tasksData = new ArrayList<>();
        for (String[] task : tasks) {
            tasksData.add(String.join(", ", task));
        }

        try {
            Files.write(path, tasksData);
        } catch (IOException e) {
            System.err.println("Cannot write tasks to file " + e.getMessage());
        }
    }

}
