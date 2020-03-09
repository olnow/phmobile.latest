package olnow.phmobile;

import javax.persistence.*;

@Entity
@Table(name = "contracts")
public class Contracts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcontracts;

    @Column private String name;
    @Column private String number;
    @Column private String operator;

    public Contracts() {}

    public Contracts(String name, String number, String operator) {
        this.name = name;
        this.number = number;
        this.operator = operator;
    }

    public int getIdcontracts() {
        return idcontracts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
