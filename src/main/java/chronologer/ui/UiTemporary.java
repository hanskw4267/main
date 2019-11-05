package chronologer.ui;

import chronologer.task.Task;
import chronologer.task.TaskList;
import javafx.beans.value.ObservableIntegerValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Ui class is used to handle all inputs and outputs used to interact with
 * the end-user.
 *
 * @author Sai Ganesh Suresh
 * @version v1.0
 */
public class UiTemporary {

    // All the different definitions including the string definitions are here.
    public static String userOutputForUI;
    private ObservableIntegerValue currentTheme;

    private static String userOutputDash = "_______________________________\n";

    private static String userInput;

    private static String goodbye = "_______________________________\n" + "Bye. Hope to see you again soon!\n"
        + "_______________________________\n";

    private static String greeting = "_______________________________\n" + "Hello! I'm Duke\n"
        + "What can I do for you?\n" + "_______________________________\n";

    private static Scanner scanner = new Scanner(System.in);

    private static List<String> multiLineList = new ArrayList<>();
    private static StringBuilder multiLineOutput;

    /**
     * Prints a 'dashed' line.
     */
    public static void printDash() {
        System.out.println(userOutputDash);
    }

    /**
     * Prints greeting.
     */
    public static void printGreeting() {
        System.out.println(greeting);
    }

    /**
     * Prints the reminders triggered.
     */
    public static void printReminder(TaskList tasks) {
        ArrayList<Task> taskList = tasks.getTasks();
        System.out.println("You have these upcoming tasks:\n");
        for (Task t : taskList) {
            if (t.isReminderTrigger()) {
                System.out.println(t.toString());
            }
        }
    }

    public static void printGoodbye() {
        System.out.println(goodbye);
    }

    /**
     * This printOutput function is used to print the output in the current Ui
     * format. Moreover it also handles the refresh of the userOutputForUT.
     *
     * @param userOutput This string will be used to print the message between to
     *                   data.
     */

    public static void printOutput(String userOutput) {
        UiTemporary.userOutputForUI = userOutput + "\n";
        System.out.println(userOutputDash);
        System.out.println(userOutput);
        System.out.println(userOutputDash);
    }

    /**
     * Loads the list of string to be printed with another line of string.
     * @param userOutput is the additional line of string to load the current string with
     */
    public static void loadMultiLineOutput(String userOutput) {
        multiLineList.add(userOutput);
    }

    /**
     * Prints out the multi-lined string to the UI and clears the list of strings for any subsequent inputs.
     */
    public static void printMultiLineOutput() {
        initialiseStringBuilder();
        for (String output: multiLineList) {
            multiLineOutput.append(output);
        }
        UiTemporary.userOutputForUI = multiLineOutput + "\n";
        System.out.println(userOutputDash);
        System.out.println(multiLineOutput);
        System.out.println(userOutputDash);
        multiLineList.clear();
    }

    private static void initialiseStringBuilder() {
        multiLineOutput = new StringBuilder();
    }

    /**
     * This readInput function is used to constantly take in the userInput by
     * checking if there is a line to be read. But if there is no line to be read
     * then the program terminates.
     *
     * @return This function will return a String which contains the user input.
     */
    public static String readInput() {
        if (scanner.hasNextLine()) {
            userInput = scanner.nextLine();
        } else {
            userInput = "bye";
        }
        return userInput;
    }

    /**
     * This printMessage function is called to print an exception message to the
     * user.
     *
     * @param message This message contains the message generated by the exception
     *                that was caught.
     */
    public static void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * This printManual function is called to aid the user in the usage of 'Duke' by
     * guiding them on how to input their task.
     */
    public static String printManual() {

        return ("Hi, this is the Chronologer manual!\n")
            + ("For all commands with date-time, the format is - dd/MM/yyyy HHmm\n\n")
            + ("1. Todo - todo<space>your task description eg. todo borrow books\n")
            + ("2. Deadline - deadline<space>your deadline description<space>/by<space> date-time\n")
            + ("3. Event - event<space>your event description<space>/at<space> date-time-date-time\n")
            + ("4. To list out all your tasks simply enter list\n")
            + ("5. Done - done<space> index of the task as listed\n")
            + ("6. Find - find<space>any word in the task\n")
            + ("7. Delete - delete<space> index of task as listed\n")
            + ("8. Undo/Redo - undo or redo changes to your tasks\n")
            + ("9. Theme - theme<space>dark or light\n")
            + ("10. Store/Restore - store or restore<space> index of storage\n")
            + ("11. To exit, enter bye\n\n")
            + ("12. If still unclear, enter manual to see our user guide!");
    }

    public static void printUnknownInput() {
        System.out.println(" ☹ OOPS!!! I'm sorry, but I don't know what that means. If you would like to know how to "
            + "use Duke, Enter duke-manual");
    }
}

