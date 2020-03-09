package olnow.phmobile;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "historytype")
public class HistoryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idhistorytype;

    @OneToMany(mappedBy = "historytype", fetch = FetchType.LAZY)
    private List<History> history;

    @Column
    private String name;

    public HistoryType() {
    }

    public HistoryType(String name) {
        this.name = name;
    }

    public int getIdhistorytype() {
        return idhistorytype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();//"HistoryType{" +
                //"name='" + name + '\'' +
                //'}';
    }
}
