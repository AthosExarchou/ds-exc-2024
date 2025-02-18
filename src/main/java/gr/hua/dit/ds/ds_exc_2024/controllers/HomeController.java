package gr.hua.dit.ds.ds_exc_2024.controllers;

/* imports */
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home(Model model) {

        model.addAttribute("title", "Home");
        return "index";
    }

    @GetMapping("contact/contactus")
    public String contactUsPage() {
        return "contact/contactus"; // The Thymeleaf template location
    }
}
