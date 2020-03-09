package olnow.phmobile;

import com.linuxense.javadbf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static java.lang.StrictMath.round;

public class PhonesMobile {
    private PhonesServices phones = new PhonesServices();
    private PeopleServices people = new PeopleServices();
    private PhoneCashServices phoneCashServices = new PhoneCashServices();
    private PhoneDetailServices phoneDetailServices = new PhoneDetailServices();
    private byte skipLinesPhones = 0;
    private String separator = ";";
    private String templateSeparator = ";";
    private String phonesTemplate = "phone;;contract;fio;account;depart;title";
    private final static int CASH_TYPE_CSV = 1;
    private final static int CASH_TYPE_DBF = 2;
    private int cashType = CASH_TYPE_CSV;
    private Timestamp importMonth = null;
    private Map<String, Integer> contracts = new HashMap<>();
    private int fileReadProgress = 0;
    private String charset = null;
    private boolean csvBom = false;
    private boolean updateMonthInfo = true;

    public String getTemplate() {
        return phonesTemplate;
    }
    public void setTemplate(String template) {
        this.phonesTemplate = template;
    }
    public String getTemplateSeparator() {
        return templateSeparator;
    }
    public void setTemplateSeparator(String template) {
        this.templateSeparator = template;
    }
    public int getFileReadProgress() {
        return fileReadProgress;
    }

    public void setUpdateMonthInfo(boolean updateMonthInfo) {
        this.updateMonthInfo = updateMonthInfo;
    }

    Logger logger = LoggerFactory.getLogger(PhonesMobile.class);

    private Map<String, String> templates = new HashMap<String, String>() {
        {
            put("phoneCashTemplateCSV",
                    "phone;contract;;" +
                    "month;;;" +
                    "internationalcalls;" +
                    "longcalls;localcalls;localsms;gprs;" +
                    "internationalroamingcalls;internationalroamingsms;" +
                    "internationalgprsroaming;internationalroamingcash;" +
                    "russiaroamingcalls;russiaroamingsms;" +
                    "russiaroaminginet;russiaroamingtraffic;" +
                    "subscriptionfee;subscriptionfeeaddon;" +
                    "discounts;onetime;;" + //peni
                    "sum;;;" + //perenos, perenos
                    "vat;" +
                    "fullsum");
            put("phoneCashTemplateDBF",
                    ";" +
                    ";" +
                    "contract;" +
                    ";" +
                    "phone;" +
                    ";" +
                    "subscriptionfeeaddon;" +
                    "subscriptionfee;" +
                    "localcalls;" +
                    "longcalls;" +
                    "internationalcalls;" +
                    "localsms;" +
                    "gprs;" +
                    "internationalroamingcalls;" +
                    "russiaroamingcalls;" +
                    ";" +
                    ";" +
                    ";" +
                    ";" +
                    ";" +
                    "sum");
            put("phoneDetailTemplateDBF",
                    "" +
                    ";" + //scala
                    ";" + //division
                    "contract;" + //ban
                    "phone;" + //SUBSCRIBER
                    "tariff;" +
                    "date;" +
                    "time;" +
                    "callcategory;" + //SERVICE
                    "description;" +
                    "duration;" +
                    "charge;" +
                    "otherpart;" +
                    "roaming");
            put("phoneDetailTemplateCSV",
                    "contract;" +
                    ";" + //group
                    "phone;" +
                    "date;" +
                    "time;" +
                    ";" + //duration hh:mm:ss
                    "duration_minutes;" + //Длительность округленная до минут
                    "charge;" +
                    "source;" + //Инициатор звонка" +
                    "destanation;" + //Принимающий номер" +
                    ";" + //Описание действия" +
                    "description;" + //Описание услуги" +
                    "callcategory;" + //Тип услуги" +
                    ";" + //Номер базовой станции" +
                    ";" + //Объем в МB" +
                    "roaming"); //Описание провайдера");
        }};

    public void log(String message) {}

    public void selectTemplate(String templateName) {
        setTemplate(templates.get(templateName));
    }

    public String getTemplate(String templateName) {
        return templates.get(templateName);
    }


    public void setCashTypeDBF() {
        cashType = CASH_TYPE_DBF;
    }

    public void setCashTypeCSV() {
        cashType = CASH_TYPE_CSV;
    }

    public void setImportMonth(Timestamp month) {
        importMonth = month;

    }

    public boolean isCSVBom() {
        return csvBom;
    }

    public void setCSVBom(boolean csvBom) {
        this.csvBom = csvBom;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public PhonesMobile() {
        contracts.put("395846009", CASH_TYPE_CSV);
        contracts.put("512710831", CASH_TYPE_DBF);
    }

    public boolean checkContract(String cont) {
        if (cont == null || cont.isEmpty())
            return false;
        if (contracts.containsKey(cont) && contracts.get(cont).equals(cashType))
            return true;
        return false;
    }

    public void testCSVFile(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
        String line = reader.readLine();
        int i = 0;
        while (line != null && i< 20) {
            i++;
            log(line);
            line = reader.readLine();
        }

    }

    public void readCsvFile(File csv_file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csv_file));
            String line = reader.readLine();
            byte skipped = 0;
            boolean endfile = false;
            while (line != null) {
                if (skipped < skipLinesPhones) {
                    skipped++;
                    continue;
                }
                String newline;
                if (line.lastIndexOf(phones.getTemplateSeparator()) == line.length() - 1 || endfile) {
                    phones.addParce(line, separator, phonesTemplate);
                    line = reader.readLine();
                    continue;
                }
                newline = reader.readLine();
                if (newline == null) {
                    endfile = true;
                } else
                    line = line.concat(newline);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void writeCsvFile(File file, ArrayList<Object[]> data) {
        BufferedWriter writer = null;
        try {
            if (data == null || file == null)
                return;

            writer = new BufferedWriter(new FileWriter(file));
            if (isCSVBom())
                writer.write('\ufeff');
            String strToWrite = "", coma = "";
            for (Object[] objArray: data) {
                strToWrite = "";
                coma = "";
                for (Object obj: objArray) {
                    if (!strToWrite.isEmpty())
                        coma=";";
                    String value = null;
                    if (obj != null)
                        if (obj.toString().contains("."))
                            value = obj.toString().replaceAll("\\.",",");
                        else
                            value = obj.toString();
                    strToWrite = strToWrite + coma + value;

                }
                writer.write(strToWrite);
                writer.newLine();
            }
            writer.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Map<String, String> parceString(String line, String separator, String template, String templateseparator) {
        try {
            if (line == null) return null;
            String[] values = line.split(separator);
            String[] template_values = template.split(templateseparator);
            if (values.length < template_values.length)
                values = Arrays.copyOf(values, template_values.length);
            Map<String, String> templatemap = new HashMap<>();
            for (int i = 0; i < template_values.length; i++) {
                if (template_values[i] != null && !template_values[i].equals(""))
                    if (values[i] != null)
                        templatemap.put(template_values[i], values[i].trim());
                    else
                        templatemap.put(template_values[i], null);
            }
            if (!PhonesServices.isNumeric(templatemap.get("phone"))
                || templatemap.get("phone").length() != Phones.PHONE_LEN
                || !templatemap.get("phone").startsWith("9"))
                return null;
            if (templatemap.get("contract") != null &&
                    !templatemap.get("contract").isEmpty() &&
                    !checkContract(templatemap.get("contract")))
                return null;

            return templatemap;
        }
        catch (Exception e) {
            logger.error(this.getClass().getName(), e);
            System.out.println("PhonesMobile: parseString: " + e.toString());
            return null;
        }
    }

    private Map<String, String> parceDBFRow(Object[] row, String separator, String template, String templateseparator) {
        try {
            if (row == null) return null;
            //String[] values = line.split(separator);
            String[] template_values = template.split(templateseparator);
            Map<String, String> templatemap = new HashMap<>();
            for (int i = 0; i < template_values.length; i++) {
                if (template_values[i] != null && !template_values[i].equals("")) {
                    //DBFField field = (DBFField) row[i];
                    //if (row[i].toString().contains("SMS"))
                    //    System.out.println(row[i]);
                    if (row[i].getClass() == Date.class) {
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                        templatemap.put(template_values[i], format.format((Date) row[i]));
                    }
                    //if (field.getType() != DBFDataType.DATE) {
                    else
                        //templatemap.put(template_values[i], PhonesServices.trimSymvols(row[i].toString()));
                        templatemap.put(template_values[i], row[i].toString());
                    //}
                }
            }
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            if (importMonth != null)
                date.setTime(importMonth.getTime());
            if (templatemap.containsKey("month") &&
                    (templatemap.get("month") == null ||
                            templatemap.get("month").isEmpty())) {
                templatemap.replace("month", format.format(date));
            }
            else
                templatemap.put("month", format.format(date));
            if (!PhonesServices.isNumeric(templatemap.get("phone"))
                    || templatemap.get("phone").length() != Phones.PHONE_LEN
                    || !templatemap.get("phone").startsWith("9"))
                return null;
            if (templatemap.get("contract") != null &&
                    !templatemap.get("contract").isEmpty() &&
                    !checkContract(templatemap.get("contract")))
                return null;
            return templatemap;
        }
        catch (Exception e) {
            System.out.println("PhonesMobile:parceDBFRow:" + e.toString());
            log("PhonesMobile:parceDBFRow:" + e.toString());
            return null;
        }
    }

    public ReadFileResult readCsvFile(File csv_file, Method method, Object obj) throws FileNotFoundException {
        return readCsvFile(new FileInputStream(csv_file.getPath()), method, obj);
    }

    public ReadFileResult readCsvFile(InputStream instream, Method method, Object obj) {
        int recordCount;
        int currentRecord = 0;
        ReadFileResult readFileResult;
        try {
            fileReadProgress = 0;
            BufferedReader reader;
            if (charset != null)
                reader = new BufferedReader(new InputStreamReader(instream, charset));
            else
                reader = new BufferedReader(new InputStreamReader(instream));
            recordCount = instream.available();
            readFileResult = new ReadFileResult(recordCount, 0);
            String line = reader.readLine();
            byte skipped = 0;
            boolean endfile = false;
            while (line != null) {
                currentRecord += line.length();
                fileReadProgress = (int)round((double)currentRecord / recordCount * 100.0);
                //log(fileReadProgress + ": " + line);
                if (skipped < skipLinesPhones) {
                    skipped++;
                    continue;
                }
                String newline;
                boolean compare;
                if ((compare = (phonesTemplate.lastIndexOf(templateSeparator) == phonesTemplate.length()-1)
                    &&(line.lastIndexOf(templateSeparator) == line.length() - 1))
                    || endfile || !compare) {
                    Map <String, String> parseRes = parceString(line, separator, phonesTemplate, templateSeparator);
                    if (updateMonthInfo && readFileResult.getDate() == null) {
                        try {
                            Method methodGetMonth = obj.getClass().getDeclaredMethod("getMonth", Map.class);
                            readFileResult.setDate((Timestamp) methodGetMonth.invoke(obj, parseRes));
                        }
                        catch (NoSuchMethodException e) {
                            logger.error("[readCsvFile] invoke getMonth()", e);
                        }
                    }
                    try {
                        method.invoke(obj, parseRes);
                    }
                    catch (Exception e) {
                        System.out.println("PhonesMobile:readCSVFile: method invoke: " + e.toString());
                    }
                    line = reader.readLine();
                    continue;
                }
                newline = reader.readLine();
                if (newline == null) {
                    endfile = true;
                } else
                    line = line.concat(newline);
            }
        } catch (Exception e) {
            logger.error("[readCsvFile] object: {}, method: {} ", method.getName(), obj.getClass().getName(), e);
            System.out.println("PhonesMobile:readCSVFile (Reflection): " + e.toString());
            fileReadProgress = 0;
            return null;
        }
        if (readFileResult != null)
            readFileResult.setRecordWrite(currentRecord);
        fileReadProgress = 0;
        return readFileResult;
    }

    public ReadFileResult readCashCSV(InputStream csvStream, String templateName) {
        setCashTypeCSV();
        if (templateName == null || templateName.equals(""))
            return null;
        setTemplate(templates.get(templateName));
        try {
            Method method = phoneCashServices.getClass().getDeclaredMethod("addPhoneCash", Map.class);
            return readCsvFile(csvStream, method, phoneCashServices);
        }
        catch (Exception e) {
            System.out.println("ReadCashCSV: " + e.toString());
            log("ReadCashDBF: " + e.toString());
        }
        return null;
    }

    public ReadFileResult readCSV(InputStream csvStream, Object object, String methodName, String templateName) {
        setCashTypeCSV();
        ImportTemplateServices importTemplateServices = new ImportTemplateServices();
        //PhonesServices phonesServices = new PhonesServices();
        if (templateName == null || templateName.isEmpty() ||
            methodName == null || methodName.isEmpty() ||
            object == null)
            return null;

        ImportTemplate importTemplate = importTemplateServices.findName(ImportTemplate.class, templateName);
        if (importTemplate == null)
            return null;
        setTemplate(importTemplate.getTemplate());
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, Map.class);
            return readCsvFile(csvStream, method, object);
        }
        catch (Exception e) {
            logger.error(this.getClass().getName(), e);
            System.out.println("ReadCSV: " + e.toString());
            log("ReadCSV: " + e.toString());
        }
        return null;
    }

    public void readPhonesTariffCSV2(InputStream csvStream, String templateName) {
        setCashTypeCSV();
        ImportTemplateServices importTemplateServices = new ImportTemplateServices();
        PhonesServices phonesServices = new PhonesServices();
        if (templateName == null || templateName.isEmpty())
            return;
        /*if (methodName == null || methodName.isEmpty())
            return;
        if (className == null)
            return;
        */
        ImportTemplate importTemplate = importTemplateServices.findName(ImportTemplate.class, templateName);
        if (importTemplate == null)
            return;
        setTemplate(importTemplate.getTemplate());
        try {
            Method method = phonesServices.getClass().getDeclaredMethod("addPhonesTariff", Map.class);
            readCsvFile(csvStream, method, phonesServices);
        }
        catch (Exception e) {
            logger.error(this.getClass().getName(), e);
            System.out.println("ReadCashCSV: " + e.toString());
            log("ReadCashDBF: " + e.toString());
        }
    }

    public ReadFileResult readPhonesTariffCSV(InputStream csvStream, String templateName) {
        PhonesServices phonesServices = new PhonesServices();
        return readCSV(csvStream, phonesServices, "addPhonesTariff", "phonesTariff");
    }

    public ReadFileResult readServicesCSV(InputStream csvStream, String templateName) {
        ServiceServices serviceServices = new ServiceServices();
        return readCSV(csvStream, serviceServices, "addService", "phonesTariff");
    }



    public ReadFileResult readDBFFile(File dbfFile, Method method, Object obj) {
        try {
            return readDBFFile(new FileInputStream(dbfFile.getPath()), method, obj);
        }
        catch (Exception e) {
            System.out.println("redDBFFile: " + e.toString());
            return null;
        }
    }

    public ReadFileResult readDBFFile(InputStream dbfStream, Method method, Object obj) {
        fileReadProgress = 0;
        int recordCount = 0;
        int currentRecord = 0;
        ReadFileResult readFileResult = null;
        DBFReader dbfReader = null;
        try {
            dbfReader = new DBFReader(dbfStream);
            //dbfReader.setTrimRightSpaces(false);
            int numberOfFileds = dbfReader.getFieldCount();
            recordCount = dbfReader.getRecordCount();
            if (recordCount <= 0) {
                log("readDBFFile recordCount:0");
                return null;
            }
            readFileResult = new ReadFileResult(recordCount, 0);
            Map<String, String> emptyData = new HashMap<>();
            try {
                for (int i = 0; i < numberOfFileds; i++) {
                    DBFField dbfField = dbfReader.getField(i);
                    emptyData.put(dbfField.getName(), null);
                }
            }
            catch (Exception e) {
                System.out.println("readDBFFile: fill emptyData: " + e.getCause().toString() + e.toString());
                log("readDBFFile: fill emptyData: " + e.getCause().toString() + e.toString());
            }
            Object[] rowObjects;
            while ((rowObjects = dbfReader.nextRecord()) != null) {
                //double progress = (double)currentRecord / recordCount * 100;
                //log("readDBFFile: " + currentRecord + ": progress: " + fileReadProgress + ": " + progress);
                currentRecord ++;
                fileReadProgress = (int)round((double)currentRecord / recordCount * 100);
                Map<String, String> data = new HashMap<String, String>(emptyData);
                    Map <String, String> parseRes = parceDBFRow(rowObjects, separator, phonesTemplate, templateSeparator);
                    if (updateMonthInfo && readFileResult.getDate() == null) {
                        try {
                            Method methodGetMonth = obj.getClass().getDeclaredMethod("getMonth", Map.class);
                            readFileResult.setDate((Timestamp) methodGetMonth.invoke(obj, parseRes));
                        }
                        catch (NoSuchMethodException e) {
                            logger.error("[readCsvFile] invoke getMonth()", e);
                        }
                    }
                    try {
                        method.invoke(obj, parseRes);
                    }
                    catch (Exception e) {
                        logger.error("[readDBFFile] method {} invoke with params ({})", method.getName(), parseRes,  e);
                        //System.out.println("PhonesMobile:readDBFFile: method invoke: " + e.toString());
                    }
            }
        } catch (Exception e) {
            logger.error("[readDBFFile] read exception", e);
            System.out.println("PhonesMobile:readDBFFile: read exception: " + e.toString());
        }
        finally {
            DBFUtils.close(dbfReader);
        }
        if (readFileResult != null)
            readFileResult.setRecordWrite(currentRecord);
        fileReadProgress = 0;
        return readFileResult;
    }

    public ReadFileResult readCashDBF(File dbf_file, Timestamp month, String templateName) {
        try {
            return readCashDBF(new FileInputStream(dbf_file.getPath()), month, templateName);
        }
        catch (Exception e) {
            System.out.println("readCashDBF: " + e.toString());
            log("ReadCashDBF: " + e.toString());
            return null;
        }
    }

    public ReadFileResult readCashDBF(InputStream dbf_stream, Timestamp month, String templateName) {
        if (month == null )
            return null;
        setCashTypeDBF();
        setImportMonth(month);
        setTemplate(templates.get(templateName));
        try {
            Method method = phoneCashServices.getClass().getDeclaredMethod("addPhoneCash", Map.class);
            return readDBFFile(dbf_stream, method, phoneCashServices);
        }
        catch (Exception e) {
            System.out.println("ReadCashDBF: " + e.toString());
            log("ReadCashDBF: " + e.toString());
            return null;
        }
    }

    public ReadFileResult readDetailDBF(InputStream dbf_stream, String templateName) throws NoSuchMethodException {
        setCashTypeDBF();
        setTemplate(templates.get(templateName));
        //Batch import in one transaction
        phoneDetailServices.openSession(); // open
        phoneDetailServices.setCommitOnClose(true);
        ReadFileResult readFileResult = null;
        try {
            Method method = phoneDetailServices.getClass().getDeclaredMethod("addPhoneDetail", Map.class);
            readFileResult = readDBFFile(dbf_stream, method, phoneDetailServices);
            phoneDetailServices.commit();
        }
        catch (NoSuchMethodException e) {
            System.out.println("ReadDetailDBF: " + e.toString());
            log("ReadDetailDBF: " + e.toString());
            throw e;
        }
        finally {
            phoneDetailServices.closeSession(); //close
            phoneDetailServices.setCommitOnClose(false);
        }
        return readFileResult;
    }

    public ReadFileResult readDetailCSV(InputStream inputStream, String templateName) throws NoSuchMethodException {
        setCashTypeCSV();
        setTemplate(templates.get(templateName));
        //Batch import in one transaction
        phoneDetailServices.openSession();
        phoneDetailServices.setCommitOnClose(true);
        ReadFileResult readFileResult = null;
        try {
            Method method = phoneDetailServices.getClass().getDeclaredMethod("addPhoneDetail", Map.class);
            readFileResult = readCsvFile(inputStream, method, phoneDetailServices);
            phoneDetailServices.commit();
        }
        catch (NoSuchMethodException e) {
            System.out.println("ReadDetailCSV: " + e.toString());
            log("ReadDetailSCV: " + e.toString());
            throw e;
        }
        finally {
            phoneDetailServices.closeSession(); //close
            phoneDetailServices.setCommitOnClose(false);
        }
        return readFileResult;
    }


    public ArrayList<Phones> checkADAllPeople(LdapUtils ldap) {
        ArrayList<Phones> inactivePhones = new ArrayList<Phones>();
        if (ldap != null && ldap.isLdapConnected()) {
            ArrayList<Phones> allphones = phones.getPhones();
            allphones.forEach(ph -> {
                if (ph.getPeople() != null &&
                        ph.getPeople().getServiceaccount() != People.SERVICE_ACCOUNT &&
                        ph.getPeople().getAccount() != null &&
                        !ph.getPeople().getAccount().isEmpty()) {
                    //mTextAreaLog.appendText(ph.getPeople().getFio() + " :");
                    if (!ldap.loadAccoutInfo(ph.getPeople().getAccount()))
                        return;
                    int adstate = ldap.isActiveUser();
                    if (adstate != ph.getPeople().getADState()) {
                        ph.getPeople().setADState(adstate);
                        //people.updatePeople(ph.getPeople());
                    }
                    if ((ldap.getDepartment() != null && !ldap.getDepartment().isEmpty()) &&
                            (ph.getPeople().getDepartment() == null || ph.getPeople().getDepartment().isEmpty())) {
                        ph.getPeople().setDepartment(ldap.getDepartment());
                    }
                    if ((ldap.getTitle() != null && !ldap.getTitle().isEmpty()) &&
                            (ph.getPeople().getPosition() == null || ph.getPeople().getPosition().isEmpty())) {
                        ph.getPeople().setPosition(ldap.getTitle());
                    }
                    people.updatePeople(ph.getPeople());
                    if (adstate != LdapUtils.ACCOUNT_ENABLED)
                        inactivePhones.add(ph);
                    //if (ldap.getTitle() != null && !ldap.getTitle().isEmpty())

                        //mTextAreaLog.appendText(ph.getPeople().getAccount() + " state is: " + adstate + "\n");
                } else {
                    //mTextAreaLog.appendText(ph.getPhone() + ": FIO - null\n");
                }
            });
        }
        if (inactivePhones.size() > 0)
            return inactivePhones;
        else return null;
    }

    public void checkADPeople(LdapUtils ldap, People p) {
        if (ldap != null && ldap.isLdapConnected()) {
            if (p != null && p.getServiceaccount()!= People.SERVICE_ACCOUNT) {
                int adstate = ldap.isActiveUser(p.getAccount());
                if (adstate != p.getADState()) {
                    p.setADState(adstate);
                    people.updatePeople(p);
                }
            }
        }
    }

    public static String getFirstUpper(String source) {
        if (source == null || source.isEmpty())
            return null;
        return source.substring(0,1).toUpperCase() + source.substring(1,source.length()).toLowerCase();
    }
}