package gr.hua.dit.ds.ds_exc_2024.repositories;

import gr.hua.dit.ds.ds_exc_2024.entities.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {
}
