package gr.hua.dit.ds.ds_exc_2024.entities;

/* imports */
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer Id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private int phoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tenant_profile_id", referencedColumnName = "id")
    private TenantProfile tenantProfile;

    /*TODO: DEN XREIAZETAI TENANT PROFILE, XREIAZETAI APARTMENT PROFILE*/

    //ToDo: should be changed into @OneToOne?
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name="apartment_tenant",
            joinColumns = @JoinColumn(name="tenant_id"),
            inverseJoinColumns = @JoinColumn(name="apartment_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "apartment_id"})
    )
    private List<Apartment> apartments;

    public TenantProfile gettenantProfile() {
        return tenantProfile;
    }

    public Tenant(String firstName, String lastName, int phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }


    public Tenant() {
    }

    public Integer getId() {
        return Id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "Id=" + Id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}