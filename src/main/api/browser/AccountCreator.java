package browser;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import data.Account;
import data.Proxy;
import data.results.CreateAccountResult;
import com.microsoft.playwright.*;
import org.imgscalr.Scalr;
import utilities.CaptchaSolver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Selector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AccountCreator {
    private static final Set<String> excludedResourceTypes = Set.of("stylesheet", "font", "media");


    private static BrowserType.LaunchOptions GetLaunchOptions(Proxy proxy){
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
        launchOptions.timeout = (double)90000;
        launchOptions.headless = false;

        if (proxy != null){
            com.microsoft.playwright.options.Proxy playwrightProxy = new com.microsoft.playwright.options.Proxy(proxy.getIp());
            if (proxy.getUsername() != null){
                playwrightProxy.setUsername(proxy.getUsername());
                playwrightProxy.setPassword(proxy.getPassword());
            }

            launchOptions.setProxy(playwrightProxy);
        }

        return launchOptions;
    }

    private static void block_resources(Route route){
        System.out.println("---");

        if(excludedResourceTypes.contains(route.request().resourceType())){
            route.abort();
        }
        else{
            System.out.println(route.request().resourceType());
            route.resume();
        }
    }

    private static void block_resources_except_image(Route route){
        if(excludedResourceTypes.contains(route.request().resourceType()) && !route.request().resourceType().equals("image")){
            route.abort();
        }
        else{
            route.resume();
        }
    }

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
        return image;
    }
    private  static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }

    public static float[][][] converToPixels(BufferedImage img){
        BufferedImage resizedImg = resizeImage(img, 320, 320);
        resizedImg = img;
        int width = resizedImg.getWidth(null);
        int height = resizedImg.getHeight(null);

        float[][][] RGB = new float[height][width][3];

        for(int w = 0; w < width; w++)
        {
            for(int h = 0; h < height; h++)
            {
                //Uses the Java color class to do the conversion from int to RGB
                Color temp = new Color(resizedImg.getRGB(w, h));
                RGB[h][w][0] = temp.getRed() / 255f;
                RGB[h][w][1] = temp.getGreen() / 255f;
                RGB[h][w][2] = temp.getBlue() / 255f;
            }
        }
        return RGB;
    }


    private static int getClassPrediction(Locator imageLocator){
        Path imagePath = Paths.get("").toAbsolutePath().resolve("src\\main\\resources\\image.png");
        imageLocator.screenshot(new Locator.ScreenshotOptions()
                .setPath(imagePath));
        BufferedImage image = AccountCreator.getImageFromPath(imagePath.toString());
        float[][][] rgb = AccountCreator.converToPixels(image);
        int[] predictions = CaptchaSolver.createPrediction(rgb, 1);
        return predictions[0];
    }


    public static boolean createAccount(Account account, Proxy proxy){
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(GetLaunchOptions(proxy));
            BrowserContext context = browser.newContext();
            context.route("**/*", AccountCreator::block_resources);
            Page page = context.newPage();

//            page.route("**/*", AccountCreator::block_resources);
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
            exampleImage = exampleImage.first().locator(".image").first();
            int actual = getClassPrediction(exampleImage);

            Locator wrappers = iframeImages.locator(".image-wrapper");
            for(int i =0; i<wrappers.count(); i++){
                Locator imageChild = wrappers.nth(i).locator(".image");
                int prediction = getClassPrediction(imageChild);
                if(actual == prediction){
                    // Click on it
                }
                imageChild.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
