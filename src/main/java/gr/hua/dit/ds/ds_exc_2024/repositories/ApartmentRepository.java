package gr.hua.dit.ds.ds_exc_2024.repositories;

/* imports */
import gr.hua.dit.ds.ds_exc_2024.entities.Apartment;
import gr.hua.dit.ds.ds_exc_2024.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {
    List<Apartment> findByOwner(Owner owner);
    List<Apartment> findByPriceBetween(int minPrice, int maxPrice);
}
