package olnow.phmobile;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

//@Entity
//@Inheritance(strategy=InheritanceType.JOINED)
@MappedSuperclass
public class Reference {
    public static final int CALLTYPE_LOCAL_CALLS = 1;
    public static final int CALLTYPE_LOCAL_SMS = 2;
    public static final int CALLTYPE_LOCAL_INTERNET = 3;
    public static final int CALLTYPE_LOCAL_MMS = 4;
    public static final int CALLTYPE_PAY_SERV = 5;
    public static final int CALLTYPE_GOOGLE_PLAY = 6;
    public static final int CALLTYPE_ROAMING_CALLS = 11;
    public static final int CALLTYPE_ROAMING_SMS = 12;
    public static final int CALLTYPE_ROAMING_INTERNET = 13;
    public static final int CALLTYPE_INTERNATIONAL_CALLS = 21;
    public static final int CALLTYPE_INTERNATIONAL_SMS = 22;
    public static final int DIRECTION_IN = 2;
    public static final int DIRECTION_OUT = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idreference;

    @Column
    @NaturalId
    private String name;

    @Column
    private int idtype = 0;

    @Column private int direction = 0;
    @Column private int calltype = 0;

    public Reference() {}
    public Reference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getCalltype() {
        return calltype;
    }

    public void setCalltype(int calltype) {
        this.calltype = calltype;
    }
}
