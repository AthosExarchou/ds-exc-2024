package gr.hua.dit.ds.ds_exc_2024.repositories;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {
    Optional<Owner> findByUserId(Integer userId);
}
