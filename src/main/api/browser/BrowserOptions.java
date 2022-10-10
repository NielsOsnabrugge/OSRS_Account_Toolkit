package browser;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Route;
import data.Proxy;

import java.util.Set;

public class BrowserOptions {
    private static final Set<String> excludedResourceTypes = Set.of("stylesheet", "font", "media");
    private static final Set<String> excludedUrls = Set.of(
            "https://www.runescape.com/img/responsive/",
            "https://www.google-analytics.com",
            "https://tags.w55c.net");
    public static void block_resources(Route route){
        if(excludedResourceTypes.contains(route.request().resourceType())){
            route.abort();
        }
        else if(route.request().resourceType().equals("image")){
            String url = route.request().url();
            for(String excluded : excludedUrls){
                if(url.contains(excluded)){
                    route.abort();
                    return;
                }
            }
            route.resume();
        }
        else{
            route.resume();
        }
    }


    public static BrowserType.LaunchOptions GetLaunchOptions(Proxy proxy, boolean headles){
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
        launchOptions.timeout = (double)10000;
        launchOptions.headless = headles;

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
}
