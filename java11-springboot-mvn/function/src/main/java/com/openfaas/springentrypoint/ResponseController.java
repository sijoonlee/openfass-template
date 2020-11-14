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
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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
    public void handleGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        //possible alternative return type: ResponseEntity<String>

        String servletPath = httpServletRequest.getServletPath();

        //logger.info("URL : " + httpServletRequest.getRequestURL());           //  http://localhost:8082/
        //logger.info("Context Path : " + httpServletRequest.getContextPath()); // nothing shows
        //logger.info("Servlet Path : " + httpServletRequest.getServletPath()); // "/"
        //if(httpServletRequest.getPathInfo() != null) logger.info("GetPathInfo : " + httpServletRequest.getPathInfo()); // null
        String queryParams = httpServletRequest.getQueryString();

        String requestBody = "";
        if(httpServletRequest.getMethod().equalsIgnoreCase("POST")){
            try {
                // https://stackoverflow.com/questions/8100634/get-the-post-request-body-from-httpservletrequest
                // Possible alternative: IOUtils.toString(request.getReader());
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
                    logger.info("REQ HEADER:" + key + " : " + value);
                    headersMap.put(key, value);
                }
            }
        }

        IRequest req = new Request(requestBody, headersMap, queryParams, servletPath);

        IResponse res = this.handler.Handle(req);

        String response = res.getBody();

        // set content type
        String contentType = res.getContentType();
        if (contentType.length() > 0) {
            httpServletResponse.setContentType(contentType);
        }

        // set other headers
        for(Map.Entry<String, String> entry : res.getHeaders().entrySet()) {
            httpServletResponse.setHeader(entry.getKey(), entry.getValue());
        }

        // set status code
        httpServletResponse.setStatus(res.getStatusCode());

        // get body
        byte[] bytesOut = response.getBytes("UTF-8");

        // set content length
        httpServletResponse.setContentLength(bytesOut.length);

        // write out body
        OutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(bytesOut);
        outputStream.flush();
        outputStream.close();

        // -- sending string (not binary) --
        // PrintWriter writer = httpServletResponse.getWriter();
        // writer.write("dfsfd");
        // writer.flush();

        // I've tried this way, but this won't work with Openfaas web ui.. not sure why
        // Although this works with faas-cli or insomnia, postman.
        // outstream.flush() is used to send out the response signal to ensure it's working in most cases
        // return ResponseEntity
        //          .status(res.getStatusCode())
        //          .headers(responseHeaders)
        //          .body(response);
    }
}