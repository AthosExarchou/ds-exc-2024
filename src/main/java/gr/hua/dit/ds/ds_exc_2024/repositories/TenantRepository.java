package gr.hua.dit.ds.ds_exc_2024.repositories;

import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
}
