package gr.hua.dit.ds.ds_exc_2024.repositories;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    Optional<Tenant> findByUserId(Integer userId);

    @Modifying
    @Query(value = "DELETE FROM tenant_apartment_applications WHERE apartment_id = :apartmentId", nativeQuery = true)
    void deleteApplicationsByApartmentId(@Param("apartmentId") Integer apartmentId);
}
