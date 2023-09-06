package com.test.site;

import com.appium.manager.ATDRunner;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Runner {

    /**
     *Test method runner to execute test classes by specifying the respective device for the test-classes
     */
    @Test
    public static void testApp() throws Exception {
        HashMap<String,List<String>> devicesAndTheirTests = new HashMap<>();

        List<String> testList1 = new ArrayList<>();
        testList1.add("DemoOneTest");
        testList1.add("GuestTests");
        testList1.add("GuestFirstDuplicateTest");
        devicesAndTheirTests.put("8CMY16DCH", testList1);

        List<String> testList2 = new ArrayList<>();
        testList2.add("DemoTwoTest");
        testList2.add("DemoThreeTest");
        testList2.add("GuestSecondDuplicateTest");
        testList2.add("DemoFourTest");
        devicesAndTheirTests.put("emulator-5554", testList2);

        System.setProperty("Platform","android");
        ATDRunner atdRunner = new ATDRunner();
        boolean hasFailures = atdRunner.runner("com.test.site", devicesAndTheirTests);
        Assert.assertFalse(hasFailures, "Testcases have failed in parallel execution");
    }

    /**
    *Test method runner to execute test classes without specifying the device details for the test-classes
     */
//    @Test
//    public static void testApp() throws Exception {
//        List<String> tests = new ArrayList<>();
//        tests.add("DemoOneTest");
//        tests.add("GuestTests");
//        tests.add("GuestFirstDuplicateTest");
//        tests.add("DemoTwoTest");
//        tests.add("DemoThreeTest");
//        tests.add("GuestSecondDuplicateTest");
//        tests.add("DemoFourTest");
//        System.setProperty("Platform","android");
////        System.out.println(" Platform : " + System.getProperty("Platform"));
//        ATDRunner atdRunner = new ATDRunner();
//        boolean hasFailures = atdRunner.runner("com.test.site", tests);
//        Assert.assertFalse(hasFailures, "Testcases have failed in parallel execution");
//    }
}
