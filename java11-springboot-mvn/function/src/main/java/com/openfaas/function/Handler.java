package com.openfaas.function;

import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Handler extends com.openfaas.model.AbstractHandler {

    //private static Logger logger = LoggerFactory.getLogger(Handler.class);

    public IResponse Handle(IRequest req) {
        Response res = new Response();

        res.setContentType(MediaType.TEXT_PLAIN_VALUE);

        String headers = "";

        for(Map.Entry<String,String> entry : req.getHeaders().entrySet()){
            headers += ("\n" + entry.getKey() + " - " + entry.getValue());
        }

        res.setBody("Hello, World!"
                + "\nBody: " + req.getBody()
                + "\nQuery: " + req.getQueryRaw()
                + "\nPath: " + req.getPathRaw()
                + "\nHeaders From Request"
                + headers);

        return res;
    }
}
