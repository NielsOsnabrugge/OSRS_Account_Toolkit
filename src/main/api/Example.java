import browser.AccountCreator;
import com.microsoft.playwright.*;
import data.Account;
import data.AccountStatus;
import data.Proxy;
import data.results.CreateAccountResult;
import utilities.CaptchaSolver;
import utilities.DataGenerator;

import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Example {
    public static void main(String[] args){
        Account acc = new Account(
                DataGenerator.username(),
                DataGenerator.emailAddress("jeoco.xyz"),
                DataGenerator.password(),
                DataGenerator.dateOfBirth(),
                AccountStatus.UNKOWN);
        AccountCreator.createAccount(acc, null);
        Proxy proxy = new Proxy("207.228.36.188");
        Proxy proxy2 = new Proxy("207.228.41.51:49767", "h2AtsPDH2umTpme", "iu6hhMphWuhW6xZ");
    }

    private void temp(){
        Account acc = new Account(
                DataGenerator.username(),
                DataGenerator.emailAddress("jeoco.xyz"),
                DataGenerator.password(),
                DataGenerator.dateOfBirth(),
                AccountStatus.UNKOWN);
        for(int j=0; j<1000; j++){
            Runtime runtime = Runtime.getRuntime();
            try {
//                Process p1 = runtime.exec("cmd /c nordvpn -c -g \"Thailand\"");
//                InputStream is = p1.getInputStream();
//                TimeUnit.SECONDS.sleep(5);
                Process p1 = runtime.exec("cmd /c nordvpn -c -g \"Netherlands\"");
                InputStream is = p1.getInputStream();
                TimeUnit.SECONDS.sleep(10);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            for(int i=0; i<10; i++){
                boolean result  = AccountCreator.createAccount(acc, null);
                if(!result && i > 0){
                    break;
                }
            }
        }
    }

    private static void filterTextFile(){
        String[] words = new String[]{"nigger", "cock", "dick", "fuck", "cancer", "pussy", "throw", "penis", "porn", "cum", "sex", "teen", "pm", "fetish", "tit", "boob",
                "bot", "yummy", "gore", "whore", "ass", "kick", "slut", "horny", "blow", "shit", "erotic", "love", "anime", "hentai"};

        Path FILE_PATH = Paths.get("C:\\Users\\Niels\\Documents\\test\\LIMES-master\\OSRS_Account_Toolkit\\src\\main\\api\\utilities\\users.txt");

        try {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(FILE_PATH, StandardCharsets.ISO_8859_1));

            for (int i = 0; i < fileContent.size(); i++) {
                String line = fileContent.get(i);
                if (line.length() <= 3){
                    line = "";
                }
                String lcaseLine = line.toLowerCase();
                for (String word : words){
                    if(lcaseLine.contains(word)){
                        line = "";
                        break;
                    }
                }
                line = line.replace("_", " ");
                line = line.replace("-", " ");
                line = line.replaceAll("[^\\w\\s]","");
                if(line.length() > 12)
                    line = line.substring(0,12);
                fileContent.set(i, line);
            }
            fileContent.removeAll(Arrays.asList("", null));

            Files.write(FILE_PATH, fileContent, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}