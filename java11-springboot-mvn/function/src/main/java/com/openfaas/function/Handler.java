package com.openfaas.function;

import com.openfaas.App;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class Handler extends com.openfaas.model.AbstractHandler {

    private static Logger logger = LoggerFactory.getLogger(Handler.class);

    public IResponse Handle(IRequest req) {
        Response res = new Response();

        logger.info("req body: " + req.getBody());
        logger.info("req params: " + req.getQuery());
        logger.info("req raw params: " + req.getQueryRaw());
        logger.info("req path " + req.getPath());
        res.setBody("Hello, World!" + req.getBody() + req.getQuery());
        return res;
    }
}
