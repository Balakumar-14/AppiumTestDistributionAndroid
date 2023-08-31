package com.test.site;

import org.testng.annotations.Test;

public class DemoFourTest {

    @Test(priority = 1)
    public void demoFourTestOne() {
        System.out.println("This is a demoFourTestOne");
        sleep();
    }

    @Test(priority = 2, dependsOnMethods = "demoFourTestOne")
    public void demoFourTestTwo() {
        System.out.println("This is a demoFourTestTwo");
        sleep();
    }
    @Test(priority = 3, dependsOnMethods = "demoFourTestOne")
    public void demoFourTestThree() {
        System.out.println("This is a demoFourTestThree");
        sleep();
    }

    @Test(priority = 4, dependsOnMethods = "demoFourTestTwo")
    public void demoFourTestFour() {
        System.out.println("This is a demoFourTestFour");
        sleep();
    }

    @Test(priority = 5, dependsOnMethods = "demoFourTestThree")
    public void demoFourTestFive() {
        System.out.println("This is a demoFourTestFive");
        sleep();
    }

    @Test(priority = 6, dependsOnMethods = "demoFourTestFour")
    public void demoFourTestSix() {
        System.out.println("This is a demoFourTestSix");
        sleep();
    }

    @Test(priority = 7, dependsOnMethods = "demoFourTestFive")
    public void demoFourTestSeven() {
        System.out.println("This is a demoFourTestSeven");
        sleep();
    }

    @Test(priority = 8, dependsOnMethods = "demoFourTestFive")
    public void demoFourTestEight() {
        System.out.println("This is a demoFourTestEight");
        sleep();
    }

    @Test(priority = 9, dependsOnMethods = "demoFourTestSeven")
    public void demoFourTestNine() {
        System.out.println("This is a demoFourTestNine");
        sleep();
    }

    @Test(priority = 10, dependsOnMethods = "demoFourTestNine")
    public void demoFourTestTen() {
        System.out.println("This is a demoFourTestTen");
        sleep();
    }

    @Test(priority = 11, dependsOnMethods = "demoFourTestSix")
    public void demoFourTestEleven() {
        System.out.println("This is a demoFourTestEleven");
        sleep();
    }

    @Test(priority = 12, dependsOnMethods = "demoFourTestEight")
    public void demoFourTestTwelve() {
        System.out.println("This is a demoFourTestTwelve");
        sleep();
    }

    @Test(priority = 13, dependsOnMethods = "demoFourTestTen")
    public void demoFourTestThirteen() {
        System.out.println("This is a demoFourTestThirteen");
        sleep();
    }

    // Helper method to simulate a delay (sleep) for demonstration purposes
    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
