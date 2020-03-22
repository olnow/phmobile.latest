package olnow.phmobile.restapi;

import olnow.phmobile.Phones;

class PhoneWithDateWrapper {
    Phones phone;
    String datestart;

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
