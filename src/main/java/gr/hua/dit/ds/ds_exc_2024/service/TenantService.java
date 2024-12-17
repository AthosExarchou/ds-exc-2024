package gr.hua.dit.ds.ds_exc_2024.service;

import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import gr.hua.dit.ds.ds_exc_2024.repositories.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public List<Tenant> getTenants() {
        return tenantRepository.findAll();
    }

    @Transactional
    public void saveTenant(Tenant tenant) {
        tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant getTenant(Integer tenantId) {
        return tenantRepository.findById(tenantId).get();
    }

}

