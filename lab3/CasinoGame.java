package lab3;

import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CasinoGame {
    public static final String MODE_LCG = "Lcg";
    public static final String MODE_MT = "Mt";

    private final String baseURL = "http://95.217.177.249/casino";
    HttpClient httpClient;

    private String mode;
    private int accountId;
    private long moneyValue;

    private final Random accountIdGenerator;

    public CasinoGame () {
        accountIdGenerator = new Random(System.currentTimeMillis());
        httpClient = HttpClient.newHttpClient();
        System.out.println("Casino Royale game was created");
    }

    public void setMode(String gameMode) {
        mode = gameMode;
        System.out.println("Casino Royale mode is " + mode);
    }

    public long getMoney() {
        return moneyValue;
    }

    public boolean createAccount() {
        accountId = accountIdGenerator.nextInt(10000) + 1000;
        System.out.println("Create account: try accountId = " + accountId);

        URI uri = URI.create(baseURL + "/createacc?id=" + accountId);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Create account: status code is " + response.statusCode());
            System.out.println(response.body());
            if (response.statusCode() != 201) {
                return false;
            }
            JSONObject jo = (JSONObject) new JSONParser().parse(response.body());
            moneyValue = (Long) jo.get("money");
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            throw new RuntimeException("Error occurred: " + e);
        }

        System.out.println("money " + moneyValue);
        return true;
    }

    public long play(long betMoney, long betNumber) {
        assert mode != null;

        System.out.println("play: bet number is " + betNumber);
        System.out.println("play: bet money is " + betMoney);
        String urlValue = String.format("/play%s?id=%d&bet=%d&number=%d", mode, accountId, betMoney, betNumber);

        URI uri = URI.create(baseURL + urlValue);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Play: status code is " + response.statusCode());
                System.out.println(response.body());
                throw new RuntimeException("Error occurred: " + response.body());
            }

            JSONObject jo = (JSONObject) new JSONParser().parse(response.body());
            String msg = (String) jo.get("message");
            System.out.println("Play: message from casino is '" + msg + "'");

            moneyValue = (long)((JSONObject) jo.get("account")).get("money");
            System.out.println("Play: my money = " + moneyValue);

            long realNumber = (Long) jo.get("realNumber");
            System.out.println("Play: real number = " + realNumber);
            return realNumber;

        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            throw new RuntimeException("Error occurred: " + e);
        }
    }
}
