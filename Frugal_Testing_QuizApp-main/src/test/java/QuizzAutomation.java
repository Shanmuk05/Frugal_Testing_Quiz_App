import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class QuizzAutomation {

    static WebDriver driver;
    static FileWriter logWriter;
    static boolean HEADLESS = false; // Set to true for headless mode

    public static void main(String[] args) throws Exception {

        // -------------------------
        // SETUP
        // -------------------------
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (HEADLESS) {
            options.addArguments("--headless");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Log file
        logWriter = new FileWriter("selenium-log.txt", false);

        log("===== QUIZ AUTOMATION STARTED =====");

        // -------------------------
        // 1. LOAD QUIZ PAGE
        // -------------------------
        String path = "C://Users//SAIKIRAN//Downloads//Frugal_Testing_QuizApp-main//Frugal_Testing_QuizApp-main//quiz.html";
        driver.get(path);
        Thread.sleep(1000);

        takeScreenshot("01_Landing_Page.png");
        log("Loaded Quiz Page");
        String title = driver.getTitle();
        String url = driver.getCurrentUrl();
        log("Page Title: " + title);
        log("Page URL: " + url);

        // Verify landing page
        if (!title.contains("Dynamic Quiz")) {
            throw new RuntimeException("Landing page title verification failed: " + title);
        }
        if (!url.contains("quiz.html")) {
            throw new RuntimeException("Landing page URL verification failed: " + url);
        }
        log("Landing page verified successfully.");

        // Select category (e.g., Python)
        selectCategory("python");
        log("Selected category: Python");

        // -------------------------
        // 2. CLICK START QUIZ
        // -------------------------
        WebElement startBtn = driver.findElement(By.id("startBtn"));
        startBtn.click();
        Thread.sleep(1500);

        takeScreenshot("02_Quiz_Started.png");
        log("Quiz Started — Question 1 displayed");

        // Verify quiz started
        WebElement quizCard = driver.findElement(By.id("quizCard"));
        if (!quizCard.isDisplayed()) {
            throw new RuntimeException("Quiz card not displayed after start.");
        }
        WebElement questionText = driver.findElement(By.id("questionText"));
        if (questionText.getText().isEmpty()) {
            throw new RuntimeException("Question text not displayed.");
        }
        log("Quiz start verified: First question displayed.");

        // -------------------------
        // 3. ANSWER ALL QUESTIONS
        // -------------------------
        for (int i = 1; i <= 5; i++) {

            log("Answering Question " + i);
            takeScreenshot("Question_" + i + ".png");

            // Verify question and options
            String qText = questionText.getText();
            log("Question " + i + " text: " + qText);
            if (qText.isEmpty()) {
                throw new RuntimeException("Question " + i + " text is empty.");
            }

            // Verify 4 options are present
            java.util.List<WebElement> choices = driver.findElements(By.className("choice"));
            if (choices.size() != 4) {
                throw new RuntimeException("Question " + i + " does not have 4 options.");
            }
            log("Question " + i + " has " + choices.size() + " options.");

            // Select the THIRD OPTION every time (example requirement)
            WebElement choice = driver.findElement(By.xpath("//div[@class='choice'][3]"));
            choice.click();
            Thread.sleep(800);

            // Click NEXT except after last question
            if (i < 5) {
                driver.findElement(By.id("nextBtn")).click();
                Thread.sleep(1200);
            }
        }

        // -------------------------
        // 4. SUBMIT QUIZ
        // -------------------------
        driver.findElement(By.id("submitBtn")).click();
        Thread.sleep(1500);

        takeScreenshot("06_Result_Page.png");
        log("Quiz Submitted. Results displayed.");

        // Verify results page
        WebElement resultsCard = driver.findElement(By.id("resultsCard"));
        if (!resultsCard.isDisplayed()) {
            throw new RuntimeException("Results card not displayed.");
        }
        log("Results page verified.");

        // -------------------------
        // 5. VERIFY SCORE PAGE
        // -------------------------
        String score = driver.findElement(By.id("scoreText")).getText();
        String correct = driver.findElement(By.id("correctCount")).getText();
        String wrong = driver.findElement(By.id("wrongCount")).getText();

        log("Score shown: " + score);
        log("Correct answers: " + correct);
        log("Wrong answers: " + wrong);

        // Assert score format
        if (!score.matches("\\d+ / \\d+")) {
            throw new RuntimeException("Score format invalid: " + score);
        }
        if (!correct.matches("\\d+")) {
            throw new RuntimeException("Correct count invalid: " + correct);
        }
        if (!wrong.matches("\\d+")) {
            throw new RuntimeException("Wrong count invalid: " + wrong);
        }
        log("Score verification passed.");

        log("===== QUIZ AUTOMATION COMPLETED SUCCESSFULLY =====");

        // cleanup
        logWriter.close();
        driver.quit();
    }

    // -------------------------
    // Take Screenshot
    // -------------------------
    public static void takeScreenshot(String filename) {
        try {
            File scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            File destDir = new File("screenshots");
            if (!destDir.exists()) destDir.mkdir();

            File destFile = new File(destDir, filename);
            FileUtils.copyFile(scr, destFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------
    // Select Category Helper
    // -------------------------
    public static void selectCategory(String category) {
        WebElement categoryBtn = driver.findElement(By.cssSelector("[data-category='" + category + "']"));
        categoryBtn.click();
    }

    // -------------------------
    // Logging Helper
    // -------------------------
    public static void log(String message) throws IOException {
        logWriter.write(message + "\n");
        System.out.println(message);
    }
}
