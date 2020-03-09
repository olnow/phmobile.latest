package olnow.phmobile;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table (name = "phonecash")
public class PhoneCash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idphonecash;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idphone")
    private Phones phone;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idpeople")
    private People people;

    @Column
    private Timestamp month;

    @Column
    private double internationalcalls;

    @Column
    private double longcalls;

    @Column
    private double localcalls;

    @Column
    private double localsms;

    @Column
    private double gprs;

    @Column
    private double internationalroamingcalls;

    @Column
    private double internationalroamingsms;

    @Column
    private double internationalgprsroaming;

    @Column
    private double internationalroamingcash;

    @Column
    private double russiaroamingcalls;

    @Column
    private double russiaroamingsms;

    @Column
    private double russiaroaminginet;

    @Column
    private double russiaroamingtraffic;

    @Column
    private double subscriptionfee;

    @Column
    private double subscriptionfeeaddon;

    @Column
    private double discounts;

    @Column
    private double onetime;

    @Column
    private double sum;

    @Column
    private double vat;

    @Column
    private double fullsum;

    public PhoneCash() {
    }

    public PhoneCash(Phones phone, People people,
                     Timestamp month, double internationalcalls,
                     double longcalls, double localcalls,
                     double localsms, double gprs,
                     double internationalroamingcalls,
                     double internationalroamingsms,
                     double internationalgprsroaming,
                     double internationalroamingcash,
                     double russiaroamingcalls,
                     double russiaroamingsms,
                     double russiaroaminginet,
                     double russiaroamingtraffic,
                     double subscriptionfee,
                     double subscriptionfeeaddon,
                     double discounts, double onetime,
                     double sum, double vat,
                     double fullsum) {
        this.phone = phone;
        this.people = people;
        this.month = month;
        this.internationalcalls = internationalcalls;
        this.longcalls = longcalls;
        this.localcalls = localcalls;
        this.localsms = localsms;
        this.gprs = gprs;
        this.internationalroamingcalls = internationalroamingcalls;
        this.internationalroamingsms = internationalroamingsms;
        this.internationalgprsroaming = internationalgprsroaming;
        this.internationalroamingcash = internationalroamingcash;
        this.russiaroamingcalls = russiaroamingcalls;
        this.russiaroamingsms = russiaroamingsms;
        this.russiaroaminginet = russiaroaminginet;
        this.russiaroamingtraffic = russiaroamingtraffic;
        this.subscriptionfee = subscriptionfee;
        this.subscriptionfeeaddon = subscriptionfeeaddon;
        this.discounts = discounts;
        this.onetime = onetime;
        this.sum = sum;
        this.vat = vat;
        this.fullsum = fullsum;
    }

    public PhoneCash(Phones phone, People people, double sum) {
        this.phone = phone;
        this.people = people;
        this.sum = sum;
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

    public Timestamp getMonth() {
        return month;
    }

    public void setMonth(Timestamp month) {
        this.month = month;
    }

    public double getInternationalcalls() {
        return internationalcalls;
    }

    public void setInternationalcalls(double internationalcalls) {
        this.internationalcalls = internationalcalls;
    }

    public double getLongcalls() {
        return longcalls;
    }

    public void setLongcalls(double longcalls) {
        this.longcalls = longcalls;
    }

    public double getLocalcalls() {
        return localcalls;
    }

    public void setLocalcalls(double localcalls) {
        this.localcalls = localcalls;
    }

    public double getLocalsms() {
        return localsms;
    }

    public void setLocalsms(double localsms) {
        this.localsms = localsms;
    }

    public double getGprs() {
        return gprs;
    }

    public void setGprs(double gprs) {
        this.gprs = gprs;
    }

    public double getInternationalroamingcalls() {
        return internationalroamingcalls;
    }

    public void setInternationalroamingcalls(double internationalroamingcalls) {
        this.internationalroamingcalls = internationalroamingcalls;
    }

    public double getInternationalroamingsms() {
        return internationalroamingsms;
    }

    public void setInternationalroamingsms(double internationalroamingsms) {
        this.internationalroamingsms = internationalroamingsms;
    }

    public double getInternationalgprsroaming() {
        return internationalgprsroaming;
    }

    public void setInternationalgprsroaming(double internationalgprsroaming) {
        this.internationalgprsroaming = internationalgprsroaming;
    }

    public double getInternationalroamingcash() {
        return internationalroamingcash;
    }

    public void setInternationalroamingcash(double internationalroamingcash) {
        this.internationalroamingcash = internationalroamingcash;
    }

    public double getRussiaroamingcalls() {
        return russiaroamingcalls;
    }

    public void setRussiaroamingcalls(double russiaroamingcalls) {
        this.russiaroamingcalls = russiaroamingcalls;
    }

    public double getRussiaroamingsms() {
        return russiaroamingsms;
    }

    public void setRussiaroamingsms(double russiaroamingsms) {
        this.russiaroamingsms = russiaroamingsms;
    }

    public double getRussiaroaminginet() {
        return russiaroaminginet;
    }

    public void setRussiaroaminginet(double russiaroaminginet) {
        this.russiaroaminginet = russiaroaminginet;
    }

    public double getRussiaroamingtraffic() {
        return russiaroamingtraffic;
    }

    public void setRussiaroamingtraffic(double russiaroamingtraffic) {
        this.russiaroamingtraffic = russiaroamingtraffic;
    }

    public double getSubscriptionfee() {
        return subscriptionfee;
    }

    public void setSubscriptionfee(double subscriptionfee) {
        this.subscriptionfee = subscriptionfee;
    }

    public double getSubscriptionfeeaddon() {
        return subscriptionfeeaddon;
    }

    public void setSubscriptionfeeaddon(double subscriptionfeeaddon) {
        this.subscriptionfeeaddon = subscriptionfeeaddon;
    }

    public double getDiscounts() {
        return discounts;
    }

    public void setDiscounts(double discounts) {
        this.discounts = discounts;
    }

    public double getOnetime() {
        return onetime;
    }

    public void setOnetime(double onetime) {
        this.onetime = onetime;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public double getFullsum() {
        return fullsum;
    }

    public void setFullsum(double fullsum) {
        this.fullsum = fullsum;
    }

    @Override
    public String toString() {
        return "PhoneCash{" +
                "phone=" + phone +
                ", people=" + people +
                ", fullsum=" + fullsum +
                '}';
    }

    public void sum(PhoneCash phoneCash) {
        if (phoneCash == null)
            return;
        if (this.phone != null && phoneCash.getPhone() != null)
            this.phone = phoneCash.getPhone();
        if (this.people != null && phoneCash.getPeople() != null)
            this.people = phoneCash.getPeople();
        if (this.month != null && phoneCash.getMonth() != null)
            this.month = phoneCash.getMonth();
        this.internationalcalls += phoneCash.getInternationalcalls();
        this.longcalls += phoneCash.getLongcalls();
        this.localcalls += phoneCash.getLocalcalls();
        this.localsms += phoneCash.getLocalsms();
        this.gprs += phoneCash.getGprs();
        this.internationalroamingcalls += phoneCash.getInternationalroamingcalls();
        this.internationalroamingsms += phoneCash.getInternationalroamingsms();
        this.internationalgprsroaming += phoneCash.getInternationalgprsroaming();
        this.internationalroamingcash += phoneCash.getInternationalroamingcash();
        this.russiaroamingcalls += phoneCash.getRussiaroamingcalls();
        this.russiaroamingsms += phoneCash.getRussiaroamingsms();
        this.russiaroaminginet += phoneCash.getRussiaroaminginet();
        this.russiaroamingtraffic += phoneCash.getRussiaroamingtraffic();
        this.subscriptionfee += phoneCash.getSubscriptionfee();
        this.subscriptionfeeaddon += phoneCash.getSubscriptionfeeaddon();
        this.discounts += phoneCash.getDiscounts();
        this.onetime += phoneCash.getOnetime();
        this.sum += phoneCash.getSum();
        this.vat += phoneCash.getVat();
        this.fullsum += phoneCash.getFullsum();
    }
}
