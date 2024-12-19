package gr.hua.dit.ds.ds_exc_2024.controllers;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Apartment;
import gr.hua.dit.ds.ds_exc_2024.entities.Owner;
import gr.hua.dit.ds.ds_exc_2024.service.OwnerService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("owner")
public class OwnerController {


    private OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    //    @PostConstruct
//    public void setup() {
//       Owner t1 = new Owner("Prof","Mark","Tailor","mark@company.com");
//       Owner t2 = new Owner("Lec","John","Marmor","john@example.com");
//
//       ownerService.saveOwner(t1);
//       ownerService.saveOwner(t2);
//    }

    @RequestMapping()
    public String showOwners(Model model) {
        model.addAttribute("owners", ownerService.getOwners());
        return "owner/owners";
    }

    @GetMapping("/{id}")
    public String showOwner(@PathVariable Integer id, Model model){
        Owner owner = ownerService.getOwner(id);
        model.addAttribute("owners", owner);
        return "owner/owners";
    }

    @GetMapping("/new")
    public String addOwner(Model model){
        Owner owner = new Owner();
        model.addAttribute("owner", owner);
        return "owner/owner";

    }

    @PostMapping("/new")
    public String saveOwner(@ModelAttribute("apartment") Owner owner, Model model) {
        ownerService.saveOwner(owner);
        model.addAttribute("owners", ownerService.getOwners());
        return "owner/owners";
    }

    @GetMapping("/{id}/apartments")
    public String showApartments(@PathVariable("id") Integer id, Model model){
        Owner owner = ownerService.getOwner(id);
        model.addAttribute("apartments", owner.getApartments());
        return "apartment/apartments";
    }

}
