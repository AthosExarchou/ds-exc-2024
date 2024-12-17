package gr.hua.dit.ds.ds_exc_2024.service;

import gr.hua.dit.ds.ds_exc_2024.entities.Owner;
import gr.hua.dit.ds.ds_exc_2024.repositories.ApartmentRepository;
import gr.hua.dit.ds.ds_exc_2024.repositories.OwnerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {

    private OwnerRepository ownerRepository;

    private ApartmentRepository apartmentRepository;

    public OwnerService(OwnerRepository ownerRepository, ApartmentRepository apartmentRepository) {
        this.ownerRepository = ownerRepository;
        this.apartmentRepository = apartmentRepository;
    }

    @Transactional
    public List<Owner> getOwners(){
        return ownerRepository.findAll();
    }

    @Transactional
    public Owner getOwner(Integer ownerId) {
        return ownerRepository.findById(ownerId).get();
    }

    @Transactional
    public void saveOwner(Owner owner) {
        ownerRepository.save(owner);
    }

}
