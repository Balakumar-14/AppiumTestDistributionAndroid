package com.appium.manager;

import com.appium.capabilities.Capabilities;
import com.appium.device.Device;
import com.appium.device.Devices;
import com.appium.executor.ATDExecutor;
import com.appium.filelocations.FileLocations;
import com.appium.utils.OverriddenVariable;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.appium.filelocations.FileLocations.SERVER_CONFIG;
import static com.appium.utils.ConfigFileManager.FRAMEWORK;
import static com.appium.utils.ConfigFileManager.RUNNER;
import static com.appium.utils.FigletHelper.figlet;
import static com.appium.utils.OverriddenVariable.getOverriddenStringValue;
import static java.lang.System.getProperty;

public class ATDRunner {
    private static final String ANDROID = "android";
    private static final String BOTH = "both";
    private static final String IOS = "iOS";
    public static final String USER_DIR = "user.dir";

    private final ATDExecutor ATDExecutor;
    private final Capabilities capabilities;
    private static final Logger LOGGER = Logger.getLogger(ATDRunner.class.getName());
    public  List<Device> availableDevices = new ArrayList<>(); // Initialize the list


    public ATDRunner() throws Exception {
        capabilities = Capabilities.getInstance();
        writeServiceConfig();
        createAppiumLogsFolder();
        AppiumServerManager appiumServerManager = new AppiumServerManager();
        appiumServerManager.startAppiumServer("127.0.0.1");

        List<Device> devices = Devices.getConnectedDevices();

        for (Device device : devices) {
            if (!device.busy) {
                availableDevices.add(device);
            }
        }
        ATDExecutor = new ATDExecutor(availableDevices); // Use availableDevices here
        createOutputDirectoryIfNotExist();
    }

    private void writeServiceConfig() {
        JSONObject serverConfig = Capabilities.getInstance()
                .getCapabilityObjectFromKey("serverConfig");
        try (FileWriter writer = new FileWriter(new File(
                getProperty("user.dir") + SERVER_CONFIG))) {
            writer.write(serverConfig.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createOutputDirectoryIfNotExist() {
        File file = new File(System.getProperty(USER_DIR), FileLocations.OUTPUT_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * Executes the specified test classes without explicitly mentioning devices for each class.
     * The test classes will be executed using the available devices.
     *
     * @param pack  The package name containing the test classes to be executed.
     * @param tests A list of test class names to be executed.
     * @return true if the tests are executed successfully; false otherwise.
     * @throws Exception If there are errors during test execution.
     */
    public boolean runner(String pack, List<String> tests) throws Exception {
        figlet(RUNNER.get());
        return parallelExecution(pack, tests);
    }

    /**
     * Executes the test classes specified in the provided package using the given devices.
     * Device details are collected from the passed HashMap.
     *
     * @param pack  The package name containing the test classes to be executed.
     * @param tests A HashMap containing device names as keys and lists of associated test names as values.
     * @return true if the tests are executed successfully; false otherwise.
     * @throws Exception If there are errors during test execution.
     */
    public boolean runner(String pack, HashMap<String, List<String>> tests) throws Exception {
        figlet(RUNNER.get());
        return parallelExecution(pack, tests);
    }

    public boolean runner(String pack) throws Exception {
        return parallelExecution(pack, new ArrayList<>());
    }

    /**
     * Executes the specified test classes using the available devices.
     * Devices do not need to be explicitly mentioned for each test class.
     * This method checks the availability of connected devices, creates an Appium logs folder,
     * and sets up a snapshot directory. It also handles platform-specific configurations
     * such as setting the automationName for Android.
     *
     * @param pack  The package name containing the test classes to be executed.
     * @param tests A list of test class names to be executed.
     * @return true if the tests are executed successfully; false otherwise.
     * @throws Exception If there are errors during test execution or configuration.
     */
    private boolean parallelExecution(String pack, List<String> tests) throws Exception {
        int deviceCount = Devices.getConnectedDevices().size();

        if (deviceCount == 0) {
            figlet("No Devices Connected");
            System.exit(0);
        }

        LOGGER.info(LOGGER.getName()
                + " Total Number of devices detected::" + deviceCount + "\n");

        createAppiumLogsFolder();
        createSnapshotDirectoryFor();
        String platform = getOverriddenStringValue("Platform");
        if (platform.equalsIgnoreCase("android")) {
            if (!capabilities.getCapabilityObjectFromKey("android").has("automationName")) {
                throw new IllegalArgumentException("Please set automationName "
                        + "as UIAutomator2 or Espresso to create Android driver");
            }
            generateDirectoryForAdbLogs();
        }

        boolean result = false;
        String runner = RUNNER.get();
        String framework = FRAMEWORK.get();

        if (framework.equalsIgnoreCase("testng")) {
            String executionType = runner.equalsIgnoreCase("distribute")
                    ? "distribute" : "parallel";
            result = ATDExecutor.constructXMLAndTriggerParallelRunner(tests, pack, deviceCount,
                    executionType);
        }
        return result;
    }

    /**
     * Executes the specified test classes with respect to the mentioned devices.
     * This method checks the availability of the specified devices among the connected devices,
     * creates an Appium logs folder, and sets up a snapshot directory. It also handles platform-specific configurations
     * such as setting the automationName for Android.
     *
     * @param pack                  The package name containing the test classes to be executed.
     * @param devicesAndTheirTests  A HashMap containing device names as keys and lists of associated test names as values.
     * @return true if the tests are executed successfully; false otherwise.
     * @throws Exception If there are errors during test execution, device availability checks, or configuration.
     */
    private boolean parallelExecution(String pack, HashMap<String,List<String>> devicesAndTheirTests) throws Exception {

        int deviceCount = availableDevices.size();

        if (deviceCount == 0) {
            figlet("No Devices Connected");
            System.exit(0);
        }

        LOGGER.info(LOGGER.getName()
                + " Total Number of devices detected::" + deviceCount + "\n");

        // Create a set to store the names of available devices
        Set<String> availableDeviceNames = new HashSet<>();
        for (Device device : availableDevices) {
            availableDeviceNames.add(device.getUdid());
        }

        // Identify devices from devicesAndTheirTests that are present in availableDevices
        for (String deviceName : devicesAndTheirTests.keySet()) {
            if (availableDeviceNames.contains(deviceName)) {
                System.out.println(deviceName + " is present in availableDevices list.");
            } else {
                throw new RuntimeException(deviceName + " is not present in availableDevices list.");
            }
        }

        createAppiumLogsFolder();
        createSnapshotDirectoryFor();
        String platform = getOverriddenStringValue("Platform");
        if (platform.equalsIgnoreCase("android")) {
            if (!capabilities.getCapabilityObjectFromKey("android").has("automationName")) {
                throw new IllegalArgumentException("Please set automationName "
                        + "as UIAutomator2 or Espresso to create Android driver");
            }
            generateDirectoryForAdbLogs();
        }

        boolean result = false;
        String runner = RUNNER.get();
        String framework = FRAMEWORK.get();

        if (framework.equalsIgnoreCase("testng")) {
            String executionType = runner.equalsIgnoreCase("distribute")
                    ? "distribute" : "parallel";
            result = ATDExecutor.constructXMLAndTriggerParallelRunner(devicesAndTheirTests, pack, deviceCount,
                    executionType);
        }
        return result;
    }

    private void generateDirectoryForAdbLogs() {
        File adb_logs = new File(System.getProperty(USER_DIR) + FileLocations.ADB_LOGS_DIRECTORY);
        if (!adb_logs.exists()) {
            try {
                adb_logs.mkdir();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }

    private void createAppiumLogsFolder() {
        File f = new File(System.getProperty(USER_DIR) + FileLocations.APPIUM_LOGS_DIRECTORY);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }

    private void createSnapshotDirectoryFor() {
        List<Device> udids = Devices.getConnectedDevices();
        for (Device udid : udids) {
            String os = udid.platform.equalsIgnoreCase(IOS) ? "iOS" : "Android";
            createPlatformDirectory(os);
            String deviceId = udid.udid;
            File file = new File(
                    System.getProperty(USER_DIR)
                            + FileLocations.SCREENSHOTS_DIRECTORY
                            + os
                            + File.separator
                            + deviceId);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    private void createPlatformDirectory(String platform) {
        File platformDirectory = new File(System.getProperty(USER_DIR)
                + FileLocations.SCREENSHOTS_DIRECTORY + platform);
        if (!platformDirectory.exists()) {
            platformDirectory.mkdirs();
        }
    }

}