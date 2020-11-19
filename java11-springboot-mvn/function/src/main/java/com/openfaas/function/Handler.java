package com.openfaas.function;

//import com.openfaas.model.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Handler {
    private static Logger logger = LoggerFactory.getLogger(Handler.class);

    @RequestMapping(value="/")
    public @ResponseBody String handle(
            @RequestParam(required = false) Map<String, String> requestParam,
            RequestEntity<String> requestEntity)  {

        String uri = requestEntity.getUrl().toString();
        String method = requestEntity.getMethod().toString();
        Map<String, List<String>> headers = requestEntity.getHeaders();
        Map<String, String> headersChopped= new HashMap<>();
        for(Map.Entry<String, List<String>> entry : headers.entrySet()){
            headersChopped.put(entry.getKey(), entry.getValue().get(0));
        }
        String params = requestParam.entrySet().toString();
        String body = requestEntity.getBody();
        String info =
                "Req URI: " + uri + "\n" +
                        "Req Method: " + method + "\n" +
                        "Req Headers: " + headers + "\n" +
                        "Req Params: " + params + "\n" +
                        "Req Body: " + body + "\n";

        //Request req = new Request(body, headersChopped);

        //String bodyFromReq = req.getBody();

        return info;
    }
}
