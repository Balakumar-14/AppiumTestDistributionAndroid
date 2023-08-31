package com.test.site;

import com.appium.manager.AppiumDriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class UserBaseTest {
    public AppiumDriver driver;
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(GuestTests.class.getName());

    public AppiumDriver getDriver() {
        driver = AppiumDriverManager.getDriver();
        return driver;
    }

    public WebElement login(String locator) {
        return waitForElementByString(locator);
    }

    public WebElement waitForElementByString(String locator) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(30)).until(ExpectedConditions
                .elementToBeClickable(AppiumBy.accessibilityId(locator)));
    }

    public WebElement waitForElement(WebElement element, int timeInSec)
    {
        FluentWait<? extends WebDriver> wait;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeInSec));
        wait = applyMobileFluentWait(timeInSec);

        WebElement temp = wait.until(ExpectedConditions.visibilityOf(element));

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeInSec));
        return temp;
    }
    public FluentWait<AppiumDriver> applyMobileFluentWait(int timeInSec)
    {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeInSec)).pollingEvery(Duration.ofSeconds(1))
                .ignoring(org.openqa.selenium.NoSuchElementException.class).ignoring(TimeoutException.class);
    }
    public void sleep_function(int sleep_time)
    {
        try { Thread.sleep(sleep_time); } catch (InterruptedException e) { e.printStackTrace(); }
        LOGGER.info(sleep_time/1000 + " Second sleep completed");
    }
    public void tap_by_coordinates( int x, int y )
    {
        //AndroidTouchActions has been deprecated in appium 8.0
        JavascriptExecutor js = driver;
        Map< String, Object> args = new HashMap<>();
        args.put("x",x);
        args.put("y",y);
        js.executeScript("mobile: doubleClickGesture", args);

        LOGGER.info("Double click done");
    }

    public boolean isElementPresent(WebElement element, int timeInSec)
    {
        try
        {
            WebElement temp = waitForElement(element, timeInSec);
            if (temp == null)
            { return false; }
            return temp.isDisplayed();
        }
        catch (NoSuchElementException | TimeoutException e)
        {
            LOGGER.info("No such element found in isElementPresent -> " + element.toString());
            return false;
        }
    }

    public WebElement returnWebElement(String id, AppiumDriver mobileDriver)
    {
        return mobileDriver.findElement(By.id(id));
    }

}
