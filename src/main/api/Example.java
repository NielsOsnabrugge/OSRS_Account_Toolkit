
import browser.AccountCreator;
import com.microsoft.playwright.*;
import data.Account;
import data.AccountStatus;
import data.Proxy;
import data.results.CreateAccountResult;
import utilities.CaptchaSolver;
import utilities.DataGenerator;
import utilities.DatabaseAPI;

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
       boolean success = AccountCreator.createAccount(acc, null);
       if(success){
           DatabaseAPI.InsertAccount(acc);
       }
    }

    private void createAccountNordVpn(){
        Account acc = new Account(
                DataGenerator.username(),
                DataGenerator.emailAddress("jeoco.xyz"),
                DataGenerator.password(),
                DataGenerator.dateOfBirth(),
                AccountStatus.UNKOWN);
        for(int j=0; j<1000; j++){
            Runtime runtime = Runtime.getRuntime();
            try {
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
}
