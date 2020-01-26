import org.openqa.selenium.By;
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
        System.out.println("SELECT WHICH ADS TO RENEW");
        System.out.println("[1] DBA");
        System.out.println("[2] GG");
        //System.out.println("[3] Faerdiggoer GG");
        Scanner keyboard = new Scanner(System.in);
        int answer = keyboard.nextInt();

        while (answer != 1 && answer != 2) {
            System.out.println("Invalid selection.");
            answer = keyboard.nextInt();
        }

        // Create a new instance of the Firefox driver
        System.setProperty("webdriver.gecko.driver", "D:\\IntelliJProjects\\RenewGG_Ads-master\\lib3.4\\geckodriver.exe");
        //File pathToFirefox = new File("D:\\Program Files\\Firefox\\firefox.exe");
        File pathToFirefox = new File("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
        //File pathToFirefox = new File("/usr/bin/firefox");

        FirefoxBinary firefoxBinary = new FirefoxBinary(pathToFirefox);
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        FirefoxOptions firefoxOptions = new FirefoxOptions().setBinary(firefoxBinary).setProfile(firefoxProfile);
        FirefoxDriver driver = new FirefoxDriver(firefoxOptions);

        wait = new WebDriverWait(driver, 60);

        switch (answer) {
            case 1:
                dbaLogin(driver);
                renewDBA(driver);
                break;
            case 2:
                ggLogin(driver);
                renewGG(driver);
                break;
//            case 3:
//                finishGG(driver);
        }
    }

    private static void dbaLogin(FirefoxDriver driver) {
        // load web page from url above
        driver.get(DBA_URL);

        // Log in to the web site
        WebElement email = driver.findElement(By.id("Email"));
        email.sendKeys("tonnyb@gmail.com");
        WebElement password = driver.findElement(By.id("Password"));
        password.sendKeys("hyldevang50");
        WebElement loginBtn = driver.findElement(By.id("LoginButton"));
        loginBtn.click();
    }

    private static void renewDBA(FirefoxDriver driver) {
        try {
            while (true) {
                // Wait for 'Genindryk' buttons to be loaded and be clickable(?)
                //WebDriverWait wait = new WebDriverWait(driver, 20);
                wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Genindryk")));

                // Click on 'Genindryk' button now that it's loaded and clickable
                WebElement renew = driver.findElement(By.linkText("Genindryk"));
                renew.click();
                //driver.executeScript("arguments[0].click();", renew);

                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div/div[2]/section[3]/div[1]/div[1]/h3")));
                WebElement revealKontaktOplysninger = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/section[3]/div[1]/div[1]/h3"));
                driver.executeScript("arguments[0].click();", revealKontaktOplysninger);
                Thread.sleep(1500);

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
                Thread.sleep(4000);

                // go back to list of ads to renew
                driver.navigate().to(DBA_URL);
            }
        } catch (org.openqa.selenium.NoSuchElementException ex) {
            System.out.println("CATCH from noSuchElement: " + ex.getMessage());

            if (!ex.getMessage().toLowerCase().contains("genindryk")) {
                driver.navigate().to(DBA_URL);
                renewDBA(driver);
            }
        } catch (org.openqa.selenium.TimeoutException ex) {
            System.out.println("Inside timeOut exception");
            System.out.println(ex.getMessage());

            if (ex.getMessage().equals("Expected condition failed: waiting for element to be clickable: By.linkText: Genindryk (tried for 20 second(s) with 500 MILLISECONDS interval)")) {
                System.out.println("-------------------\n!!! THERE ARE NO MORE ADS TO RENEW !!!\n-------------------\n");
                // time to quit the app because there are no more ads to renew
                driver.close();
                driver.quit();
            } else {
                driver.navigate().refresh();
                driver.navigate().to(DBA_URL);
                renewDBA(driver);
            }
        } catch (Exception ex) {
            System.out.println("CATCH from general exception: " + ex.getMessage());
            //System.out.println("Get cause toString:" + ex.getCause().toString()); causes NPE

            if (ex.getMessage().toLowerCase().contains("gallup")) {
                driver.navigate().to(DBA_URL);
                renewDBA(driver);
            } else {
                driver.close();
                driver.quit();
            }
        }
    }

    private static void ggLogin(FirefoxDriver driver) {
        // load web page from url above
        driver.get(GG_URL);

        // Log in to website
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
            WebElement cookiePolicyPopUpBtn = driver.findElement(By.id("onetrust-accept-btn-handler"));
            cookiePolicyPopUpBtn.click();

            // Get the login overlay window to show up
            driver.get(GG_URL);
            wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));

            // begin filling in login information
            WebElement email = driver.findElement(By.name("email"));
            email.sendKeys("ton.nyb@gmail.com");
            WebElement password = driver.findElement(By.name("password"));
            password.sendKeys("(6DWnM]E;Rv=za*3}c*<");

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div[2]/div/div/article/div[1]/div[2]/section/div/form/button")));
            WebElement loginBtn = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/article/div[1]/div[2]/section/div/form/button"));
            loginBtn.click();
        } catch (Exception ex) {
            System.out.println("CATCH (during login): " + ex.getMessage());
            driver.close();
            driver.quit();
        }
    }

    private static void renewGG(FirefoxDriver driver) {
        try {
            while (true) {
                String fornyBtnText = "/html/body/div[1]/div[5]/div/div/div[2]/div/div[2]/div[2]/div[1]/article/div[2]/div[3]/button/span";
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(fornyBtnText)));
                driver.findElement(By.xpath(fornyBtnText)).click();

                Thread.sleep(5000);
                String descriptionTextField = "/html/body/div[1]/div[5]/main/div[1]/section[2]/div[2]/div/fieldset/div[2]/label/span/textarea";
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(descriptionTextField)));
                String description = driver.findElement(By.xpath(descriptionTextField)).getText();

                if (description.length() == 0) {
                    String overskiftText = "/html/body/div[1]/div[5]/main/div[1]/section[1]/div[2]/div/form/fieldset[1]/div/div/label/span/input";
                    String overskrift = driver.findElement(By.xpath(overskiftText)).getAttribute("value");
                    driver.findElement(By.xpath(descriptionTextField)).sendKeys(overskrift);
                }

                //String gratisAnonceBtnText = "/html/body/div[1]/div[5]/main/div[1]/section[4]/div[2]/div/section/div/div/div/div[3]/div/button/span";
                String gratisAnnonceBtnText = "/html/body/div[1]/div[5]/main/div[1]/section[4]/div[2]/div/section/div/div/div/div[3]/div/button/span";
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(gratisAnnonceBtnText)));
                driver.findElement(By.xpath(gratisAnnonceBtnText)).click();

                String opretAnnonceText = "/html/body/div[1]/div[5]/main/div[1]/section[5]/div[2]/div/div/button/span[1]";
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(opretAnnonceText)));
                driver.findElement(By.xpath(opretAnnonceText)).click();

                // sleep a bit just to be sure the request was sent to server before navigating away from page
                Thread.sleep(5000);
                String seAnnoncenText = "/html/body/div[1]/div[2]/div[21]/div/article/div/div[2]/section/div/div[2]/div[2]/a[1]/span";
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(seAnnoncenText)));

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
                //System.out.println("Closing with error count: " + errorCount);
                driver.close();
                driver.quit();
            }
        } catch (Exception ex) {
            // will throw exception when there are no more ads to renew
            System.out.println("CATCH (General Exception): " + ex.getMessage());

            //String gratisAnonnceBtnTextError = "Expected condition failed: waiting for element to be clickable: By.xpath: /html/body/div[1]/div[5]/main/div[1]/section[4]/div[2]/div/section/div/div/div/div[3]/div/button/span (tried for 60 second(s) with 500 MILLISECONDS interval)";
            //String fornyBtnTextError = "Expected condition failed: waiting for element to be clickable: By.xpath: /html/body/div[1]/div[5]/div/div/div[2]/div/div[2]/div[2]/div[1]/article/div[2]/div[3]/button/span (tried for 60 second(s) with 500 MILLISECONDS interval)";

            // sometimes the web page containing the list of inactve ads till return 0 by mistake. Keep reloading the page 3 times before giving up.
//            if (errorCount < 4 && (ex.getMessage().equals(gratisAnonnceBtnTextError) || ex.getMessage().equals(fornyBtnTextError))) {
//                driver.navigate().to(GG_URL);
//                renewGG(driver);
//                errorCount++;
//            }
            if (errorCount < 10 ) {
                driver.navigate().to(GG_URL);
                renewGG(driver);
                errorCount++;
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
}
