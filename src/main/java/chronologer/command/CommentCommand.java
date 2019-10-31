package chronologer.command;

import chronologer.exception.ChronologerException;
import chronologer.storage.ChronologerStateList;
import chronologer.storage.Storage;
import chronologer.task.Task;
import chronologer.task.TaskList;
import chronologer.ui.UiTemporary;

/**
 * Adds a comment to an existing task.
 * 
 * @author Hans Kurnia
 * @version 1.2
 */
public class CommentCommand extends Command {
    private Integer indexOfTask;
    private String comment;

    /**
     * Initializes the different parameters when commenting on a task.
     *
     * @param indexOfTask Holds the index of the task to be commented on.
     * @param comment Holds the comment for the task as given by the user.
     */
    public CommentCommand(Integer indexOfTask, String comment) {
        this.indexOfTask = indexOfTask;
        this.comment = comment;
    }

    /**
     * Adds the comment to the task and saves the updated TaskList it to persistent storage.
     *
     * @param tasks   Holds the list of all the tasks the user has.
     * @param storage Allows the saving of the file to persistent storage.
     */
    @Override
    public void execute(TaskList tasks, Storage storage) throws ChronologerException {
        if (isIndexValid(indexOfTask, tasks.getSize())) {
            Task taskToEdit = tasks.editTaskComment(indexOfTask, comment);
            ChronologerStateList.addState((tasks.getTasks()));
            storage.saveFile(tasks.getTasks());
            UiTemporary.printOutput("Noted. Your new task comment is:" + "\n " + taskToEdit.getComment());
        }
    }

    public Integer getIndexOfTask() {
        return indexOfTask;
    }

    public String getComment() {
        return comment;
    }

}