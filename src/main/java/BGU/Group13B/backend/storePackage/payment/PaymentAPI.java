package BGU.Group13B.backend.storePackage.payment;

import BGU.Group13B.backend.storePackage.delivery.DeliveryAdapter;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.SingletonCollection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class PaymentAPI implements PaymentAdapter, DeliveryAdapter {
    private final String API_URL = "https://php-server-try.000webhostapp.com/";
    private static final Logger LOGGER_INFO = Logger.getLogger(Session.class.getName());
    private static final Logger LOGGER_ERROR = Logger.getLogger(Session.class.getName());

    static {
        SingletonCollection.setFileHandler(LOGGER_INFO, true);
        SingletonCollection.setFileHandler(LOGGER_ERROR, false);

    }

    public static void main(String[] args) {
        PaymentAPI paymentApi = new PaymentAPI();

        // Perform a handshake
        paymentApi.handshake();

        // Perform a payment
        String cardNumber = "2222333344445555";
        String month = "4";
        String year = "2021";
        String holder = "Israel Israelovice";
        String ccv = "262";
        String id = "20444444";
        paymentApi.pay(cardNumber, month, year, holder, ccv, id);

        // Cancel a payment
        String transactionId = "20123";
        paymentApi.cancelPay(transactionId);

        // Perform a supply
        String name = "Israel Israelovice";
        String address = "Rager Blvd 12";
        String city = "Beer Sheva";
        String country = "Israel";
        String zip = "8458527";
        paymentApi.supply(name, address, city, country, zip);

        // Cancel a supply
        transactionId = "30525";
        paymentApi.cancelSupply(transactionId);
    }

    public void handshake() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("action_type", "handshake");
            String response = responseOutput(con, parameters);
            if (response.equals("OK")) {
                LOGGER_INFO.info("Handshake successful");
            } else {
                LOGGER_ERROR.severe("Handshake failed");
            }
        } catch (Exception e) {
            LOGGER_ERROR.severe(e.getMessage());
        }
    }

    private String responseOutput(HttpURLConnection con, Map<String, String> parameters) throws IOException {
        String postBody = buildPostBody(parameters);
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(postBody);
        out.flush();
        out.close();
        AtomicBoolean timeout = new AtomicBoolean(false);
        Timer timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                con.disconnect();
                timeout.set(true);
            }
        }, 5000);
        int responseCode = con.getResponseCode();
        if (timeout.get()) {
            LOGGER_ERROR.severe("Connection timed out");
            return "Connection timed out";
        }
        LOGGER_INFO.info("Response code: " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
//        LOGGER_INFO.info("Response: " + response);
        return response.toString();
    }

    //TODO handle transaction number
    public boolean pay(String cardNumber, String month, String year, String holder, String ccv, String id) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("action_type", "pay");
            parameters.put("card_number", cardNumber);
            parameters.put("month", month);
            parameters.put("year", year);
            parameters.put("holder", holder);
            parameters.put("ccv", ccv);
            parameters.put("id", id);
            String response = responseOutput(con, parameters);
            if (Integer.parseInt(response) != -1) {
                LOGGER_INFO.info("Payment successful");
                //TODO handle transaction number
            } else {
                LOGGER_ERROR.severe("Payment failed");
            }
            return Integer.parseInt(response) != -1;
        } catch (Exception e) {
            LOGGER_ERROR.severe("Failed to pay: " + e.getMessage());
            return false;
        }
    }

    public void cancelPay(String transactionId) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("action_type", "cancel_pay");
            parameters.put("transaction_id", transactionId);
            String response = responseOutput(con, parameters);
            if (response.equals("1")) {
                LOGGER_INFO.info("Payment cancellation successful");
            } else {
                LOGGER_ERROR.severe("Payment cancellation failed");
            }
        } catch (Exception e) {
            LOGGER_ERROR.severe(e.getMessage());
        }
    }


    public boolean supply(String name, String address, String city, String country, String zip) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("action_type", "supply");
            parameters.put("name", name);
            parameters.put("address", address);
            parameters.put("city", city);
            parameters.put("country", country);
            parameters.put("zip", zip);

            String response = responseOutput(con, parameters);
            if (Integer.parseInt(response) != -1) {
                LOGGER_INFO.info("Supply successful");
            } else {
                LOGGER_ERROR.severe("Supply failed");
            }
            return Integer.parseInt(response) != -1;
        } catch (Exception e) {
            LOGGER_ERROR.severe(e.getMessage());
            return false;
        }
    }

    public void cancelSupply(String transactionId) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("action_type", "cancel_supply");
            parameters.put("transaction_id", transactionId);
            responseOutput(con, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildPostBody(Map<String, String> parameters) {
        StringBuilder postBody = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            postBody.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        postBody.deleteCharAt(postBody.length() - 1); // Remove the last "&" character
        return postBody.toString();
    }

}
