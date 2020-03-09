package olnow.phmobile;

import org.hibernate.HibernateException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static java.lang.Math.*;

public class PhoneDetailServices {
    private PhoneDetailDAO phoneDetailDAO = new PhoneDetailDAO();
    private PhonesServices phonesServices = new PhonesServices();
    private HistoryServices historyServices = new HistoryServices();
    private CallCategoryServices callCategoryServices = new CallCategoryServices();
    private DescriptionServices descriptionServices = new DescriptionServices();
    private RoamingServices roamingServices = new RoamingServices();
    private TariffServices tariffServices = new TariffServices();
    private PhoneCashServices phoneCashServices = new PhoneCashServices();
    private Phones phone = null;
    private People people = null;
    private Timestamp olddate = null;
    private CallCategory callCategory = null;
    private Description description = null;
    private int progress = 0;
    private boolean commitOnClose = false;

    public void addPhoneDetail(PhoneDetail phoneDetail) {
        phoneDetailDAO.addPhoneDetail(phoneDetail);
    }
    public void updatePhoneDetail(PhoneDetail phoneDetail) {
        phoneDetailDAO.updatePhoneDetail(phoneDetail);
    }
    public PhoneDetail findPhoneDetail(int idphonedetail) {
        return phoneDetailDAO.findPhoneDetail(idphonedetail);
    }
    public ArrayList<PhoneDetail> getPhoneDetail() {
        return phoneDetailDAO.getPhoneDetail();
    }
    public int getProgress() { return progress; }
    public void openSession() { phoneDetailDAO.openSession(); }
    public void closeSession() { phoneDetailDAO.closeSession(); }
    public void commit() { phoneDetailDAO.commit(); }
    public void addPhoneDetailToCurrentSession(PhoneDetail phoneDetail) {
        phoneDetailDAO.addPhoneDetailToCurrentSession(phoneDetail);
    }

    public void setCommitOnClose(boolean commitOnClose) {
        this.commitOnClose = commitOnClose;
    }

    static final Logger logger = LoggerFactory.getLogger(PhoneDetailServices.class);

    private class TariffDetail {
        private int detailminutes = 0;
        private double detailcost = 0;
        //private boolean optimal = false;
        private Tariff tariff;

        public void addMinutes(double minutes) {
            this.detailminutes += round(minutes);
        }

        public void addCost(double cost) {
            this.detailcost += cost;
        }

        public int getDetailminutes() {
            return detailminutes;
        }

        public double getDetailcost() {
            return detailcost;
        }

        public Tariff getTariff() {
            return tariff;
        }

        public void setTariff(Tariff tariff) {
            this.tariff = tariff;
        }

    }

    private class TariffDetailYear {
        private double currentCash[];
        private ArrayList<TariffDetail>[] tariffDetails;// = new ArrayList<>(12);

        public ArrayList<TariffDetail> getTariffDetails(int index) {
            return tariffDetails[index];
        }

        public void setTariffDetails(int index, ArrayList<TariffDetail> tariffDetails) {
            this.tariffDetails[index] = tariffDetails;
        }

        public TariffDetailYear(int tariffCount) {
            tariffDetails = new ArrayList[tariffCount];
            for (int i = 0; i < tariffCount; i++)
                tariffDetails[i] = new ArrayList<>(12);
            currentCash = new double[12];
        }

        public TariffDetail getTariffDetailMonth(int index, int month) throws IndexOutOfBoundsException {
            if (tariffDetails[index].size() < month)
                for (int i = tariffDetails[index].size(); i < month; i++)
                    tariffDetails[index].add(new TariffDetail());
            return tariffDetails[index].get(month-1);
        }

        public double getCurrentCash(int month) {
            return currentCash[month-1];
        }

        public void setCurrentCash(int month, double currentCash) {
            this.currentCash[month-1] = currentCash;
        }

        public double[] getCurrentCash() {
            return currentCash;
        }

        public ArrayList<TariffDetail>[] getTariffDetails() {
            return tariffDetails;
        }
    }

    private class SumDetail {
        private Phones phone = null;
        //private Tariff tariff;
        private double currentCash = 0;
        private TariffDetail optimalTariff = null;
        private double economy = 0;
        private ArrayList<TariffDetail> tariffDetails;

        public Phones getPhone() {
            return phone;
        }

        public void setPhone(Phones phone) {
            this.phone = phone;
        }

        public SumDetail() {
            this.tariffDetails = new ArrayList<>();
        }

        public SumDetail(Phones phone) {
            //this.phone = new Phones(phone);
            this.phone = phone;
            this.tariffDetails = new ArrayList<>();
        }

        public SumDetail(Phones phone, int tariffCount) {
            //this.phone = new Phones(phone);
            this.phone = phone;
            this.tariffDetails = new ArrayList<>(tariffCount);
            for (int i = 0 ; i < tariffCount; i++) {
                TariffDetail tariffDetail1 = new TariffDetail();
                this.tariffDetails.add(tariffDetail1);
            }

        }

        public ArrayList<TariffDetail> getTariffDetails() {
            return tariffDetails;
        }

        public void setTariffDetails(ArrayList<TariffDetail> tariffDetails) {
            this.tariffDetails = tariffDetails;
        }

        public void selectOptimalTariff() {
            for (int i = 0; i < tariffDetails.size(); i++) {
                if (tariffDetails.get(i).getDetailcost() < currentCash) {
                    optimalTariff = tariffDetails.get(i);
                    economy = currentCash - optimalTariff.getDetailcost();
                }
            }
        }

        public void selectOptimalTariffYear() {
        }

        public TariffDetail getOptimalTariff() {
            return optimalTariff;
        }

        public double getEconomi() {
            return economy;
        }

        public double getCurrentCash() {
            return currentCash;
        }

        public void setCurrentCash(double currentCash) {
            this.currentCash = currentCash;
        }

        @Override
        public String toString() {
            return "SumDetail{" +
                    "phone=" + phone +
                    '}';
        }
    }

    private class SumDetailYear {
        private Phones phone;
        //private ArrayList<SumDetail> sumDetailsYear = new ArrayList(12);
        //private SumDetail sumDetail;
        private double currentCash = 0;
        private Tariff optimalTariff = null;
        private double economy = 0;
        private ArrayList<Tariff> tariffs = new ArrayList<>();
        private TariffDetailYear tariffDetailYears;

        public SumDetailYear(Phones phone, int tarrifsCount) {
            this.phone = phone;
            tariffDetailYears = new TariffDetailYear(tarrifsCount);
            //for (int i = 0; i < 12; i++) {
            //    this.phone = phone;
            //    TariffDetailYear tariffDetailYear = new TariffDetailYear();
            //    tariffDetailYears.add(tariffDetailYear);
            //    //sumDetailsYear.add(sumDetail);
            //}
        }

        public Phones getPhone() {
            return phone;
        }

        public void setPhone(Phones phone) {
            this.phone = phone;
        }

        public double getCurrentCash() {
            return currentCash;
        }

        public void setCurrentCash(double currentCash) {
            this.currentCash = currentCash;
        }

        public Tariff getOptimalTariff() {
            return optimalTariff;
        }

        public void setOptimalTariff(Tariff optimalTariff) {
            this.optimalTariff = optimalTariff;
        }

        public double getEconomy() {
            return economy;
        }

        public void setEconomy(double economy) {
            this.economy = economy;
        }

        public ArrayList<Tariff> getTariffs() {
            return tariffs;
        }

        public void setTariffs(ArrayList<Tariff> tariffs) {
            this.tariffs = tariffs;
        }

        public TariffDetailYear getTariffDetailYears() {
            return tariffDetailYears;
        }

        public void setTariffDetailYears(TariffDetailYear tariffDetailYears) {
            this.tariffDetailYears = tariffDetailYears;
        }

        public void selectOptimalTariff() {
            int cash = 0;
            for (int month = 1; month <= 12; month ++)
                cash += tariffDetailYears.getCurrentCash(month);
            currentCash = cash;
            for (int i = 0; i < tariffs.size(); i++) {
                cash = 0;
                for (int k = 0; k < tariffDetailYears.getTariffDetails(i).size(); k ++)
                    cash += tariffDetailYears.getTariffDetailMonth(i, k+1).getDetailcost();
                if ((currentCash - cash) > economy) {
                    optimalTariff = tariffs.get(i);
                    economy = currentCash - cash;
                }
            }
        }

        public void addTariffCost() {
            try {
                for (int i = 0; i < tariffs.size(); i++)
                    for (int k = 0; k < tariffDetailYears.getTariffDetails(i).size(); k++)
                        if (tariffDetailYears.getCurrentCash(k + 1) > 0)
                            tariffDetailYears.getTariffDetailMonth(i, k + 1).addCost(tariffs.get(i).getCost());
            }
            catch (Exception e) {
                logger.error(this.getClass().getName(), e);
                //throw e;
            }
        }

    }

    public Timestamp getMonth(Map<String, String> maps) {
        if (maps == null)
            return null;
        if (maps.get("date") != null && !maps.get("date").isEmpty() &&
            maps.get("time") != null && !maps.get("time").isEmpty())
            try {
                DateFormat parser = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date timeParsed = parser.parse(maps.get("date").replaceAll("/", ".") +
                        " " + maps.get("time"));
                return new Timestamp(timeParsed.getTime());
            }
            catch (Exception e) {
                logger.info("[getMonth] date: {}, time: {}", maps.get("date"), maps.get("time"), e);
            }
        return null;
    }

    public void addPhoneDetail(Map<String, String> maps) {
        if (maps == null)
            return;
        //PhoneCash phoneCashReflex = new PhoneCash();
        PhoneDetail phoneDetail = new PhoneDetail();
        Method methodSet;
        //Phones phone;

        String oldphone = null;
        if (phone != null)
            oldphone = phone.getPhone();
        if (phone == null || !phone.getPhone().equals(maps.get("phone")))
            if ((phone = phonesServices.findPhone(maps.get("phone"))) == null) {
                phone = new Phones();
                phone.setPhone(maps.get("phone"));
                phone.setState(Phones.STATE_ACTIVE);
                phonesServices.addPhone(phone);
            }
        phoneDetail.setPhone(phone);
        //convert date
        Timestamp date = null;
        try {
            DateFormat parser = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date timeParsed = parser.parse(maps.get("date").replaceAll("/", ".") + " " + maps.get("time"));
            date = new Timestamp(timeParsed.getTime());

            phoneDetail.setDatetime(date);
        } catch (Exception e) {
            System.out.println("PhoneDetailServices:addPhoneDetail: " + e.toString());
        }
        //People people;
        if (date != null) {
            if (people == null || !((phone.getPhone().equals(oldphone)) && olddate != null &&
                    olddate.toLocalDateTime().getYear() == date.toLocalDateTime().getYear() &&
                    olddate.toLocalDateTime().getMonth() == date.toLocalDateTime().getMonth() &&
                    olddate.toLocalDateTime().getDayOfMonth() == date.toLocalDateTime().getDayOfMonth()))
                people = historyServices.getPeopleAtDate(phone, date);
            if (people != null)
                phoneDetail.setPeople(people);

        }

        int direction = 0;
        if (maps.get("source") != null && !maps.get("source").isEmpty()) {
            if (maps.get("source").equals(maps.get("phone"))) {
                maps.put("otherpart", maps.get("destanation"));
                //maps.put("direction", String.valueOf(Reference.DIRECTION_OUT));
                direction = Description.DIRECTION_OUT;
            }
            else {
                maps.put("otherpart", maps.get("source"));
                //maps.put("direction", String.valueOf(Reference.DIRECTION_IN));
                direction = Description.DIRECTION_IN;
            }
        }

        olddate = date;
        //CallCategory callCategory = null;
        if (maps.get("callcategory") != null && !maps.get("callcategory").isEmpty()) {
            if (callCategory == null ||
                    (callCategory != null && !callCategory.getCallcategory().equals(maps.get("callcategory"))))
                callCategory = callCategoryServices.findOrCreateCallCategory(maps.get("callcategory"));
        }
        phoneDetail.setCallCategory(callCategory);

        if (maps.get("description") != null && !maps.get("description").isEmpty()) {
            if (description == null ||
                    (description != null && !description.getName().equals(maps.get("description"))))
                description = descriptionServices.findOrCreate(maps.get("description"), direction);
        }
        phoneDetail.setDescription(description);

        if (maps.get("duration_minutes") != null && !maps.get("duration_minutes").isEmpty()) {
            try {
                maps.put("duration", String.valueOf(
                        (int) Double.parseDouble(
                                maps.get("duration_minutes").replaceAll(",", ".")) * 60));
            }
            catch (Exception e) {
                System.out.println("addPhoneDetail: parse duration_minutes exception:" + e.toString());
            }
        }

        Roaming roaming = null;
        if (maps.get("roaming") != null && !maps.get("roaming").isEmpty())
            roaming = roamingServices.findOrCreateRoaming(maps.get("roaming"));
        phoneDetail.setRoaming(roaming);

        for (Map.Entry<String, String> res : maps.entrySet()) {
            switch (res.getKey()) {
                case "phone":
                case "people":
                case "contract":
                case "date":
                case "time":
                case "roaming":
                case "month":
                case "source":
                case "destanation":
                case "duration_minutes":
                case "callcategory":
                    continue;
                default:
                    try {
                        //Get field type
                        Field field = phoneDetail.getClass().getDeclaredField(res.getKey());
                        //Parse double
                        if (field.getType().equals(double.class)) {
                            try {
                                methodSet = phoneDetail.getClass().
                                        getDeclaredMethod("set" + PhonesMobile.getFirstUpper(res.getKey()), double.class);
                                //phoneCashReflex.getClass().getDeclaredMethod("", double.class);
                                double doubleRes = 0;
                                try {

                                    doubleRes = Double.parseDouble(res.getValue().replaceAll(",", "."));
                                } catch (Exception e) {
                                    System.out.println("PhoneDetailServices:addPhoneDetail: double convert: " + e.toString());
                                }

                                methodSet.invoke(phoneDetail, doubleRes);
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                System.out.println("PhoneDetailServices:addPhoneDetail: " + e.toString());
                            }
                        }
                        // Parse int
                        if (field.getType().equals(int.class)) {
                            try {
                                methodSet = phoneDetail.getClass().
                                        getDeclaredMethod("set" + PhonesMobile.getFirstUpper(res.getKey()), int.class);
                                //phoneCashReflex.getClass().getDeclaredMethod("", double.class);
                                int intRes = 0;
                                try {

                                    intRes = Integer.parseInt(res.getValue());
                                } catch (Exception e) {
                                    System.out.println("PhoneDetailServices:addPhoneDetail: int convert: " + e.toString());
                                }

                                methodSet.invoke(phoneDetail, intRes);
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                System.out.println("PhoneDetailServices:addPhoneDetail: " + e.toString());
                            }
                        }
                        // Operate String
                        if (field.getType().equals(String.class)) {
                            try {
                                methodSet = phoneDetail.getClass().
                                        getDeclaredMethod("set" + PhonesMobile.getFirstUpper(res.getKey()), String.class);
                                //phoneCashReflex.getClass().getDeclaredMethod("", double.class);
                                methodSet.invoke(phoneDetail, res.getValue());
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                System.out.println("PhoneDetailServices:addPhoneDetail: " + e.toString());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("getField exception: " + e.toString());
                    }

            }

        }
        if (commitOnClose)
            addPhoneDetailToCurrentSession(phoneDetail);
        else
            phoneDetailDAO.addPhoneDetail(phoneDetail);
    }

    public ArrayList<PhoneDetail> getPhoneDetailMonth(int year, int month) {
        return phoneDetailDAO.getPhoneDetailMonth(year, month, 0);
    }

    public ArrayList<PhoneDetail> getPhoneDetailMonthIn(int year, int month) {
        return phoneDetailDAO.getPhoneDetailMonth(year, month, 1);
    }

    public ArrayList<PhoneDetail> getPhoneDetailLocalMonth(int year, int month) {
        return phoneDetailDAO.getPhoneDetailLocalMonth(year, month);
    }

    public Object[] analizePhoneDetailTariffsMonth(int year, int month) throws HibernateException {
        ArrayList<PhoneDetail> phoneDetails = getPhoneDetailLocalMonth(year, month);
        if (phoneDetails == null)
            return null;
        progress = 10;
        int position =0;
        boolean newphone = true;
        ArrayList<Tariff> tariffs = tariffServices.getSorted();
        if (tariffs == null)
            return null;
        //System.out.println(tariffs.size());
        ArrayList<SumDetail> sumDetails = new ArrayList<>();
        SumDetail sum = null; // = new SumDetail();
        for (PhoneDetail phoneDetailsRow : phoneDetails) {
            position ++;
            progress = (int)((double)position / (double)phoneDetails.size() * 100.0);
            //System.out.println(position + ": " + progress);
            if (sum == null || !(sum != null && phoneDetailsRow.getPhone().equals(sum.getPhone()))) {
                if (sum != null) {
                    //System.out.println(sum);
                    sum.selectOptimalTariff();
                    sumDetails.add(sum);
                }
                sum = new SumDetail(phoneDetailsRow.getPhone(), tariffs.size());
                newphone = true;

                //Load phone cash
                String[] localCalls = {"getSubscriptionfeeaddon",
                        "getSubscriptionfee",
                        "getLocalcalls",
                        "getLongcalls",
                        "getLocalsms",
                        "getGprs",
                        "getRussiaroamingcalls"};
                PhoneCash phoneCash = phoneCashServices.getPhoneCashDetail(
                        year, month, phoneDetailsRow.getPhone().getPhone());
                double currentCash = 0;
                for (String name : localCalls) {
                    try {
                        Method method = phoneCash.getClass().getDeclaredMethod(name);
                        double res = (double)method.invoke(phoneCash);
                        currentCash +=res;
                    }
                    catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        System.out.println("analizePhoneDetailTariffsMonth: " + e.toString());
                    }

                }
                sum.setCurrentCash(currentCash);
            }

            for (int i = 0; i < tariffs.size(); i++) {
                if (newphone) {
                    sum.getTariffDetails().get(i).setTariff(tariffs.get(i));
                    sum.getTariffDetails().get(i).addCost(
                            sum.getTariffDetails().get(i).getTariff().getCost()
                    );
                }
                //if (phoneDetailsRow.getCallCategory().getDirection() == CallCategory.DIRECTION_OUT &&
                //    (phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_CALLS ||
                //     phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_SMS ||
                //     phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_MMS )) {
                if (sum.getTariffDetails().get(i).getDetailminutes() >= sum.getTariffDetails().get(i).getTariff().getMinutes())
                    sum.getTariffDetails().get(i).addCost(
                            (double)phoneDetailsRow.getDuration()/60*
                                    sum.getTariffDetails().get(i).getTariff().getMinutescost());
                sum.getTariffDetails().get(i).addMinutes((double)phoneDetailsRow.getDuration()/60);
                if (phoneDetailsRow.getDuration() == 0 && phoneDetailsRow.getCallCategory().getCalltype() == Reference.CALLTYPE_LOCAL_SMS)
                    sum.getTariffDetails().get(i).addCost(
                            sum.getTariffDetails().get(i).getTariff().getSmscost());
                //}
            }
            newphone = false;
            //money += phoneDetailsRow.getCharge();
            //min += phoneDetailsRow.getDuration();
        }
        return sumDetails.toArray();
    }

    private SumDetailYear findSumDetailYear(ArrayList<SumDetailYear> array, Phones phone) {
        for (SumDetailYear sum : array) {
            if (sum.getPhone().equals(phone))
                return sum;
        }
        return null;
    }

    public Object[] analizePhoneDetailTariffsYearLocal(int year) throws Exception {
        //field year ignored
        try {
            progress = 0;
            int position = 0;
            boolean newphone = false;
            ArrayList<Tariff> tariffs = tariffServices.get();
            if (tariffs == null)
                return null;
            //ArrayList<SumDetail> sumDetails = new ArrayList<>();
            ArrayList<SumDetailYear> sumDetailsYear = new ArrayList<>();
            SumDetail sum = null; // = new SumDetail();
            SumDetailYear sumYear = null;
            SumDetailYear sumYearFind = null;
            //Count for last 12 month
            Calendar calendar = Calendar.getInstance();
            int curYear = calendar.get(Calendar.YEAR) - 1;
            int curMonth = calendar.get(Calendar.MONTH) + 1;
            for (int k = 0; k < 12; k++) {
                int month = curMonth + k;
                year = curYear;
                if (month > 12) {
                    month = month - 12;
                    year ++;
                }
                ArrayList<PhoneDetail> phoneDetails = getPhoneDetailLocalMonth(year, month);
                if (phoneDetails == null) {
                    // progress += (int) ((double) 100 / 12);
                    continue;
                }
                else {
                    position = 0;
                }
                //System.out.println(tariffs.size());
                for (PhoneDetail phoneDetailsRow : phoneDetails) {
                    position++;
                    // progress = (int) (((double) position / ((double) phoneDetails.size() * 12.0)) * 100.0);
                    progress = (int) ((((double) 100 / 12) * k ) + (((double) position / phoneDetails.size()) * (100 / 12)));
                    // logger.info("[analize] size: {}, pos: {}, progress: {}", phoneDetails.size(), position, progress);
                    if (sumYear == null ||
                            !(sumYear != null && phoneDetailsRow.getPhone().equals(sumYear.getPhone()))) {
                        if ((sumYearFind = findSumDetailYear(sumDetailsYear, phoneDetailsRow.getPhone())) == null) {
                            if (sumYear != null && newphone) {
                                //sumYear.selectOptimalTariff();
                                sumDetailsYear.add(sumYear);
                            }
                            //System.out.println("new phone");
                            sumYear = new SumDetailYear(phoneDetailsRow.getPhone(), tariffs.size());
                            newphone = true;

                            //System.out.println(sum);
                            //sum.selectOptimalTariffYear();
                            //sumDetailsYear.add(sumYear);
                        } else {
                            sumYear = sumYearFind;
                            newphone = false;
                            //sumYear.addTariffCost();
                            //sumYear.selectOptimalTariff();
                        }
                    }
                    if (sumYear.getPhone().getPeople() == null && phoneDetailsRow.getPeople() != null) {
                        sumYear.getPhone().setPeople(phoneDetailsRow.getPeople());
                    }
                    //sum = new SumDetail(phoneDetailsRow.getPhone(), 12);
                    //newphone = true;
                    if (sumYear.getTariffDetailYears().getCurrentCash(month) == 0) {
                        //System.out.println("init cahs");
                        //Load phone cash
                        String[] localCalls = {"getSubscriptionfeeaddon",
                                "getSubscriptionfee",
                                "getLocalcalls",
                                "getLongcalls",
                                "getLocalsms",
                                "getGprs",
                                "getRussiaroamingcalls"};
                        PhoneCash phoneCash = phoneCashServices.getPhoneCashDetail(
                                year, month, phoneDetailsRow.getPhone().getPhone());
                        if (phoneCash != null) {
                            double currentCash = 0;
                            for (String name : localCalls) {
                                try {
                                    Method method = phoneCash.getClass().getDeclaredMethod(name);
                                    double res = (double) method.invoke(phoneCash);
                                    currentCash += res;
                                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                    logger.error(this.getClass().getName(), e);
                                    System.out.println("analizePhoneDetailTariffsMonth: " + e.toString());
                                }

                            }
                            //sum.setCurrentCash(currentCash);
                            sumYear.getTariffDetailYears().setCurrentCash(month, currentCash);
                        }
                        //System.out.println("end init cahs");
                    }

                    if (newphone && sumYear.getTariffs().size() <= 0)
                        sumYear.setTariffs(tariffs);
                    //System.out.println("Begin count");
                    for (int i = 0; i < tariffs.size(); i++) {
                    /*if (newphone) {
                        //sum.getTariffDetails().get(i).setTariff(tariffs.get(i));
                        sumYear.getTariffs().get(month).addCost(
                                sum.getTariffDetails().get(month).getTariff().getCost()
                        );
                    }*/
                        //if (phoneDetailsRow.getCallCategory().getDirection() == CallCategory.DIRECTION_OUT &&
                        //    (phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_CALLS ||
                        //     phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_SMS ||
                        //     phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_MMS )) {
                        //System.out.println(i);
                        if (sumYear.getTariffDetailYears().getTariffDetailMonth(i, month).getDetailminutes() >=
                                tariffs.get(i).getMinutes())
                            sumYear.getTariffDetailYears().getTariffDetailMonth(i, month).addCost(
                                    (double) phoneDetailsRow.getDuration() / 60 *
                                            tariffs.get(i).getMinutescost());
                        sumYear.getTariffDetailYears().getTariffDetailMonth(i, month).addMinutes(
                                (double) phoneDetailsRow.getDuration() / 60);
                        if (phoneDetailsRow.getDuration() == 0 &&
                                phoneDetailsRow.getCallCategory().getCalltype() == Reference.CALLTYPE_LOCAL_SMS)
                            sumYear.getTariffDetailYears().getTariffDetailMonth(i, month).addCost(
                                    tariffs.get(i).getSmscost());
                        //}
                    }
                    //System.out.println("end count");
                    // newphone = false;
                    //money += phoneDetailsRow.getCharge();
                    //min += phoneDetailsRow.getDuration();
                }

            }
            if (sumDetailsYear == null)
                return null;
            for (SumDetailYear rec : sumDetailsYear) {
                rec.addTariffCost();
                rec.selectOptimalTariff();
            }
            return sumDetailsYear.toArray();
        }
        catch (Exception e) {
            logger.error(this.getClass().getName(), e);
            throw e;
        }
    }

    public Object[] analizePhoneDetailTariffsYear(int year) throws HibernateException {
        progress = 0;
        int position = 0;
        boolean newphone = false;
        ArrayList<Tariff> tariffs = tariffServices.get();
        if (tariffs == null)
            return null;
        //ArrayList<SumDetail> sumDetails = new ArrayList<>();
        ArrayList<SumDetailYear> sumDetailsYear = new ArrayList<>();
        SumDetail sum = null; // = new SumDetail();
        SumDetailYear sumYear = null;
        SumDetailYear sumYearFind = null;
        for (int month = 1; month <= 12; month ++) {
            ArrayList<PhoneDetail> phoneDetails = getPhoneDetailMonthIn(year, month);
            if (phoneDetails == null)
                continue;
            //System.out.println(tariffs.size());
            for (PhoneDetail phoneDetailsRow : phoneDetails) {
                position++;
                progress = (int) ((double) position / ((double) phoneDetails.size()* 12.0) * 100.0);
                //System.out.println(phoneDetailsRow.getPhone() + ": " + month);
                if (sumYear == null ||
                        !(sumYear != null && phoneDetailsRow.getPhone().equals(sumYear.getPhone()))) {
                    if ((sumYearFind = findSumDetailYear(sumDetailsYear, phoneDetailsRow.getPhone())) == null) {
                        if (sumYear != null && newphone) {
                            //sumYear.selectOptimalTariff();
                            sumDetailsYear.add(sumYear);
                        }
                        //System.out.println("new phone");
                        sumYear = new SumDetailYear(phoneDetailsRow.getPhone(), tariffs.size());
                        newphone = true;

                        //System.out.println(sum);
                        //sum.selectOptimalTariffYear();
                        //sumDetailsYear.add(sumYear);
                    } else {
                        sumYear = sumYearFind;
                        newphone = false;
                        //sumYear.addTariffCost();
                        //sumYear.selectOptimalTariff();
                    }
                }
                if (sumYear.getPhone().getPeople() == null && phoneDetailsRow.getPeople() != null) {
                    sumYear.getPhone().setPeople(phoneDetailsRow.getPeople());
                }
                //sum = new SumDetail(phoneDetailsRow.getPhone(), 12);
                //newphone = true;
                if (sumYear.getTariffDetailYears().getCurrentCash(month) == 0) {
                    //System.out.println("init cahs");
                    //Load phone cash
                    String[] localCalls = {"getSubscriptionfeeaddon",
                            "getSubscriptionfee",
                            "getLocalcalls",
                            "getLongcalls",
                            "getLocalsms",
                            "getGprs",
                            "getRussiaroamingcalls"};
                    PhoneCash phoneCash = phoneCashServices.getPhoneCashDetail(
                            year, month, phoneDetailsRow.getPhone().getPhone());
                    if (phoneCash != null) {
                        double currentCash = 0;
                        for (String name : localCalls) {
                            try {
                                Method method = phoneCash.getClass().getDeclaredMethod(name);
                                double res = (double) method.invoke(phoneCash);
                                currentCash += res;
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                logger.error(this.getClass().getName(), e);
                                System.out.println("analizePhoneDetailTariffsMonth: " + e.toString());
                            }

                        }
                        //sum.setCurrentCash(currentCash);
                        sumYear.getTariffDetailYears().setCurrentCash(month, currentCash);
                    }
                    //System.out.println("end init cahs");
                }

                if (newphone && sumYear.getTariffs().size() <= 0)
                    sumYear.setTariffs(tariffs);
                //System.out.println("Begin count");

                //Count cash
                switch (phoneDetailsRow.getCallCategory().getCalltype()) {
                    case Reference.CALLTYPE_LOCAL_CALLS:
                    case Reference.CALLTYPE_LOCAL_INTERNET:
                    case Reference.CALLTYPE_LOCAL_SMS:
                    case Reference.CALLTYPE_LOCAL_MMS:
                        break;
                    default:
                        break;
                }
                for (int i = 0; i < tariffs.size(); i++) {
                    /*if (newphone) {
                        //sum.getTariffDetails().get(i).setTariff(tariffs.get(i));
                        sumYear.getTariffs().get(month).addCost(
                                sum.getTariffDetails().get(month).getTariff().getCost()
                        );
                    }*/
                    //if (phoneDetailsRow.getCallCategory().getDirection() == CallCategory.DIRECTION_OUT &&
                    //    (phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_CALLS ||
                    //     phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_SMS ||
                    //     phoneDetailsRow.getCallCategory().getCalltype() == CallCategory.CALLTYPE_LOCAL_MMS )) {
                    //System.out.println(i);
                    if (sumYear.getTariffDetailYears().getTariffDetailMonth(i, month).getDetailminutes() >=
                            tariffs.get(i).getMinutes())
                        sumYear.getTariffDetailYears().getTariffDetailMonth(i, month).addCost(
                                (double) phoneDetailsRow.getDuration() / 60 *
                                        tariffs.get(i).getMinutescost());
                    sumYear.getTariffDetailYears().getTariffDetailMonth(i, month).addMinutes(
                            (double) phoneDetailsRow.getDuration() / 60);
                    //}
                }
                //System.out.println("end count");
                // newphone = false;
                //money += phoneDetailsRow.getCharge();
                //min += phoneDetailsRow.getDuration();
            }

        }
        if (sumDetailsYear == null)
            return null;
        for (SumDetailYear rec: sumDetailsYear) {
            rec.addTariffCost();
            rec.selectOptimalTariff();
        }
        return sumDetailsYear.toArray();
    }
}
