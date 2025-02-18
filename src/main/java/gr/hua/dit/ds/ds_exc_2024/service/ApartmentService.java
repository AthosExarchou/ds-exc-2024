package gr.hua.dit.ds.ds_exc_2024.service;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.*;
import gr.hua.dit.ds.ds_exc_2024.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;

@Service
public class ApartmentService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private ApartmentRepository apartmentRepository;
    private TenantRepository tenantRepository;
    private OwnerRepository ownerRepository;

    public ApartmentService(RoleRepository roleRepository, UserRepository userRepository, UserService userService, ApartmentRepository apartmentRepository, TenantRepository tenantRepository, OwnerRepository ownerRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.apartmentRepository = apartmentRepository;
        this.tenantRepository = tenantRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional
    public List<Apartment> getApartments(){
        return apartmentRepository.findAll();
    }

    public List<Apartment> getApartmentsByOwner(Owner owner) {
        return apartmentRepository.findByOwner(owner);
    }

    @Transactional
    public void saveApartment(Apartment apartment) {
        apartmentRepository.save(apartment);
    }

    @Transactional
    public Apartment getApartment(Integer apartmentId) {
        return apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Apartment with id " + apartmentId + " not found"
                ));
    }

    @Transactional
    public void deleteApartment(Integer apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found with ID: " + apartmentId));

        /* unassigns tenant */ //todo
        Tenant tenant = apartment.getTenant();
        if (tenant != null) {
            unassignTenantFromApartment(apartmentId, tenant.getId());
        }

        /* unassigns owner */
        Owner owner = apartment.getOwner();
        if (owner != null) {
            unassignOwnerFromApartment(apartmentId);
        }

        /* deletes apartment */
        apartmentRepository.delete(apartment);
    }

    @Transactional
    public void assignOwnerToApartment(Integer apartmentId, Owner owner) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apartment not found"));

        apartment.setOwner(owner);

        Integer currentUserId = userService.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Role ownerRole = roleRepository.findByName("ROLE_OWNER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        if (!currentUser.getRoles().contains(ownerRole)) {
            currentUser.getRoles().add(ownerRole);
            userService.updateUser(currentUser); // Save the user
        }
        apartmentRepository.save(apartment);
    }

    @Transactional
    public void unassignOwnerFromApartment(Integer apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId).get();
        apartment.setOwner(null);
        apartmentRepository.save(apartment);
    }

    @Transactional
    public void assignTenantToApartment(Integer apartmentId, Tenant tenant, String RoleUserIs) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apartment not found"));

        apartment.setTenant(tenant);

        Integer currentUserId = userService.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Role tenantRole = roleRepository.findByName("ROLE_TENANT")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
        Role ownerRole = roleRepository.findByName("ROLE_OWNER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        if (!RoleUserIs.equals("owner")) {
            currentUser.getRoles().add(tenantRole);
            userService.updateUser(currentUser); //saves the user
        }
        apartmentRepository.save(apartment);
    }

    @Transactional
    public void unassignTenantFromApartment(Integer apartmentId, Integer tenantId) {
        Apartment apartment = apartmentRepository.findById(apartmentId).get();
        Tenant tenant = tenantRepository.findById(tenantId).get();
        apartment.setTenant(null); //unlinks tenant from apartment
        tenant.setAppliedApartments(null);
        tenantRepository.save(tenant);
        apartmentRepository.save(apartment);
    }

    @Transactional
    public boolean isFirstApartment(Owner owner) {
        /* checks if the owner already has apartments */
        List<Apartment> apartments = getApartmentsByOwner(owner);
        return apartments.isEmpty(); //if no apartments found, it's the first one
    }

    @Transactional
    public void assignRoleToUserForFirstApartment(Owner owner, HttpSession session) {
        if (isFirstApartment(owner)) {
            User user = owner.getUser();
            Optional<Role> optionalRole = roleRepository.findByName("ROLE_OWNER");
            if (optionalRole.isPresent()) {
                Role ownerRole = optionalRole.get();
                System.out.println("User roles before adding: " + user.getRoles());
                if (!user.getRoles().contains(ownerRole)) {
                    user.getRoles().add(ownerRole);
                }
                userService.updateUser(user);
                session.invalidate();
                System.out.println("User roles after adding: " + user.getRoles());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role 'ROLE_OWNER' does not exist in the database.");
            }
        }
    }

    @Transactional
    public List<Apartment> filterApartments(int minPrice, int maxPrice) {
        return apartmentRepository.findByPriceBetween(minPrice, maxPrice);
    }

}