//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.openfaas.entrypoint;

import com.openfaas.model.IHandler;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Request;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

public class App {
    public App() {
    }

    public static void main(String[] args) throws Exception {
        int port = 8082;
        HandlerProvider p = HandlerProvider.getInstance(); // singleton
        IHandler handler = p.getHandler(); 
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        App.InvokeHandler invokeHandler = new App.InvokeHandler(handler);
        server.createContext("/", invokeHandler);
        server.setExecutor((Executor)null);
        server.start();
    }

    static class InvokeHandler implements HttpHandler {
        IHandler handler;

        private InvokeHandler(IHandler handler) {
            this.handler = handler;
        }
       
        public void handle(HttpExchange t) throws IOException {
            String requestBody = "";
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase("POST")) {
                InputStream inputStream = t.getRequestBody();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];

                int length;
                while((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }

                requestBody = result.toString("UTF-8");
            }

            Headers reqHeaders = t.getRequestHeaders();
            Map<String, String> reqHeadersMap = new HashMap();
            Iterator var16 = reqHeaders.entrySet().iterator();

            while(var16.hasNext()) {
                Entry<String, List<String>> header = (Entry)var16.next();
                List<String> headerValues = (List)header.getValue();
                if (headerValues.size() > 0) {
                    reqHeadersMap.put((String)header.getKey(), (String)headerValues.get(0));
                }
            }
            
            // https://stackoverflow.com/questions/48776437/uri-getrawquery-vs-getquery
            

            IRequest req = new Request(requestBody, reqHeadersMap, t.getRequestURI().getRawQuery(), t.getRequestURI().getPath());
            IResponse res = this.handler.Handle(req);
            String response = res.getBody();
            byte[] bytesOut = response.getBytes("UTF-8");
            Headers responseHeaders = t.getResponseHeaders();
            String contentType = res.getContentType();
            if (contentType.length() > 0) {
                responseHeaders.set("Content-Type", contentType);
            }

            Iterator var12 = res.getHeaders().entrySet().iterator();

            while(var12.hasNext()) {
                Entry<String, String> entry = (Entry)var12.next();
                responseHeaders.set((String)entry.getKey(), (String)entry.getValue());
            }

            t.sendResponseHeaders(res.getStatusCode(), (long)bytesOut.length);
            OutputStream os = t.getResponseBody();
            os.write(bytesOut);
            os.close();
            System.out.println("Request / " + Integer.toString(bytesOut.length) + " bytes written.");
        }
    }
}
