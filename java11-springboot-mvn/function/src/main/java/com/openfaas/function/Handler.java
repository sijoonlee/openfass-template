package com.openfaas.function;

import com.openfaas.model.Request;

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
    public @ResponseBody Map<String, String>  handle(
            @RequestParam(required = false) Map<String, String> requestParam,
            RequestEntity<String> requestEntity)  {

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Req URI", requestEntity.getUrl().toString());
        responseBody.put("Req Method", requestEntity.getHeaders().toString());
        responseBody.put("Req Params", requestParam.toString());
        responseBody.put("Req Body", requestEntity.getBody());

        Map<String, List<String>> headers = requestEntity.getHeaders();
        Map<String, String> headersChopped= new HashMap<>();
        for(Map.Entry<String, List<String>> entry : headers.entrySet()){
            headersChopped.put(entry.getKey(), entry.getValue().get(0));
        }
        Request req = new Request(requestEntity.getBody(), headersChopped);

        String bodyFromReq = req.getBody();

        return responseBody;
    }
}
