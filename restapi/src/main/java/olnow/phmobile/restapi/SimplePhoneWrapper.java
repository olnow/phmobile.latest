package olnow.phmobile.restapi;

class SimplePhoneWrapper {
    private String phone;

    private String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return getPhone();
    }
}
