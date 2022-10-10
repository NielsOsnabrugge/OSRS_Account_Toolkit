package browser;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import data.Account;
import data.AccountStatus;
import data.Proxy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static browser.BrowserOptions.GetLaunchOptions;

public class AccountChecker {
    private static void declineCookies(Page page){
        System.out.println("Declining cookies");
        Locator cookieButton = page.locator("#CybotCookiebotDialogBodyButtonDecline");
        cookieButton.click();
    }

    private static void acceptCookies(Page page){
        Locator cookieButton = page.locator("#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll");
        cookieButton.click();
    }

    public static AccountStatus checkAccount(Account account, Proxy proxy) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(GetLaunchOptions(proxy, true));
            BrowserContext context = browser.newContext();
            context.route("**/*", BrowserOptions::block_resources);

            Page page = context.newPage();

            page.navigate("https://secure.runescape.com/m=offence-appeal/account-history");

            //declineCookies(page);
            page.waitForSelector("[data-testid=continue-with-runescape]");

            Locator login_button = page.locator("[data-testid=continue-with-runescape]");
            login_button.click();

            // Fill in login form
            //declineCookies(page);
            System.out.println("Logging in");
            Locator usernameField = page.locator("#login-username");
            usernameField.fill(account.getEmail());

            Locator passwordField = page.locator("#login-password");
            passwordField.fill(account.getPassword());

            page.click("button:text(\"Log In\")");

            //declineCookies(page);
//            page.click("[data-test=account-status]");

            page.waitForSelector(":is(:text(\"There are no active offences on your account\"), " +
                    ":text(\"ban\"))");
            Locator noOffenses = page.locator(":text(\"There are no active offences on your account\")");
            if(noOffenses.count() > 0){
                return AccountStatus.ACTIVE;
            }
            else{
                return AccountStatus.BANNED;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return AccountStatus.UNKOWN;
    }
}
