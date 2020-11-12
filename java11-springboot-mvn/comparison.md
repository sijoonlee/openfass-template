## Notation
- com.openfaas.springendpoint.ResponseController = S.App (under function)
- com.openfass.entrypoint.App = O.App (under external lib)

## Comparison
|                |  O.App    | S.App             |
|---             |  ---      |   ---             |
|Handler Instance| @Autowire | Singleton Pattern |
|Arg             | HttpExchange(com.sun.net.httpserver)|HttpServletRequest(javax.servlet.http)|
|Step1           | Extract Req from HttpExchange | Extract Req from HttpServletRequest |
|Step2           | Instantiate IRequest obj(req) | Instantiate IRequest obj(req) |
|Step3           | Pass req to Handler.handle()  | Pass req to Handler.handle() |
|                | Instantiate IResponse res     | Instantiate IResponse res |
|Step4           | Extract info from res         | Extract info from res |
|Step5           | Send back using HttpExchange  | Send back using ResponseEntity(org.springframework.http)|
  


