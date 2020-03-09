package olnow.phmobile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "people")
@JsonIgnoreProperties(ignoreUnknown = true)
public class People {
    public final static int SERVICE_ACCOUNT = 1;
    @JsonProperty("people")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private int idpeople;

    @OneToMany(mappedBy = "people")
    private List<History> history;

    @OneToMany(mappedBy = "people", fetch = FetchType.LAZY)
    private List<Phones> phones;

    @Column
    private String first;

    @Column
    private String last;

    @Column
    private String second;

    @NaturalId(mutable = true)
    @Column
    private String fio;

    @Column
    private String account;

    @Column
    private String department;

    @Column
    private String position;

    @Column
    private int adstate;

    @Column
    private int serviceaccount;

    public People() {
    }

    public People(String first, String last, String second,
                  String fio, String account,
                  String department, String position,
                  int serviceaccount) {
        this.first = first;
        this.last = last;
        this.second = second;
        this.fio = fio;
        this.account = account;
        this.department = department;
        this.position = position;
        this.serviceaccount = serviceaccount;
    }

    public People(String fio,
                  String account,
                  String department,
                  String position,
                  int serviceaccount) {
        this.setFio(fio);
        this.account = account;
        this.department = department;
        this.position = position;
        this.serviceaccount = serviceaccount;
    }

    public int getIdpeople() {
        return idpeople;
    }

    public void setIdpeople(int idpeople) {
        this.idpeople = idpeople;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getFio() {
        return fio;
    }

    public int getServiceaccount() {
        return serviceaccount;
    }

    public void setServiceaccount(int serviceaccount) {
        this.serviceaccount = serviceaccount;
    }

    public void setFio(String fio) {
        this.fio = fio;
        String[] val = fio.split(" ");
        if (val == null || val.length <= 1) {
            this.first = fio;
            this.last = "";
            this.second = "";
            return;
        }
        if (val.length == 2) {
            this.first = val[0];
            this.last = val[1];
            this.second = "";
        }
        else {
            this.first = val[0];
            this.last = val[1];
            this.second = val[2];
        }
    }

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public int getADState() { return adstate; }
    public void setADState(int adstate) { this.adstate = adstate; }

    public boolean isActive() {
        return adstate == LdapUtils.ACCOUNT_ENABLED;
    }

    @Override
    public String toString() {
        return getFio();
        /*return "People{" +
                "first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", second='" + second + '\'' +
                ", fio='" + fio + '\'' +
                ", account='" + account + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                '}';*/
    }

    public boolean isSameID(People people) {
        if (this == people) return true;
        if (people == null || getClass() != people.getClass()) return false;
        return idpeople == people.idpeople;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        People people = (People) o;
        return idpeople == people.idpeople &&
                adstate == people.adstate &&
                serviceaccount == people.serviceaccount &&
                first.equals(people.first) &&
                Objects.equals(last, people.last) &&
                Objects.equals(second, people.second) &&
                fio.equals(people.fio) &&
                Objects.equals(account, people.account) &&
                Objects.equals(department, people.department) &&
                Objects.equals(position, people.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpeople, first, last, second, fio,
                account, department, position, adstate, serviceaccount);
    }
}
