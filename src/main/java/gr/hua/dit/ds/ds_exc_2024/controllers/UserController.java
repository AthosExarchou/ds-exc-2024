package gr.hua.dit.ds.ds_exc_2024.controllers;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Owner;
import gr.hua.dit.ds.ds_exc_2024.entities.Role;
import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import gr.hua.dit.ds.ds_exc_2024.entities.User;
import gr.hua.dit.ds.ds_exc_2024.repositories.OwnerRepository;
import gr.hua.dit.ds.ds_exc_2024.repositories.RoleRepository;
import gr.hua.dit.ds.ds_exc_2024.repositories.TenantRepository;
import gr.hua.dit.ds.ds_exc_2024.services.EmailService;
import gr.hua.dit.ds.ds_exc_2024.services.OwnerService;
import gr.hua.dit.ds.ds_exc_2024.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

@Controller
public class UserController {

    private final OwnerService ownerService;
    private final OwnerRepository ownerRepository;
    private final TenantRepository tenantRepository;
    private UserService userService;
    private RoleRepository roleRepository;
    private EmailService emailService;

    public UserController(UserService userService, RoleRepository roleRepository, OwnerService ownerService, OwnerRepository ownerRepository, TenantRepository tenantRepository, EmailService emailService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.ownerService = ownerService;
        this.ownerRepository = ownerRepository;
        this.tenantRepository = tenantRepository;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "auth/register";
    }

    @PostMapping("/saveUser")
    public String saveUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        System.out.println("Roles: "+user.getRoles());
        Integer id = userService.saveUser(user);

        try {
            emailService.sendWelcomeEmail(user.getEmail(), user);
        } catch (Exception e) {
            model.addAttribute("emailError", "User edited, but notification email could not be sent.");
        }

        String message = "User '"+id+"' saved successfully !";
        model.addAttribute("msg", message);
        return "index";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public String showUsers(Model model){
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        Integer currentUserId = userService.getCurrentUserId();
        model.addAttribute("currentUserId", currentUserId);
        return "auth/users";
    }

    @GetMapping("/user/{user_id}")
    public String showUser(@PathVariable Integer user_id, Model model) {
        User user = (User) userService.getUser(user_id);
        System.out.println(user);
        model.addAttribute("user", user);
        return "auth/user";
    }

    @PostMapping("/user/{user_id}")
    public String editUser(@PathVariable Integer user_id, @ModelAttribute("user") User user, Model model) {

        User the_user = (User) userService.getUser(user_id);

        String oldUsername = the_user.getUsername();
        String oldEmail = the_user.getEmail();

        /* checks for changes */
        boolean usernameChanged = !the_user.getUsername().equals(user.getUsername());
        boolean emailChanged = !the_user.getEmail().equals(user.getEmail());

        /* updates the user's information */
        the_user.setEmail(user.getEmail());
        the_user.setUsername(user.getUsername());

        userService.updateUser(the_user);
        System.out.println("Edited: "+ the_user);

        /* sends email notification to said user */
        if (usernameChanged || emailChanged) {
            try {
                /* sends email only if it was changed */
                if (emailChanged) {
                    /* sends to the old email address first */
                    emailService.sendUserDetailsChangedEmail(
                            oldEmail, // old email
                            the_user.getUsername(),
                            user.getEmail(), // new email
                            oldUsername,
                            oldEmail,
                            usernameChanged,
                            emailChanged
                    );

                    /* sends to the new email address as well */
                    emailService.sendUserDetailsChangedEmail(
                            user.getEmail(), // new email
                            the_user.getUsername(),
                            user.getEmail(), // new email
                            oldUsername,
                            oldEmail,
                            usernameChanged,
                            emailChanged
                    );
                }

                /* sends an email notification to the user if their username changed */
                if (usernameChanged) {
                    /* if email is changed, this will already be covered in the above logic */
                    /* if email is not changed, send email only for username change */
                    if (!emailChanged) {
                        emailService.sendUserDetailsChangedEmail(
                                the_user.getEmail(),
                                user.getUsername(), // new username
                                user.getEmail(),
                                oldUsername,
                                oldEmail,
                                usernameChanged,
                                emailChanged
                        );
                    }
                }

            } catch (Exception e) {
                model.addAttribute("emailError", "User edited, but notification email could not be sent.");
            }
        }

        model.addAttribute("users", userService.getUsers());
        return "auth/users";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user/role/delete/{user_id}/{role_id}")
    public String deleteRolefromUser(@PathVariable Integer user_id, @PathVariable Integer role_id, Model model){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();
        user.getRoles().remove(role);
        System.out.println("Roles: "+user.getRoles());
        userService.updateUser(user);
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "auth/users";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user/role/add/{user_id}/{role_id}")
    public String addRoletoUser(@PathVariable Integer user_id, @PathVariable Integer role_id, Model model) {
        User user = (User) userService.getUser(user_id);
        if (role_id.equals(4)) {
            if (user.getOwner() != null) {
                Optional<Role> optionalRole = roleRepository.findByName("ROLE_OWNER");
                if (optionalRole.isPresent()) {
                    Role ownerRole = optionalRole.get();
                    System.out.println("User roles before adding: " + user.getRoles());
                    if (!user.getRoles().contains(ownerRole)) {
                        user.getRoles().add(ownerRole);
                    }
                    userService.updateUser(user);
                    System.out.println("User roles after adding: " + user.getRoles());
                }
                return "auth/users";
            }
            Owner owner = new Owner();
            model.addAttribute("owner", owner);
            model.addAttribute("userId", user_id);
            return "owner/ownerform";
        } else if (role_id.equals(3)) {
            if (user.getTenant() != null) {
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
                return "auth/users";
            }
            Tenant tenant = new Tenant();
            tenant.setId(user.getId());
            tenant.setEmail(user.getEmail());
            model.addAttribute("tenant", tenant);
            model.addAttribute("userId", user_id);
            return "tenant/tenantformforadmin";
        } else if (role_id.equals(1)) {
            if (user != null) {
                Optional<Role> optionalRole = roleRepository.findByName("ROLE_USER");
                if (optionalRole.isPresent()) {
                    Role tenantRole = optionalRole.get();
                    System.out.println("User roles before adding: " + user.getRoles());
                    if (!user.getRoles().contains(tenantRole)) {
                        user.getRoles().add(tenantRole);
                    }
                    userService.updateUser(user);
                    System.out.println("User roles after adding: " + user.getRoles());
                }
                return "auth/users";
            }
        }
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "auth/users";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user/delete/{user_id}")
    public String deleteUser(@PathVariable Integer user_id, Model model) {
        User user = (User) userService.getUser(user_id);
        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");

        if (adminRole.isPresent() && user.getRoles().contains(adminRole.get())) {
            model.addAttribute("errorMessage", "You do not have the permission to delete this user!.");
            return "index";
        }

        /* sends email BEFORE deleting the user */
        try {
            emailService.sendAccountDeletionEmail(user.getEmail(), user);
            System.out.println("Account deletion email sent to user.");
        } catch (Exception e) {
            model.addAttribute("emailError", "User deleted, but email could not be sent.");
            System.out.println("Failed to send account deletion email.");
            e.printStackTrace();
        }

        userService.deleteUser(user_id);
        return "index";
    }

}
