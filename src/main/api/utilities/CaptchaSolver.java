package utilities;

import com.google.gson.JsonArray;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.imgscalr.Scalr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CaptchaSolver {

    private static final Path labelsPath = Paths.get("").toAbsolutePath().resolve("src\\main\\api\\utilities\\labels.txt");

    public static int[] createPrediction(float[][][] rgb, int topAmount) {
        JSONArray predictions = createPredictionPostRequest(rgb);
        int[] topX = getTopXPredictions(predictions, topAmount);

        return topX;
    }

    public static boolean verifyIfMatch(int[] actual, int[] predicted){
        for(int correct : actual){
            for(int predict : predicted){
                if(predict == correct){
                    return true;
                }
            }
        }
        return false;
    }

    public static int[] get_N_mostFrequentNumber(int[] arr, int N)
    {
        Map<Integer,Integer> integersCount = new HashMap<Integer,Integer>();

        for (Integer i : arr){
            if (!integersCount.containsKey(i))
                integersCount.put(i, 1);
            else
                integersCount.put(i, integersCount.get(i) + 1);
        }

        int[] topX = new int[N];
        Arrays.fill(topX, -1);

        for (Map.Entry<Integer, Integer> entry : integersCount.entrySet()) {
            Integer k = entry.getKey();
            Integer current = entry.getValue();
            for(int j=topX.length - 1; j>=0; j--){
                if (!integersCount.containsKey(topX[j]) || current >= integersCount.get(topX[j])){
                    if(j == 0){
                        topX[0] = k;
                    }
                    else{
                        topX[j] = topX[j - 1];
                    }
                }
                else{
                    if(j != topX.length - 1){
                        topX[j + 1] = k;
                    }
                    break;
                }
            }
        }

        printPredictions(topX);
        return null;
    }

    public static void printPredictions(int[] predictions){
        try {
            List<String> names = new ArrayList<>(Files.readAllLines(labelsPath, StandardCharsets.ISO_8859_1));
            for(int nr : predictions){
                System.out.println(names.get(nr));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] getTopXPredictions(JSONArray predictions, int am){
        int[] topX = new int[am];
        Arrays.fill(topX, 0);
        List<String> names = null;
        try {
            names = new ArrayList<>(Files.readAllLines(labelsPath, StandardCharsets.ISO_8859_1));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < predictions.length(); ++i) {
            float current = predictions.optFloat(i);
            for(int j=topX.length - 1; j>=0; j--){
                if (current >= predictions.optFloat(topX[j])){
                    if(j == 0){
                        topX[0] = i;
                    }
                    else{
                        topX[j] = topX[j - 1];
                    }
                }
                else{
                    if(j != topX.length - 1){
                        topX[j + 1] = i;
                    }
                    break;
                }
            }
        }
        return topX;
    }


    private static JSONArray createPredictionPostRequest(float[][][] rgb){
        String payload =
                "{" +
                        "\"signature_name\": \"serving_default\", " +
                        "\"instances\":[" + Arrays.deepToString(rgb) + "]" +
                        "}";
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://84.87.6.144:8501/v1/models/inceptionnet:predict");
        request.setEntity(entity);
        String json_string = "";
        try {
            HttpResponse response = httpClient.execute(request);
            json_string = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject(json_string);

        return obj.getJSONArray("predictions").optJSONArray(0);
    }
}
