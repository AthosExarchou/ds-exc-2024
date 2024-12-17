package gr.hua.dit.ds.ds_exc_2024.dao;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import java.util.List;

public interface TenantDAO {
    public List<Tenant> getTenants();
    public Tenant getTenant(Integer tenant_id);
    public void saveTenant(Tenant tenant);
    public void deleteTenant(Integer tenant_id);
}
