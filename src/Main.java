import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.Scanner;

public class Main {
    private static final String DBA_URL = "http://www.dba.dk/min-dbadk/mine-annoncer/inaktive-annoncer/";
    //private static final String GG_URL = "https://mit.guloggratis.dk/min-side/annoncer?&offset=250";
    private static final String GG_URL = "https://mit.guloggratis.dk/ny/min-side/annoncer/udloebne";
    private static boolean firstRun = true;
    private static WebDriverWait wait = null;
    private static int errorCount = 0;

    public static void main(String[] args) {
        // Create a new instance of the Firefox driver
        System.setProperty("webdriver.gecko.driver", "D:\\IntelliJProjects\\RenewGG_Ads-master\\lib3.4\\geckodriver.exe");
        File pathToFirefox = new File("C:\\Program Files\\Mozilla Firefox\\firefox.exe");

        FirefoxBinary firefoxBinary = new FirefoxBinary(pathToFirefox);
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        FirefoxOptions firefoxOptions = new FirefoxOptions().setBinary(firefoxBinary).setProfile(firefoxProfile);
        FirefoxDriver driver = new FirefoxDriver(firefoxOptions);

        wait = new WebDriverWait(driver, 60);

        //dbaLogin(driver);
        driver.get(DBA_URL);
        delay(30000);
        renewDBA(driver);
    }

    private static void dbaLogin(FirefoxDriver driver) {
        // load web page from url above
        driver.get(DBA_URL);

        // pop-up
        //wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"onetrust-accept-btn-handler\"]")));
        //driver.findElement(By.xpath("//*[@id=\"onetrust-accept-btn-handler\"]")).click();

        delay(1500);

        // Fortsæt button
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/div/div[4]/a[2]")));
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div[4]/a[2]")).click();

        delay(1500);

        // Log in to the web site
        WebElement email = driver.findElement(By.id("email"));
        email.sendKeys("tonnyb@gmail.com");

        // Give human time to click "I'm not a robot"
        delay(10000);

        // Næste
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/div/div/form/div[2]/div[3]/button/span[1]")));
        driver.findElement(By.xpath("/html/body/div[1]/div/div/div/form/div[2]/div[3]/button/span[1]")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"password\"]")));
        driver.findElement(By.xpath("//*[@id=\"password\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys("Th8zYKDHaKi6r9V");

        // Give human time to click "I'm not a robot"
        delay(10000);

        WebElement loginBtn = driver.findElement(By.id("ActionButton_0"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("ActionButton_0")));
        loginBtn.click();
    }

    private static void renewDBA(FirefoxDriver driver) {
            while (true) {
                // Wait for 'Genindryk' buttons to be loaded and be clickable(?)
                wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Genindryk")));

                // Click on 'Genindryk' button now that it's loaded and clickable
                WebElement renew = driver.findElement(By.linkText("Genindryk"));
                renew.click();
                //driver.executeScript("arguments[0].click();", renew);

                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/div[2]/section[3]/div[1]/div[1]/h3")));
                WebElement revealKontaktOplysninger = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/section[3]/div[1]/div[1]/h3"));
                driver.executeScript("arguments[0].click();", revealKontaktOplysninger);
                delay(1500);

                // Don't allow people to ask questions by text
                wait.until(ExpectedConditions.elementToBeClickable(By.id("QuestionsAndAnswersEnabled")));
                WebElement uncheckQuestionsAndAnswersEnabled = driver.findElement(By.id("QuestionsAndAnswersEnabled"));
                boolean isChecked = uncheckQuestionsAndAnswersEnabled.isSelected();
                if (isChecked)
                    uncheckQuestionsAndAnswersEnabled.click();

                // click 'Renew Ad' button
                WebElement nextButton = driver.findElement(By.xpath("//button[contains(text(),'Genindryk annonce')]"));
                //nextButton.click();
                driver.executeScript("arguments[0].click();", nextButton);

                // sleep a bit just to be sure the request was sent to server before navigating away from page
                delay(4000);

                // go back to list of ads to renew
                driver.navigate().to(DBA_URL);
            }

    }

    private static void ggLogin(FirefoxDriver driver, boolean cookiePolicyIsEnabled) {
        // load web page from url above
        driver.get(GG_URL);

        // Log in to website
        try {
            if (cookiePolicyIsEnabled) {
                wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
                WebElement cookiePolicyPopUpBtn = driver.findElement(By.id("onetrust-accept-btn-handler"));
                cookiePolicyPopUpBtn.click();
            }

            // Get the login overlay window to show up
            driver.get(GG_URL);
            wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));

            // begin filling in login information
            WebElement email = driver.findElement(By.name("email"));
            email.sendKeys("ton.nyb@gmail.com");
            WebElement password = driver.findElement(By.name("password"));
            password.sendKeys("(6DWnM]E;Rv=za*3}c*<");

            String loginBtnXpath = "/html/body/div[1]/div/div[2]/div[1]/div/article/div[1]/div[2]/section/div/form/button/span[1]";

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(loginBtnXpath)));
            WebElement loginBtn = driver.findElement(By.xpath(loginBtnXpath));
            loginBtn.click();
        } catch (Exception ex) {
            System.out.println("CATCH (during login): " + ex.getMessage());
            ggLogin(driver, false);

            //driver.close();
            //driver.quit();
        }
    }

    private static void renewGG(FirefoxDriver driver) {
        try {
            while (true) {
                String fornyBtnText = "/html/body/div[1]/div/div[4]/div[1]/div[2]/div[2]/div[2]/div[2]/div[1]/article/div[2]/div[3]/button";
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(fornyBtnText)));
                driver.findElement(By.xpath(fornyBtnText)).click();

                Thread.sleep(5000);
                String descriptionTextField = "/html/body/div[1]/div/div[4]/main/div[1]/section[2]/div[2]/div/fieldset/div[2]/label/span/textarea";
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(descriptionTextField)));
                String description = driver.findElement(By.xpath(descriptionTextField)).getText();

                if (description.length() == 0) {
                    String overskiftText = "/html/body/div[1]/div[5]/main/div[1]/section[1]/div[2]/div/form/fieldset[1]/div/div/label/span/input";
                    String overskrift = driver.findElement(By.xpath(overskiftText)).getAttribute("value");
                    driver.findElement(By.xpath(descriptionTextField)).sendKeys(overskrift);
                }

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,document.body.scrollHeight)");

                String gratisAnnonceBtnText = "/html/body/div[1]/div/div[4]/main/div[1]/section[4]/div[2]/div/section/div/div/div/div[3]/div/button/span";
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(gratisAnnonceBtnText)));
                driver.findElement(By.xpath(gratisAnnonceBtnText)).click();

                String opretAnnonceText = "/html/body/div[1]/div/div[4]/main/div[1]/section[5]/div[2]/div/div/button/span[1]";
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(opretAnnonceText)));
                driver.findElement(By.xpath(opretAnnonceText)).click();

                // sleep a bit just to be sure the request was sent to server before navigating away from page
                Thread.sleep(5000);
//                String seAnnoncenText = "/html/body/div[1]/div/div[4]/div[1]/div[2]/div[2]/div[2]/div[2]/div[1]/article/div[2]/div[3]/button";
//                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(seAnnoncenText)));

                // get back to overview of inactive ads
                driver.get(GG_URL);
            }
        } catch (org.openqa.selenium.NoSuchElementException ex) {
            // will throw exception when there are no more ads to renew
            System.out.println("CATCH (NoSuchElementException): " + ex.getMessage());

            if (ex.getMessage().toLowerCase().contains("gallup")) {
                driver.navigate().to(GG_URL);
                renewGG(driver);
            } else {
                // closes the current window the WebDriver is currently controlling.
                driver.close();
                driver.quit();
            }
        } catch (Exception ex) {
            // will throw exception when there are no more ads to renew
            System.out.println("CATCH (General Exception): " + ex.getMessage());

            if (errorCount < 10 ) {
                errorCount++;
                driver.navigate().to(GG_URL);
                renewGG(driver);
            }
            else if (ex.getMessage().toLowerCase().contains("gallup")) {
                driver.navigate().to(GG_URL);
                renewGG(driver);
            } else {
                // closes the current window the WebDriver is currently controlling.
                driver.close();
                driver.quit();
            }
        }
    }

    private static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ex) {
            System.out.println("Exception during thread sleep at login");
        }
    }

}
