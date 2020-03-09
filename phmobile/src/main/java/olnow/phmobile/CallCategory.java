package olnow.phmobile;

import javax.persistence.*;

@Entity
@Table(name = "reference")
public class CallCategory extends Reference {
    public static final int TYPE_CATEGORYCALL = 1;
    public static final int TYPE_DESCRIPTION = 2;

    //@OneToMany(mappedBy = "callCategory", fetch = FetchType.LAZY)
    //@JsonBackReference
    //private List<PhoneDetail> phoneDetail;

    //@OneToMany(mappedBy = "description", fetch = FetchType.LAZY)
    //@JsonBackReference
    //private List<PhoneDetail> phoneDetaildescription;

    @Column
    private int idtype = TYPE_CATEGORYCALL;

    public CallCategory() {}

    public CallCategory(String callcategory) {
        setName(callcategory);
    }

    public String getCallcategory() {
        return getName();
    }

    public void setCallcategory(String callcategory) {
        setName(callcategory);
    }

    //public List<PhoneDetail> getPhoneDetail() {
    //    return phoneDetail;
    //}

    //public void setPhoneDetail(List<PhoneDetail> phoneDetail) {
    //    this.phoneDetail = phoneDetail;
    //}

}
