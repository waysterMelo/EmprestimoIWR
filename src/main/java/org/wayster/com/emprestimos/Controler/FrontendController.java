package org.wayster.com.emprestimos.Controler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
    

    @RequestMapping(value = { "/", "/{path:^(?!api|static).*$}", "/{path:^(?!api|static).*$}/**" })
    public String redirect() {
        return "forward:/index.html";
    }

}
