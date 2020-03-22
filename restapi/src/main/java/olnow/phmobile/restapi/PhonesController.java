package olnow.phmobile.restapi;

import olnow.phmobile.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
class PhonesController {
    private PhonesServices phonesServices = new PhonesServices();
    private PhonesMobile phmobileServices = new PhonesMobile();
    private HistoryServices historyServices = new HistoryServices();
    private PeopleServices peopleServices = new PeopleServices();
    // private HistoryTypeServices historyTypeServices;
    private Logger logger = LoggerFactory.getLogger(PhonesController.class);

    @Autowired
    private
    Mail mail;

    private PhonesController() {
        // historyTypeServices = new HistoryTypeServices();
    }

    @RequestMapping("/getPhones")
    @ResponseBody
    ArrayList<Phones> getPhones() {
        logger.info("[getPhones]");
        try {
            return phonesServices.getPhones();
        }
        catch (Exception e) {
            logger.error("[getPhones] exception", e);
            return null;
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping("/updatePhones")
    @ResponseBody
    String updatePhones(@RequestBody PeopleWrapper peopleWrapper) {
        //ObjectMapper objectMapper = new ObjectMapper();
        People people = null;
        try {
            //PeopleWrapper peopleWrapper = objectMapper.readValue(request, PeopleWrapper.class);
            people = peopleWrapper.getPeople();
        }
        catch (Exception e) {
            System.out.println("PhonesController: updatePhones:" + e.toString());
            return "updatePhones error: " + e.toString();
        }
        if (people != null) {
            System.out.println("!!!  updatePhones: " + people);
            return "updatePhones: OK";
        }
        return "updatePhones: Error";
    }

    @RequestMapping("/syncADState")
    @ResponseBody
    public ResponseEntity syncADState() {
        // System.out.println("Request: /syncADState");
        logger.info("/syncADState");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Object credentials = authentication.getCredentials();
        String username = null;
        String password = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername() + "@" + AppProperties.getStringValue("LDAP_DOMAIN");
            password = credentials == null ? null : credentials.toString();
        } else {
            username = principal.toString();
        }
        if (password == null || username == null) {
            logger.error("/syncADState empty ldap username/password: ", principal);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        LdapUtils ldapUtils = new LdapUtils(AppProperties.getStringValue("LDAP_URL"),
                username, password,
                AppProperties.getStringValue("LDAP_BASE"));
        if (ldapUtils == null) {
            logger.error("Create LdapUtils Error, username: ", username, ", password: ", password);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // ldapUtils.find("Новиков Олег");
        ArrayList<Phones> inactivePhones = phmobileServices.checkADAllPeople(ldapUtils);

        //Send report to EMail
        if (inactivePhones != null && inactivePhones.size() > 0) {
            StringBuilder subject = new StringBuilder("Отчет о заблокированных пользователях");

            StringBuilder text = new StringBuilder("Здравствуйте!")
                    .append("\n")
                    .append("\nПеречень заблокированных пользователей:");
            inactivePhones.forEach(phone -> {
                text.append("\n")
                        .append(phone.getPhone());
                if (phone.getPeople() != null)
                    text.append(": ")
                            .append(phone.getPeople().getFio())
                            .append(" [")
                            .append(phone.getPeople().getDepartment())
                            .append(", ")
                            .append(phone.getPeople().getPosition())
                            .append("]");
            });
            text.append("\n")
                    .append("\n---")
                    .append("\nНе отвечайте на данное сообщение,")
                    .append("\nсообщение сформированно автоматически.");

            mail.sendEMail(AppProperties.getStringValue("MAIL_TO"), subject.toString(), text.toString());
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
        //phmobileServices.checkADAllPeople();
    }

    @RequestMapping("/freePhone")
    @ResponseBody
    ResponseEntity<Phones> freePhone(@RequestBody HistoryWrapper history) {
        try {
            System.out.println("Request: /freePhone - " +
                    history.getHistory() + " end:" + history.getHistory().getDateend());
            System.out.println("2: " +
                    history.getHistory().getPeople());
            historyServices.updateHistory(history.getHistory());
            Phones phone = history.getHistory().getPhone();
            phone.setPeople(null);
            phonesServices.updatePhone(phone);
            return ResponseEntity.status(HttpStatus.OK).body(phone);
        }
        catch (Exception e) {
            System.out.println("freePhone error: " + e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        //phmobileServices.checkADAllPeople();
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping("/createAndSetPeople")
    @ResponseBody
    ResponseEntity<People> createAndSetPeople(@RequestBody PhoneWithDateWrapper phone) {
    //ResponseEntity createAndSetPeople(@RequestBody String str) {
        try {
            System.out.println("Request: /createAndSetPeople - " + phone.getPhone() + ", fio: " + phone.getPhone().getPeople());
            System.out.println("PeopleID: " + phone.getPhone().getPeople().getIdpeople());
            System.out.println("Request: /createAndSetPeople hist - " + phone.getDatestart());
            Timestamp datestart = null;
            try {
                datestart = new Timestamp(Long.parseLong(phone.getDatestart()));
                // System.out.println(datestart);
            }
            catch (Exception e) {
                logger.error("[createAndSetPeople] parse Date error", e);

                // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (phone.getPhone().getPeople().getIdpeople() == 0) {
                peopleServices.addPeople(phone.getPhone().getPeople());
            }
            phonesServices.updatePhone(phone.getPhone());
            History history = new History();
            HistoryTypeServices historyTypeServices = new HistoryTypeServices();
            history.setPhone(phone.getPhone());
            history.setPeople(phone.getPhone().getPeople());
            history.setDate(datestart);
            HistoryType histType = historyTypeServices.findHistoryType(HistoryTypeServices.TYPE_NEW_PHONE);
            history.setType(histType);
            historyServices.addHistory(history);

            System.out.println("Request: /createAndSetPeople OK - " + phone.getPhone().getPeople().getIdpeople());
            return ResponseEntity.status(HttpStatus.OK).body(phone.getPhone().getPeople());
        }
        catch (Exception e) {
            logger.error("[createAndSetPeople] exception", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PostMapping("/createOrUpdatePhone")
    ResponseEntity<Phones> createOrUpdatePhone(@RequestBody PhoneWrapper phone) {
        //ResponseEntity createAndSetPeople(@RequestBody String str) {
        try {
            System.out.println("Request: /createOrUpdatePhone - " + phone.getPhone());
            if (phone.getPhone().getId() == 0) {
                phonesServices.addPhone(phone.getPhone());
            }
            else {
                phonesServices.updatePhone(phone.getPhone());
            }
            System.out.println("Request: /createOrUpdatePhone OK - ");
            return ResponseEntity.status(HttpStatus.OK).body(phone.getPhone());
        }
        catch (Exception e) {
            System.out.println("createOrUpdatePhone: " + e.toString());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }


    @PostMapping("/loadPhonesTariff")
    @ResponseBody
    public ResponseEntity loadPhonesTariff(@RequestParam(value = "file", required = true) MultipartFile file,
                                           @RequestParam(value = "charset", required = false) String charset) {
        System.out.println("/loadPhonesTariff" + file.getOriginalFilename() + ":" + file.getSize());
        logger.info(this.getClass().getName(), "/loadPhonesTariff: ", file.getOriginalFilename(), file.getSize());
        try {
            if (charset != null && !charset.isEmpty())
                phmobileServices.setCharset(charset);
            phmobileServices.setUpdateMonthInfo(false);
            //phonesServices.checkAndDeleteFromPhonesList();
            phmobileServices.readCSV(file.getInputStream(), phonesServices,
                    "addPhonesTariff", "phonesTariff");
            phmobileServices.setUpdateMonthInfo(true);
        }
        catch (IOException e) {
            logger.error(this.getClass().getName(), e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/checkExcludedPhones")
    @ResponseBody
    public ResponseEntity checkExcludedPhones(@RequestParam(value = "file", required = true) MultipartFile file,
                                              @RequestParam(value = "charset", required = false) String charset) {
        logger.info("[checkExcludedPhones] file: {} size: {}", file.getOriginalFilename(), file.getSize());
        try {
            if (charset != null && !charset.isEmpty())
                phmobileServices.setCharset(charset);
            phmobileServices.setUpdateMonthInfo(false);
            phonesServices.loadPhoneList();
            phmobileServices.readCSV(file.getInputStream(), phonesServices,
                    "checkAndDeleteFromPhonesList", "phonesTariff");
            phonesServices.updateExcludedStatusFromPhonesList();
            phmobileServices.setUpdateMonthInfo(true);
        }
        catch (IOException e) {
            logger.error(this.getClass().getName(), e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}

