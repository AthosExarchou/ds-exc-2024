package gr.hua.dit.ds.ds_exc_2024.services;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.*;
import gr.hua.dit.ds.ds_exc_2024.repositories.TenantRepository;
import gr.hua.dit.ds.ds_exc_2024.repositories.ApartmentRepository;
import gr.hua.dit.ds.ds_exc_2024.repositories.UserRepository;
import gr.hua.dit.ds.ds_exc_2024.repositories.RoleRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TenantService {

    private TenantRepository tenantRepository;
    private ApartmentRepository apartmentRepository;
    private UserService userService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public TenantService(TenantRepository tenantRepository, ApartmentRepository apartmentRepository, UserService userService, UserRepository userRepository, RoleRepository roleRepository) {
        this.tenantRepository = tenantRepository;
        this.apartmentRepository = apartmentRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public List<Tenant> getTenants() {
        return tenantRepository.findAll();
    }

    @Transactional
    public void saveTenant(Tenant tenant) {
        tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant getTenant(Integer tenantId) {
        Optional<Tenant> optionalTenant = tenantRepository.findById(tenantId);

        if (optionalTenant.isPresent()) {
            return optionalTenant.get();
        } else {
            Integer currentUserId = userService.getCurrentUserId(); //gets the current user's id
            return tenantRepository.findByUserId(currentUserId)
                    .orElseThrow(() -> new NoSuchElementException("Tenant not found with current user id: " + currentUserId));
        }
    }

    @Transactional
    public Tenant createTenantForCurrentUser(String firstName, String lastName, String phoneNumber) {
        Integer userId = userService.getCurrentUserId();
        User user = (User) userService.getUser(userId); //fetches current user by ID
        /* tenant creation */
        Optional<Tenant> existingTenant = tenantRepository.findByUserId(userId);
        if (existingTenant.isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setFirstName(firstName);
            tenant.setLastName(lastName);
            tenant.setEmail(user.getEmail());
            tenant.setUsername(user.getUsername());
            tenant.setPhoneNumber(phoneNumber);
            tenant.setUser(user); //associates tenant with the current user
            return tenantRepository.save(tenant);
        } else {
            System.out.println("User with id: " + userId + " is a tenant");
            return null;
        }
    }

    @Transactional
    public void assignRoleToUserForFirstApartment(Tenant tenant, HttpSession session) {
        if (isFirstApartment(tenant)) {
            User user = (User) userService.getUser(userService.getCurrentUserId());
            Optional<Role> optionalRole = roleRepository.findByName("ROLE_TENANT");

            if (optionalRole.isPresent()) {
                Role tenantRole = optionalRole.get();
                System.out.println("User roles before adding: " + user.getRoles());
                user.getRoles().add(tenantRole);
                userService.updateUser(user);
                session.invalidate(); //invalidates session to refresh roles
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role 'ROLE_TENANT' does not exist in the database");
            }
        }
    }

    @Transactional
    public Integer getTenantIdForCurrentUser() {
        Integer userId = userService.getCurrentUserId();
        System.out.println("Current User id: " + userId);
        Optional<Tenant> tenantOptional = tenantRepository.findByUserId(userId);
        if (tenantOptional.isPresent()) {
            System.out.println("Tenant id: " + tenantOptional.get().getId());
            return tenantOptional.get().getId();
        }
        System.out.println("No tenant found for user id: " + userId);
        return null;
    }

    @Transactional
    public boolean isFirstApartment(Tenant tenant) {
        /* checks if the tenant already has an apartment */
        return tenant.getApartment() == null; // If no apartment is linked, it's the first one
    }

    @Transactional
    public boolean isUserTenant() {
        Integer currentUserId = userService.getCurrentUserId();
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User id must not be null.");
        }
        User currentUser = (User) userService.getUser(currentUserId);
        for (Role role : currentUser.getRoles()) {
            if ("ROLE_TENANT".equals(role.getName())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean submitApplication(Integer apartmentId, Tenant tenant) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apartment not found"));
        if (apartment.getApplicants().contains(tenant) || tenant.getAppliedApartments().contains(apartment)) {
            return true;
        }
        tenant.setRentalStatus(Tenant.RentalStatus.APPLIED);
        apartment.addApplicant(tenant);
        tenant.applyToApartment(apartment);
        tenantRepository.save(tenant);
        apartmentRepository.save(apartment);
        return false;
    }

    @Transactional
    public void approveApplication(Integer tenantId, Integer apartmentId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found with ID: " + tenantId));
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apartment not found with ID: " + apartmentId));

        /* checks if the apartment already has an approved tenant */
        if (apartment.getTenant() != null && apartment.getTenant().getRentalStatus() == Tenant.RentalStatus.RENTING) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "This apartment is already being rented.");
        }
        if (tenant.getApartment() != null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "This tenant is already renting an apartment.");
        }
        tenant.setRentalStatus(Tenant.RentalStatus.RENTING);
        apartment.setTenant(tenant);
        tenantRepository.save(tenant);
        apartmentRepository.save(apartment);
    }

    @Transactional
    public Tenant getTenantByAdmin(@Valid Integer tenantId, String firstName, String lastName, String phoneNumber) {
        User user = (User) userService.getUser(tenantId); //fetches current user by ID
        /* tenant creation */
        Optional<Tenant> existingTenant = tenantRepository.findByUserId(tenantId);
        if (existingTenant.isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setFirstName(firstName);
            tenant.setLastName(lastName);
            tenant.setEmail(user.getEmail());
            tenant.setUsername(user.getUsername());
            tenant.setPhoneNumber(phoneNumber);
            tenant.setUser(user); //associates tenant with the current user
            tenant.setRentalStatus(Tenant.RentalStatus.APPLIED);
            user = tenant.getUser();
            user.setTenant(tenant);
            Optional<Role> optionalRole = roleRepository.findByName("ROLE_TENANT");
            if (optionalRole.isPresent()) {
                Role tenantRole = optionalRole.get();
                System.out.println("User roles before adding: " + user.getRoles());
                if (!user.getRoles().contains(tenantRole)) {
                    user.getRoles().add(tenantRole);
                }
                userService.updateUser(user);
                System.out.println("User roles after adding: " + user.getRoles());
            }
            return tenant;
        }
        return null;
    }

}