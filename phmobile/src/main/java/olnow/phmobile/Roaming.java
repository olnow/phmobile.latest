package olnow.phmobile;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "roaming")
public class Roaming {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idroaming;

    @Column
    @NaturalId
    private String roaming;

    @OneToMany(mappedBy = "roaming", fetch = FetchType.LAZY)
    private List<PhoneDetail> phoneDetail;

    public Roaming() {}

    public Roaming(String roaming) {
        this.roaming = roaming;
    }

    public String getRoaming() {
        return roaming;
    }

    public void setRoaming(String roaming) {
        this.roaming = roaming;
    }

    public List<PhoneDetail> getPhoneDetail() {
        return phoneDetail;
    }

    public void setPhoneDetail(List<PhoneDetail> phoneDetail) {
        this.phoneDetail = phoneDetail;
    }
}
