package gr.hua.dit.ds.ds_exc_2024.controllers;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Apartment;
import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import gr.hua.dit.ds.ds_exc_2024.entities.Owner;
import gr.hua.dit.ds.ds_exc_2024.repositories.ApartmentRepository;
import gr.hua.dit.ds.ds_exc_2024.service.ApartmentService;
import gr.hua.dit.ds.ds_exc_2024.service.TenantService;
import gr.hua.dit.ds.ds_exc_2024.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("apartment")
public class ApartmentController {


    private final TenantService tenantService;
    private ApartmentService apartmentService;


    private OwnerService ownerService;

    public ApartmentController(ApartmentService apartmentService, OwnerService ownerService, TenantService tenantService) {
        this.apartmentService = apartmentService;
        this.ownerService = ownerService;
        this.tenantService = tenantService;
    }

    @RequestMapping()
    public String showApartments(Model model) {
        model.addAttribute("apartments", apartmentService.getApartments());
        return "apartment/apartments";
    }

    @GetMapping("/{id}")
    public String showApartment(@PathVariable Integer id, Model model){
        Apartment apartment = apartmentService.getApartment(id);
        model.addAttribute("apartments", apartment);
        return "apartment/apartments";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String addApartment(Model model){
        Apartment apartment = new Apartment();
        model.addAttribute("apartment", apartment);
        return "apartment/apartment";

    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/new")
    public String saveTenant(@Valid @ModelAttribute("apartment") Apartment apartment,BindingResult theBindingResult, Model model) {
        if (theBindingResult.hasErrors()) {
            System.out.println("error");
            return "apartment/apartment";
        } else {
            apartmentService.saveApartment(apartment);
            model.addAttribute("apartments", apartmentService.getApartments());
            model.addAttribute("successMessage", "Apartment added successfully!");
            return "apartment/apartments";
        }

    }

    @GetMapping("/assign/{id}")
    public String showAssignOwnerToApartment(@PathVariable int id, Model model) {
        Apartment apartment = apartmentService.getApartment(id);
        List<Owner> owners = ownerService.getOwners();
        model.addAttribute("apartment", apartment);
        model.addAttribute("owners", owners);
        return "apartment/assignowner";
    }

    @GetMapping("/unassign/{id}")
    public String unassignOwnerToApartment(@PathVariable int id, Model model) {
        apartmentService.unassignOwnerFromApartment(id);
        model.addAttribute("apartments", apartmentService.getApartments());
        return "apartment/apartments";
    }

    @GetMapping("/tenantassign/{id}")
    public String showAssignTenantToApartment(@PathVariable int id, Model model) {
        Apartment apartment = apartmentService.getApartment(id);
        List<Tenant> tenants = tenantService.getTenants();
        List<Tenant> existing_tenants = apartment.getTenants();
        tenants.removeAll(existing_tenants);
        model.addAttribute("apartment", apartment);
        model.addAttribute("tenants", tenants);
        return "apartment/assigntenant";
    }


    @PostMapping("/assign/{id}")
    public String assignOwnerToApartment(@PathVariable int id, @RequestParam(value = "owner", required = true) int ownerId, Model model) {
        System.out.println(ownerId);
        Owner owner = ownerService.getOwner(ownerId);
        Apartment apartment = apartmentService.getApartment(id);
        System.out.println(apartment);
        apartmentService.assignOwnerToApartment(id, owner);
        model.addAttribute("apartments", apartmentService.getApartments());
        model.addAttribute("successMessage", "Form submitted successfully!");
        return "apartment/apartments";
    }


    @PostMapping("/tenantassign/{id}")
    public String assignTenantToApartment(@PathVariable int id, @RequestParam(value = "tenant", required = true) int tenantId, Model model) {
        System.out.println(tenantId);
        Tenant tenant = tenantService.getTenant(tenantId);
        Apartment apartment = apartmentService.getApartment(id);
        System.out.println(apartment);
        apartmentService.assignTenantToApartment(id, tenant);
        model.addAttribute("apartments", apartmentService.getApartments());
        return "apartment/apartments";
    }


}