package gr.hua.dit.ds.ds_exc_2024.controllers;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Apartment;
import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import gr.hua.dit.ds.ds_exc_2024.entities.User;
import gr.hua.dit.ds.ds_exc_2024.services.ApartmentService;
import gr.hua.dit.ds.ds_exc_2024.services.EmailService;
import gr.hua.dit.ds.ds_exc_2024.services.TenantService;
import gr.hua.dit.ds.ds_exc_2024.services.UserService;
import gr.hua.dit.ds.ds_exc_2024.repositories.RoleRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("tenant")
public class TenantController {

    private TenantService tenantService;
    private UserService userService;
    private RoleRepository roleRepository;
    private ApartmentService apartmentService;
    private EmailService emailService;

    public TenantController(TenantService tenantService, UserService userService, RoleRepository roleRepository, ApartmentService apartmentService, EmailService emailService) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.apartmentService = apartmentService;
        this.emailService = emailService;
    }

    @Secured("ROLE_USER")
    @GetMapping("/rent/{id}")
    public String showTenantForm(@PathVariable("id") Integer apartmentId, Model model) {
        model.addAttribute("apartmentId", apartmentId);
        Tenant tenant;
        if (tenantService.isUserTenant()) {
            tenant = tenantService.getTenant(userService.getCurrentUserId());
            if (tenant == null) {
                model.addAttribute("errorMessage", "Tenant not found.");
                return "apartment/apartments";
            }
            if (tenant.getApartment() != null) {
                model.addAttribute("errorMessage",
                        "You are already renting an apartment. You can't rent or apply for another apartment!");
                return "apartment/apartments";
            }
            if (tenantService.submitApplication(apartmentId, tenant)) {
                model.addAttribute("errorMessage",
                        "You have already applied for this apartment!");
                return "apartment/apartments";
            }
            model.addAttribute("successMessage",
                    "Application for rental submitted successfully!");
            return "apartment/apartments";
        }
        tenant = new Tenant();
        model.addAttribute("tenant", tenant);
        return "tenant/tenantform";
    }

    @PostMapping("/new")
    public String saveTenant(@Valid @RequestParam("userId") Integer userId,
                            @RequestParam(value = "firstName", required = false) String firstName,
                            @RequestParam(value = "lastName", required = false) String lastName,
                            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                            Model model) {

        if (firstName == null || lastName == null || phoneNumber == null) {
            model.addAttribute("errorMessage",
                    "First name, last name, and phone number are required for new tenant.");
            return "tenant/tenantformforadmin";
        }
        User user = (User) userService.getUser(userId);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "auth/users";
        }
        Tenant tenant = tenantService.getTenantByAdmin(userId, firstName, lastName, phoneNumber);
        if (tenant == null) {
            model.addAttribute("errorMessage",
                    "Your role as 'TENANT' has been revoked by the Administrator." +
                            " Please contact us for further details.");
            return "apartment/apartment";
        }
        tenantService.saveTenant(tenant);
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "auth/users";
    }

    @Secured("ROLE_USER")
    @PostMapping("/rent/{id}")
    public String rentApartment(@PathVariable("id") Integer apartmentId,
                                @Valid @ModelAttribute("tenant") Tenant tenant,
                                BindingResult theBindingResult,
                                @RequestParam(value = "firstName", required = false) String firstName,
                                @RequestParam(value = "lastName", required = false) String lastName,
                                @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                Model model, HttpSession session) {

        if (theBindingResult.hasErrors()) {
            System.out.println(theBindingResult.getAllErrors());
            return "tenant/tenantform";
        }
        Apartment apartment = apartmentService.getApartment(apartmentId);
        if (apartment == null) {
            model.addAttribute("errorMessage", "Apartment not found.");
            return "apartment/apartments";
        }
        if (apartment.isRented()) {
            model.addAttribute("errorMessage", "This apartment is currently being rented.");
            return "apartment/apartments";
        }
        Tenant currentTenant;
        /* if the user is not already a tenant, create a new tenant */
        if (!tenantService.isUserTenant()) {
            if (firstName == null || lastName == null || phoneNumber == null) {
                model.addAttribute("errorMessage",
                        "First name, last name, and phoneNumber are required to rent an apartment for the first time.");
                return "tenant/tenantform";
            }
            currentTenant = tenantService.createTenantForCurrentUser(firstName, lastName, phoneNumber);
            if (currentTenant == null) {
                model.addAttribute("errorMessage",
                        "Tenant creation failed. Please contact us for further assistance.");
                return "apartment/apartments";
            }
            tenantService.assignRoleToUserForFirstApartment(tenant, session); //assigns 'ROLE_TENANT' if renting for the first time
        } else {
            /* if user is already a tenant, fetch the tenant associated with the current user */
            currentTenant = tenantService.getTenant(userService.getCurrentUserId());

            if (currentTenant == null) {
                model.addAttribute("errorMessage", "Tenant not found.");
                return "apartment/apartments";
            }
        }

        if (tenantService.submitApplication(apartmentId, currentTenant)) {
            model.addAttribute("errorMessage",
                    "You have already applied for this apartment!");
            return "apartment/apartments";
        }
            model.addAttribute("successMessage",
                    "Application for rental submitted successfully!");
        return "apartment/apartments";
    }

    @PostMapping("/{apartmentId}/approveApplication/{tenantId}")
    public String approveApplication(@PathVariable Integer tenantId, @PathVariable Integer apartmentId, Model model) {
        Apartment apartment = apartmentService.getApartment(apartmentId);
        if (apartment == null) {
            model.addAttribute("errorMessage", "Apartment not found.");
            return "apartment/myapartment";
        }
        Tenant tenant = tenantService.getTenant(tenantId);
        if (tenant == null) {
            model.addAttribute("errorMessage", "Tenant not found.");
            return "apartment/myapartment";
        }
        if (tenant.getApartment() != null) {
            model.addAttribute("errorMessage", "This tenant is already renting an apartment.");
            return "apartment/myapartment";
        }
        if (apartment.isRented()) {
            model.addAttribute("errorMessage", "This apartment is being rented.");
            return "apartment/myapartment";
        }
        String roleUserIs = "owner";
        apartmentService.assignTenantToApartment(apartmentId, tenant, roleUserIs);
        tenantService.approveApplication(tenantId, apartmentId);

        /* sends email notification to the specified applicant of said apartment */
        try {
            emailService.sendEmailNotification(
                    tenant.getUser().getEmail(),
                    tenant.getFirstName() + " " + tenant.getLastName(),
                    apartment,
                    "tenantApproval"
            );
        } catch (Exception e) {
            model.addAttribute("emailError", "The application was approved, but the confirmation email could not be sent.");
        }

        model.addAttribute("successMessage", "Application approved, apartment is being rented.");
        return "apartment/myapartment";
    }

}
