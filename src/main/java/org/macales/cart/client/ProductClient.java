package org.macales.cart.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.macales.cart.model.Product;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class ProductClient {
    private final String url = "http://route256.pavl.uk:8080";
    private final String token = "testtoken";

    public ProductClient() {
    }

    public Product getProduct(long sku) throws Exception {
        String addr = url + "/get_product";
        URL urlObj = new URL(addr);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = String.format("{\"token\": \"%s\", \"sku\": %d}", token, sku);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new Exception("SKU not found");
        } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new Exception("Invalid token");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Assuming the response JSON has fields "Name" and "Price"
            // You can use a library like Jackson or Gson to parse the JSON response
            // Here, we'll assume a simple parsing for demonstration
            String responseBody = response.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            ProductResponse productResponse = objectMapper.readValue(responseBody, ProductResponse.class);

            String name = productResponse.getName();
            long price = productResponse.getPrice();

            return new Product(name, price);
        }
    }

    public static class ProductResponse {
        private String name;
        private long price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }
    }
}
