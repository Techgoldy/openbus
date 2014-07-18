package com.produban.openbus.console.web;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/login/**")
@Controller
public class LoginController {

    @RequestMapping(value = "/ok", method = RequestMethod.GET)
    public String login(Model model) {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	String name = auth.getName();
	model.addAttribute("username", name);	
	return "/login";
    }
    
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String loginError(Model model) {
	model.addAttribute("error", true);
	return "/login";
    }      
}
