package olnow.phmobile;

import javax.persistence.*;

@Entity
@Table(name = "reference")
public class Description extends Reference {
    public static final int TYPE_CATEGORYCALL = 1;
    public static final int TYPE_DESCRIPTION = 2;

    //@OneToMany(mappedBy = "callCategory", fetch = FetchType.LAZY)
    //@JsonBackReference
    //private List<PhoneDetail> phoneDetail;

    //@OneToMany(mappedBy = "description", fetch = FetchType.LAZY)
    //@JsonBackReference
    //private List<PhoneDetail> phoneDetail;

    @Column
    private int idtype = TYPE_DESCRIPTION;

    public Description() {}

    public Description(String description) {
        setName(description);
    }
    public Description(String name, int direction) {
        setName(name);
        setDirection(direction);
    }


    public String getDescription() {
        return getName();
    }

    public void setDescription(String description) {
        setName(description);
    }
}
