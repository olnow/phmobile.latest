package olnow.phmobile;

//import com.sun.jna.platform.win32.Advapi32Util;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
//import waffle.windows.auth.*;
//import waffle.windows.auth.impl.WindowsAuthProviderImpl;
//import waffle.windows.auth.impl.WindowsCredentialsHandleImpl;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

public class Controller {
    @FXML private TableView<Phones> mTablePhones;
    @FXML private TableView<PhoneInfo> mTablePhoneInfo;
    @FXML private TableView<History> mTableHistory;
    @FXML private TableColumn<Phones, String> mTableColumnPhone;
    @FXML private TableColumn<Phones, String> mTableColumnFIO;
    @FXML private TableColumn<PhoneInfo, String> mTableColumnPropertyName, mTableColumnPropertyValue;
    @FXML private TableColumn<History, String> mTableHistoryType;


    @FXML private TextField mTFieldFilterPhone, mTFieldFilterFIO;
    @FXML private Label mLabelRecCount, mLabelADWarning;
    @FXML private TextArea mTextAreaLog;
    @FXML private CheckBox mCheckInActivePeople, mCheckFreePhone;

    private PhonesMobile phmobile = new PhonesMobile();
    private PhonesServices phones = new PhonesServices();
    private PeopleServices people = new PeopleServices();
    private HistoryServices historySrv = new HistoryServices();
    private HistoryTypeServices histiryTypeSrv =  new HistoryTypeServices();
    private History activeHistory;
    private PhoneCashServices phoneCashServices = new PhoneCashServices();
    private PhoneDetailServices phoneDetailServices = new PhoneDetailServices();

    public final static String INFO_FIO = "FIO";
    private final String INFO_PHONE = "Phone";
    private final String INFO_ACCOUNT = "AD Account";
    private final String INFO_ADSTATE = "People AD State";
    private final String INFO_PONE_STATE = "Phone State";
    private final String INFO_SERVICE_ACCOUNT = "Service account";
    private final String INFO_DATE_START = "Date start";
    private final String INFO_DATE_END = "Date end";
    private final String INFO_DEPARTMENT = "Department";
    private final String INFO_POSITION = "Position";
    private String phoneCashTemplate = "phone;contract;;" +
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
            "fullsum";
    private String phoneCashTemplateDBF = ";" +
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
            "sum";
    private String phoneDetailTemplateDBF = "" +
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
            "roaming";

    private LdapUtils ldap;

    public class PhoneInfo {
        private String name;
        private String value;
        PhoneInfo(String name, String value) {
            this.name = name;
            this.value = value;
        }
        public void setName(String name) { this.name = name; }
        public void setValue(String value) { this.value = value; }
        public String getName() { return name; }
        public String getValue() { return value; }
        @Override
        public String toString() {
            return name;
        }
    }

    private FXMLLoader loadForm(String formName) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(formName));
            return loader;
    }

    private void showForm(FXMLLoader loader, int width, int height) {
        try {
            //Object resultForm;
            //resultForm = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent) loader.load(), width, height));
            stage.initOwner(mTablePhones.getScene().getWindow());
            //resultForm = loader.getController();
            stage.initModality(Modality.WINDOW_MODAL);
        }
        catch (Exception e) {
            System.out.println(e.toString());
            return;
        }
    }

    private Object loadAndShowForm(String formName, int width, int height) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(formName));

            Object resultForm;
            //resultForm = loadForm(formName, width, height);
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent) loader.load(), 200, 100));
            stage.initOwner(mTablePhones.getScene().getWindow());
            resultForm = loader.getController();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
            return resultForm;
        }
        catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private void updateTablePhones() {
        int index = mTablePhones.getSelectionModel().getSelectedIndex();
        ArrayList<Phones> phonesArray = phones.getPhones();
        if (phonesArray != null && !phonesArray.isEmpty()) {
            mTablePhones.getItems().setAll(phonesArray);
            mLabelRecCount.setText(Integer.toString(phonesArray.size()));
            if (index < 0 )
                index = 0;
            mTablePhones.getSelectionModel().select(index);
            onScrollPhones(mTablePhones.getSelectionModel().getSelectedItem());
        }
    }

    private void updateTableHistory() {
        Phones ph = mTablePhones.getSelectionModel().getSelectedItem();
        ArrayList<History> historyArray = historySrv.getHistory(ph);
        if (historyArray != null && !historyArray.isEmpty()) {
            mTableHistory.getItems().setAll(historyArray);
        }
        else mTableHistory.getItems().clear();
    }

    @FXML private void onActionAuth() {
        auth();

        mTextAreaLog.appendText(PhoneCashServices.getFirstUpper("defaults"));
    }

    private void auth() {
        /*
        IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
        try {

            IWindowsIdentity windowsIdentity = prov.logonDomainUser("", "", "");
            String str = windowsIdentity.getFqn();
            mTextAreaLog.appendText(str + "\n");
        }
        catch (Exception e) {
            mTextAreaLog.appendText(e.toString()+"\n");
        }

        try {
            IWindowsComputer computer = prov.getCurrentComputer();
            System.out.println(computer.getComputerName());
            System.out.println(computer.getJoinStatus());
            System.out.println(computer.getMemberOf());

            IWindowsCredentialsHandle credentialsHandle = WindowsCredentialsHandleImpl.getCurrent("Negotiate");
            String userName = Advapi32Util.getUserName();
            mTextAreaLog.appendText(userName + "\n");

            IWindowsIdentity identity = prov.logonUser("", "");
            mTextAreaLog.appendText("User identity: " + identity.getFqn()+"\n");
            for (IWindowsAccount group : identity.getGroups()) {
                mTextAreaLog.appendText(" " + group.getFqn() + " (" + group.getSidString() + ")\n");
            }
        }
        catch (Exception e) {
            mTextAreaLog.appendText(e.toString()+"\n");
        }*/
    }

    @FXML
    protected void initialize() {
        //auth();
        String sourcefile = Controller.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String path = "";
        if (!sourcefile.endsWith("/"))
            //path = sourcefile + "hibernate.xml";
        {
            int index = sourcefile.lastIndexOf("/");
            if (index >= 0)
                path = sourcefile.substring(0, index+1) + "hibernate.cfg.xml";
            System.out.println(sourcefile + "\n len: " + sourcefile.length() + "\n index: " + index + "\n");
        }

        System.out.println(path);
        //path = "C:/Programs/Projects/IdeaProjects/phmobile/out/artifacts/phmobile_jar/hibernate.cfg.xml";
        HibernateSessionFactoryUtil.setConfig(path);

        mTableColumnPhone.setCellFactory(TextFieldTableCell.<Phones>forTableColumn());
        mTableColumnPropertyValue.setCellFactory(TextFieldTableCell.<PhoneInfo>forTableColumn());

        mTablePhones.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {
                    onScrollPhones(newValue);
                });

        mTablePhones.setRowFactory(row -> new TableRow<Phones>() {
            @Override
            protected void updateItem(Phones item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty || item.getPeople() == null) { //If the cell is empty
                    //setTextFill(Color.RED);
                    setStyle("");
                } else { //If the cell is not empty
                    // Style all persons wich state is "ACTIVE"
                    if (item.getPeople().getServiceaccount() != People.SERVICE_ACCOUNT &&
                            item.getPeople().getADState() == LdapUtils.ACCOUNT_DISABLED) {
                        //setTextFill(Color.RED); //The text in red
                        setStyle("-fx-background-color: red"); //The background of the cell in yellow
                    }
                    else if (item.getPeople().getServiceaccount() != People.SERVICE_ACCOUNT &&
                            item.getPeople().getADState() == LdapUtils.ACCOUNT_UNKNOWN) {
                        //setTextFill(Color.RED); //The text in red
                        setStyle("-fx-background-color: gray"); //The background of the cell in yellow
                    }
                    else {
                        //Here I see if the row of this cell is selected or not
                        if (getTableView().getSelectionModel().getSelectedItems().contains(item)) {
                            setStyle("");
                            //setTextFill(Color.RED);
                        }
                        else {
                            setStyle("");
                            //setTextFill(Color.BLACK);
                        }
                    }
                }
            }
        });

        updateTablePhones();
    }

    private void showErrorMessage(String func, String msg) {
        Dialog dialog = new Dialog();
        dialog.setTitle("Error in " + func);
        dialog.setContentText(msg);
        ButtonType ok_button = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(ok_button);
        dialog.showAndWait();
    }

    @FXML
    private void onKeyPressedPhones(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case INSERT:
                Phones phone = new Phones();
                mTablePhones.getItems().add(phone);
                mTablePhones.scrollTo(phone);
                break;
        }
    }

    @FXML private void onEditCommitPhones(TableColumn.CellEditEvent<Phones, String> column) {
        if (column.getNewValue()!=null && column.getNewValue().length() == 10) {
            column.getRowValue().setPhone(column.getNewValue());
            column.getTableView().refresh();
            phones.updatePhone(column.getRowValue());
        }
        else {
            showErrorMessage("CommitPhones", "Incorrect number length");
        }
    }


    @FXML private void onActionFilterPhone() {
        if (mTFieldFilterPhone.getText() == null || mTFieldFilterPhone.getText().equals(""))
            phones.clearPhoneFilter();
        else
            phones.setPhoneFilter(mTFieldFilterPhone.getText());
        updateTablePhones();
    }

    @FXML private void onActionFilterFIO() {
        if (mTFieldFilterFIO.getText() == null || mTFieldFilterFIO.getText().equals(""))
            phones.clearFIOFilter();
        else
            phones.setFIOFilter(mTFieldFilterFIO.getText());

        updateTablePhones();
    }

    @FXML private void menuImportPhones() {
        ImportCsvForm importcsvform;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("importcsvform.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 400));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            importcsvform = loader.getController();
            importcsvform.setTemplate(phmobile.getTemplate());
            stage.showAndWait();
            if (importcsvform.getOKPressed()) {
                File csv_file = importcsvform.getCSVFile();
                if (csv_file != null) {
                    PhonesMobile phmobile = new PhonesMobile();
                    phmobile.setTemplate(importcsvform.getTemplate());
                    phmobile.readCsvFile(csv_file);
                    updateTablePhones();
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML private void menuImportPhoneCash() {
        ImportCsvForm importcsvform;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("importcsvform.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 400));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            importcsvform = loader.getController();

            importcsvform.setTemplate(phoneCashTemplate);
            stage.showAndWait();
            //showForm(loader, 400, 150);
            if (importcsvform.getOKPressed()) {
                Method method = phoneCashServices.getClass().getDeclaredMethod("addPhoneCash", Map.class);
                File csv_file = importcsvform.getCSVFile();
                if (csv_file != null) {
                    PhonesMobile phmobile = new PhonesMobile();
                    phmobile.setTemplate(importcsvform.getTemplate());
                    phmobile.readCsvFile(csv_file, method, phoneCashServices);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @FXML private void menuImportPhoneCashDBF() {
        ImportCsvForm importcsvform;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("importcsvform.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 400));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            importcsvform = loader.getController();
            importcsvform.setTemplate(phoneCashTemplateDBF);
            importcsvform.setExtensionDBF();
            stage.showAndWait();
            if (importcsvform.getOKPressed()) {
                Method method = phoneCashServices.getClass().getDeclaredMethod("addPhoneCash", Map.class);
                File dbf_file = importcsvform.getCSVFile();
                if (dbf_file != null) {
                    PhonesMobile phmobile = new PhonesMobile();
                    phmobile.setCashTypeDBF();
                    phmobile.setImportMonth(new Timestamp(importcsvform.getDate().getTime()));
                    phmobile.setTemplate(importcsvform.getTemplate());
                    //phmobile.readDBFFile(dbf_file, method, phoneCashServices);
                    phmobile.readCashDBF(new FileInputStream(dbf_file.getPath()),
                            new Timestamp(importcsvform.getDate().getTime()),
                            "phoneCashTemplateDBF");
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @FXML private void menuImportPhoneDetailDBF() {
        ImportCsvForm importcsvform;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("importcsvform.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 400));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            importcsvform = loader.getController();
            importcsvform.setTemplate(phoneDetailTemplateDBF);
            importcsvform.setExtensionDBF();
            stage.showAndWait();
            if (importcsvform.getOKPressed()) {
                Method method = phoneDetailServices.getClass().getDeclaredMethod("addPhoneDetail", Map.class);
                File dbf_file = importcsvform.getCSVFile();
                if (dbf_file != null) {
                    PhonesMobile phmobile = new PhonesMobile();
                    phmobile.setCashTypeDBF();
                    phmobile.setImportMonth(new Timestamp(importcsvform.getDate().getTime()));
                    phmobile.setTemplate(importcsvform.getTemplate());

                    phmobile.readDBFFile(dbf_file, method, phoneDetailServices);
                    //phmobile.readDBFFile2(dbf_file, method, phoneDetailServices);
                }
            }
        }
        catch (Exception e) {
            System.out.println("menuImportPhoneDetailDBF: " + e.toString());
        }
    }

    @FXML private void menuImportPhoneDetailCSV() {
        ImportCsvForm importcsvform;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("importcsvform.fxml"));
            PhonesMobile phmobile = new PhonesMobile();
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 400));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            importcsvform = loader.getController();
            importcsvform.setTemplate(phmobile.getTemplate("phoneDetailTemplateCSV"));
            //importcsvform.setExtensionDBF();
            stage.showAndWait();
            if (importcsvform.getOKPressed()) {
                Method method = phoneDetailServices.getClass().getDeclaredMethod("addPhoneDetail", Map.class);
                File dbf_file = importcsvform.getCSVFile();
                if (dbf_file != null) {
                    phmobile.setCashTypeCSV();
                    phmobile.setImportMonth(new Timestamp(importcsvform.getDate().getTime()));
                    phmobile.setTemplate(importcsvform.getTemplate());
                    phmobile.readCsvFile(dbf_file, method, phoneDetailServices);
                    //phmobile.readDBFFile2(dbf_file, method, phoneDetailServices);
                }
            }
        }
        catch (Exception e) {
            System.out.println("menuImportPhoneDetailCSV: " + e.toString());
        }
    }

    @FXML private void menuImportPhonesTariffCSV() {
        ImportCsvForm importcsvform;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("importcsvform.fxml"));
            PhonesMobile phmobile = new PhonesMobile();
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 400));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            importcsvform = loader.getController();

            ImportTemplateServices importTemplateServices = new ImportTemplateServices();
            ImportTemplate importTemplate = importTemplateServices.findName(ImportTemplate.class, "phonesTariff");
            if (importTemplate == null)
                return;

            importcsvform.setTemplate(importTemplate.getTemplate());
            //importcsvform.setExtensionDBF();
            stage.showAndWait();
            if (importcsvform.getOKPressed()) {
                // Method method = phones.getClass().getDeclaredMethod("addPhonesTariffs", Map.class);
                File dbf_file = importcsvform.getCSVFile();
                if (dbf_file != null) {
                    phmobile.setCashTypeCSV();
                    //phmobile.setImportMonth(new Timestamp(importcsvform.getDate().getTime()));
                    //phmobile.setTemplate(importcsvform.getTemplate());
                    // phmobile.readCsvFile(dbf_file, method, phoneDetailServices);
                    //phmobile.readDBFFile2(dbf_file, method, phoneDetailServices);
                    phmobile.readPhonesTariffCSV(new FileInputStream(dbf_file.getPath()), "phonesTariff");
                }
            }
        }
        catch (Exception e) {
            System.out.println("menuImportPhoneDetailCSV: " + e.toString());
        }
    }

    @FXML private void menuImportServicesCSV() {
        ImportCsvForm importcsvform;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("importcsvform.fxml"));
            PhonesMobile phmobile = new PhonesMobile();
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 400));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            importcsvform = loader.getController();

            ImportTemplateServices importTemplateServices = new ImportTemplateServices();
            ImportTemplate importTemplate = importTemplateServices.findName(ImportTemplate.class, "phonesTariff");
            if (importTemplate == null)
                return;

            importcsvform.setTemplate(importTemplate.getTemplate());
            //importcsvform.setExtensionDBF();
            stage.showAndWait();
            if (importcsvform.getOKPressed()) {
                // Method method = phones.getClass().getDeclaredMethod("addPhonesTariffs", Map.class);
                File dbf_file = importcsvform.getCSVFile();
                if (dbf_file != null) {
                    phmobile.setCashTypeCSV();
                    //phmobile.setImportMonth(new Timestamp(importcsvform.getDate().getTime()));
                    //phmobile.setTemplate(importcsvform.getTemplate());
                    // phmobile.readCsvFile(dbf_file, method, phoneDetailServices);
                    //phmobile.readDBFFile2(dbf_file, method, phoneDetailServices);
                    phmobile.readServicesCSV(new FileInputStream(dbf_file.getPath()), "phonesTariff");
                }
            }
        }
        catch (Exception e) {
            System.out.println("menuImportServicesCSV: " + e.toString());
        }
    }


    @FXML private void menuDetailAnalize() throws Exception {
        phoneDetailServices.analizePhoneDetailTariffsYearLocal(2019);
    }

    @FXML private void menuExportCashYear() {
        DateSelectForm dateSelectForm = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dateselect.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 100));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            dateSelectForm = loader.getController();
            dateSelectForm.setDialogTypeOpen(false);
            dateSelectForm.showFileSelect();
            stage.showAndWait();
            if (!dateSelectForm.getOKPressed()) {
                return;
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        File csvFile;
        if (dateSelectForm != null && (csvFile = dateSelectForm.getCSVFile()) != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateSelectForm.getDate());
            ArrayList<Object[]> cash = phoneCashServices.getPhoneCashYear(calendar.get(Calendar.YEAR));
            phmobile.writeCsvFile(csvFile, cash);
        }
    }

    @FXML private void menuExportDepartCashYear() {
        DateSelectForm dateSelectForm = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dateselect.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 100));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            dateSelectForm = loader.getController();
            dateSelectForm.setDialogTypeOpen(false);
            dateSelectForm.showFileSelect();
            stage.showAndWait();
            if (!dateSelectForm.getOKPressed()) {
                return;
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        File csvFile;
        if (dateSelectForm != null && (csvFile = dateSelectForm.getCSVFile()) != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateSelectForm.getDate());
            ArrayList<Object[]> cash = phoneCashServices.getDepartmentCashYear(calendar.get(Calendar.YEAR));
            phmobile.writeCsvFile(csvFile, cash);
        }
    }

    @FXML private void menuExportCashLastMonth() {
        DateSelectForm dateSelectForm = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("dateselect.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent)loader.load(), 600, 100));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            dateSelectForm = loader.getController();
            dateSelectForm.setDialogTypeOpen(false);
            dateSelectForm.showFileSelect();
            stage.showAndWait();
            if (!dateSelectForm.getOKPressed()) {
                return;
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        File csvFile;
        if (dateSelectForm != null && (csvFile = dateSelectForm.getCSVFile()) != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateSelectForm.getDate());
            ArrayList<Object[]> cash = phoneCashServices.getPhoneCashMonth(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
            phmobile.writeCsvFile(csvFile, cash);
        }
    }

    @FXML private void menuCashUpdatePeople() {
        phoneCashServices.updatePhoneCashWOPeople();
        updateTablePhones();
    }

    private void initLdap(String ldap_url, String ldap_user, String ldap_pass, String ldap_base) {
        if (ldap == null) {
            ldap = new LdapUtils(ldap_url, ldap_user, ldap_pass, ldap_base);
        }
    }

    @FXML private void menuCheckAD() {
        //initLdap();
        initLdap("ldap://dc-3.ncfu.net/", "onovikov@ncfu.ru", "80PaRlem", "DC=ncfu,DC=net");
        ldap.find("Уханов Сергей");
        //phmobile.checkADAllPeople(ldap);
        //updateTablePhones();
    }

    @FXML private void menuGetLastHistoryWithCashAnalize() {
        ArrayList<Object> obj =
        historySrv.getLastHistoryWithCashAnalizeAndServices();
        mTextAreaLog.appendText(obj.toArray().toString());
        //historySrv.getLastHistoryWithCashAnalize();
    }


    private ArrayList<PhoneInfo> loadPhoneInfo(Phones phone) {
        if (phone == null)
            return null;
        ArrayList<PhoneInfo> phoneinfo = new ArrayList<>();
        phoneinfo.add(new PhoneInfo(INFO_PHONE, phone.getPhone()));
        if (phone.getPeople() != null) {
            phoneinfo.add(new PhoneInfo(INFO_FIO, phone.getPeople().getFio()));
            phoneinfo.add(new PhoneInfo(INFO_ACCOUNT, phone.getPeople().getAccount()));
            phoneinfo.add(new PhoneInfo(INFO_ADSTATE, Integer.toString(phone.getPeople().getADState())));
            phoneinfo.add(new PhoneInfo(INFO_SERVICE_ACCOUNT, Integer.toString(phone.getPeople().getServiceaccount())));
            phoneinfo.add(new PhoneInfo(INFO_DEPARTMENT, phone.getPeople().getDepartment()));
            phoneinfo.add(new PhoneInfo(INFO_POSITION, phone.getPeople().getPosition()));

            activeHistory = historySrv.getActiveHistory(phone);
            if (activeHistory != null) {
                phoneinfo.add(new PhoneInfo(INFO_DATE_START, activeHistory.getDatestart().toString()));
                phoneinfo.add(new PhoneInfo(INFO_DATE_END, ""));
            }
            else {
                phoneinfo.add(new PhoneInfo(INFO_DATE_START, ""));
                phoneinfo.add(new PhoneInfo(INFO_DATE_START, ""));
            }
        }
        else {
            phoneinfo.add(new PhoneInfo(INFO_FIO, "Select people..."));
        }
        phoneinfo.add(new PhoneInfo(INFO_PONE_STATE, Integer.toString(phone.getState())));
        return phoneinfo;
    }

    private void onScrollPhones(Phones newphone) {
        try {
            ArrayList<PhoneInfo> phoneinfo = loadPhoneInfo(newphone);//mTablePhones.getItems().get(selected));
            if (phoneinfo != null)
                mTablePhoneInfo.getItems().setAll(phoneinfo);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        updateTableHistory();
    }

    @FXML private void onActionCheckInActive() {
        if (mCheckInActivePeople.isSelected())
            phones.setPeopleType(LdapUtils.ACCOUNT_DISABLED);
        else
            phones.clearPeopleType();
        updateTablePhones();
    }

    @FXML private void onActionSelectFreeOfPeople() {
        if (mCheckFreePhone.isSelected())
            phones.setFIOFilter("null");
        else
            phones.clearFIOFilter();
        updateTablePhones();
    }

    @FXML private void onEditCommitPhoneInfo(TableColumn.CellEditEvent<PhoneInfo, String> column) {
        if (column.getNewValue()!=null && !column.getNewValue().isEmpty()) {
            switch (column.getRowValue().getName()) {
                case INFO_PHONE:
                    mTablePhones.getSelectionModel().getSelectedItem().setPhone(column.getNewValue());
                    //phones.updatePhone(mTablePhones.getSelectionModel().getSelectedItem());
                    break;
                case INFO_ACCOUNT:
                    mTablePhones.getSelectionModel().getSelectedItem().
                            getPeople().setAccount(column.getNewValue());
                    break;
                case INFO_ADSTATE:
                    mTablePhones.getSelectionModel().getSelectedItem().
                            getPeople().setADState(Integer.parseInt(column.getNewValue()));
                    break;
                case INFO_FIO:
                    mTablePhones.getSelectionModel().getSelectedItem().
                            getPeople().setFio(column.getNewValue());
                    break;
                case INFO_PONE_STATE:
                    mTablePhones.getSelectionModel().getSelectedItem().
                            setState(Byte.parseByte(column.getNewValue()));
                    break;
                case INFO_SERVICE_ACCOUNT:
                    mTablePhones.getSelectionModel().getSelectedItem().
                            getPeople().setServiceaccount(Integer.parseInt(column.getNewValue()));
                    break;
                case INFO_DATE_START:
                    break;
                case INFO_DEPARTMENT:
                    mTablePhones.getSelectionModel().getSelectedItem().
                            getPeople().setDepartment(column.getNewValue());
                    break;
                case INFO_POSITION:
                    mTablePhones.getSelectionModel().getSelectedItem().
                            getPeople().setPosition(column.getNewValue());
                    break;
            }
        }
        else {
            showErrorMessage("CommitPhoneInfo", "Empty field");
        }
    }

    @FXML private void onActionBtnApply() {
        //int index = mTablePhones.getSelectionModel().getSelectedIndex();
        people.updatePeople(mTablePhones.getSelectionModel().getSelectedItem().getPeople());
        phones.updatePhone(mTablePhones.getSelectionModel().getSelectedItem());
        updateTablePhones();
        //mTablePhones.getSelectionModel().select(index);
    }

    @FXML private void onActionCheckAD() {
        //initLdap();
        //int index = mTablePhones.getSelectionModel().getSelectedIndex();
        //phmobile.checkADPeople(ldap, mTablePhones.getSelectionModel().getSelectedItem().getPeople());
        //updateTablePhones();
        //mTablePhones.getSelectionModel().select(index);
    }

    @FXML private void onActionGenerateHistory() {
        Phones ph = mTablePhones.getSelectionModel().getSelectedItem();
        historySrv.GenerateHistory(ph);
        onScrollPhones(mTablePhones.getSelectionModel().getSelectedItem());
    }

    @FXML private void onActionFreePhone() {
        Phones ph = mTablePhones.getSelectionModel().getSelectedItem();
        if (ph == null || ph.getPeople() == null)
            return;
        Calendar now = Calendar.getInstance();

        DateSelectForm dateSelectForm = (DateSelectForm) loadAndShowForm("dateselect.fxml", 200, 100);
        if (activeHistory != null) {
            if (dateSelectForm.getOKPressed()) {
                activeHistory.setDateEnd(new Timestamp(dateSelectForm.getDate().getTime()));
                historySrv.updateHistory(activeHistory);
            }
                //else
                //    activeHistory.setDateEnd(new Timestamp(now.getTime().getTime()));

        }
        if (dateSelectForm.getOKPressed()) {
            ph.setPeople(null);
            phones.updatePhone(ph);
            updateTablePhones();
        }
    }

    @FXML private void onActionSelectPeople() {
        PeopleSelectForm peopleSelectForm;
        Phones phone = mTablePhones.getSelectionModel().getSelectedItem();
        if (phone == null)
            return;
        try {
            if (phone.getPeople() != null && activeHistory != null)
                onActionFreePhone();
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("peopleselect.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene((Parent) loader.load(), 400, 500));
            stage.initOwner(mTablePhones.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            peopleSelectForm = loader.getController();
            if (activeHistory != null)
                peopleSelectForm.setDateStart(activeHistory.getDatestart());
            //activeHistory.get
            //peopleSelectForm.setTemplate(phmobile.getTemplate());
            if (phone.getPeople() != null)
                peopleSelectForm.selectPeople(phone.getPeople());
            stage.showAndWait();
            if (peopleSelectForm.getOKPressed()) {
                if (phone.getPeople() != null && activeHistory != null &&
                        !phone.getPeople().isSameID(peopleSelectForm.getPeople())) {
                    Calendar now = Calendar.getInstance();
                    activeHistory.setDateEnd(new Timestamp(now.getTime().getTime()));
                    historySrv.updateHistory(activeHistory);
                }
                if (phone.getPeople() == null || (
                        phone.getPeople() != null && !phone.getPeople().isSameID(peopleSelectForm.getPeople()))) {
                    phone.setPeople(peopleSelectForm.getPeople());
                    phones.updatePhone(phone);
                }
                HistoryType histType;
                histType = histiryTypeSrv.findHistoryType(HistoryTypeServices.TYPE_NEW_PHONE);

                if (phone.getPeople() != null && !phone.getPeople().isSameID(peopleSelectForm.getPeople())) {
                    activeHistory = new History(phone,
                            peopleSelectForm.getPeople(),
                            new Timestamp(peopleSelectForm.getDate().getTime()),
                            histType);
                    //activeHistory.setDate(new Timestamp(peopleSelectForm.getDate().getTime()));
                    //activeHistory.setType(histType);
                    //activeHistory.setPhone(phone);
                    //activeHistory.setPeople(peopleSelectForm.getPeople());
                    historySrv.addHistory(activeHistory);
                }
                updateTablePhones();
                updateTableHistory();
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
