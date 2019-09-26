package task;
import java.time.LocalDateTime;
import java.io.Serializable;
import parser.DateTimeExtractor;

import static parser.DateTimeExtractor.NULL_DATE;

/**
 * This extension of the task class will allow the user to add a task of to-do type.
 *
 * @author Sai Ganesh Suresh
 * @version v2.0
 */
public class Todo extends Task implements Serializable {

    public Todo(String description){
        super(description);
    }

    public Todo(String description, LocalDateTime at, LocalDateTime to){
        super(description);
        this.startDate = at;
        this.endDate = to;
        this.createdDate = LocalDateTime.now();
    }

    /**
     * This override of the toString function of the task class etches the different portions of the user input into a
     * single string.
     *
     * @return This function returns a string of the required task in the desired output format of string type.
     */
    @Override
    public String toString() {
        if(!this.startDate.isEqual(NULL_DATE) && !this.endDate.isEqual(NULL_DATE)){
            return "[T]" + "[" + super.getStatusIcon() + "] " + this.description + " " + " (from: " +
                    this.startDate.format(DateTimeExtractor.DATE_FORMATTER) + ")" + " (to: " +
                    this.endDate.format(DateTimeExtractor.DATE_FORMATTER) + ")";
        }
        else {
            return "[T]" + "[" + super.getStatusIcon() + "] " + this.description;
        }
    }

    @Override
    public boolean checkReminderTrigger(){
        return LocalDateTime.now().isAfter(createdDate.plusDays(remindInHowManyDays));
    }

    @Override
    boolean checkForClash(Task taskToCheck) {
        return false;
    }
}
