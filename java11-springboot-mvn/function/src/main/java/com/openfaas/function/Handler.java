package com.openfaas.function;

import org.springframework.http.ResponseEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class Handler {

    @RequestMapping(value="/")
    public ResponseEntity<Map<String, String>> handle(
            @RequestParam(required = false) Map<String, String> requestParam,
            RequestEntity<String> requestEntity)  {

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Req URI", requestEntity.getUrl().toString());
        responseBody.put("Req Headers", requestEntity.getHeaders().toString());
        responseBody.put("Req Method", requestEntity.getMethod().toString());
        responseBody.put("Req Params", requestParam.toString());
        responseBody.put("Req Body", requestEntity.getBody());

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
