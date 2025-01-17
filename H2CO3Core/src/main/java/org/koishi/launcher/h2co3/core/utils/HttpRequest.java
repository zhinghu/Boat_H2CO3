package org.koishi.launcher.h2co3.core.utils;

import static org.koishi.launcher.h2co3.core.utils.Lang.mapOf;
import static org.koishi.launcher.h2co3.core.utils.NetworkUtils.resolveConnection;

import com.google.gson.JsonParseException;

import org.koishi.launcher.h2co3.core.utils.function.ExceptionalBiConsumer;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;
import org.koishi.launcher.h2co3.core.utils.io.IOUtils;
import org.koishi.launcher.h2co3.core.utils.io.ResponseCodeException;
import org.koishi.launcher.h2co3.core.utils.task.Schedulers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class HttpRequest {
    protected final String url;
    protected final String method;
    protected final Map<String, String> headers = new HashMap<>();
    protected final Set<Integer> toleratedHttpCodes = new HashSet<>();
    protected ExceptionalBiConsumer<URL, Integer, IOException> responseCodeTester;
    protected boolean ignoreHttpCode;

    private HttpRequest(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public static HttpGetRequest GET(String url) {
        return new HttpGetRequest(url);
    }

    @SafeVarargs
    public static HttpGetRequest GET(String url, Pair<String, String>... query) throws UnsupportedEncodingException {
        return GET(NetworkUtils.withQuery(url, mapOf(query)));
    }

    public static HttpPostRequest POST(String url) {
        return new HttpPostRequest(url);
    }

    public HttpRequest accept(String contentType) {
        return header("Accept", contentType);
    }

    public HttpRequest authorization(String token) {
        return header("Authorization", token);
    }

    public HttpRequest authorization(String tokenType, String tokenString) {
        return authorization(tokenType + " " + tokenString);
    }

    public HttpRequest authorization(Authorization authorization) {
        return authorization(authorization.getTokenType(), authorization.getAccessToken());
    }

    public HttpRequest header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpRequest ignoreHttpCode() {
        ignoreHttpCode = true;
        return this;
    }

    public abstract String getString() throws IOException;

    public CompletableFuture<String> getStringAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, Schedulers.io());
    }

    public <T> T getJson(Class<T> typeOfT) throws IOException, JsonParseException {
        return JsonUtils.fromNonNullJson(getString(), typeOfT);
    }

    public <T> T getJson(java.lang.reflect.Type type) throws IOException, JsonParseException {
        return JsonUtils.fromNonNullJson(getString(), type);
    }

    public <T> CompletableFuture<T> getJsonAsync(Class<T> typeOfT) {
        return getStringAsync().thenApplyAsync(jsonString -> JsonUtils.fromNonNullJson(jsonString, typeOfT));
    }

    public <T> CompletableFuture<T> getJsonAsync(java.lang.reflect.Type type) {
        return getStringAsync().thenApplyAsync(jsonString -> JsonUtils.fromNonNullJson(jsonString, type));
    }

    public HttpRequest filter(ExceptionalBiConsumer<URL, Integer, IOException> responseCodeTester) {
        this.responseCodeTester = responseCodeTester;
        return this;
    }

    public HttpRequest ignoreHttpErrorCode(int code) {
        toleratedHttpCodes.add(code);
        return this;
    }

    public HttpURLConnection createConnection() throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod(method);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return con;
    }

    public interface Authorization {
        String getTokenType();

        String getAccessToken();
    }

    public static class HttpGetRequest extends HttpRequest {
        public HttpGetRequest(String url) {
            super(url, "GET");
        }

        public String getString() throws IOException {
            HttpURLConnection con = createConnection();
            con = resolveConnection(con);
            return IOUtils.readFullyAsString(con.getInputStream());
        }
    }

    public static final class HttpPostRequest extends HttpRequest {
        private byte[] bytes;

        public HttpPostRequest(String url) {
            super(url, "POST");
        }

        public HttpPostRequest contentType(String contentType) {
            headers.put("Content-Type", contentType);
            return this;
        }

        public HttpPostRequest json(Object payload) throws JsonParseException, UnsupportedEncodingException {
            return string(payload instanceof String ? (String) payload : JsonUtils.GSON.toJson(payload), "application/json");
        }

        public HttpPostRequest form(Map<String, String> params) throws UnsupportedEncodingException {
            return string(NetworkUtils.withQuery("", params), "application/x-www-form-urlencoded");
        }

        @SafeVarargs
        public final HttpPostRequest form(Pair<String, String>... params) throws UnsupportedEncodingException {
            return form(mapOf(params));
        }

        public HttpPostRequest string(String payload, String contentType) throws UnsupportedEncodingException {
            bytes = payload.getBytes("UTF-8");
            header("Content-Length", String.valueOf(bytes.length));
            contentType(contentType + "; charset=utf-8");
            return this;
        }

        public String getString() throws IOException {
            HttpURLConnection con = createConnection();
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(bytes);
            }

            if (responseCodeTester != null) {
                responseCodeTester.accept(new URL(url), con.getResponseCode());
            } else {
                if (con.getResponseCode() / 100 != 2) {
                    if (!ignoreHttpCode && !toleratedHttpCodes.contains(con.getResponseCode())) {
                        String data = NetworkUtils.readData(con);
                        throw new ResponseCodeException(new URL(url), con.getResponseCode(), data);
                    }
                }
            }

            return NetworkUtils.readData(con);
        }
    }
}