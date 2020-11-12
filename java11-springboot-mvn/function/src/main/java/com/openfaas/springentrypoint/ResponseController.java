package com.openfaas.springentrypoint;

import com.google.common.io.CharStreams;
import com.openfaas.App;
import com.openfaas.model.IHandler;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;


@Controller
public class ResponseController {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    IHandler handler;

    @Autowired
    public ResponseController(IHandler handler){
        this.handler = handler;
    }

    @RequestMapping(value="/")
    public ResponseEntity<String> handleGet(HttpServletRequest httpServletRequest){

        String baseURL = httpServletRequest.getRequestURL().toString();
        String queryParams = httpServletRequest.getQueryString();

        String requestBody = "";
        if(httpServletRequest.getMethod().equalsIgnoreCase("POST")){
            try {
                // https://stackoverflow.com/questions/8100634/get-the-post-request-body-from-httpservletrequest
                // IOUtils.toString(request.getReader());
                requestBody = CharStreams.toString(httpServletRequest.getReader());
                logger.info(requestBody);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Error occurs while getting request body");
            }
        }

        Map<String, String> headersMap = new HashMap<>();
        Enumeration<String> keys = httpServletRequest.getHeaderNames();
        if(keys != null){
            while(keys.hasMoreElements()){
                String key = keys.nextElement();
                Enumeration<String> values = httpServletRequest.getHeaders(key);
                if(values!=null) {
                    // A Header Key can have multiple values
                    // e.g. Accept: application/json
                    //      Accept: application/xml
                    // But here, only the first value will be kept if multiple fields have the same key
                    // I am not sure why but that's how openfaas library does
                    // Please refer below (com.openfaas.entrypoint.App)
                    // while(var16.hasNext()) {
                    //      Entry<String, List<String>> header = (Entry)var16.next();
                    //      List<String> headerValues = (List)header.getValue();
                    //      if (headerValues.size() > 0) {
                    //          reqHeadersMap.put((String)header.getKey(), (String)headerValues.get(0));
                    //      }
                    // }
                    String value = values.nextElement();
                    logger.info(key + " : " + value);
                    headersMap.put(key, value);
                }
            }
        }

        IRequest req = new Request(requestBody, headersMap, queryParams, baseURL);

        IResponse res = this.handler.Handle(req);

        String response = res.getBody();
        HttpHeaders responseHeaders = new HttpHeaders();
        String contentType = res.getContentType();
        if (contentType.length() > 0) {
            responseHeaders.set("Content-Type", contentType);
        }

        Iterator headersFromHandler = res.getHeaders().entrySet().iterator();

        while(headersFromHandler.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) headersFromHandler.next();
            logger.info(entry.getKey() + " = " + entry.getValue());
            responseHeaders.set(entry.getKey(), entry.getValue());
        }

        return ResponseEntity
                .status(res.getStatusCode())
                .headers(responseHeaders)
                .body(response);
    }
}

// Extract requestBody, requestHeader, URI from HttpExchange(com.sun.net.httpserver)
// Translate them into IRequest req(com.openfaas.model)
// Use Handler.Handle method to produce IResponse res (com.openfaas.model)
// Attach Header(content-type) to res
// Attach StatusCode to res
// Attach Message length to res
// Send out res
