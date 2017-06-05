package pw.cakemc.plugin.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;

public class Request implements Callable<Response> {
    private String url;
    private HashMap<String, String> args = new HashMap<String, String>();
    private String method = "GET";

    public Request(String url, HashMap<String, String> arguments) {
        this.url = url;
        this.args = arguments;
    }
    public Request(String url) {
        this.url = url;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public Response call() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();


        if (this.method.equalsIgnoreCase("post")) {
            HttpPost httpReq = new HttpPost(this.url);

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<NameValuePair>(args.size());
            for (Map.Entry<String, String> arg : args.entrySet()) {
                params.add(new BasicNameValuePair(arg.getKey(), arg.getValue()));
            }
            System.out.print(new UrlEncodedFormEntity(params, "UTF-8"));
            httpReq.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httpReq);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                return new Response(instream);
            }

            return new Response(null);
        } else {
            HttpGet httpReq = new HttpGet(this.url);

            //Execute and get the response.
            HttpResponse response = httpclient.execute(httpReq);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                return new Response(instream);
            }

            return new Response(null);
        }
    }
}