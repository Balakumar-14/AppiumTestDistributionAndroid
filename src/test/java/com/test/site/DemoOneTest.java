package com.test.site;

import org.testng.annotations.Test;

public class DemoOneTest extends UserBaseTest{
    @Test(priority = 1)
    public void demoOneTestOne() {
        System.out.println("This is a demoOneTestOne test method");
        sleep();
    }

    @Test(priority = 2/*, dependsOnMethods = "demoOneTestOne"*/)
    public void demoOneTestTwo() {
        System.out.println("This is a demoOneTestTwo test method");
        sleep();
    }

    @Test(priority = 3/*, dependsOnMethods = "demoOneTestTwo"*/)
    public void demoOneTestThree() {
        System.out.println("This is a demoOneTestThree test method");
        sleep();
    }

    @Test(priority = 4/*, dependsOnMethods = "demoOneTestThree"*/)
    public void demoOneTestFour() {
        System.out.println("This is a demoOneTestFour test method");
        sleep();
    }

    @Test(priority = 5/*, dependsOnMethods = "demoOneTestFour"*/)
    public void demoOneTestFive() {
        System.out.println("This is a demoOneTestFive test method");
        sleep();
    }


}
