package olnow.phmobile;

import javax.persistence.*;

@Entity
@Table (name = "tariff")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idtariff;

    @Column private String name;
    @Column private String code;
    @Column private int minutes = 0;
    @Column private int sms = 0;
    @Column private int internet = 0;
    @Column private int cost = 0;
    @Column double minutescost = 0;
    @Column double smscost = 0;
    @Column double internetcost = 0;

    public Tariff() {
    }

    public Tariff(String name, int minutes, int sms, int internet,
                  int cost, double minutescost, double smscost, double internetcost) {
        this.name = name;
        this.minutes = minutes;
        this.sms = sms;
        this.internet = internet;
        this.cost = cost;
        this.minutescost = minutescost;
        this.smscost = smscost;
        this.internetcost = internetcost;
    }

    public String getName() {
        return name;
    }

    public int getIdtariff() {
        return idtariff;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSms() {
        return sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public int getInternet() {
        return internet;
    }

    public void setInternet(int internet) {
        this.internet = internet;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public double getMinutescost() {
        return minutescost;
    }

    public void setMinutescost(double minutescost) {
        this.minutescost = minutescost;
    }

    public double getSmscost() {
        return smscost;
    }

    public void setSmscost(double smscost) {
        this.smscost = smscost;
    }

    public double getInternetcost() {
        return internetcost;
    }

    public void setInternetcost(double internetcost) {
        this.internetcost = internetcost;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
