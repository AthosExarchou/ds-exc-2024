package gr.hua.dit.ds.ds_exc_2024.entities;

/* imports */
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 50)
    private String title;


    //ToDo: should be changed into @OneToOne?
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "owner_id")
    private Owner owner;

    //ToDo: should be changed into @OneToOne?
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "apartment_tenant",
            joinColumns = @JoinColumn(name = "apartment_id"),
            inverseJoinColumns = @JoinColumn(name = "tenant_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"apartment_id", "tenant_id"})
    )
    private List<Tenant> tenants;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public void addTenant(Tenant tenant) {
        tenants.add(tenant);
    }

    public Apartment(String title) {
        this.title = title;
    }

    public Apartment() {
    }

    //ToDo: we have way too many data for the apartment, we should probably create a profile for it.
    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", owner=" + owner +
                '}';
    }
}