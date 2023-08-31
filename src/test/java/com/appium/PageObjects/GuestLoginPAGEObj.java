package com.appium.PageObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GuestLoginPAGEObj
{

    // 2 coach-marks are present in otp auth
    // Tap to copy OTP  || Tap and hold to manage account

    @FindBy(id = "com.zoho.accounts.oneauth:id/guest_login")
    public WebElement btn_guest_login;

    @FindBy(id = "com.zoho.accounts.oneauth:id/animation_view")
    public WebElement animation_view;

    @FindBy(id = "com.zoho.accounts.oneauth:id/add_authenticator")
    public WebElement guest_add_authenticator;

    @FindBy(id = "com.zoho.accounts.oneauth:id/navigation_app_promotion_text")
    public WebElement guest_btn_upgrade;

    @FindBy(id = "com.zoho.accounts.oneauth:id/sign_up")
    public WebElement guest_btn_sign_up;

    @FindBy(id = "com.zoho.accounts.oneauth:id/sign_in")
    public WebElement guest_btn_sign_in;

    @FindBy(id = "com.zoho.accounts.oneauth:id/navigation_authenticator_text")
    public WebElement navigation_authenticator_text;

    @FindBy(id = "com.zoho.accounts.oneauth:id/navigation_app_promotion_text")
    public WebElement navigation_app_promotion_text;

    @FindBy(id = "com.zoho.accounts.oneauth:id/navigation_settings_text")
    public WebElement navigation_settings_text;

    @FindBy(id = "com.zoho.accounts.oneauth:id/manual_enter_secret")
    public WebElement manual_enter_secret;
    //android.widget.TextView[contains(@text,"Enter secret manually")]

    @FindBy(xpath = "//android.widget.TextView[contains(@text,\"Enter secret manually\")]")
    public WebElement manual_enter_secretXpath;
    @FindBy(id = "com.zoho.accounts.oneauth:id/account_name")
    public WebElement account_name;

    @FindBy(id = "com.zoho.accounts.oneauth:id/email")
    public WebElement email;

    @FindBy(id = "com.zoho.accounts.oneauth:id/secret_code")
    public WebElement secret_code;

    @FindBy(id = "com.zoho.accounts.oneauth:id/done")
    public WebElement done;

    @FindBy(id = "com.zoho.accounts.oneauth:id/image1")
    public WebElement otp_coachmark;

    @FindBy(id = "com.zoho.accounts.oneauth:id/add_tpa")
    public WebElement add_tpa;

    @FindBy(id = "com.zoho.accounts.oneauth:id/totp_text")
    public WebElement totp_text;

    @FindBy(id = "com.zoho.accounts.oneauth:id/auth_email")
    public WebElement auth_email;

    @FindBy(id = "com.zoho.accounts.oneauth:id/auth_name")
    public WebElement auth_name;

    @FindBy(id = "com.zoho.accounts.oneauth:id/auth_account_icon")
    public WebElement auth_account_icon;

    // TPA

    @FindBy(id = "com.zoho.accounts.oneauth:id/tpa_main_text")
    public WebElement OTP_Main_Text;

    @FindBy(id = "com.zoho.accounts.oneauth:id/up_button")
    public WebElement up_button;

    @FindBy(id = "com.zoho.accounts.oneauth:id/empty_authenticator")
    public WebElement empty_authenticator;

    @FindBy(id = "com.zoho.accounts.oneauth:id/add_authenticator")
    public WebElement add_authenticator;

    @FindBy(id = "com.zoho.accounts.oneauth:id/qr_image")
    public WebElement qr_image;

    @FindBy(id = "com.zoho.accounts.oneauth:id/close")
    public WebElement close_X_mark_button;

    @FindBy(id = "com.zoho.accounts.oneauth:id/email")
    public WebElement email_id;

    @FindBy(id = "com.zoho.accounts.oneauth:id/done")
    public WebElement done_button;

    @FindBy(id = "com.zoho.accounts.oneauth:id/add_tpa")
    public WebElement add_tpa_icon;

    @FindBy(xpath = "//android.widget.ImageView[@resource-id='com.zoho.accounts.oneauth:id/add_tpa']")
    public WebElement add_tpa_icon_xpath;

    @FindBy(xpath = "//*[contains(@text,\"google@zohomail.com\")]")
    public WebElement email_xpath;

    @FindBy(xpath = "//*[contains(@text,\"Google\")]")
    public WebElement name_xpath;

    @FindBy(xpath = "//*[contains(@text,\"facebook@zohomail.com\")]")
    public WebElement email_edited_xpath;

    @FindBy(xpath = "//android.widget.TextView[contains(@text,\"facebook@zohomail.com\")]")
    public WebElement email_edited_xpath2;

    @FindBy(xpath = "//*[contains(@text,\"Facebook\")]")
    public WebElement name_edited_xpath;

    @FindBy(xpath = "//android.widget.TextView[contains(@text,\"Facebook\")]")
    public WebElement name_edited_xpath1;

    //android.widget.TextView[@resource-id="com.zoho.accounts.oneauth:id/auth_email"]

    @FindBy(id = "com.zoho.accounts.oneauth:id/edit_tpa")
    public WebElement edit_tpa;

    @FindBy(id = "com.zoho.accounts.oneauth:id/delete_tpa")
    public WebElement delete_tpa;

    @FindBy(id = "com.zoho.accounts.oneauth:id/auth_name")
    public WebElement edit_auth_name;

    @FindBy(id = "com.zoho.accounts.oneauth:id/clear_txt_auth")
    public WebElement edit_auth_name_clear;

    @FindBy(id = "com.zoho.accounts.oneauth:id/title_add_email")
    public WebElement edit_email;

    @FindBy(id = "com.zoho.accounts.oneauth:id/clear_txt_email")
    public WebElement edit_email_clear;

    @FindBy(id = "com.zoho.accounts.oneauth:id/manual_enter_done")
    public WebElement edit_done_button;

    // INVALID TOAST MESSAGES

    @FindBy(xpath = "//*[contains(@text,\"Please enter an Account Name\")]")
    public WebElement enter_name_toast;

    @FindBy(xpath = "//*[contains(@text,\"Please enter an Email address\")]")
    public WebElement enter_email_toast;

    @FindBy(xpath = "//*[contains(@text,\"Please enter an Secret\")]")
    public WebElement enter_secret_toast;

    // Please enter an Account Name
    // Please enter an Email address
    // Please enter a Secret

    @FindBy(id = "com.zoho.accounts.oneauth:id/dialog_title")
    public WebElement dialog_title;

    @FindBy(id = "com.zoho.accounts.oneauth:id/dialog_negative_button")
    public WebElement delete_NO_button;

    @FindBy(id = "com.zoho.accounts.oneauth:id/dialog_positive_button")
    public WebElement delete_YES_button;


    @FindBy(id = "com.zoho.accounts.oneauth:id/authenticator_list")
    public WebElement authenticator_list;


    @FindBy(id = "com.zoho.accounts.oneauth:id/navigation_otp_auth")
    public WebElement TPA_Screen;

    @FindBy(id = "com.zoho.accounts.oneauth:id/arrow_icon")
    public WebElement coachmark_arrow_icon;

    // Android Scan QR permission controller
    @FindBy(id = "com.android.permissioncontroller:id/permission_message")
    public WebElement permission_message;

    @FindBy(id = "com.android.permissioncontroller:id/permission_allow_foreground_only_button")
    public WebElement permission_while_using_the_app;

    @FindBy(id = "com.android.permissioncontroller:id/permission_allow_button")
    public WebElement permissionAllow;

    @FindBy(id = "com.zoho.accounts.oneauth:id/manual_entry")
    public WebElement try_Manual_entry;

    // LoggedIn Page Objects

    @FindBy(id = "com.zoho.accounts.oneauth:id/edit_icon")
    public WebElement mfa_edit_button;

    @FindBy(xpath = "//*[contains(@text,\"MANAGE YOUR PRIVACY\")]")
    public WebElement manage_your_privacyPopUp;

    @FindBy(id = "com.zoho.accounts.oneauth:id/positive_button")
    public WebElement analytics;

    @FindBy(id =   "com.zoho.accounts.oneauth:id/with_email_address_toggle")
    public WebElement share_email;

    @FindBy(id = "com.zoho.accounts.oneauth:id/back_button")
    public WebElement back_button;

    @FindBy(id="com.zoho.accounts.oneauth:id/skip")
    public WebElement skip_button;

    @FindBy(id = "com.zoho.accounts.oneauth:id/login")
    public WebElement btnSignIn;

    // policy agreement

    @FindBy(id = "com.zoho.accounts.oneauth:id/close")
    public WebElement closePrivacyPolicyPopUp;

    @FindBy(xpath = "//android.widget.TextView[@text=\"Privacy Policy\"]")
    public WebElement title_privacy_policy_agreement;

    @FindBy(id = "com.zoho.accounts.oneauth:id/acknowledgment_note")
    public WebElement terms_conditions_agree_checkbox;
    @FindBy(id="com.zoho.accounts.oneauth:id/accept")
    public WebElement terms_conditions_agree_btn;

    // SignUp
    @FindBy(xpath = "//input[@name=\"email\"]")
    public WebElement signUpEmail;

    @FindBy(xpath = "//input[@name=\"password\"]")
    public WebElement signUpPassword;

    @FindBy(xpath = "//select[@name=\"country\"]")
    public WebElement signUpCountry;

    @FindBy(xpath = "//select[@name=\"country_state\"]")
    public WebElement signUpState;

    @FindBy(xpath = "//span[@id=\"signup-termservice\"]")
    public WebElement tos_checkbox;

    @FindBy(xpath = "//*[@value=\"Sign Up\"]")
    public WebElement signUpButton;

    @FindBy(xpath = "//*[contains(text(),\"Account Confirmation\")]")
    public WebElement accountConfirmationTitle;

    @FindBy(xpath = "//*[@value=\"Continue Sign In→\"]")
    public WebElement continueToSignInCTA;

    @FindBy(xpath = "//*[@class=\"android.widget.Image\"]")
    public WebElement captcha;

    @FindBy(xpath = "//input[@name=\"captcha\"]")
    public WebElement inputCaptcha;

    @FindBy(xpath = "//*[contains(@text, \"Search image with Google Lens\")]")
    public WebElement googleLensSearchBtn;

    @FindBy(id = "com.google.android.googlequicksearchbox:id/filter_item_view_text")
    public WebElement textBtnGoogleLens;

    @FindBy(xpath = "//android.widget.Button[@content-desc=\"Select all\"]")
    public WebElement selectAllBtnGoogleLens;

    @FindBy(xpath = "//android.widget.Button[@content-desc=\"Copy text\"]")
    public WebElement copyTextBtnGoogleLens;

    @FindBy(xpath = "//*[contains(text(),\"Image Text not entered correctly\")]")
    public WebElement wrongCaptchaErrorText;

    //input[@name="email"]

    //input[@name="password"]

    //select[@name="country"]

    //select[@name="country_state"]

    //span[@id="signup-termservice"]

    //*[@value="Sign Up"]

    //*[contains(text(),"Account Confirmation")]

    //*[@value="Continue Sign In→"]

    //    @FindBy(id = "")
//    public WebElement devices_button;

    //    @FindBy(id = "")
//    public WebElement devices_button;

    //    @FindBy(id = "")
//    public WebElement devices_button;

}
