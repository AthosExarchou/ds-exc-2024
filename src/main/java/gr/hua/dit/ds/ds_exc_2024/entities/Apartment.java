package gr.hua.dit.ds.ds_exc_2024.entities;

/* imports */
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column
    @Min(value = -3, message = "Floor level must be no lower than -3")
    @Max(value = 164, message = "Floor level must be no higher than 164")
    private int floor;

    @Column
    @Min(value = 1, message = "There must be at least one bathroom")
    @Max(value = 4, message = "There can be no more than 4 bathrooms")
    private int bathrooms;

    @Column
    @Min(value = 1, message = "Apartment must have at least one bedroom")
    @Max(value = 10, message = "Apartment must have no more than 10 bedrooms")
    private int bedrooms;

    @Column
    @NotEmpty(message = "City is required")
    @Size(min = 1, max = 50)
    private String city;

    @Column
    @NotEmpty(message = "Street is required")
    @Size(min = 1, max = 50)
    private String street;

    @Column
    @Min(value = 1000, message = "Postal code must be at least 1000")
    private int pc;

    @Column
    @Min(value = 10, message = "Apartment must be at least 10 square meters")
    @Max(value = 900, message = "Apartment must be no more than 900 square meters")
    private int squareMeters;

    @Column
    @Min(value = 0, message = "Price must be a positive value")
    @Max(value = 20000, message = "Price can be no more than 20000 euros")
    private int price;

    @Column
    private Boolean parking;

    @Column
    @Min(value = 1800, message = "Year built must be 1800 or later")
    private int yearBuilt;

    @Column(nullable = false)
    private Boolean approved = false;

    /* APARTMENT-OWNER RELATIONSHIP */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "owner_id")
    private Owner owner;

    /* APARTMENT-TENANT RELATIONSHIP */
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                        CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "tenant_id", referencedColumnName = "id", unique = true)
    private Tenant tenant;

    /* APARTMENT-TENANT (APPLICATIONS) RELATIONSHIP */
    @ManyToMany(mappedBy = "appliedApartments", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Tenant> applicants = new HashSet<>();

    public Set<Tenant> getApplicants() {
        return applicants;
    }

    public void setApplicants(Set<Tenant> applicants) {
        this.applicants = applicants;
    }

    public Apartment(int floor, int bathrooms, int bedrooms, String city, String street, int pc, int squareMeters, int price, Boolean parking, int yearBuilt, Boolean approved, Owner owner, Tenant tenant) {
        this.floor = floor;
        this.bathrooms = bathrooms;
        this.bedrooms = bedrooms;
        this.city = city;
        this.street = street;
        this.pc = pc; //postal code
        this.squareMeters = squareMeters;
        this.price = price;
        this.parking = parking;
        this.yearBuilt = yearBuilt;
        this.approved = approved;
        this.owner = owner;
        this.tenant = tenant;
    }

    public Apartment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getSquareMeters() {
        return squareMeters;
    }

    public void setSquareMeters(int squareMeters) {
        this.squareMeters = squareMeters;
    }

    public Boolean getParking() {
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(int yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public Boolean getApproved() {return approved;}

    public void setApproved(Boolean approved) {this.approved = approved;}

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public boolean isRented() {
        return tenant != null && tenant.getRentalStatus() == Tenant.RentalStatus.RENTING;
    }

    public void addApplicant(Tenant tenant) {
        if (!applicants.contains(tenant)) {
            applicants.add(tenant);
            tenant.getAppliedApartments().add(this);
        }
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", floor=" + floor +
                ", bathrooms=" + bathrooms +
                ", bedrooms=" + bedrooms +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", pc=" + pc +
                ", squareMeters=" + squareMeters +
                ", price=" + price +
                ", parking=" + parking +
                ", yearBuilt=" + yearBuilt +
                ", approved=" + approved +
                '}';
    }
}