package com.test.site;

import com.appium.PageObjects.GuestLoginPAGEObj;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

@Test(groups = { "GuestTests" })
public class GuestTests extends UserBaseTest{

    private AppiumDriver mobileDriver;
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(GuestTests.class.getName());
    public GuestLoginPAGEObj guestLoginPAGEObj;

//    public GuestTests() {
//        super();
//        guestLoginPAGEObj = new GuestLoginPAGEObj();
//        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), guestLoginPAGEObj);
//    }

    @Test(priority = 1)
    public void setPageObjects()
    {
        mobileDriver = getDriver();
        guestLoginPAGEObj = new GuestLoginPAGEObj();
        PageFactory.initElements(new AppiumFieldDecorator(mobileDriver), guestLoginPAGEObj);
    }

    @Test(priority = 2/*, dependsOnMethods = "setPageObjects"*/)
    public void Guest_Login_and_Set_TPA()
    {
        mobileDriver = getDriver();
        LOGGER.info("-------  OneAuth Login as Guest and set TPA  ------");
        LOGGER.info("Driver status " + getDriver().getStatus());
        if(isElementPresent(mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/guest_login")),5))
        {
            waitForElement(mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/guest_login")),5).click();
            LOGGER.info("GUEST LOGIN BUTTON IS CLICKED");
        }

        sleep_function(2000);

        mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/navigation_authenticator_text")).click();
        LOGGER.info("OTP AUTHENTICATOR SCREEN IN GUEST IS CLICKED");

        if(isElementPresent(mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/empty_authenticator")),5))
        {
            LOGGER.info("NO TPA IS ADDED");

            returnWebElement("com.zoho.accounts.oneauth:id/add_authenticator", mobileDriver).click();
//            waitForElement(guestLoginPAGEObj.add_authenticator,6).click();
            LOGGER.info("ADD BUTTON IS CLICKED");
        }
        else
        {
            LOGGER.info("TPA IS ADDED");

            returnElementWithID(guestLoginPAGEObj.add_tpa_icon,mobileDriver).click();

//            waitForElement(guestLoginPAGEObj.add_tpa_icon,6).click();
            LOGGER.info("ADD ICON IS CLICKED");
        }

//        if(isElementPresent(guestLoginPAGEObj.permission_message,6))
//        {
//            LOGGER.info("PERMISSION IS ASKED FOR SCAN QR");
//            if(isElementPresent(guestLoginPAGEObj.permission_while_using_the_app,2)) {
//                guestLoginPAGEObj.permission_while_using_the_app.click();
//                LOGGER.info("PERMISSION - WHILE USING THE APP FOR SCAN QR IS GIVEN");
//            } else {
//                guestLoginPAGEObj.permissionAllow.click();
//                LOGGER.info("ALLOW button has been clicked on the Camera permission pop-up");
//            }
//
//        }
//        else
//        {
//            LOGGER.info("PERMISSION POPUP IS NOT ASKED FOR SCAN QR");
//        }
//        if(isElementPresent(returnElementWithID(guestLoginPAGEObj.try_Manual_entry,mobileDriver),6))
////        if(isElementPresent(guestLoginPAGEObj.try_Manual_entry,6))
//        {
//            waitForElement(returnElementWithID(guestLoginPAGEObj.try_Manual_entry,mobileDriver),6).click();
//            LOGGER.info("TRY MANUAL ENTRY BUTTON IS CLICKED");
//        }
         if(isElementPresent(mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/manual_enter_secret")),6))
//        else if(isElementPresent(guestLoginPAGEObj.manual_enter_secret,6))
        {
            mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/manual_enter_secret")).click();
            LOGGER.info("TRY MANUAL ENTRY BUTTON IS CLICKED");
        }
        else
        {
            LOGGER.info("SPECIFIED CTA'S NOT SHOWN");
        }
        mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/account_name")).sendKeys("accountName");
//        waitForElement(guestLoginPAGEObj.account_name,6).sendKeys("accountName");
        LOGGER.info("ACCOUNT NAME IS GIVEN");

        mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/email")).sendKeys("email@gmail.com");
        LOGGER.info("EMAIL ID IS GIVEN");

        mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/secret_code")).sendKeys("accountName");
        LOGGER.info("SECRET IS ENTERED");

        mobileDriver.findElement(By.id("com.zoho.accounts.oneauth:id/done")).click();
        LOGGER.info("DONE BUTTON IS CLICKED");

        sleep_function(2000);
//        sleep_function(5000);
//        tap_by_coordinates(500,300);
//        sleep_function(2000);
//        tap_by_coordinates(500,300);
//        sleep_function(2000);
//
//        String tpa_email =  guestLoginPAGEObj.auth_email.getText();
//        LOGGER.info("DISPLAYED EMAIL IS - " + tpa_email);
//
//        if(tpa_email.equals("email"))
//        {
//            LOGGER.info("CORRECT EMAIL IS DISPLAYED");
//        }
//        else
//        {
//            LOGGER.info("WRONG EMAIL IS DISPLAYED");
//        }
//
//        String tpa_name = guestLoginPAGEObj.auth_name.getText();
//        LOGGER.info("DISPLAYED NAME IS - " + tpa_name);
//
//        if(tpa_name.equals("accountName"))
//        {
//            LOGGER.info("CORRECT NAME IS DISPLAYED");
//        }
//        else
//        {
//            LOGGER.info("WRONG NAME IS DISPLAYED");
//        }

    }

    public WebElement returnElementWithID(WebElement element, AppiumDriver mobileDriver)
    {
        return mobileDriver.findElement(By.id(element.getAttribute("id")));
    }

    public WebElement returnElementWithXPATH(WebElement element, AppiumDriver mobileDriver)
    {
        return mobileDriver.findElement(By.xpath(guestLoginPAGEObj.manual_enter_secretXpath.getAttribute("id")));
    }
}
