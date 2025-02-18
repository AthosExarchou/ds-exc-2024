package gr.hua.dit.ds.ds_exc_2024.controllers;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.*;
import gr.hua.dit.ds.ds_exc_2024.service.ApartmentService;
import gr.hua.dit.ds.ds_exc_2024.service.TenantService;
import gr.hua.dit.ds.ds_exc_2024.service.OwnerService;
import gr.hua.dit.ds.ds_exc_2024.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("apartment")
public class ApartmentController {

    private final UserService userService;
    private final TenantService tenantService;
    private ApartmentService apartmentService;
    private OwnerService ownerService;

    public ApartmentController(UserService userService, ApartmentService apartmentService, OwnerService ownerService, TenantService tenantService) {
        this.userService = userService;
        this.apartmentService = apartmentService;
        this.ownerService = ownerService;
        this.tenantService = tenantService;
    }

    @RequestMapping()
    public String showApartments(Model model) {
        Integer currentUserId = userService.getCurrentUserId();
        model.addAttribute("apartments", apartmentService.getApartments());
        model.addAttribute("currentUserId", currentUserId);
        return "apartment/apartments";
    }

    @GetMapping("/{id}")
    public String showApartment(@PathVariable Integer id, Model model){
        Apartment apartment = apartmentService.getApartment(id);
        model.addAttribute("apartments", apartment);
        return "apartment/apartments";
    }

    @Secured("ROLE_OWNER")
    @GetMapping("/myapartment")
    public String myApartments(Model model) {
        Integer currentUserId = userService.getCurrentUserId();
        Owner owner = ownerService.getOwner(currentUserId);

        /* fetches apartments owned by the current owner */
        List<Apartment> ownerApartments = apartmentService.getApartmentsByOwner(owner);

        model.addAttribute("apartments", ownerApartments);
        model.addAttribute("currentUserId", currentUserId);

        return "apartment/myapartment";
    }

    @Secured("ROLE_USER")
    @GetMapping("/new")
    public String addApartment(Model model){
        Apartment apartment = new Apartment();
        model.addAttribute("apartment", apartment);
        Integer ownerId = ownerService.getOwnerIdForCurrentUser();
        if (ownerId == null) {
            ownerId = userService.getCurrentUserId();
        }
        if (ownerId != null) {
            model.addAttribute("ownerId", ownerId);
        }
        boolean isUserOwner = userService.isUserOwner();
        model.addAttribute("isUserOwner", isUserOwner);

        Integer tenantId = tenantService.getTenantIdForCurrentUser();
        if (tenantId == null) {
            tenantId = userService.getCurrentUserId();
        }
        if (tenantId != null) {
            model.addAttribute("tenantId", tenantId);
        }
        boolean isUserTenant = tenantService.isUserTenant();
        model.addAttribute("isUserTenant", isUserTenant);
        return "apartment/apartment";
    }

    @Secured("ROLE_USER")
    @PostMapping("/new")
    public String saveApartment(@Valid @ModelAttribute("apartment") Apartment apartment,
                                BindingResult theBindingResult,
                                @RequestParam(value = "owner_id", required = false) Integer ownerId,
                                @RequestParam(value = "firstName", required = false) String firstName,
                                @RequestParam(value = "lastName", required = false) String lastName,
                                @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                Model model, HttpSession session) {

        if (theBindingResult.hasErrors()) {
            return "apartment/apartment";
        }
        Owner owner;
        /* if the user is not already an owner, create an owner and assign 'ROLE_OWNER' */
        if (!userService.isUserOwner()) {
            if (firstName == null || lastName == null || phoneNumber == null) {
                model.addAttribute("errorMessage",
                        "First name, last name, and phone number are required for new owner.");
                return "apartment/apartment";
            }
            owner = ownerService.createOwnerForCurrentUser(firstName, lastName, phoneNumber);

            if (owner == null) {
                model.addAttribute("errorMessage",
                        "Your role as 'OWNER' has been revoked by the Administrator." +
                                    " Please contact us for further details.");
                return "apartment/apartment";
            }
            apartmentService.assignRoleToUserForFirstApartment(owner, session);
        } else {
            if (ownerId == null) {
                ownerId = userService.getCurrentUserId();
            }
            owner = ownerService.getOwner(ownerId);
            if (owner == null) {
                model.addAttribute("errorMessage", "Owner not found.");
                return "apartment/apartment";
            }
        }
        apartment.setApproved(false);
        apartment.setTenant(null); //upon apartment creation, there is no tenant
        apartmentService.saveApartment(apartment);
        apartmentService.assignOwnerToApartment(apartment.getId(), owner);
        model.addAttribute("apartments", apartmentService.getApartments());
        model.addAttribute("successMessage",
                "Apartment submitted successfully! Awaiting approval.");
        return "apartment/apartments";
    }

    @GetMapping("/assign/{id}")
    public String showAssignOwnerToApartment(@PathVariable Integer id, Model model) {
        Apartment apartment = apartmentService.getApartment(id);
        List<Owner> owners = ownerService.getOwners();
        model.addAttribute("apartment", apartment);
        model.addAttribute("owners", owners);
        return "apartment/assignowner";
    }

    @GetMapping("/unassign/owner/{id}")
    public String unassignOwnerToApartment(@PathVariable Integer id, Model model) {
        apartmentService.unassignOwnerFromApartment(id);
        model.addAttribute("apartments", apartmentService.getApartments());
        return "apartment/apartments";
    }

    @GetMapping("/unassign/tenant/{id}")
    public String unassignTenantToApartment(@PathVariable Integer id, Model model) {
        apartmentService.unassignTenantFromApartment(id, tenantService.getTenantIdForCurrentUser());
        model.addAttribute("apartments", apartmentService.getApartments());
        return "apartment/apartments";
    }

    @GetMapping("/tenantassign/{id}")
    public String showAssignTenantToApartment(@PathVariable Integer id, Model model) {
        Apartment apartment = apartmentService.getApartment(id);
        List<Tenant> tenants = tenantService.getTenants();
        model.addAttribute("apartment", apartment);
        model.addAttribute("tenants", tenants);
        return "apartment/assigntenant";
    }

    @PostMapping("/assign/{id}")
    public String assignOwnerToApartment(@PathVariable Integer id, @RequestParam(value = "owner_id", required = true) Integer ownerId, Model model) {
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
    public String assignTenantToApartment(@PathVariable Integer id, @RequestParam(value = "tenant", required = true) Integer tenantId, Model model) {
        System.out.println(tenantId);
        Tenant tenant = tenantService.getTenant(tenantId);
        Apartment apartment = apartmentService.getApartment(id);
        System.out.println(apartment);
        String roleUserIs = "tenant";
        apartmentService.assignTenantToApartment(id, tenant, roleUserIs);
        model.addAttribute("apartments", apartmentService.getApartments());
        return "apartment/apartments";
    }

    @Secured("ROLE_OWNER")
    @PostMapping("/delete/{id}")
    public String deleteApartment(@PathVariable Integer id, Model model) {
        Apartment apartment = apartmentService.getApartment(id);
        if (apartment.getTenant() != null) {
            model.addAttribute("errorMessage", "This Apartment is currently being rented, hence cannot be deleted");
            return "apartment/apartments";
        }

        if (apartment == null) {
            model.addAttribute("errorMessage", "Apartment not found!");
            return "apartment/myapartment"; //back to the apartments list page
        }

        Integer currentUserId = userService.getCurrentUserId();
        /* check if the logged-in user is the owner of this apartment */
        Owner apartmentOwner = apartment.getOwner();
        if (apartmentOwner == null || apartmentOwner.getUser() == null || !Objects.equals(apartmentOwner.getUser().getId(), currentUserId)) {
            model.addAttribute("errorMessage", "You are not authorized to delete this apartment!");
            return "apartment/myapartment"; //back with error
        }
        /* proceeds with deletion if the owner matches */
        apartmentService.deleteApartment(id);

        model.addAttribute("apartments", apartmentService.getApartments()); //list of remaining apartments
        model.addAttribute("successMessage", "Apartment deleted successfully!");
        return "apartment/myapartment"; //back to the apartments list page
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/apartmentsforapproval")
    public String showApartmentsForApproval(Model model) {
        List<Apartment> apartments = apartmentService.getApartments();

        /* filters apartments where approved == false */
        List<Apartment> unapprovedApartments = apartments.stream()
                .filter(apartment -> Boolean.FALSE.equals(apartment.getApproved()))
                .collect(Collectors.toList());

        model.addAttribute("apartments", unapprovedApartments);
        return "apartment/apartmentsforapproval";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/approve/{id}")
    public String changeApprovedStatus(@PathVariable Integer id, Model model) {
        Apartment apartment = apartmentService.getApartment(id);

        if (apartment == null) {
            model.addAttribute("errorMessage", "Apartment not found!");
            return "apartment/apartments";
        }
        apartment.setApproved(true);
        apartmentService.saveApartment(apartment);
        model.addAttribute("successMessage", "Apartment approved successfully!");
        return "apartment/apartments";
    }

    @Secured("ROLE_OWNER")
    @GetMapping("/{id}/applications")
    public String viewApplications(@PathVariable("id") Integer apartmentId, Model model) {
        Apartment apartment = apartmentService.getApartment(apartmentId);

        if (apartment == null || !apartment.getOwner().getUser().getId().equals(userService.getCurrentUserId())) {
            model.addAttribute("errorMessage", "You do not have access to this apartment or it does not exist.");
            return "apartment/applications";
        }
        model.addAttribute("apartment", apartment);
        model.addAttribute("applications", apartment.getApplicants());
        return "apartment/applications";
    }

    /* every role is allowed to, at the very least, filter apartments */
    @GetMapping("/filter")
    public String filterApartments(@RequestParam(required = false) Integer minPrice,
                                @RequestParam(required = false) Integer maxPrice, Model model) {
        int min = (minPrice != null) ? minPrice : 0;
        int max = (maxPrice != null) ? maxPrice : 20000;
        List<Apartment> filteredApartments = apartmentService.filterApartments(min, max);
        model.addAttribute("apartments", filteredApartments);
        return "apartment/apartments";
    }

}
