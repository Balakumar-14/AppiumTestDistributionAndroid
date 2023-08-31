package com.test.site;

import org.testng.annotations.Test;

public class DemoThreeTest {

    @Test(priority = 1)
    public void demoThreeTestOne() {
        System.out.println("This is a demoTwoTestOne");
        sleep();
    }

    @Test(priority = 2, dependsOnMethods = "demoThreeTestOne")
    public void demoThreeTestTwo() {
        System.out.println("This is a demoTwoTestTwo");
        sleep();
    }

    @Test(priority = 3, dependsOnMethods = "demoThreeTestTwo")
    public void demoThreeTestThree() {
        System.out.println("This is a demoThreeTestThree");
        sleep();
    }

    @Test(priority = 4, dependsOnMethods = "demoThreeTestTwo")
    public void demoThreeTestFour() {
        System.out.println("This is a demoThreeTestFour");
        sleep();
    }

    @Test(priority = 5, dependsOnMethods = "demoThreeTestFour")
    public void demoThreeTestFive() {
        System.out.println("This is a demoThreeTestFive");
        sleep();
    }

    @Test(priority = 6, dependsOnMethods = "demoThreeTestThree")
    public void demoThreeTestSix() {
        System.out.println("This is a demoThreeTestSix");
        sleep();
    }

    @Test(priority = 7)
    public void demoThreeTestSeven() {
        System.out.println("This is a demoThreeTestSeven");
        sleep();
    }

    @Test(priority = 8, dependsOnMethods = "demoThreeTestSeven")
    public void demoThreeTestEight() {
        System.out.println("This is a demoThreeTestEight");
        sleep();
    }

    @Test(priority = 9, dependsOnMethods = "demoThreeTestSeven")
    public void demoThreeTestNine() {
        System.out.println("This is a demoThreeTestNine");
        sleep();
    }

    // Helper method to simulate a delay (sleep) for demonstration purposes
    private void sleep() {
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
