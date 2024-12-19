package gr.hua.dit.ds.ds_exc_2024.service;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Apartment;
import gr.hua.dit.ds.ds_exc_2024.entities.Tenant;
import gr.hua.dit.ds.ds_exc_2024.entities.Owner;
import gr.hua.dit.ds.ds_exc_2024.repositories.ApartmentRepository;
import gr.hua.dit.ds.ds_exc_2024.repositories.OwnerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentService {

    private ApartmentRepository apartmentRepository;

    private OwnerRepository ownerRepository;

    public ApartmentService(ApartmentRepository apartmentRepository, OwnerRepository ownerRepository) {
        this.apartmentRepository = apartmentRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional
    public List<Apartment> getApartments(){
        return apartmentRepository.findAll();
    }

    @Transactional
    public void saveApartment(Apartment apartment) {
        apartmentRepository.save(apartment);
    }

    @Transactional
    public Apartment getApartment(Integer apartmentId) {
        return apartmentRepository.findById(apartmentId).get();
    }

    @Transactional
    public void assignOwnerToApartment(int apartmentId, Owner owner) {
        Apartment apartment = apartmentRepository.findById(apartmentId).get();
        System.out.println(apartment);
        System.out.println(apartment.getOwner());
        apartment.setOwner(owner);
        System.out.println(apartment.getOwner());
        apartmentRepository.save(apartment);
    }

    @Transactional
    public void unassignOwnerFromApartment(int apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId).get();
        apartment.setOwner(null);
        apartmentRepository.save(apartment);
    }

    //ToDo: should also create unassignTenantToApartment function?
    @Transactional
    public void assignTenantToApartment(int apartmentId, Tenant tenant) {
        Apartment apartment = apartmentRepository.findById(apartmentId).get();
        apartment.addTenant(tenant);
        System.out.println("Apartment tenants: ");
        System.out.println(apartment.getTenants());
        apartmentRepository.save(apartment);
    }
}