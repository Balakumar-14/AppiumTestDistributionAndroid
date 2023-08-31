package com.appium.manager;

import com.appium.filelocations.FileLocations;
import com.appium.capabilities.Capabilities;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Duration;

public class AppiumServerManager {

    private static AppiumDriverLocalService appiumDriverLocalService;

    // This private static method returns the appiumDriverLocalService instance.
    // It is used to access the Appium driver local service within the class.
    private static AppiumDriverLocalService getAppiumDriverLocalService() {
        return appiumDriverLocalService;
    }

    private static final Logger LOGGER = Logger.getLogger(AppiumServerManager.class.getName());

    // This private static method sets the appiumDriverLocalService to the provided value.
    // It is used to update the Appium driver local service instance.
    private static void setAppiumDriverLocalService(
            AppiumDriverLocalService appiumDriverLocalService) {
        AppiumServerManager.appiumDriverLocalService = appiumDriverLocalService;
    }

    // This private method returns the URL of the Appium server.
    // It internally calls getAppiumDriverLocalService().getUrl() to obtain the server URL.
    private URL getAppiumUrl() {
        return getAppiumDriverLocalService().getUrl();
    }

    // This method is responsible for shutting down the Appium server.
    public void destroyAppiumNode() {
        LOGGER.info("Shutting down Appium Server");
        getAppiumDriverLocalService().stop();
        if (getAppiumDriverLocalService().isRunning()) {
            LOGGER.info("AppiumServer didn't shut... Trying to quit again....");
            getAppiumDriverLocalService().stop();
        }
        if (getAppiumDriverLocalService().isRunning()) {
            LOGGER.info("AppiumServer is not shutting down");
        }
    }

    // This method returns the URL of the Appium server as a string.
    // It calls getAppiumUrl().toString() to get the server URL.
    public String getRemoteWDHubIP() {
        return getAppiumUrl().toString();
    }

    // This method starts the Appium server on the specified host.
    // It configures AppiumServiceBuilder with logfile location, IP address, timeout.
    public void startAppiumServer(String host) throws Exception {
        LOGGER.info(LOGGER.getName() + " Starting Appium Server on Localhost");
        AppiumDriverLocalService appiumDriverLocalService;
        AppiumServiceBuilder builder =
                getAppiumServerBuilder(host)
                        .withLogFile(new File(
                                System.getProperty("user.dir")
                                        + FileLocations.APPIUM_LOGS_DIRECTORY
                                        + "appium_logs.txt"))
                        .withIPAddress(host)
                        .withTimeout(Duration.ofSeconds(60))
                        .withArgument(GeneralServerFlag.BASEPATH, "/wd/hub")
                        .withArgument(() -> "--config", System.getProperty("user.dir")
                                + FileLocations.SERVER_CONFIG)
                        .withArgument(GeneralServerFlag.RELAXED_SECURITY)
                        .usingAnyFreePort();
        appiumDriverLocalService = builder.build();
        appiumDriverLocalService.start();
        LOGGER.info(LOGGER.getName() + "Appium Server Started at......"
                + appiumDriverLocalService.getUrl());
        setAppiumDriverLocalService(appiumDriverLocalService);
    }

    /* private void getWindowsDevice(String platform, List<Device> devices) {
        if (platform.equalsIgnoreCase(OSType.WINDOWS.name())
                && Capabilities.getInstance().isWindowsApp()) {
            Device device = new Device();
            device.setName("windows");
            device.setOs("windows");
            device.setName("windows");
            device.setUdid("win-123");
            device.setDevice(true);
            List<Device> deviceList = new ArrayList<>();
            deviceList.add(device);
            devices.addAll(deviceList);
        }
    } */

    // This method finds and returns an available port on the specified host machine.
    // It creates a ServerSocket with a port value of 0, which will bind to any available port.
    // It then returns the port number selected by the operating system and closes the socket.
    public int getAvailablePort(String hostMachine) throws IOException {
        ServerSocket socket = new ServerSocket(0);
        socket.setReuseAddress(true);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }

    // This private method returns an AppiumServiceBuilder instance.
    // If the path is provided, it returns the builder with the specified path;
    // otherwise, it returns the default builder.
    private AppiumServiceBuilder getAppiumServerBuilder(String host) throws Exception {
        if (Capabilities.getInstance().getCapabilities().has("appiumServerPath")) {
            Path path = FileSystems.getDefault().getPath(Capabilities.getInstance()
                    .getCapabilities().get("appiumServerPath").toString());
            String serverPath = path.normalize().toAbsolutePath().toString();
            LOGGER.info("Picking UserSpecified Path for AppiumServiceBuilder - " + serverPath);
            return getAppiumServiceBuilderWithUserAppiumPath(serverPath);
        } else {
            LOGGER.info("Picking Default Path for AppiumServiceBuilder");
            return getAppiumServiceBuilderWithDefaultPath();

        }
    }

    // This private method returns an AppiumServiceBuilder with the provided appiumServerPath.
    private AppiumServiceBuilder getAppiumServiceBuilderWithUserAppiumPath(String appiumServerPath) {
        return new AppiumServiceBuilder().withAppiumJS(
                new File(appiumServerPath));
    }

    // This private method returns an AppiumServiceBuilder with the default path.
    private AppiumServiceBuilder getAppiumServiceBuilderWithDefaultPath() {
        return new AppiumServiceBuilder();
    }

}
