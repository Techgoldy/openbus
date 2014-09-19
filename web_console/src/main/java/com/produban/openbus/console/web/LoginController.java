package com.produban.openbus.console.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @RequestMapping(value = { "/error" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
    public String loginError(Model model) {
	model.addAttribute("error", Boolean.valueOf(true));
	return "/login";
    }

    @RequestMapping({ "/login" })
    public String login() {
	return "/login";
    }

    @RequestMapping({ "/" })
    public String root(Locale locale) {
	return "/index";
    }

    @RequestMapping({ "/index.html" })
    public String index() {
	return "/index";
    }

    @RequestMapping({ "/index" })
    public String index2() {
	return "/index";
    }
    
    @RequestMapping({ "/logout" })
    public String logout() {
	return "/login";
    }
}
