package olnow.phmobile;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PhonesServices {
    private PhonesDAO phones = new PhonesDAO();
    private PeopleServices people = new PeopleServices();
    private String templateseparator = ";";
    private HistoryServices historySrvc = new HistoryServices();
    private LinkedList<Phones> phonesList;

    public void addPhone(Phones phone) {
        this.phones.addPhone(phone);
    }
    public void updatePhone(Phones phone) {
        this.phones.updatePhone(phone);
    }
    public Phones findPhoneById(int id) {
        return this.phones.findPhone(id);
    }
    public Phones findPhonesByPhone(String phone) {
        return this.phones.findPhone(phone);
    }
    public void setTemplateSeparator(String separator) { this.templateseparator = separator; }
    public String getTemplateSeparator() {return templateseparator; }
    public Phones findPhone(String phone) { return phones.findPhone(phone); }
    public ArrayList<Phones> getPhonesList() {
        return new ArrayList<>(phonesList);
    }

    private final String STATE_ACTIVE_NAME = "Активный";
    private final String STATE_INACTIVE_NAME = "Заблокированный";

    Logger logger = LoggerFactory.getLogger(PhonesServices.class);

    static boolean isNumeric(String num) {
        if (num == null || num.isEmpty())
            return false;
        for (int i=0; i < num.length(); i++)
            if (!Character.isDigit(num.charAt(i)))
                return false;
        return true;
    }

    public static String trimSymvols(String source) {
        if (source == null || source.isEmpty())
            return source;
        String res;
        if (source.indexOf("\"") == 0)
            if (source.lastIndexOf("\"") == source.length()-1)
                res = source.substring(1, source.length()-2).trim();
            else
                res = source.substring(1, source.length()-1).trim();
        else
        if (source.lastIndexOf("\"") == source.length()-1)
            res = source.substring(0, source.length()-2).trim();
        else res = source;
        return res;
    }

    public void addParce(String line, String separator, String template) {
        try {
            if (line == null) return;
            String[] values = line.split(separator);
            String[] template_values = template.split(templateseparator);
            Map<String, String> templatemap = new HashMap<>();
            for (int i=0; i<template_values.length ; i++) {
                if (template_values[i]!=null && !template_values[i].equals(""))
                    templatemap.put(template_values[i], trimSymvols(values[i]));
            }
            if (!isNumeric(templatemap.get("phone")) || templatemap.get("phone").length() != Phones.PHONE_LEN)
                return;
            Phones phone;
            if ((phone = phones.findPhone(templatemap.get("phone"))) == null) {
                phone = new Phones();
                phone.setPhone(templatemap.get("phone"));
                phone.setState(Phones.STATE_ACTIVE);
                if (templatemap.containsKey("contract"))
                    phone.setContract(templatemap.get("contract"));
            }
            People human;
            if ((human = people.findPeople(templatemap.get("fio"))) == null &&
                    !templatemap.get("fio").isEmpty() &&
                    !templatemap.get("fio").equals("Свободно") &&
                    !templatemap.get("fio").equals("Блокировка")) {
                human = new People();
                human.setFio(templatemap.get("fio"));
                people.addPeople(human);
            }
            if (human!=null) {
                if (templatemap.containsKey("account") &&
                    !templatemap.get("account").isEmpty() &&
                    Character.isAlphabetic(templatemap.get("account").charAt(0)) &&
                    (human.getAccount() == null || !human.getAccount().equals(templatemap.get("account"))))
                        human.setAccount(templatemap.get("account"));
                if (templatemap.containsKey("depart") &&
                        !templatemap.get("depart").isEmpty() &&
                        Character.isAlphabetic(templatemap.get("depart").charAt(0)) &&
                        (human.getDepartment() == null || !human.getDepartment().equals(templatemap.get("depart"))))
                    human.setDepartment(templatemap.get("depart"));
                if (templatemap.containsKey("title") &&
                        !templatemap.get("title").isEmpty() &&
                        Character.isAlphabetic(templatemap.get("title").charAt(0)) &&
                        (human.getPosition() == null || !human.getPosition().equals(templatemap.get("title"))))
                    human.setPosition(templatemap.get("title"));

                people.updatePeople(human);
            }
            if (human!=null)
                phone.setPeople(human);
            updatePhone(phone);
            historySrvc.GenerateHistory(phone);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void addPhonesTariff(Map<String, String> maps) throws NotFoundException {
        if (maps == null)
            return;

        Phones phones = findPhone(maps.get("phone"));
        if (phones == null) {
            logger.error(this.getClass().getName(), "Phone not found: ", maps.get("phone"));
            System.out.println(this.getClass().getName() + "Phone not found: " + maps.get("phone"));
            throw new NotFoundException("Phone not found: " + maps.get("phone"));
        }
        if (maps.get("status") != null && !maps.get("status").isEmpty()) {
            logger.debug(maps.get("status"), maps.get("status"), maps.get("status").equals(STATE_ACTIVE_NAME));
            if (maps.get("status").equals(STATE_ACTIVE_NAME))
                phones.setState(Phones.STATE_ACTIVE);
            else
                phones.setState(Phones.STATE_INACTIVE);
        }

        if (maps.get("tariff_code") != null && !maps.get("tariff_code").isEmpty()) {
            phones.setTariff(maps.get("tariff_code"));
        }
        updatePhone(phones);
    }

    public ArrayList<Phones> getPhones() {
        return this.phones.getPhones();
    }

    public void setPhoneFilter(String filter) { phones.setPhoneFilter(filter); }
    public void clearPhoneFilter() { phones.clearPhoneFilter(); }
    public void setFIOFilter(String filter) { phones.setFIOFilter(filter); }
    public void clearFIOFilter() { phones.clearFIOFilter(); }
    public void setPeopleType(int type) { phones.setPeopleType(type); }
    public void clearPeopleType() { phones.clearPeopleType(); }
    //public History getActiveHistory(Phones phone) { return phones.getActiveHistory(phone); }

    public void loadPhoneList() {
        phonesList = new LinkedList<>(getPhones());
        logger.debug("[loadPhoneList] phones loaded: {}", phonesList.size());
    }

    public void checkAndDeleteFromPhonesList(Map<String, String> maps) {
        if (maps == null || phonesList == null)
            return;

        Phones phones = findPhone(maps.get("phone"));
        if (phones != null) {
            phonesList.remove(phones);
            logger.trace("[checkAndDeleteFromPhonesList] phone remove: {}", phones.getPhone());
        }
    }

    public void updateExcludedStatusFromPhonesList() {
        if (phonesList == null)
            return;
        logger.debug("[updateExcludedStatusFromPhonesList] excluded phones count: {}", phonesList.size());
        phonesList.forEach(phones1 -> {
            phones1.setState(Phones.STATE_EXCLUDED);
            updatePhone(phones1);
            logger.trace("Excluded phone: {}, people: {}", phones1.getPhone(), phones1.getPeople());
        });
    }
}
