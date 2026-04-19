package framework.visa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam("fullName") String fullName, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("fullName", fullName);
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
