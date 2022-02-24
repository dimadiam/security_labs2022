package lab56;


import com.sun.net.httpserver.*;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// html and css from here
// https://www.c-sharpcorner.com/article/creating-a-simple-login-page-using-html-and-css/
public class LoginWebServer {

    private static Storage dbObject;

    public static void main(String[] args) throws Exception {
        System.out.println("Create DB");
        dbObject = new Storage();

        int port = 8080;
        System.out.println("Create Http server");
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8080), 0);
        addTLS(server);
        // main page
        System.out.println("Add / handler");
        HttpContext context = server.createContext("/");
        context.setHandler(LoginWebServer::handleRootRequest);
        // login
        context = server.createContext("/login");
        context.setHandler(LoginWebServer::handleLoginRequest);
        // registration
        System.out.println("Add /register handler");
        context = server.createContext("/register");
        context.setHandler(LoginWebServer::handleRegistrationRequest);
        // css
        System.out.println("Add /html/ handler");
        context = server.createContext("/html/");
        context.setHandler(LoginWebServer::handleStyleCSSRequest);
        // update personal info
        System.out.println("Add /updatepersonalinfo handler");
        context = server.createContext("/updatepersonalinfo");
        context.setHandler(LoginWebServer::handleUpdatePersonalInfoRequest);

        System.out.println("Run Http server on " + port + " port");
        server.start();
    }

    private static void addTLS(HttpsServer server) throws Exception {
        // https://stackoverflow.com/questions/36819113/how-to-require-client-certificate-with-com-sun-net-httpserver-httpsserver
        // https://gist.github.com/idurucz/992d95296e39f02646456dc9fc908db8
        SSLContext sslContext = SSLContext.getInstance("TLS");
        char[] password = "p@ssw0rd".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");

        // the file was generated via following command
        // keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass p@ssw0rd -validity 360 \
        //      -keysize 2048 -dname "CN=lab7, OU=security-labs, O=KPI, L=Kyiv, ST=Unknown, C=UA"
        FileInputStream fis = new FileInputStream("src/lab56/tls/keystore.jks");
        ks.load(fis, password);

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        // setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                try {
                    // initialise the SSL context
                    SSLContext c = getSSLContext();
                    SSLEngine engine = c.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Set the SSL parameters
                    SSLParameters sslParameters = c.getSupportedSSLParameters();
                    params.setSSLParameters(sslParameters);

                } catch (Exception ex) {
                    System.out.println("Failed to create HTTPS port");
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    private static void handleRootRequest(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        System.out.println("Handle / request. Path is " + requestPath);

        String response;
        int responseCode = 200;

        if (requestPath.isEmpty() || requestPath.equals("/")) {
            Path path = Paths.get("src/lab56/html/login.html");
            response = Files.readString(path, StandardCharsets.UTF_8);
        } else if (requestPath.equals("/registration.html")) {
            Path path = Paths.get("src/lab56/html/registration.html");
            response = Files.readString(path, StandardCharsets.UTF_8);
        } else {
            response = "Path " + requestPath + " not found!";
            responseCode = 401;
        }

        exchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleLoginRequest(HttpExchange exchange) throws IOException {
        System.out.println("Handle login request");
        String error = "";
        String response;
        int statusCode = 200;

        String data = getPostData(exchange);
        System.out.println("POST data: "+ data);
        String[] creds = parsePostDataLogin(data);
        String username = creds[0];
        String password = creds[1];
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            error = "Username and password cannot be empty";
            statusCode = 400;
        } else if (!validateUsername(username)) {
            error = "Username should contain only letters and numbers";
            statusCode = 400;
        } else if (!dbObject.verifyUserPassword(username, password)) {
            error = "Username or Password is not correct";
            statusCode = 400;
        }


        if (statusCode == 200) {
            Path path = Paths.get("src/lab56/html/login-ok.html");
            response = Files.readString(path, StandardCharsets.UTF_8);
            UserPrivateData info = dbObject.getUserData(username);
            response = response.replace("{username}", username);
            response = response.replace("{phonenumber}", info.phone);
            response = response.replace("{address}", info.address);
        } else {
            Path path = Paths.get("src/lab56/html/login-failed.html");
            response = Files.readString(path, StandardCharsets.UTF_8);
            response = String.format(response, error);
        }

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleRegistrationRequest(HttpExchange exchange) throws IOException {
        System.out.println("Handle registration request");

        String error = "";
        String response;
        int statusCode = 200;

        String data = getPostData(exchange);
        System.out.println("POST data: "+ data);
        String[] creds = parsePostDataLogin(data);
        String username = creds[0];
        String password = creds[1];
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            error = "Username and password cannot be empty";
            statusCode = 400;
        } else if (!isValidPassword(password)) {
            error = "Bad password! Password does not meet requirements: " +
                    "<br>1. Password must contain at least one digit [0-9]." +
                    "<br>2. Password must contain at least one lowercase Latin character [a-z]." +
                    "<br>3. Password must contain at least one uppercase Latin character [A-Z]." +
                    "<br>4. Password must contain at least one special character like ! @ # & ( )." +
                    "<br>5. Password must contain a length of at least 8 characters." +
                    "<br>";
            statusCode = 400;
        } else if (!validateUsername(username)) {
            error = "Username should contain only letters and numbers";
            statusCode = 400;
        } else if (dbObject.doesUserExist(username)) {
            error = "The user is already exist";
            statusCode = 400;
        } else {
            System.out.println("Add user to db");
            dbObject.storeNewUser(username, password);
        }


        if (statusCode == 200) {
            Path path = Paths.get("src/lab56/html/registration-ok.html");
            response = Files.readString(path, StandardCharsets.UTF_8);
        } else {
            Path path = Paths.get("src/lab56/html/registration-failed.html");
            response = Files.readString(path, StandardCharsets.UTF_8);
            response = String.format(response, error);
        }
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleStyleCSSRequest(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        System.out.println("Handle /html/ request. Path is " + requestPath);
        String response;
        int responseCode = 200;

        if (requestPath.equals("/html/style.css")) {
            Path path = Paths.get("src/lab56/html/style.css");
            response = Files.readString(path, StandardCharsets.UTF_8);
        } else {
            response = "File " + requestPath + " not found!";
            responseCode = 401;
        }
        exchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void handleUpdatePersonalInfoRequest(HttpExchange exchange) throws IOException {
        System.out.println("Handle update personal info request");

        String data = getPostData(exchange);
        System.out.println("POST data: "+ data);
        String[] info = parsePostDataUpdatePersonalInfo(data);
        String username = info[0];
        String phone = info[1];
        String address = info[2];
        System.out.println("Username: " + username);
        System.out.println("Phone Number: " + phone);
        System.out.println("Address: " + address);

        dbObject.storeUserData(username, phone, address);
        System.out.println("Stored personal info in the DB");

        Path path = Paths.get("src/lab56/html/updated-personal-info.html");
        String response = Files.readString(path, StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void printRequestInfo(HttpExchange exchange) throws IOException {
        // https://www.javatips.net/api/com.sun.net.httpserver.httpexchange
        System.out.println("-- headers --");
        Headers requestHeaders = exchange.getRequestHeaders();
        requestHeaders.entrySet().forEach(System.out::println);

        System.out.println("-- principle --");
        HttpPrincipal principal = exchange.getPrincipal();
        System.out.println(principal);

        System.out.println("-- HTTP method --");
        String requestMethod = exchange.getRequestMethod();
        System.out.println(requestMethod);

        System.out.println("-- query --");
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        System.out.println(query);
        System.out.println(requestURI.getPath());

        StringBuilder sb = new StringBuilder();
        InputStream ios = exchange.getRequestBody();
        int i;
        while ((i = ios.read()) != -1) {
            sb.append((char) i);
        }
        System.out.println("hm: " + sb);
    }

    private static String getPostData(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream ios = exchange.getRequestBody();
        int i;
        while ((i = ios.read()) != -1) {
            sb.append((char) i);
        }
        return sb.toString();
    }
    private static String[] parsePostDataLogin(String data) {
        Pattern pattern = Pattern.compile("^Username=(.+)&Password=(.+)$");
        Matcher matcher = pattern.matcher(data);
        String[] up = new String[2];
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            up[0] = matcher.group(1);
            up[1] = matcher.group(2);

            up[0] = URLDecoder.decode(up[0], StandardCharsets.UTF_8);
            up[1] = URLDecoder.decode(up[1], StandardCharsets.UTF_8);
        } else {
            System.out.println("parsePostDataLogin - not found");
            up[0] = null;
            up[1] = null;
        }
        return up;
    }

    private static boolean validateUsername(String username) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(username);
        if (matcher.find()) {
            System.out.println("User " + username + " passed");
            return true;
        } else {
            System.out.println("User " + username + " failed");
            return false;
        }
    }

    // digit + lowercase char + uppercase char + punctuation + symbol
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    private static final Pattern patternPassword = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValidPassword(String password) {
        Matcher matcher = patternPassword.matcher(password);
        return matcher.matches();
    }

    private static String[] parsePostDataUpdatePersonalInfo(String data) {
        Pattern pattern = Pattern.compile("^Username=(.+)&Phone=(.+)&Address=(.+)$");
        Matcher matcher = pattern.matcher(data);
        String[] upa = new String[3];
        if (matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            upa[0] = matcher.group(1);
            upa[1] = matcher.group(2);
            upa[2] = matcher.group(3);

            upa[0] = URLDecoder.decode(upa[0], StandardCharsets.UTF_8);
            upa[1] = URLDecoder.decode(upa[1], StandardCharsets.UTF_8);
            upa[2] = URLDecoder.decode(upa[2], StandardCharsets.UTF_8);
        } else {
            System.out.println("parsePostDataUpdatePersonalInfo - not found");
            upa[0] = null;
            upa[1] = null;
            upa[2] = null;
        }
        return upa;
    }
}
