package olnow.phmobile;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table (name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idhistory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idphone")
    private Phones phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idpeople")
    private People people;

    @Column
    private Timestamp datestart;

    @Column
    private Timestamp dateend;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idhistorytype")
    private HistoryType historytype;

    @Column
    private String comment;

    public History() {
    }

    public History(Phones phone, People people, Timestamp datestart, HistoryType type, String comment) {
        this.phone = phone;
        this.people = people;
        this.historytype = type;
        this.comment = comment;
        this.datestart = datestart;
    }

    public History(Phones phone, People people, Timestamp datestart, HistoryType type) {
        this.phone = phone;
        this.people = people;
        this.historytype = type;
        this.datestart = datestart;
    }

    public int getIdhistory() {
        return idhistory;
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

    public Timestamp getDatestart() {
        return datestart;
    }
    public Timestamp getDateend() {
        return dateend;
    }

    public void setDate(Timestamp datestart) {
        this.datestart = datestart;
    }
    public void setDateEnd(Timestamp dateend) {
        this.dateend = dateend;
    }

    public HistoryType getType() {
        return historytype;
    }

    public void setType(HistoryType type) {
        this.historytype = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "History{" +
                "idphone=" + phone.toString() +
                ", idpeople=" + people.toString() +
                ", date=" + datestart +
                ", type=" + historytype.toString() +
                ", comment='" + comment + '\'' +
                '}';
    }
}
