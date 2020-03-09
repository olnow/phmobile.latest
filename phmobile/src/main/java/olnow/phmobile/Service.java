package olnow.phmobile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "service")
public class Service {
    public final static int TYPE_TARIFF = 1;
    public final static int TYPE_SERVICE = 2;
    public final static String STATE_ACTIVE_NAME = "Активный";
    public final static String STATE_INACTIVE_NAME = "Заблокированный";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idservice;

    @JsonIgnoreProperties
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idphone")
    private Phones phone;

    @Column private String name;
    @Column private String code;
    @Column private int type = TYPE_SERVICE;
    @Column private Timestamp datestart;
    @Column private Timestamp dateend;

    public Service() {
    }

    public Service(Phones phone, String name, String code, int type, Timestamp datestart, Timestamp dateend) {
        this.phone = phone;
        this.name = name;
        this.code = code;
        this.type = type;
        this.datestart = datestart;
        this.dateend = dateend;
    }

    public Service(Phones phone, String name, String code) {
        this.phone = phone;
        this.name = name;
        this.code = code;
    }

    public Phones getPhone() {
        return phone;
    }

    public void setPhone(Phones phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Timestamp getDatestart() {
        return datestart;
    }

    public void setDatestart(Timestamp datestart) {
        this.datestart = datestart;
    }

    public Timestamp getDateend() {
        return dateend;
    }

    public void setDateend(Timestamp dateend) {
        this.dateend = dateend;
    }
}
