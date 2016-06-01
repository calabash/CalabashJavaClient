package com.xamarin;

import com.xamarin.core.App;
import com.xamarin.core.Device;
import com.xamarin.core.Element;
import junit.framework.TestCase;

import java.awt.*;

/**
 * Created by chrisf on 5/25/16.
 */
public class MainTest extends TestCase {

    private String serverURL = "http://10.0.2.199:27753";
    private Device device = new Device(serverURL);
    App tasky;

    public void setUp() throws Exception {
        String taskyBundleID = "com.xamarin.samples.taskytouch";
        tasky = device.launchApp(taskyBundleID);
        super.setUp();
    }

    public void testAddTask() {
        addTask("Write Some Java!", "I just did!", false);
    }

    public void testCompleteTask() {
        addTask("Not done yet", "", false);

        Element taskButton = tasky.elementWithText("Not done yet");
        taskButton.tap();

        Element taskNameTextfield = tasky.elementWithText("other task info");
        taskNameTextfield.enterText("Done!");

        Element doneToggle = tasky.elementWithType("switch");
        doneToggle.tap();

        tasky.dragCoordinates(new Point(100, 200), new Point(100, 100));

        Element saveButton = tasky.elementWithText("Save");
        saveButton.tap();
    }

    public void testDeleteTasks() {
        deleteTask("Not done yet");
        deleteTask("Write Some Java!");
    }

    /*
        Helpers
     */

    public void deleteTask(String name) {
        Element taskButton = tasky.elementWithID(name);
        if (taskButton != null) {
            taskButton.tap();

            tasky.elementWithText("Delete").tap();
        }
    }

    public void addTask(String name, String desc, boolean done) {
        Element addButton = tasky.elementWithID("Add");
        addButton.tap();

        Element taskNameTextfield = tasky.elementWithText("task name");
        taskNameTextfield.enterText(name);

        Element otherTaskInfo = tasky.elementWithText("other task info");
        otherTaskInfo.enterText(desc);

        if (done) {
            Element doneToggle = tasky.elementWithType("switch");
            doneToggle.tap();
        }

        tasky.dragCoordinates(new Point(100, 200), new Point(100, 100));

        Element saveButton = tasky.elementWithText("Save");
        saveButton.tap();
    }
}