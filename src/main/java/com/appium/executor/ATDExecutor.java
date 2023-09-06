package com.appium.executor;

import com.appium.device.Device;
import com.appium.utils.ConfigFileManager;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.ParallelMode;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.appium.filelocations.FileLocations.PARALLEL_XML_LOCATION;
import static com.appium.utils.ConfigFileManager.*;
import static com.appium.utils.FigletHelper.figlet;
import static java.lang.System.getProperty;
import static java.util.Collections.addAll;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ATDExecutor {
    private final List<Device> deviceList;
    private final List<String> items = new ArrayList<String>();
    private final List<String> listeners = new ArrayList<>();
    private final List<String> groupsInclude = new ArrayList<>();
    private final List<String> groupsExclude = new ArrayList<>();

    public ATDExecutor(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    /**
     * Constructs an XML suite for parallel test execution and triggers the TestNG parallel runner.
     * XML File constructor to execute test classes with available devices
     * Depending on the specified execution type ("distribute" or "parallel") and runner level ("class" or "method"),
     * this method generates the appropriate TestNG XML suite configuration and initiates test execution.
     *
     * @param test         A list of test names to be executed.
     * @param pack         The package name containing the test methods.
     * @param deviceCount  The number of devices or parallel threads for test execution.
     * @param executionType  The execution type, which can be "distribute" or "parallel".
     * @return true if the tests are executed successfully; false otherwise.
     * @throws Exception If there are errors during TestNG suite construction or test execution.
     */
    public boolean constructXMLAndTriggerParallelRunner(List<String> test, String pack,
                                                        int deviceCount, String executionType)
            throws Exception {
        boolean result;
        String suiteName = SUITE_NAME.get();
        String categoryName = CATEGORY.get();
        Set<Method> setOfMethods = getMethods(pack);
        String runnerLevel = RUNNER_LEVEL.get();

        if (executionType.equalsIgnoreCase("distribute")) {
            if (runnerLevel != null && runnerLevel.equalsIgnoreCase("class")) {
                constructXmlSuiteForMultipleClassDistribution(test, getTestMethods(setOfMethods),
                        suiteName, categoryName, deviceCount);
            } else {
                constructXmlSuiteForMethodLevelDistributionRunner(test,
                        getTestMethods(setOfMethods), suiteName, categoryName, deviceCount);
            }
        } else {
            constructXmlSuiteForParallelRunner(test, getTestMethods(setOfMethods),
                    suiteName, categoryName, deviceCount);
        }

        parallelXMLFileEdit();

        result = testNGParallelRunner();
        figlet("Test Completed");
        return result;
    }

    /**
     * Constructs an XML suite for parallel test execution based on a provided HashMap of devices and their associated tests,
     * and triggers the TestNG parallel runner. The method determines whether to distribute tests by class or method level,
     * based on the specified execution type ("distribute" or "parallel") and runner level ("class" or "method").
     * XML file constructor for executing test classes with respect to mentioned device for the classes
     * If tests are distributed by class and the required device count exceeds the available devices count,
     * it displays a message indicating that the required devices count is greater than the available devices count.
     *
     * @param devicesAndTheirTests A HashMap containing device names as keys and lists of associated test names as values.
     * @param pack                 The package name containing the test methods.
     * @param deviceCount          The number of devices or parallel threads for test execution.
     * @param executionType        The execution type, which can be "distribute" or "parallel".
     * @return true if the tests are executed successfully; false otherwise.
     * @throws Exception If there are errors during TestNG suite construction or test execution.
     */

    public boolean constructXMLAndTriggerParallelRunner(HashMap<String,List<String>> devicesAndTheirTests, String pack,
                                                        int deviceCount, String executionType)
            throws Exception {
        boolean result;
        String suiteName = SUITE_NAME.get();
        String categoryName = CATEGORY.get();
        Set<Method> setOfMethods = getMethods(pack);
        String runnerLevel = RUNNER_LEVEL.get();

        if (executionType.equalsIgnoreCase("distribute")) {
            if (runnerLevel != null && runnerLevel.equalsIgnoreCase("class")) {

                if(devicesAndTheirTests.size() > deviceCount) {
                    System.out.println("Required devices count is greater than the available devices count");
                }
                else {
                    constructXmlSuiteForMultipleClassDistribution(devicesAndTheirTests, getTestMethods(setOfMethods),
                            suiteName, categoryName, deviceCount);
                }
            }
        }
        parallelXMLFileEdit();

        result = testNGParallelRunner();
        figlet("Test Completed");
        return result;
    }

    /**
     * Edits the TestNG XML configuration file to disable method-level parallelism and remove thread count settings.
     * Specifically, it modifies the XML file to replace "parallel="methods"" with "parallel="false"" and removes
     * any "thread-count" attributes, if value is '1'.
     */
    public void parallelXMLFileEdit(){
        try {
            // Define the path to the XML file
            String xmlFilePath = System.getProperty("user.dir") + PARALLEL_XML_LOCATION;

            // Read the content of the XML file
            String xmlContent = readXmlFile(xmlFilePath);

            // Replace "parallel="methods"" with "parallel="false""
            xmlContent = xmlContent.replaceAll("parallel=\"methods\"", "parallel=\"false\"");

            xmlContent = xmlContent.replaceAll("thread-count=\"1\"", "");

            // Save the modified content back to the same file
            saveXmlFile(xmlContent, xmlFilePath);

            System.out.println("XML file updated successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readXmlFile(String filePath) throws IOException {
        // Read the content of the XML file
        return Files.readString(Path.of(filePath));
    }

    private static void saveXmlFile(String content, String filePath) throws IOException {
        // Backup the original file
        File originalFile = new File(filePath);
        File backupFile = new File(filePath + ".bak");
        Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Write the modified content back to the original file
        try (FileWriter writer = new FileWriter(originalFile)) {
            writer.write(content);
        }
    }

    public XmlSuite constructXmlSuiteForParallelRunner(List<String> tests,
                                                       Map<String, List<Method>> methods,
                                                       String suiteName, String categoryName,
                                                       int deviceCount) {
        ArrayList<String> listeners = new ArrayList<>();
        listeners.add("com.appium.manager.AppiumParallelTestListener");
        include(listeners, LISTENERS);
        include(groupsInclude, INCLUDE_GROUPS);
        include(groupsExclude, EXCLUDE_GROUPS);
        XmlSuite suite = new XmlSuite();
        suite.setName(suiteName);
        suite.setThreadCount(deviceCount);
        suite.setDataProviderThreadCount(deviceCount);
        suite.setParallel(ParallelMode.TESTS);
        suite.setVerbose(2);
        suite.setListeners(listeners);
        for (int i = 0; i < deviceCount; i++) {
            XmlTest test = new XmlTest(suite);
            test.setName(categoryName + "-" + i);
            test.setPreserveOrder(false);
            Device device = deviceList.get(i);
            test.addParameter("device", device.udid);
            test.addParameter("hostName", device.host);
            test.setIncludedGroups(groupsInclude);
            test.setExcludedGroups(groupsExclude);
            List<XmlClass> xmlClasses = writeXmlClass(tests, methods);
            test.setXmlClasses(xmlClasses);
        }
        writeTestNGFile(suite);
        return suite;
    }

    public XmlSuite constructXmlSuiteForClassLevelDistributionRunner(List<String> tests,
                   Map<String, List<Method>> methods,
                   String suiteName, String categoryName, int deviceCount) {
        XmlSuite suite = new XmlSuite();
        suite.setName(suiteName);
        suite.setThreadCount(deviceCount);
        suite.setParallel(ParallelMode.CLASSES);
        suite.setVerbose(2);
        listeners.add("com.appium.manager.AppiumParallelMethodTestListener");
        include(listeners, LISTENERS);
        suite.setListeners(listeners);
        XmlTest test = new XmlTest(suite);
        test.setName(categoryName);
        test.addParameter("device", "");
        include(groupsExclude, EXCLUDE_GROUPS);
        include(groupsInclude, INCLUDE_GROUPS);
        test.setIncludedGroups(groupsInclude);
        test.setExcludedGroups(groupsExclude);
        List<XmlClass> xmlClasses = writeXmlClass(tests, methods);
        test.setXmlClasses(xmlClasses);
        writeTestNGFile(suite);
        return suite;
    }

    /**
     * Constructs an XML TestNG suite configuration to execute test classes with respect to hashmap specified devices.
     * This method creates separate XML test configurations for each device, associating the specified test classes
     * with their respective devices. It sets up TestNG suite properties such as thread count, parallel execution mode,
     * and listeners.
     *
     * @param devicesAndTheirTests A HashMap containing device names as keys and lists of associated test class names as values.
     * @param methods              A mapping of test class names to their corresponding test methods.
     * @param suiteName            The name of the TestNG suite.
     * @param categoryName         The name of the TestNG category.
     * @param deviceCount          The number of devices or parallel threads for test execution.
     * @return An XmlSuite object representing the constructed TestNG suite configuration.
     */
    public XmlSuite constructXmlSuiteForMultipleClassDistribution(HashMap<String,List<String>> devicesAndTheirTests,
                                                                  Map<String, List<Method>> methods,
                                                                  String suiteName, String categoryName, int deviceCount) {
        XmlSuite suite = new XmlSuite();
        suite.setName(suiteName);
        suite.setThreadCount(deviceCount);
        suite.setParallel(ParallelMode.TESTS);
        suite.setVerbose(2);
        suite.setPreserveOrder(true);
        listeners.add("com.appium.manager.AppiumParallelMethodTestListener");
        include(listeners, LISTENERS);
        suite.setListeners(listeners);
        System.out.println("Is Preserve Order Enabled: " + suite.getPreserveOrder());

        for (String deviceName : devicesAndTheirTests.keySet()) {
            xmlTestsCreatorForDeviceSpecificMultipleClasses(suite, devicesAndTheirTests.get(deviceName), methods, deviceName);
        }

        writeTestNGFile(suite);
        return suite;
    }

    /**
     * Creates an XML Test configuration for executing multiple test classes on a specific device.
     * This method takes a list of test class names and associates them with a device-specific XML test configuration.
     * It sets up properties for the XML test, including thread count, parallel execution mode, and parameters.
     *
     * @param suite        The parent XmlSuite to which the created XmlTest will be added.
     * @param testClasses  A list of test class names to be associated with the XML test.
     * @param methods      A mapping of test class names to their corresponding test methods.
     * @param deviceName   The name of the device for which the tests are configured.
     * @return An XmlTest object representing the device-specific test configuration.
     */
    public XmlTest xmlTestsCreatorForDeviceSpecificMultipleClasses(XmlSuite suite, List<String> testClasses,
                                                                   Map<String, List<Method>> methods , String deviceName){

        XmlTest test = new XmlTest(suite);

        List<XmlClass> xmlClasses = new ArrayList<>();
        for(String className : methods.keySet()) {
            for(String testClass : testClasses) {
                if (className.contains("Test") && className.contains(testClass)) {
                    XmlClass xmlClass = new XmlClass();
                    xmlClass.setName(className);
                    xmlClasses.add(xmlClass);
                    test.setName(deviceName + " Device tests");
                    test.addParameter("device", deviceName);
                    test.setThreadCount(1);
                    test.setXmlClasses(xmlClasses);
                    test.setParallel(ParallelMode.METHODS);
                    test.setVerbose(2);
                }
            }
        }
        return test;
    }

    /**
     * Constructs an XML TestNG suite configuration to execute test classes with available devices.
     * This method distributes the specified test classes across multiple devices based on the provided device count.
     * It sets up TestNG suite properties such as thread count, parallel execution mode, and listeners.
     *
     * @param tests         A list of test class names to be executed.
     * @param methods       A mapping of test class names to their corresponding test methods.
     * @param suiteName     The name of the TestNG suite.
     * @param categoryName  The name of the TestNG category.
     * @param deviceCount   The number of devices or parallel threads for test execution.
     * @return An XmlSuite object representing the constructed TestNG suite configuration.
     */
    public XmlSuite constructXmlSuiteForMultipleClassDistribution(List<String> tests,
                                                                                     Map<String, List<Method>> methods,
                                                                                     String suiteName, String categoryName, int deviceCount) {
        int devicesCount = deviceList.size();
        XmlSuite suite = new XmlSuite();
        suite.setName(suiteName);
        suite.setThreadCount(deviceCount);
        suite.setParallel(ParallelMode.TESTS);
        suite.setVerbose(2);
        suite.setPreserveOrder(true);
        listeners.add("com.appium.manager.AppiumParallelMethodTestListener");
        include(listeners, LISTENERS);
        suite.setListeners(listeners);
        System.out.println("Is Preserve Order Enabled: " + suite.getPreserveOrder());

        int deviceIterator = 0;

        List<List<String>> segregatedTestCases = distributeTestCases(tests, devicesCount);

        // Display the segregated test cases for each device
        for (int i = 0; i < devicesCount; i++) {
            System.out.println("Device " + (i + 1) + " Test Cases: " + segregatedTestCases.get(i));
        }

        if(segregatedTestCases.size() == deviceCount) {
            for(List<String> testClasses : segregatedTestCases)
            {
                if(!deviceList.get(deviceIterator).busy){
                    xmlTestsCreatorForDeviceSpecificMultipleClasses(suite, testClasses, methods, deviceList.get(deviceIterator).udid);
                    if(deviceIterator == (devicesCount-1))
                    {
                        deviceIterator = 0;
                    }
                    else {
                        deviceIterator++;
                    }
                }
            }
        }

        writeTestNGFile(suite);
        return suite;
    }

    public XmlTest xmlTestsCreatorWithMultipleClasses(XmlSuite suite,List<String> testClasses,
                                   Map<String, List<Method>> methods , String deviceName){

        XmlTest test = new XmlTest(suite);

        List<XmlClass> xmlClasses = new ArrayList<>();
        for(String className : methods.keySet()) {

            for(String testClass : testClasses) {
                if (className.contains("Test") && className.contains(testClass)) {
                    XmlClass xmlClass = new XmlClass();
                    xmlClass.setName(className);
                    xmlClasses.add(xmlClass);
                    test.setName(deviceName + " Device tests");
                    test.addParameter("device", deviceName);
                    test.setThreadCount(1);
                    test.setXmlClasses(xmlClasses);
                    test.setParallel(ParallelMode.METHODS);
                    test.setVerbose(2);
                }
            }
        }
        return test;
    }

    /**
     * Distributes a list of test cases evenly among a specified number of devices.
     * This method creates separate lists for each device and assigns test cases to these lists
     * in a round-robin fashion to ensure an even distribution.
     *
     * @param tests        A list of test cases to be distributed among devices.
     * @param devicesCount The number of devices or lists to distribute the test cases to.
     * @return A list of lists, where each inner list contains the test cases assigned to a specific device.
     */
    private static List<List<String>> distributeTestCases(List<String> tests, int devicesCount) {
        List<List<String>> segregatedTestCases = new ArrayList<>();

        // Initialize empty lists for each device
        for (int i = 0; i < devicesCount; i++) {
            segregatedTestCases.add(new ArrayList<>());
        }

        // Distribute test cases evenly among devices if the test case list is not empty
        if (!tests.isEmpty()) {
            int currentDeviceIndex = 0;
            for (String testCase : tests) {
                List<String> deviceTestCases = segregatedTestCases.get(currentDeviceIndex);
                deviceTestCases.add(testCase);
                currentDeviceIndex = (currentDeviceIndex + 1) % devicesCount; // Rotate devices
            }
        }

        return segregatedTestCases;
    }

    /**
     * Creates an XML Test configuration for executing a specific single test case on a device.
     * This method associates a single test case with an XML test configuration, sets up properties for the XML test,
     * including thread count, parallel execution mode, and parameters.
     *
     * @param suite       The parent XmlSuite to which the created XmlTest will be added.
     * @param testCase    The name of the test case to be associated with the XML test.
     * @param methods     A mapping of test class names to their corresponding test methods.
     * @param deviceName  The name of the device for which the test is configured.
     * @return An XmlTest object representing the test configuration for the specified test case and device.
     */
    public XmlTest xmlTestsCreator(XmlSuite suite,String testCase,
                                   Map<String, List<Method>> methods , String deviceName){

        XmlTest test = new XmlTest(suite);
        for(String className : methods.keySet()) {
            List<XmlClass> xmlClasses = new ArrayList<>();
            if (className.contains("Test") && className.contains(testCase)) {
                XmlClass xmlClass = new XmlClass();
                xmlClass.setName(className);
                xmlClasses.add(xmlClass);
                test.setName(className);
                test.addParameter("device", deviceName);
                test.setThreadCount(1);
                test.setXmlClasses(xmlClasses);
                test.setParallel(ParallelMode.METHODS);
//                test.setPreserveOrder(true);
//                test.addParameter("parallel", "false");
                test.setVerbose(2);
            }
        }
        return test;
    }

    public XmlSuite constructXmlSuiteForClassLevelForSingleDevicesDistributionRunner(List<String> tests,
                                                                     Map<String, List<Method>> methods,
                                                                     String suiteName, String categoryName, int deviceCount) {

        int devicesCount = deviceList.size();
        XmlSuite suite = new XmlSuite();
        suite.setName(suiteName);
        suite.setThreadCount(deviceCount);
//        suite.setThreadCount(1);
        suite.setParallel(ParallelMode.TESTS);
        suite.setVerbose(2);
        suite.setPreserveOrder(true);
        listeners.add("com.appium.manager.AppiumParallelMethodTestListener");
        include(listeners, LISTENERS);
        suite.setListeners(listeners);
        System.out.println("Is Preserve Order Enabled: " + suite.getPreserveOrder());

        int deviceIterator = 0;

        for(String testCase : tests)
        {
            if(!deviceList.get(deviceIterator).busy){
                xmlTestsCreator(suite, testCase, methods, deviceList.get(deviceIterator).udid);
                if(deviceIterator == (devicesCount-1))
                {
                    deviceIterator = 0;
                }
                else {
                    deviceIterator++;
                }
            }
        }

        writeTestNGFile(suite);
        return suite;
    }

    public XmlSuite constructXmlSuiteForMethodLevelDistributionRunner(List<String> tests,
                             Map<String, List<Method>> methods, String suiteName,
                             String category, int deviceCount) {
        include(groupsInclude, INCLUDE_GROUPS);
        XmlSuite suite = new XmlSuite();
        suite.setName(suiteName);
        suite.setThreadCount(deviceCount);
        suite.setDataProviderThreadCount(deviceCount);
        suite.setVerbose(2);
        suite.setParallel(ParallelMode.METHODS);
        listeners.add("com.appium.manager.AppiumParallelMethodTestListener");
        include(listeners, LISTENERS);
        suite.setListeners(listeners);
        CreateGroups createGroups = new CreateGroups(tests, methods, category, suite).invoke();
        List<XmlClass> xmlClasses = createGroups.getXmlClasses();
        XmlTest test = createGroups.getTest();
        List<XmlClass> writeXml = createGroups.getWriteXml();
        for (XmlClass xmlClass : xmlClasses) {
            writeXml.add(new XmlClass(xmlClass.getName()));
            test.setClasses(writeXml);
        }
        writeTestNGFile(suite);
        return suite;
    }

    /**
     * Executes TestNG parallel test suites based on the specified TestNG XML configuration file.
     * This method runs TestNG tests by loading the specified XML suite and checks for test failures.
     *
     * @return true if there are test failures; false otherwise.
     */
    public boolean testNGParallelRunner() {
        TestNG testNG = new TestNG();
        List<String> suites = Lists.newArrayList();
        suites.add(getProperty("user.dir") + PARALLEL_XML_LOCATION);
        testNG.setTestSuites(suites);
        testNG.run();
        return testNG.hasFailure();
    }

    private Set<Method> getMethods(String pack) throws MalformedURLException {
        URL newUrl;
        List<URL> newUrls = new ArrayList<>();
        addAll(items, pack.split("\\s*,\\s*"));
        int a = 0;
        Collection<URL> urls = ClasspathHelper.forPackage(items.get(a));
        Iterator<URL> iter = urls.iterator();

        URL url = null;

        while (iter.hasNext()) {
            url = iter.next();
            if (url.toString().contains("test-classes")) {
                break;
            }
        }
        for (String item : items) {
            newUrl = new URL(url.toString() + item.replaceAll("\\.", "/"));
            newUrls.add(newUrl);
            a++;
        }
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(newUrls)
            .setScanners(new MethodAnnotationsScanner()));
        return reflections.getMethodsAnnotatedWith(Test.class);
    }

    private List<XmlClass> writeXmlClass(List<String> testCases, Map<String,
            List<Method>> methods) {
        List<XmlClass> xmlClasses = new ArrayList<>();
        for (String className : methods.keySet()) {
            XmlClass xmlClass = new XmlClass();
            xmlClass.setName(className);
            if (className.contains("Test")) {
                if (testCases.size() == 0) {
                    xmlClasses.add(xmlClass);
                } else {
                    for (String s : testCases) {
                        for (String item : items) {
                            String testName = item.concat("." + s);
                            if (testName.equals(className)) {
                                xmlClasses.add(xmlClass);
                            }
                        }
                    }
                }
            }
        }
        return xmlClasses;
    }

    private void writeTestNGFile(XmlSuite suite) {
        try (FileWriter writer = new FileWriter(new File(
            getProperty("user.dir") + PARALLEL_XML_LOCATION))) {
            writer.write(suite.toXml());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void include(List<String> groupsInclude, ConfigFileManager config) {
        String listItems = config.get();
        if (isNotEmpty(listItems)) {
            addAll(groupsInclude, listItems.split("\\s*,\\s*"));
        }
    }

    public Map<String, List<Method>> getTestMethods(Set<Method> methods) {
        Map<String, List<Method>> listOfMethods = new HashMap<>();
        methods.forEach(method -> {
            List<Method> methodsList = listOfMethods.computeIfAbsent(
                method.getDeclaringClass().getPackage().getName()
                    + "." + method.getDeclaringClass()
                    .getSimpleName(), k -> new ArrayList<>());
            methodsList.add(method);
        });
        return listOfMethods;
    }

    private class CreateGroups {
        private List<String> tests;
        private Map<String, List<Method>> methods;
        private String category;
        private XmlSuite suite;
        private List<XmlClass> xmlClasses;
        private XmlTest test;
        private List<XmlClass> writeXml;

        public CreateGroups(List<String> tests, Map<String, List<Method>> methods,
                            String category, XmlSuite suite) {
            this.tests = tests;
            this.methods = methods;
            this.category = category;
            this.suite = suite;
        }

        public List<XmlClass> getXmlClasses() {
            return xmlClasses;
        }

        public XmlTest getTest() {
            return test;
        }

        public List<XmlClass> getWriteXml() {
            return writeXml;
        }

        public CreateGroups invoke() {
            xmlClasses = writeXmlClass(tests, methods);
            test = new XmlTest(suite);
            test.setName(category);
            test.addParameter("device", "");
            include(groupsExclude, EXCLUDE_GROUPS);
            test.setIncludedGroups(groupsInclude);
            test.setExcludedGroups(groupsExclude);
            writeXml = new ArrayList<>();
            return this;
        }
    }
}