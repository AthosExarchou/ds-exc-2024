package gr.hua.dit.ds.ds_exc_2024.controllers;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Role;
import gr.hua.dit.ds.ds_exc_2024.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {


    RoleRepository roleRepository;

    public AuthController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void setup() {
        initializeRole("ROLE_USER");
        initializeRole("ROLE_ADMIN");
        initializeRole("ROLE_TENANT");
        initializeRole("ROLE_OWNER");
    }

    private void initializeRole(String roleName) {
        roleRepository.updateOrInsert(new Role(roleName));
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
