package utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static final Path txtPath = Paths.get("").toAbsolutePath().resolve("src\\main\\api\\utilities\\users.txt");

    public static LocalDate dateOfBirth(){
        int day = getRandNumber(1, 28);
        int month = getRandNumber(1, 12);
        int year = getRandNumber(1970, 2001);
        return LocalDate.of(year, month, day);
    }

    public static String emailAddress(String domain){
        String email = "";
        try {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(txtPath, StandardCharsets.ISO_8859_1));
            String name = fileContent.get(getRandNumber(0, fileContent.size() - 1));
            name = name.replace(" ", "");
            String secondName = fileContent.get(getRandNumber(0, fileContent.size() - 1));
            email = name + secondName.substring(0, 2) + "@" + domain;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return email;
    }

    public static String password(){
        StringBuilder password = new StringBuilder();
        String alphabet = "abcdefghijklmnopqrstuvwxyz123456789";
        int passLength = getRandNumber(5, 20);
        for (int i = 0; i < passLength; i++) {
            password.append(alphabet.charAt(getRandNumber(0, alphabet.length() - 1)));
        }

        return password.toString();
    }

    public static String username(){
        String username = "";
        try {
            List<String> fileContent = new ArrayList<>(Files.readAllLines(txtPath, StandardCharsets.ISO_8859_1));
            username = fileContent.get(getRandNumber(0, fileContent.size() - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return username;
    }

    private static int getRandNumber(int min, int max){
        Random rand = new Random();
        return rand.nextInt(max + 1 - min) + min;
    }
}
