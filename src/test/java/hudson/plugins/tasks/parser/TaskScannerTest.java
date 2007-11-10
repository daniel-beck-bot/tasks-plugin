package hudson.plugins.tasks.parser;

import static org.junit.Assert.*;
import hudson.plugins.tasks.model.Priority;
import hudson.plugins.tasks.model.WorkspaceFile;
import hudson.plugins.tasks.parser.Task;
import hudson.plugins.tasks.parser.TaskScanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

/**
 * Tests the class {@link TaskScanner}.
 */
public class TaskScannerTest {
    /** Test file. */
    private static final String FILE_WITH_TASKS = "file-with-tasks.txt";
    /** Error message. */
    private static final String WRONG_MESSAGE_ERROR = "Wrong message returned.";
    /** Error message. */
    private static final String WRONG_NUMBER_OF_TASKS_ERROR = "Wrong number of tasks found.";

    /**
     * Checks whether we find the two task in the test file.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void scanFileWithTasksAndDefaults() throws IOException {
        InputStream file = TaskScannerTest.class.getResourceAsStream(FILE_WITH_TASKS);

        Collection<Task> result = new TaskScanner().scan(file);

        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 2, result.size());
        Iterator<Task> iterator = result.iterator();
        assertEquals(WRONG_MESSAGE_ERROR, "here we have a task with priority NORMAL", iterator.next().getDetailMessage());
        assertEquals(WRONG_MESSAGE_ERROR, "here another task with priority HIGH", iterator.next().getDetailMessage());
    }

    /**
     * Checks whether we assign the right priorities for the test file.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void testPriorities() throws IOException {
        InputStream file = TaskScannerTest.class.getResourceAsStream(FILE_WITH_TASKS);

        Collection<Task> result = new TaskScanner().scan(file);
        WorkspaceFile workspaceFile = new WorkspaceFile();
        workspaceFile.addAnnotations(result);

        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 2, result.size());
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, workspaceFile.getNumberOfAnnotations(Priority.HIGH));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, workspaceFile.getNumberOfAnnotations(Priority.NORMAL));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, workspaceFile.getNumberOfAnnotations(Priority.LOW));
    }

    /**
     * Checks whether we find one high priority task in the test file.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void testHighPriority() throws IOException {
        InputStream file = TaskScannerTest.class.getResourceAsStream(FILE_WITH_TASKS);

        Collection<Task> result = new TaskScanner("FIXME", null, null).scan(file);
        WorkspaceFile workspaceFile = new WorkspaceFile();
        workspaceFile.addAnnotations(result);

        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, result.size());
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, workspaceFile.getNumberOfAnnotations(Priority.HIGH));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, workspaceFile.getNumberOfAnnotations(Priority.NORMAL));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, workspaceFile.getNumberOfAnnotations(Priority.LOW));
    }

    /**
     * Checks whether we correctly strip whitespace from the message.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void testTwoItemsWithWhiteSpaceAndHighPriority() throws IOException {
        InputStream file = TaskScannerTest.class.getResourceAsStream(FILE_WITH_TASKS);

        Collection<Task> result = new TaskScanner(" FIXME , TODO ", null, null).scan(file);
        WorkspaceFile workspaceFile = new WorkspaceFile();
        workspaceFile.addAnnotations(result);

        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 2, result.size());
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 2, workspaceFile.getNumberOfAnnotations(Priority.HIGH));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, workspaceFile.getNumberOfAnnotations(Priority.NORMAL));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, workspaceFile.getNumberOfAnnotations(Priority.LOW));
    }

    /**
     * Checks whether we find two high priority tasks with different identifiers in the test file.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void testTwoItemsWithHighPriority() throws IOException {
        InputStream file = TaskScannerTest.class.getResourceAsStream(FILE_WITH_TASKS);

        Collection<Task> result = new TaskScanner("FIXME,TODO", null, null).scan(file);
        WorkspaceFile workspaceFile = new WorkspaceFile();
        workspaceFile.addAnnotations(result);

        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 2, result.size());
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 2, workspaceFile.getNumberOfAnnotations(Priority.HIGH));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, workspaceFile.getNumberOfAnnotations(Priority.NORMAL));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, workspaceFile.getNumberOfAnnotations(Priority.LOW));
    }

    /**
     * Checks whether we find all priority task in the test file.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void testAllPriorities() throws IOException {
        InputStream file = TaskScannerTest.class.getResourceAsStream(FILE_WITH_TASKS);

        Collection<Task> result = new TaskScanner("FIXME", "FIXME,TODO", "TODO").scan(file);
        WorkspaceFile workspaceFile = new WorkspaceFile();
        workspaceFile.addAnnotations(result);

        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 4, result.size());
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, workspaceFile.getNumberOfAnnotations(Priority.HIGH));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 2, workspaceFile.getNumberOfAnnotations(Priority.NORMAL));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, workspaceFile.getNumberOfAnnotations(Priority.LOW));

    }

    /**
     * Checks whether we find no task in the test file.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void scanFileWithoutTasks() throws IOException {
        InputStream file = TaskScannerTest.class.getResourceAsStream("file-without-tasks.txt");

        Collection<Task> result = new TaskScanner().scan(file);
        WorkspaceFile workspaceFile = new WorkspaceFile();
        workspaceFile.addAnnotations(result);

        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, result.size());
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, workspaceFile.getNumberOfAnnotations());
    }
}
