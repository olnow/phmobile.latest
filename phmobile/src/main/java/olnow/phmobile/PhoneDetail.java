package olnow.phmobile;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table (name = "phonedetail")
public class PhoneDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idphonedetail;

    @ManyToOne
    @JoinColumn(name = "idphone")
    @JsonManagedReference
    private Phones phone;

    @ManyToOne
    @JoinColumn(name = "idpeople")
    @JsonManagedReference
    private People people;

    @Column
    private String tariff;

    @Column
    private Timestamp datetime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinColumn(name = "idcallcategory", referencedColumnName = "idreference")
    private CallCategory callCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    @JoinColumn(name = "iddescription", referencedColumnName = "idreference")
    private Description description;

    @Column
    private int duration;

    @Column
    private double charge;

    @Column
    private String otherpart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idroaming")
    private Roaming roaming;

    public PhoneDetail(Phones phone,
                       People people,
                       String tariff,
                       Timestamp datetime,
                       CallCategory callCategory,
                       Description description,
                       int duration,
                       double charge,
                       String otherpart,
                       Roaming roaming) {
        this.phone = phone;
        this.people = people;
        this.tariff = tariff;
        this.datetime = datetime;
        this.callCategory = callCategory;
        this.description = description;
        this.duration = duration;
        this.charge = charge;
        this.otherpart = otherpart;
        this.roaming = roaming;
    }

    public PhoneDetail() {
    }

    public Phones getPhone() {
        return phone;
    }

    public void setPhone(Phones phone) {
        this.phone = phone;
    }

    public People getPeople() {
        return people;
    }

    public void setPeople(People people) {
        this.people = people;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public CallCategory getCallCategory() {
        return callCategory;
    }

    public void setCallCategory(CallCategory callCategory) {
        this.callCategory = callCategory;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public String getOtherpart() {
        return otherpart;
    }

    public void setOtherpart(String otherpart) {
        this.otherpart = otherpart;
    }

    public Roaming getRoaming() {
        return roaming;
    }

    public void setRoaming(Roaming roaming) {
        this.roaming = roaming;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneDetail that = (PhoneDetail) o;
        return duration == that.duration &&
                Double.compare(that.charge, charge) == 0 &&
                phone.equals(that.phone) &&
                Objects.equals(people, that.people) &&
                Objects.equals(tariff, that.tariff) &&
                datetime.equals(that.datetime) &&
                callCategory.equals(that.callCategory) &&
                Objects.equals(description, that.description) &&
                Objects.equals(otherpart, that.otherpart) &&
                Objects.equals(roaming, that.roaming);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone, people,
                tariff, datetime,
                callCategory, description,
                duration, charge,
                otherpart, roaming);
    }
}
