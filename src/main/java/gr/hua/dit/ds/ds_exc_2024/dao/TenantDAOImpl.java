package gr.hua.dit.ds.ds_exc_2024.dao;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TenantDAOImpl implements TenantDAO {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public List<Tenant> getTenants() {
        TypedQuery<Tenant> query = entityManager.createQuery("from Tenant", Tenant.class);
        return query.getResultList();
    }

    @Override
    public Tenant getTenant(Integer tenant_id) {
        return entityManager.find(Tenant.class, tenant_id);
    }

    @Override
    @Transactional
    public void saveTenant(Tenant tenant) {
        System.out.println("tenant "+ tenant.getId());
        if (tenant.getId() == null) {
            entityManager.persist(tenant);
        } else {
            entityManager.merge(tenant);
        }
    }

    @Override
    @Transactional
    public void deleteTenant(Integer tenant_id) {
        System.out.println("Deleting tenant with id: " + tenant_id);
        entityManager.remove(entityManager.find(Tenant.class, tenant_id));
    }
}
