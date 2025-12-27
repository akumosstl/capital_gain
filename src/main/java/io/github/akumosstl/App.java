package io.github.akumosstl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.github.akumosstl.model.Operation;
import io.github.akumosstl.service.CapitalGainService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
    private static final int PORT = 8080;
    private static final Gson gson = new GsonBuilder().create();
    private static final CapitalGainService service = new CapitalGainService();

    public static void main(String[] args) throws IOException {
        // Start HTTP Server mode if argument provided, otherwise CLI mode
        if (args.length > 0 && args[0].equals("server")) {
            startServer();
        } else {
            runCliMode();
        }
    }

    private static void runCliMode() {
        var scanner = new Scanner(System.in);
        // Use a custom executor to ensure threads are not daemon and we can control shutdown
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;

            CompletableFuture.supplyAsync(() -> {
                var listType = new TypeToken<List<Operation>>() {}.getType();
                List<Operation> operations = gson.fromJson(line, listType);
                return service.calculateTaxes(operations);
            }, executor).thenAccept(results -> {
                IO.println(gson.toJson(results));
            }).exceptionally(ex -> {
                IO.println("Error processing line: " + ex.getMessage());
                return null;
            });
        }

        // Shutdown executor and wait for pending tasks to complete
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void startServer() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/capital-gain", new CapitalGainHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        IO.println("Server started on port " + PORT);
    }

    static class CapitalGainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                var reader = new InputStreamReader(exchange.getRequestBody());
                var listType = new TypeToken<List<Operation>>() {}.getType();
                
                try {
                    //believing/trusting that JSON is error-free
                    List<Operation> operations = gson.fromJson(reader, listType);
                    
                    CompletableFuture.supplyAsync(() -> service.calculateTaxes(operations))
                        .thenAccept(results -> {
                            try {
                                var response = gson.toJson(results);
                                exchange.sendResponseHeaders(200, response.length());
                                var os = exchange.getResponseBody();
                                os.write(response.getBytes());
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                } catch (Exception e) {
                    var response = "Error: " + e.getMessage();
                    exchange.sendResponseHeaders(500, response.length());
                    var os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }
}