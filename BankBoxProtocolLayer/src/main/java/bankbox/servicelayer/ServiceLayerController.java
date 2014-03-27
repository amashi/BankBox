package bankbox.servicelayer;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ServiceLayerController {

    @RequestMapping("/")
    public String index() {
        return "WoW - Greetings from Spring Boot! my name is :" + Math.random();
    }
    
    
    
    

}