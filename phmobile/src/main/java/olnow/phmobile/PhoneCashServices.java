package olnow.phmobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PhoneCashServices {
    PhoneCashDAO phoneCashDAO = new PhoneCashDAO();
    PhonesServices phonesServices = new PhonesServices();
    HistoryServices historyServices = new HistoryServices();
    Logger logger = LoggerFactory.getLogger(PhoneCashServices.class);

    public void addPhoneCash(PhoneCash phoneCash) { phoneCashDAO.addPhoneCash(phoneCash); }
    public void updatePhoneCash(PhoneCash phoneCash) { phoneCashDAO.updatePhoneCash(phoneCash); }
    public ArrayList<Object[]> getPhoneCashYear(int year) { return phoneCashDAO.getPhoneCashYear(year); }
    public ArrayList<Object[]> getPhoneCashMonth(int year, int month) {
        return phoneCashDAO.getPhoneCashMonth(year, month, false);
    }
    public ArrayList<Object[]> getPhoneCashMonth(int year, int month, boolean skipZeroSum) {
        return phoneCashDAO.getPhoneCashMonth(year, month, skipZeroSum);
    }

    public ArrayList<PhoneCash> getPhoneCashWOPeople() { return phoneCashDAO.getPhoneCashWOPeople(); }
    public ArrayList<Object[]> getDepartmentCashYear(int year) { return phoneCashDAO.getDepartmentCashYear(year); }
    public ArrayList<Object[]> getPhonesCashArray(int year, String phone) {
        return phoneCashDAO.getPhoneCashArray(year, phone);
    }
    public double getPhoneCash(int year, int month, String phone) {
        return phoneCashDAO.getPhoneCash(year, month, phone);
    }
    public PhoneCash getPhoneCashDetail(int year, int month, String phone) {
        return phoneCashDAO.getPhoneCashDetail(year, month, phone);
    }

    public ArrayList<Object[]> getContractCashInfo() { return phoneCashDAO.getContractCashInfo(); }
    public ArrayList<Object[]> getContractCashAndDetailInfo() { return phoneCashDAO.getContractCashAndDetailInfo(); }

    public static String getFirstUpper(String source) {
        if (source == null || source.isEmpty())
            return null;
        return source.substring(0,1).toUpperCase() + source.substring(1,source.length()).toLowerCase();
    }

    public Timestamp getMonth(Map<String, String> maps) {
        if (maps == null)
            return null;
        if (maps.get("month") != null && !maps.get("month").isEmpty())
            try {
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date monthFormat = format.parse(maps.get("month"));
                return new Timestamp(monthFormat.getTime());
            }
            catch (Exception e) {
                logger.info("[getMonth] month: {}", maps.get("month"), e);
            }
        return null;
    }

    public void addPhoneCash(Map<String, String> maps) {
        if (maps == null)
            return;
        PhoneCash phoneCashReflex = new PhoneCash();
        Method methodSet;
        Phones phone;
        if ((phone = phonesServices.findPhone(maps.get("phone"))) == null) {
            phone = new Phones();
            phone.setPhone(maps.get("phone"));
            phone.setState(Phones.STATE_ACTIVE);
            phonesServices.addPhone(phone);
        }
        phoneCashReflex.setPhone(phone);
        //convert month
        Timestamp month = null;
        try {
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date monthFormat = format.parse(maps.get("month"));
                month = new Timestamp(monthFormat.getTime());
                phoneCashReflex.setMonth(month);
            }
        catch (Exception e) {
            System.out.println("PhoneCashServices:addPhoneCash: " + e.toString());
        }
        People people;
        if (month != null ) {
            people = historyServices.getPeopleAtDate(phone, month);
            if (people != null)
                phoneCashReflex.setPeople(people);
        }

        for ( Map.Entry<String, String> res : maps.entrySet() ) {
            try {
                switch (res.getKey()) {
                    case "phone":
                    case "people":
                    case "contract":
                    case "month":
                        continue;
                    default:
                        methodSet = phoneCashReflex.getClass().
                                getDeclaredMethod("set" + getFirstUpper(res.getKey()), double.class);
                        //phoneCashReflex.getClass().getDeclaredMethod("", double.class);
                        double doubleRes = 0;
                        try {
                            doubleRes = Double.parseDouble(res.getValue().replaceAll(",","."));
                        }
                        catch (Exception e) {
                            System.out.println("PhoneCashServices:addPhoneCash: double convert: " + e.toString());
                        }
                        methodSet.invoke(phoneCashReflex, doubleRes);
                }
            }
            catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                System.out.println("PhoneCashServices:addPhoneCash: " + e.toString());
            }
        }
        phoneCashDAO.addPhoneCash(phoneCashReflex);
    }

    public void updatePhoneCashWOPeople() {
        ArrayList<PhoneCash> phoneCashArrayList = phoneCashDAO.getPhoneCashWOPeople();
        if (phoneCashArrayList == null)
            return;
        for (PhoneCash cash: phoneCashArrayList) {
            People people;
            if (cash.getMonth() != null ) {
                people = historyServices.getPeopleAtDate(cash.getPhone(), cash.getMonth());
                if (people != null)
                    cash.setPeople(people);
                    phoneCashDAO.updatePhoneCash(cash);
            }
        }
    }
}
