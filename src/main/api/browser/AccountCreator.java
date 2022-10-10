package browser;
import static browser.BrowserOptions.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import data.Account;
import data.Proxy;
import com.microsoft.playwright.*;
import org.imgscalr.Scalr;
import utilities.CaptchaSolver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AccountCreator {
    private static String getImageUrlInLocator(Locator locator){
        String url = "";
        for(int i =0; i<locator.count(); i++){
            String style = locator.nth(i).getAttribute("style");
            System.out.println(style);
            if(style.contains("url(")){
                url = style.substring(style.indexOf("url(") + 5, style.indexOf("\")"));
                break;
            }
        }
        return url;
    }


    public static BufferedImage getImageFromPath(String path){
        BufferedImage image = null;
        try {
            File pathToFile = new File(path);
            image = ImageIO.read(pathToFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if(image.getWidth() != 120){
            image = resizeImage(image, 120, 1200);
        }
        return image;
    }
    private  static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }

    public static int[][][] converToPixels(BufferedImage img){
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        int[][][] RGB = new int[height][width][3];

        for(int w = 0; w < width; w++)
        {
            for(int h = 0; h < height; h++)
            {
                //Uses the Java color class to do the conversion from int to RGB
                Color temp = new Color(img.getRGB(w, h));
                RGB[h][w][0] = temp.getRed();
                RGB[h][w][1] = temp.getGreen();
                RGB[h][w][2] = temp.getBlue();
            }
        }
        return RGB;
    }


    private static int getClassPrediction(Locator imageLocator){
        Path imagePath = Paths.get("").toAbsolutePath().resolve("src\\main\\resources\\image.png");
        imageLocator.screenshot(new Locator.ScreenshotOptions()
                .setPath(imagePath));
        BufferedImage image = AccountCreator.getImageFromPath(imagePath.toString());
        int[][][] rgb = AccountCreator.converToPixels(image);
        int[] predictions = CaptchaSolver.createPrediction(rgb, 1);
        return predictions[0];
    }


    public static boolean createAccount(Account account, Proxy proxy){
        try (Playwright playwright = Playwright.create()) {
            Browser browserOptions = playwright.firefox().launch(GetLaunchOptions(proxy, false));
            BrowserContext context = browserOptions.newContext();
            context.route("**/*", BrowserOptions::block_resources);
            Page page = context.newPage();

            page.navigate("https://secure.runescape.com/m=account-creation/create_account?theme=oldschool");
            System.out.println(page.title());
            Locator cookieButton = page.locator("#CybotCookiebotDialogBodyButtonDecline");
            cookieButton.click();

            Locator emailField = page.locator("#create-email");
            emailField.fill(account.getEmail());

            Locator passwordField = page.locator("#create-password");
            passwordField.fill(account.getPassword());


            Locator bdayDayField = page.locator("[name=day]");
            bdayDayField.fill(
                    String.valueOf(
                            account.getDateOfBirth().getDayOfMonth()
                    ));

            Locator bdayMonthField = page.locator("[name=month]");
            bdayMonthField.fill(
                    String.valueOf(
                            account.getDateOfBirth().getMonthValue()
                    ));

            Locator bdayYearField = page.locator("[name=year]");
            bdayYearField.fill(
                    String.valueOf(
                            account.getDateOfBirth().getYear()
                    ));

            Locator termsField = page.locator("[name=agree_terms]");
            termsField.click();

            Locator submitButton = page.locator("#create-submit");
            submitButton.click();
            System.out.println("X-X-X-X-X-X-X-X-X-X-X-X-X-X-X-X-X-X-X-X-X-X-");
            Locator pleaseWaitText = page.locator("#cf-spinner-please-wait");

            pleaseWaitText.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));

            page.waitForSelector("[title='widget containing checkbox for hCaptcha security challenge'] >> visible=true");
            FrameLocator iframe = page.frameLocator("[title='widget containing checkbox for hCaptcha security challenge'] >> visible=true"); //
            Locator checkbox = iframe.locator("#checkbox");
            checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            checkbox.click();

            FrameLocator iframeImages = page.frameLocator("[title='Main content of the hCaptcha challenge']").last();

            Locator exampleImage = iframeImages.locator(".challenge-example");
            // TODO wait for selector to show instead of ugly for loop
            for(int i =0; i<5;i++){
                if(exampleImage.count() == 0){
                    TimeUnit.SECONDS.sleep(1);
                    exampleImage = iframeImages.locator(".challenge-example");
                }
            }
            TimeUnit.SECONDS.sleep(1);
            exampleImage = exampleImage.first().locator(".image").first();
            int actual = getClassPrediction(exampleImage);
            for(int j=0; j<2; j++){
                Locator wrappers = iframeImages.locator(".task-image");
                for(int i =0; i<wrappers.count(); i++){
                    Locator imageChild = wrappers.nth(i).locator(".image");
                    int prediction = getClassPrediction(imageChild);
                    if(actual == prediction){
                        wrappers.nth(i).click();
                    }
                }

                Locator nextButton = iframeImages.locator(".button-submit");
                nextButton.click();
            }
            page.waitForSelector("[id='p-account-created'] >> visible=true");
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
