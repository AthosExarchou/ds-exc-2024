package gr.hua.dit.ds.ds_exc_2024.repositories;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<Object> findByEmail(String name);
}