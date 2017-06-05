package pw.cakemc.plugin.http;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Response {
    private InputStream body;

    public Response(InputStream body) {
        this.body = body;
    }

    public String getBody() throws IOException {
        if (body != null) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(this.body, writer, "UTF-8");
            String theString = writer.toString();
            return theString;
        }
        return null;
    }
}