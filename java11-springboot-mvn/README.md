## Openfaas Template
- Java 11
- Springframework : 2.3.5.RELEASE
    - spring-boot-starter-web with jetty server
- Maven : 3.6.3
- Stick to using com.openfaas.model
- com.openfaas.endpoint replaced with customized code (ResponseController.java)
    - See the comparison below

## Comparison
This is to compare the endpoint.App from openfaas's offical endpoint library
with the customized code for Springframework

### Notation
- com.openfaas.springendpoint.ResponseController = Spring.App (under function)
- com.openfass.entrypoint.App = Openfaas.App (under external lib)

### Comparison Table
|                |  Openfaas.App    | Spring.App             |
|---             |  ---      |   ---             |
|Handler Instance| @Autowire | Singleton Pattern |
|Arg             | HttpExchange(com.sun.net.httpserver)|HttpServletRequest(javax.servlet.http)|
|Step1           | Extract Req from HttpExchange | Extract Req from HttpServletRequest |
|Step2           | Instantiate IRequest obj(req) | Instantiate IRequest obj(req) |
|Step3           | Pass req to Handler.handle()  | Pass req to Handler.handle() |
|                | Instantiate IResponse res     | Instantiate IResponse res |
|Step4           | Extract info from res         | Extract info from res |
|Step5           | Send back using HttpExchange  | Send back using ResponseEntity(org.springframework.http)|
  
  




