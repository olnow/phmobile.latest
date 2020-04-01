package olnow.phmobile.restapi;

import olnow.phmobile.Phones;

class PhoneWithDateWrapper {
    private Phones phone;
    private String datestart;

    public Phones getPhone() {
        return phone;
    }

    public void setPhone(Phones phone) {
        this.phone = phone;
    }

    public String getDatestart() {
        return datestart;
    }

    public void setDatestart(String datestart) {
        this.datestart = datestart;
    }
}
