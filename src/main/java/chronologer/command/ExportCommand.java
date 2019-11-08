package chronologer.command;

import chronologer.exception.ChronologerException;
import chronologer.storage.CalendarOutput;
import chronologer.storage.Storage;
import chronologer.task.Priority;
import chronologer.task.Task;
import chronologer.task.TaskList;
import chronologer.ui.UiTemporary;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.validate.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;


/**
 * Processes and export the timeline as an ics file.
 *
 * @author Tan Yi Xiang
 * @version v1.7
 */
public class ExportCommand extends Command {

    private static final String DEADLINE = "DEADLINE";
    private static final String EVENT = "EVENT";
    private static final String TODO_PERIOD = "TODO PERIOD";

    private String fileName;
    private boolean hasDeadlineFlag;
    private boolean hasEventFlag;
    private boolean hasTodoFlag;

    /**
     * Initializes the different parameters for the export command.
     *
     * @param fileName        Name of the file
     * @param hasDeadlineFlag Indication to extract deadline tasks.
     * @param hasEventFlag    Indication to extract event tasks.
     * @param hasTodoFlag     Indication to extract todo with period tasks.
     */
    public ExportCommand(String fileName, Boolean hasDeadlineFlag, Boolean hasEventFlag, Boolean hasTodoFlag) {
        this.fileName = fileName;
        this.hasDeadlineFlag = hasDeadlineFlag;
        this.hasEventFlag = hasEventFlag;
        this.hasTodoFlag = hasTodoFlag;
    }

    @Override
    public void execute(TaskList tasks, Storage storage) throws ChronologerException, ValidationException {
        Calendar calendar = initializeCalendar();
        ArrayList<Task> taskList = tasks.getTasks();
        checkEmptyList(taskList);
        if (hasDeadlineFlag) {
            extractDeadline(taskList, calendar);
        }
        if (hasEventFlag) {
            extractEvent(taskList, calendar);
        }
        if (hasTodoFlag) {
            extractTodoPeriod(taskList, calendar);
        }
        if (!hasDeadlineFlag && !hasEventFlag && !hasTodoFlag) {
            extractDeadline(taskList, calendar);
            extractEvent(taskList, calendar);
            extractTodoPeriod(taskList, calendar);
        }

        if (checkCalendarEmpty(calendar)) {
            CalendarOutput.outputCalendar(fileName.trim(), calendar);
        } else {
            UiTemporary.printOutput(ChronologerException.emptyCalendar());
            throw new ChronologerException(ChronologerException.emptyCalendar());
        }
    }

    private Calendar initializeCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Chronologer//iCal4j 1.1//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        return calendar;
    }

    private void extractDeadline(ArrayList<Task> taskList, Calendar calendar) {
        for (Task task : taskList) {
            if (isDeadline(task)) {
                VEvent deadline = convertDeadline(task);
                calendar.getComponents().add(deadline);
            }
        }
    }

    private void extractEvent(ArrayList<Task> taskList, Calendar calendar) {
        for (Task task : taskList) {
            if (isEvent(task)) {
                VEvent event = convertEventOrTodoPeriod(task);
                calendar.getComponents().add(event);
            }
        }
    }

    private void extractTodoPeriod(ArrayList<Task> taskList, Calendar calendar) {
        for (Task task : taskList) {
            if (isTodoPeriod(task)) {
                VEvent todoPeriod = convertEventOrTodoPeriod(task);
                calendar.getComponents().add(todoPeriod);
            }
        }
    }

    private java.util.Calendar convertToCalendar(LocalDateTime startDate) {
        java.util.Calendar utilCalendar = new GregorianCalendar();
        utilCalendar.set(java.util.Calendar.YEAR, startDate.getYear());
        utilCalendar.set(java.util.Calendar.MONTH, startDate.getMonthValue() - 1);
        utilCalendar.set(java.util.Calendar.DAY_OF_MONTH, startDate.getDayOfMonth());
        utilCalendar.set(java.util.Calendar.HOUR_OF_DAY, startDate.getHour());
        utilCalendar.set(java.util.Calendar.MINUTE, startDate.getMinute());
        utilCalendar.set(java.util.Calendar.SECOND, 0);
        return utilCalendar;
    }

    private VEvent convertDeadline(Task task) {
        java.util.Calendar deadlineCalendar = convertToCalendar(task.getStartDate());
        DateTime deadlineDate = new DateTime(deadlineCalendar.getTime());
        DateTime currentDate = getCurrentDate();
        String title = createTitle(task);
        VEvent deadline = new VEvent(currentDate, deadlineDate, title);
        createDescription(task, deadline);
        createLocation(task, deadline);
        setPriority(task, deadline);
        UidGenerator generator = new RandomUidGenerator();
        deadline.getProperties().add(generator.generateUid());
        return deadline;
    }

    private VEvent convertEventOrTodoPeriod(Task task) {
        java.util.Calendar eventStartCalendar = convertToCalendar(task.getStartDate());
        java.util.Calendar eventEndCalendar = convertToCalendar(task.getEndDate());
        DateTime startEventDate = new DateTime(eventStartCalendar.getTime());
        DateTime endEventDate = new DateTime(eventEndCalendar.getTime());
        String title = createTitle(task);
        VEvent event = new VEvent(startEventDate, endEventDate, title);
        createDescription(task, event);
        createLocation(task, event);
        setPriority(task, event);
        UidGenerator generator = new RandomUidGenerator();
        event.getProperties().add(generator.generateUid());
        return event;
    }

    private DateTime getCurrentDate() {
        LocalDateTime currentDate = LocalDateTime.now();
        java.util.Calendar currentCalendar = convertToCalendar(currentDate);
        return new DateTime(currentCalendar.getTime());
    }

    private String createTitle(Task task) {
        if ("".equals(task.getModCode())) {
            return task.getDescription();
        } else {
            return task.getModCode() + ": " + task.getDescription();
        }
    }

    private void createDescription(Task task, VEvent event) {
        if (task.getComment() != null) {
            event.getProperties().add(new Description(task.getComment()));
        }

    }

    private void createLocation(Task task, VEvent event) {
        if (task.getLocation() != null) {
            event.getProperties().add(new Location(task.getLocation()));
        }
    }

    private void setPriority(Task task, VEvent event) {
        if (task.getPriority() == Priority.HIGH) {
            event.getProperties().add(net.fortuna.ical4j.model.property.Priority.HIGH);
        } else if (task.getPriority() == Priority.MEDIUM) {
            event.getProperties().add(net.fortuna.ical4j.model.property.Priority.MEDIUM);
        } else {
            event.getProperties().add(net.fortuna.ical4j.model.property.Priority.LOW);
        }
    }

    private void checkEmptyList(ArrayList<Task> taskList) throws ChronologerException {
        if (taskList.size() == 0) {
            UiTemporary.printOutput(ChronologerException.emptyExport());
            throw new ChronologerException(ChronologerException.emptyExport());
        }
    }

    private boolean checkCalendarEmpty(Calendar calendar) {
        try {
            calendar.validate(true);
            return true;
        } catch (ValidationException e) {
            return false;
        }

    }

    private boolean isDeadline(Task task) {
        return (DEADLINE.equals(task.getType()));
    }

    private boolean isEvent(Task task) {
        return (EVENT.equals(task.getType()));
    }

    private boolean isTodoPeriod(Task task) {
        return (TODO_PERIOD.equals(task.getType()));
    }

}
