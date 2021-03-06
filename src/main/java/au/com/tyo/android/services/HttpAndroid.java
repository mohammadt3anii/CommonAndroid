package au.com.tyo.android.services;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GZipEncoding;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpResponseInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ObjectParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

import au.com.tyo.android.utils.SimpleDateUtils;
import au.com.tyo.services.HttpConnection;

;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 16/5/17.
 *
 * most code is from Google JAVA API Client implementation
 *
 * TODO
 *
 * please be noted this class is partly implemented
 *
 */

public class HttpAndroid extends HttpConnection<HttpAndroid> {

    private static final String TAG = "HttpAndroid";

    private static JsonFactory JSON_FACTORY = new JacksonFactory();

    /** HTTP method. */
    private String requestMethod;

    /** URI template for the path relative to the base URL. */
    private String uriTemplate;

    /** HTTP content or {@code null} for none. */
    private HttpContent httpContent;

    /** HTTP headers used for the Google client request. */
    private HttpHeaders requestHeaders = new HttpHeaders();

    /** HTTP headers of the last response or {@code null} before request has been executed. */
    private HttpHeaders lastResponseHeaders;

    /** Status code of the last response or {@code -1} before request has been executed. */
    private int lastStatusCode = -1;

    /** Status message of the last response or {@code null} before request has been executed. */
    private String lastStatusMessage;

    /** Whether to disable GZip compression of HTTP content. */
    // private boolean disableGZipContent;

    private HttpRequestFactory httpRequestFactory;

    private ObjectParser responseParser;

    public static class DisableTimeout implements HttpRequestInitializer {
        public void initialize(com.google.api.client.http.HttpRequest request) {
            request.setConnectTimeout(0);
            request.setReadTimeout(0);
        }
    }

    public static HttpRequestFactory createRequestFactory(HttpTransport transport) {
        final DisableTimeout disableTimeout = new DisableTimeout();
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(com.google.api.client.http.HttpRequest request) {
                disableTimeout.initialize(request);
            }
        });
    }

    final DisableTimeout disableTimeout = new DisableTimeout();

    public HttpAndroid() {
        httpRequestFactory = createRequestFactory(AndroidHttp.newCompatibleTransport());
        responseParser = new JsonObjectParser(JSON_FACTORY);
    }

    @Override
    public InputStream upload(String url, HttpRequest settings) throws Exception {
        return null;
    }

    @Override
    public InputStream post(HttpRequest settings, int postMethod) throws Exception {
        return null;
    }

    @Override
    public String get(String url, long storedModifiedDate, boolean keepAlive) throws Exception {
        return connect(url);
    }

    public InputStream getAsInputStream(String url) throws Exception {
        return connectForInputStream(url);
    }

    @Override
    public void setHeaders(Object[] objects) {

    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String createCookieFile() {
        return null;
    }

    protected IOException newExceptionOnError(HttpResponse response) {
        return new HttpResponseException(response);
    }

    /**
     *
     * @param url
     * @return
     */
    private com.google.api.client.http.HttpRequest buildHttpRequest(String url) throws IOException {
        return buildHttpRequest(HttpMethods.GET, url, null);
    }

    /**
     *
     * @param requestMethod
     * @param url
     * @return
     */
    private com.google.api.client.http.HttpRequest buildHttpRequest(String requestMethod, String url) throws IOException {
        return buildHttpRequest(requestMethod, url, null);
    }

    /**
     *
     * @param requestMethod
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    private com.google.api.client.http.HttpRequest buildHttpRequest(String requestMethod, String url, HttpContent content) throws IOException {
        return buildHttpRequest(requestMethod, url, content, false);
    }

    /**
     *
     * @param requestMethod
     * @param url
     * @param content
     * @param disableGZipContent
     * @return
     * @throws IOException
     */
    private com.google.api.client.http.HttpRequest buildHttpRequest(String requestMethod, String url, HttpContent content, boolean disableGZipContent) throws IOException {
        if (null == requestMethod)
            requestMethod = HttpMethods.GET;
        final com.google.api.client.http.HttpRequest httpRequest = httpRequestFactory.buildRequest(requestMethod, new GenericUrl(url), content);
        new MethodOverride().intercept(httpRequest);

        // custom methods may use POST with no content but require a Content-Length header
        if (content == null && (requestMethod.equals(HttpMethods.POST)
                || requestMethod.equals(HttpMethods.PUT) || requestMethod.equals(HttpMethods.PATCH))) {
            httpRequest.setContent(new EmptyContent());
        }
        httpRequest.getHeaders().putAll(requestHeaders);
        if (!disableGZipContent) {
            httpRequest.setEncoding(new GZipEncoding());
        }
        final HttpResponseInterceptor responseInterceptor = httpRequest.getResponseInterceptor();
        httpRequest.setResponseInterceptor(new HttpResponseInterceptor() {

            public void interceptResponse(HttpResponse response) throws IOException {
                if (responseInterceptor != null) {
                    responseInterceptor.interceptResponse(response);
                }
                if (!response.isSuccessStatusCode() && httpRequest.getThrowExceptionOnExecuteError()) {
                    // error = httpRequest.getThrowExceptionOnExecuteError();
                    throw newExceptionOnError(response);
                }
            }
        });
//        httpRequest.getHeaders().setUserAgent(BROWSER_USER_AGENT_MOBILE);
//        httpRequest.setSuppressUserAgentSuffix(true);
        httpRequest.setLoggingEnabled(true);
        return httpRequest;
    }

    /**
     *
     * @param url
     * @return
     * @throws Exception
     */
    private String connect(String url) throws Exception {
        return httpInputStreamToText(connectForInputStream(url));
    }

    @Override
    protected InputStream connectForInputStream(String url) throws Exception {
        setInUsed(true);
        com.google.api.client.http.HttpRequest request = buildHttpRequest(url);
        HttpResponse response = request.execute();
        setInUsed(false);
        response.getHeaders().getLastModified();
        return response.getContent();
    }

    @Override
    public long getLastModifiedDate(String url) throws MalformedURLException, IOException {
        com.google.api.client.http.HttpRequest request = buildHttpRequest(HttpMethods.HEAD, url);
        HttpResponse response = request.execute();
        Date date;
        long lastModifiedDate = 0;
        try {
            if (response.getHeaders() != null && response.getHeaders().getLastModified() != null) {
                date = SimpleDateUtils.parseDate(response.getHeaders().getLastModified());
                lastModifiedDate = date.getTime();
            }
        } catch (Exception e) {
        }
        return lastModifiedDate;
    }

    /**
     * post urlencoded content
     *
     * @param settings
     * @return
     * @throws Exception
     */
    @Override
    public InputStream post(HttpRequest settings) throws Exception {
        UrlEncodedContent content = new UrlEncodedContent(settings.paramsToMap());
        return post(settings.getUrl(), content);
    }

    /**
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    @Override
    public InputStream postJSON(String url, Object json) throws IOException {
        JsonHttpContent content = new JsonHttpContent(JSON_FACTORY, json);

        // not needed
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType("application/json");
//        request.setHeaders(httpHeaders);
        InputStream returnedStream = post(url, content);
        return returnedStream;
    }

    /**
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    private InputStream post(String url, HttpContent content) throws IOException {
        setInUsed(true);
        com.google.api.client.http.HttpRequest request = buildHttpRequest(HttpMethods.POST, url, content, true);
        HttpResponse response = request.execute();
        setInUsed(false);
        return response.getContent();
    }

    @Override
    public String postJSONForResult(String url, String json) throws Exception {
        return httpInputStreamToText(postJSON(url, json));
    }

    /**
     *
     * @param url
     * @param settings
     * @return
     * @throws Exception
     */
    @Override
    public String uploadWithResult(String url, HttpRequest settings) throws Exception {
        return httpInputStreamToText(upload(url, settings));
    }

    @Override
    public void setHeader(String header, String value) {
        requestHeaders.put(header, value);
    }

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

}
