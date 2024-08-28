package com.eci;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleWebServer {
    private static final int PORT = 8080;
    public static String WEB_ROOT = "target/";
    public static Map<String, Service> services = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        ServerSocket serverSocket = new ServerSocket(PORT);

        staticfiles("classes/webroot");
        get("/helloWorld", (req, resp) -> "Hello world");
        get("/hello", (req, res) -> "hello " + req.getValues("name", ""));
        get("/nameAge", (req, res) -> "hello " + req.getValues("name", "") + " your age is " + req.getValues("age", ""));

        while (true) {
            Socket clientSocket = serverSocket.accept();
            threadPool.submit(new ClientHandler(clientSocket));
        }
    }

    // Método para definir la ubicación de los archivos estáticos
    public static void staticfiles(String folder) {
        WEB_ROOT = "target/" + folder;
    }
    
    public static void get(String url, Service s) {
        services.put(url, s);
    } 

    // Define the Service interface
    public interface Service {
        String getValues(Request req, String response);
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream())) {

                String requestLine = in.readLine();
                if (requestLine == null) return;
                String[] tokens = requestLine.split(" ");
                String method = tokens[0];
                String fileRequested = tokens[1];

                if (method.equals("GET")) {
                    handleGetRequest(fileRequested, out, dataOut);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleGetRequest(String fileRequested, PrintWriter out, BufferedOutputStream dataOut) throws IOException {
            File file = new File(WEB_ROOT, fileRequested);
            int fileLength = (int) file.length();
            String carrer = fileRequested.split("/")[1];
            String content = getContentType(carrer);
            String[] pathAndQuery = fileRequested.split("\\?");
            String path = pathAndQuery[0];
            String query = pathAndQuery.length > 1 ? pathAndQuery[1] : null;

            if (services.containsKey(path)) {
                // Crea un objeto Request y Response
                Request req = new Request(fileRequested); // Pasa la ruta completa

                // Llama al servicio registrado
                String response = services.get(path).getValues(req, "");

                // Envía la respuesta
                out.println("HTTP/1.1 200 OK");
                out.println("Content-type: text/plain");
                out.println("Content-length: " + response.length());
                out.println();
                out.flush();
                dataOut.write(response.getBytes());
                dataOut.flush();
            } else if (file.exists()) {
                byte[] fileData = readFileData(file, fileLength);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-type: " + content);
                out.println("Content-length: " + fileLength);
                out.println();
                out.flush();
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
            } else {
                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-type: text/html");
                out.println();
                out.flush();
                out.println("<html><body><h1>File Not Found</h1></body></html>");
                out.flush();
            }
        }

        private String getContentType(String fileRequested) {
            if (fileRequested.endsWith(".html")) return "text/html";
            else if (fileRequested.endsWith(".css")) return "text/css";
            else if (fileRequested.endsWith(".js")) return "application/javascript";
            else if (fileRequested.endsWith(".png")) return "image/png";
            else if (fileRequested.endsWith(".jpg")) return "image/jpeg";
            else if (fileRequested.endsWith(".json")) return "application/json";
            return "text/plain";
        }

        private byte[] readFileData(File file, int fileLength) throws IOException {
            FileInputStream fileIn = null;
            byte[] fileData = new byte[fileLength];
            try {
                fileIn = new FileInputStream(file);
                fileIn.read(fileData);
            } finally {
                if (fileIn != null) fileIn.close();
            }
            return fileData;
        }
    }
}
