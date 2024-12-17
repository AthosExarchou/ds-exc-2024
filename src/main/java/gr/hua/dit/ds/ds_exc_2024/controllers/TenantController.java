package gr.hua.dit.ds.ds_exc_2024.controllers;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import gr.hua.dit.ds.ds_exc_2024.repositories.TenantRepository;
import gr.hua.dit.ds.ds_exc_2024.service.TenantService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("tenant")
public class TenantController {


    TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    //    @PostConstruct
//    public void setup() {
//        Tenant Stud1= new Tenant("Nick", "Jones", "nick@hua.gr");
//        Tenant Stud2= new Tenant("Jack", "James", "jack@hua.gr");
//        Tenant Stud3= new Tenant("John", "Stone", "john@hua.gr");
//        TenantService.saveTenant(Stud1);
//        TenantService.saveTenant(Stud2);
//        TenantService.saveTenant(Stud3);
//    }


    @GetMapping("")
    public String showTenants(Model model){
        model.addAttribute("tenants", tenantService.getTenants());
        return "tenant/tenants";
    }

    @GetMapping("/{id}")
    public String showTenant(@PathVariable Integer id, Model model){
        model.addAttribute("tenants", tenantService.getTenant(id));
        return "tenant/tenants";
    }

    @GetMapping("/profile/{id}")
    public String showProfile(@PathVariable Integer id, Model model){
        model.addAttribute("tenant", tenantService.getTenant(id));
        return "tenant/tenant-profile";
    }

    @GetMapping("/new")
    public String addTenant(Model model){
        Tenant tenant = new Tenant();
        model.addAttribute("tenant", tenant);
        return "tenant/tenant";
    }

    @PostMapping("/new")
    public String saveTenant(@ModelAttribute("tenant") Tenant tenant, Model model) {
        tenantService.saveTenant(tenant);
        model.addAttribute("tenants", tenantService.getTenants());
        return "tenant/tenants";
    }
}
