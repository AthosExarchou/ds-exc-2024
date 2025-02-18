package gr.hua.dit.ds.ds_exc_2024.service;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.*;
import gr.hua.dit.ds.ds_exc_2024.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private ApartmentRepository apartmentRepository;
    private TenantRepository tenantRepository;
    private OwnerRepository  ownerRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, ApartmentRepository apartmentRepository, TenantRepository tenantRepository, OwnerRepository  ownerRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.apartmentRepository = apartmentRepository;
        this.tenantRepository = tenantRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional
    public Integer saveUser(User user) {
        String passwd= user.getPassword();
        String encodedPassword = passwordEncoder.encode(passwd);
        user.setPassword(encodedPassword);

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public Integer updateUser(User user) {
        userRepository.save(user);
        return user.getId();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findByUsername(username);

        if(opt.isEmpty())
            throw new UsernameNotFoundException("User with name: " +username +" not found !");
        else {
            User user = opt.get();
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.getRoles()
                            .stream()
                            .map(role-> new SimpleGrantedAuthority(role.toString()))
                            .collect(Collectors.toSet())
            );
        }
    }

    @Transactional
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User is not authenticated.");
        }
        User user = (User) userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + authentication.getName()));
        return user.getId();
    }

    @Transactional
    public boolean isUserOwner() {
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User id must not be null");
        }
        User currentUser = (User) getUser(currentUserId);
        for (Role role : currentUser.getRoles()) {
            if ("ROLE_OWNER".equals(role.getName())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Optional<Role> ownerRole = roleRepository.findByName("ROLE_OWNER");
        if (ownerRole.isPresent() && user.getRoles().contains(ownerRole.get())) {
            if (user.getOwner() != null) {
                List<Apartment> apartments = user.getOwner().getApartments();
                if (apartments != null) {
                    for (Apartment apartment : apartments) {
                        apartment.setOwner(null);
                        if (apartment.isRented()) {
                            Tenant tenant = apartment.getTenant();
                            apartment.setTenant(null);
                            apartment.setApplicants(null);
                            tenant.setApartment(null);
                        }
                        tenantRepository.deleteApplicationsByApartmentId(apartment.getId());
                        apartmentRepository.save(apartment);
                        apartmentRepository.delete(apartment);
                    }
                }
                ownerRepository.delete(user.getOwner());
            }
        }
        Optional<Role> tenantRole = roleRepository.findByName("ROLE_TENANT");
        if (tenantRole.isPresent() && user.getRoles().contains(tenantRole.get())) {
            if (user.getTenant() != null) {
                Tenant tenant = user.getTenant();
                if (tenant.getApartment() != null) {
                    tenant.getApartment().setTenant(null);
                    tenant.setApartment(null);
                }
                tenantRepository.delete(user.getTenant());
            }
        }

        userRepository.delete(user);
    }


    @Transactional
    public Object getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public Object getUser(Integer userId) {
        return userRepository.findById(userId).get();
    }

}