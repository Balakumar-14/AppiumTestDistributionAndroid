package com.appium.manager;

import com.annotation.values.SkipIf;
import com.appium.device.Device;
import com.appium.device.Devices;
import com.appium.plugin.PluginClI;
import com.appium.utils.FileFilterParser;
import com.appium.utils.Helpers;
import com.context.SessionContext;
import com.context.TestExecutionContext;
import io.appium.java_client.AppiumDriver;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AppiumParallelMethodTestListener extends Helpers
        implements ITestListener, IInvokedMethodListener, ISuiteListener {

    private static final Logger LOGGER = Logger.getLogger(
            AppiumParallelMethodTestListener.class.getName());
    private TestLogger testLogger;
    private AppiumServerManager appiumServerManager;
    private AppiumDriverManager appiumDriverManager;
    private static ThreadLocal<ITestNGMethod> currentMethods = new ThreadLocal<>();
    private static ThreadLocal<HashMap<String, String>> testResults = new ThreadLocal<>();
    private List<ITestNGListener> listeners;
    private List<String> emulatorTests = new ArrayList<>();
    private List<String> realDeviceTests = new ArrayList<>();


    public AppiumParallelMethodTestListener() {
        testLogger = new TestLogger();
        appiumServerManager = new AppiumServerManager();
        appiumDriverManager = new AppiumDriverManager();
        listeners = initialiseListeners();
    }

    /*
     * Allocates Devices to each test method
     * Instantiate Android or IOS Driver Instance
     * Starts ADB, Video Logging
     * Set Description for each test method with platform and UDID allocated to it.
     */
    @Override
    public void onTestStart(ITestResult iTestResult) {
        try {
            List<Device> devices = Devices.getConnectedDevices();
            for (Device device : devices) {
                if (!device.busy) {
                    // Command 1: adb uninstall io.appium.uiautomator2.server
                    ProcessBuilder processBuilder1 = new ProcessBuilder("adb", "-s" , device.udid,"uninstall", "io.appium.uiautomator2.server");
                    Process process1 = processBuilder1.start();
                    int exitCode1 = process1.waitFor();
                    LOGGER.info("Command 1 exit code: " + exitCode1);

                    // Command 2: adb uninstall io.appium.uiautomator2.server.test
                    ProcessBuilder processBuilder2 = new ProcessBuilder("adb", "uninstall", "io.appium.uiautomator2.server.test");
                    Process process2 = processBuilder2.start();
                    int exitCode2 = process2.waitFor();
                    LOGGER.info("Command 2 exit code: " + exitCode2);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isCloudExecution() {
        return PluginClI.getInstance().isCloudExecution();
    }

    private void startReportLogging(ITestResult iTestResult) throws IOException,
            InterruptedException {
        testLogger.startDeviceLogAndVideoCapture(iTestResult);
    }

    /*
     * Skips execution based on platform
     */
    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult, ITestContext context) {
        String testMethodName = iInvokedMethod.getTestMethod().getMethodName();
        String testName = context.getCurrentXmlTest().getName();
        String deviceName = context.getCurrentXmlTest().getParameter("device");

        LOGGER.info("TestName : "+ testMethodName + ", DeviceName : " + deviceName);

        allocateDeviceAndStartDriver(testMethodName, iTestResult, deviceName);

        if (AppiumDeviceManager.getAppiumDevice() != null) {
            LOGGER.info("Driver Session created!");
            currentMethods.set(iInvokedMethod.getTestMethod());
            SkipIf annotation = iInvokedMethod.getTestMethod().getConstructorOrMethod().getMethod()
                    .getAnnotation(SkipIf.class);
            if (annotation != null && AppiumDriverManager.getDriver().getCapabilities()
                    .getCapability("platformName")
                    .toString().equalsIgnoreCase(annotation.platform())) {
                throw new SkipException("Skipped because property was set to :::"
                        + annotation.platform());
            }
            TestExecutionContext testExecutionContext =
                    new TestExecutionContext(testMethodName);
            testExecutionContext.addTestState("appiumDriver", AppiumDriverManager.getDriver());
            testExecutionContext.addTestState("deviceId",
                    AppiumDeviceManager.getAppiumDevice().getUdid());

            queueBeforeInvocationListeners(iInvokedMethod, iTestResult, listeners);
        }
    }

    private void allocateDeviceAndStartDriver(String testMethodName, ITestResult iTestResult, String deviceName) {
        try {
            AppiumDriver driver = AppiumDriverManager.getDriver();
            if (driver == null || driver.getSessionId() == null) {
                if (!testMethodName.equalsIgnoreCase("tearDown")) {
                    appiumDriverManager.startAppiumDriverInstanceWithUDID(testMethodName, deviceName);
                }
                if (!isCloudExecution()) {
                    startReportLogging(iTestResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /*
     * Stops driver after each test method execution
     * De-allocates device after each test method execution
     * Terminate logs getting captured after each test method execution
     */
    @SneakyThrows
    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        {

            String testMethodName = iInvokedMethod.getTestMethod().getMethodName();
            String  classNamePath = String.valueOf(iInvokedMethod.getTestMethod().getRealClass());
            String className = new String();
            int lastDotIndex = classNamePath.lastIndexOf('.');

            if (lastDotIndex != -1 && lastDotIndex < classNamePath.length() - 1) {
                className = classNamePath.substring(lastDotIndex + 1);
                System.out.println(classNamePath);
            } else {
                System.out.println("Invalid input string");
            }

            String deviceName = AppiumDeviceManager.getAppiumDevice().deviceName;
            if(deviceName.contains("emulator"))
            {
                emulatorTests.add(className + "." + testMethodName + " : " + formattedTime() +" ");
            }
            else {
                realDeviceTests.add(className + "." + testMethodName + " : " + formattedTime() +" ");
            }

            try {
                LOGGER.info("Driver Session exissts"
                        + (AppiumDeviceManager.getAppiumDevice() != null)
                        + AppiumDeviceManager.getAppiumDevice());
                if (!isCloudExecution()
                        && AppiumDeviceManager.getAppiumDevice() != null) {
                    HashMap<String, String> logs = testLogger.endLogging(iTestResult,
                            AppiumDeviceManager.getAppiumDevice().getUdid());
                    new FileFilterParser()
                            .getScreenShotPaths(AppiumDeviceManager.getAppiumDevice()
                                    .getUdid(), iTestResult);
                    testResults.set(logs);
                }
                if (iInvokedMethod.isTestMethod()) {
                    appiumDriverManager.stopAppiumDriver();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SessionContext.remove(Thread.currentThread().getId());
            queueAfterInvocationListener(iInvokedMethod, iTestResult, listeners);
        }
    }

    /*
     * Stops Appium Server after each test suite
     */
    @Override
    public void onFinish(ISuite iSuite) {
        try {
            appiumServerManager.destroyAppiumNode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        LOGGER.info("Reached onTestSuccess listener");
    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onTestFailure(ITestResult iTestResult) {

    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onTestSkipped(ITestResult iTestResult) {
    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onStart(ITestContext iTestContext) {

    }

    /*
     * Document to make codacy happy
     */
    @Override
    public void onFinish(ITestContext iTestContext) {
        System.out.println("Emulator methods : " + emulatorTests);
        System.out.println("Real Device methods : " + realDeviceTests);
        SessionContext.setReportPortalLaunchURL(iTestContext);
    }

    public static ITestNGMethod getTestMethod() {
        return checkNotNull(currentMethods.get(),
                "Did you forget to register the %s listener?",
                AppiumParallelMethodTestListener.class.getName());
    }

    public String formattedTime(){
        // Get the current timestamp in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Create a Date object from the timestamp
        Date date = new Date(currentTimeMillis);

        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        // Format the Date object to HH:MM:SS format
        String formattedTime = sdf.format(date);
        return formattedTime;
    }
}
