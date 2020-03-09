package olnow.phmobile;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "phones")
public class Phones {
    public final static byte STATE_ACTIVE = 1;
    public final static byte STATE_INACTIVE = 0;
    public final static byte STATE_EXCLUDED = 2;
    public final static byte STATE_REISSUED = 3;

    public final static int PHONE_LEN = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "phone", fetch = FetchType.LAZY)
    private List<History> history;

    @OneToMany(mappedBy = "phone", fetch = FetchType.LAZY)
    private List<PhoneCash> phoneCash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idpeople")
    private People people;

    @Column(name = "phone")
    private String phone;

    @Column(name = "state")
    private byte state;

    @Column(name = "contract")
    private String contract;

    @Column private String tariff;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    //@OneToMany(mappedBy = "phone", fetch = FetchType.LAZY)
    private ArrayList<Service> services = null;

    public Phones() {
    }

    public Phones(Phones phone) {
        this.phone = phone.getPhone();
        this.state = phone.getState();
        if (phone.getContract() != null)
            this.contract = phone.getContract();
        if (phone.getPeople() != null)
            this.people = phone.getPeople();
    }


    public Phones(String phone, byte state) {
        this.phone = phone;
        this.state = state;
    }

    public byte getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public People getPeople() { return people; }

    public void setPeople(People people) { this.people = people; }

    @Override
    public String toString() {
        return phone;//"Phones{id=" + id + ", phone=" + phone + ", state=" + state;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public boolean isActive() {
        return state == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phones phones = (Phones) o;
        return state == phones.state &&
                phone.equals(phones.phone) &&
                Objects.equals(contract, phones.contract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone, state, contract);
    }

    public List<Service> getServices() {
        if (services == null)
            return new ArrayList<>();
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }
}
