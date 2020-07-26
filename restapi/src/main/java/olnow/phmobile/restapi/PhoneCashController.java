package olnow.phmobile.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import olnow.phmobile.*;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

@CrossOrigin(origins = "http://localhost:8080")
@Controller
class PhoneCashController {
    private PhoneCashServices phoneCashServices = new PhoneCashServices();
    private PhonesMobileLog phonesMobile = new PhonesMobileLog();
    private PhonesServices phonesServices = new PhonesServices();
    private HistoryServices historyServices = new HistoryServices();
    private PeopleServices peopleServices = new PeopleServices();
    private ImportHistoryServices importHistoryServices = new ImportHistoryServices();
    private boolean stopUploadProcess = false;
    private Logger logger = LoggerFactory.getLogger(PhoneCashController.class);

    @Autowired
    private
    Mail mail;

    private class PhonesMobileLog extends PhonesMobile {
        @Override
        public void log(String message) {
            System.out.println(message);
        }
    }

    @RequestMapping("/getCashYear")
    @ResponseBody
    ArrayList<Object[]> getCashYear(@RequestParam(value = "year", required = false) Integer year) {
        logger.info(this.getClass().getName(), year);
        if (year == null)
            year = 2019;
        return phoneCashServices.getPhoneCashYear(year);
    }

    @RequestMapping("/getCashMonth")
    @ResponseBody
    ArrayList<Object[]> getCashMonth(@RequestParam(value = "year", required = true) Integer year,
                                     @RequestParam(value = "month", required = true) Integer month) {
        logger.info("/getCashMonth executed: year: {}, month: {}", year, month);
        return phoneCashServices.getPhoneCashMonth(year, month);
    }

    @RequestMapping("/getCashYearDepartment")
    @ResponseBody
    ArrayList<Object[]> getCashYearDepartment(@RequestParam(value = "year", required = true) Integer year) {
        return phoneCashServices.getDepartmentCashYear(year);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping("/getCashDetail")
    @ResponseBody
    ArrayList<Object[]> getCashDetail(@RequestParam(value = "phone", required = true) String phone,
                                      @RequestParam(value = "year", required = true) Integer year) {
        logger.info("[getCashDetail] phone: {}, year: {}", phone, year);
        // ObjectMapper om = new ObjectMapper();
        // logger.info(om.wr(new SimplePhoneWrapper()));
        // System.out.println("getCashDetail: " + phone.getPhone());
        return phoneCashServices.getPhonesCashArray(year, phone);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping(value = "/getContractCashDetail", method = RequestMethod.POST)
    @ResponseBody
    ArrayList<Object[]> getContractCashDetail() {
        System.out.println("getContractCashDetail: ");
        return phoneCashServices.getContractCashInfo();
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping("/getFileUploadProgress")
    @ResponseBody
    IntProgress getFileUploadProgress(@RequestBody IntProgressWrapper progress) {
        //System.out.println("getFileUploadProgress: " + progress.getProgress().getProgress());//) + ": " + phonesMobile.getFileReadProgress());
        int i = 0;
        if (progress.getProgress().getProgress() == 0)
            stopUploadProcess = false;
        while (!stopUploadProcess && progress.getProgress().getProgress() == (i = phonesMobile.getFileReadProgress())) {
            try {
                Thread.sleep(500);
                //System.out.println("sleep: " + i);
            }
            catch (InterruptedException e) {
                System.out.println("getFileUploadProgress exception: " + e.toString());
            }
        }
        //System.out.println("progress: " + stopUploadProcess + ": " + i);
        if (stopUploadProcess)
            stopUploadProcess = false;
        IntProgress res = new IntProgress();
        res.setProgress(i);
        return res;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping("/fileUploadPhoneCash")
    @ResponseBody
    ResponseEntity fileUploadPhoneCash(@RequestParam(value = "year", required = true) int year,
                                       @RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
        System.out.println("fileUploadPhoneCash:" + file.getOriginalFilename() + ":" + file.getSize());
        System.out.println("year:" + year);
        ReadFileResult readFileResult;
        if (file == null) return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        try {
            if (file.getOriginalFilename().toLowerCase().contains(".csv")) {
                readFileResult = phonesMobile.readCashCSV(file.getInputStream(),
                        "phoneCashTemplateCSV");
                //Add import history
                if (readFileResult != null && readFileResult.getRecordWrite() > 0) {
                    ImportHistory importHistory = new ImportHistory(file.getOriginalFilename(),
                            ImportHistory.TYPE_CASH,
                            new Timestamp(Calendar.getInstance().getTime().getTime()),
                            readFileResult.getRecordCount(), readFileResult.getRecordWrite(),
                            readFileResult.getDate());
                    importHistoryServices.add(importHistory);
                    logger.info("[fileUploadPhoneCash CSV] loaded, record write: {}", readFileResult.getRecordWrite());
                    // System.out.println("fileUploadPhoneCash dbf: OK, exit");
                    return ResponseEntity.status(HttpStatus.OK).body(null);
                }
                else
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                // System.out.println("fileUploadPhoneCash csv: OK, exit");
                // return ResponseEntity.status(HttpStatus.OK).body(null);
            }
            else if (file.getOriginalFilename().toLowerCase().contains(".dbf")) {
                //LocalDate currentDate = LocalDate.now();
                Timestamp month = null;
                Calendar importDate = Calendar.getInstance();
                try {
                    String fileName = file.getOriginalFilename().split("\\.")[0];
                    String stmonth = fileName.substring(fileName.length() - 2);

                    importDate.set(year,
                            Integer.parseInt(stmonth)-1, 1);
                    month = new Timestamp(importDate.getTime().getTime());
                } catch (Exception e) {
                    System.out.println("fileUploadPhoneCash: Convert month:" + e.toString());
                }

                readFileResult = phonesMobile.readCashDBF(file.getInputStream(),
                        month,"phoneCashTemplateDBF");
                //Add import history
                if (readFileResult != null && readFileResult.getRecordWrite() > 0) {
                    ImportHistory importHistory = new ImportHistory(file.getOriginalFilename(),
                            ImportHistory.TYPE_CASH,
                            new Timestamp(Calendar.getInstance().getTime().getTime()),
                            readFileResult.getRecordCount(), readFileResult.getRecordWrite(),
                            new Timestamp(importDate.getTime().getTime()));
                    importHistoryServices.add(importHistory);
                    logger.info("[fileUploadPhoneCash DBF] loaded, record write: {}", readFileResult.getRecordWrite());
                    // System.out.println("fileUploadPhoneCash dbf: OK, exit");
                    return ResponseEntity.status(HttpStatus.OK).body(null);
                }
                else
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        catch (Exception e) {
            System.out.println("fileUploadPhoneCash exception: " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        finally {
            stopUploadProcess = true;
        }
    }

    @RequestMapping("/checkFileImportStatus")
    @ResponseBody
    public boolean checkFileImportStatus(@RequestParam(value = "year", required = true) int year,
                                         @RequestParam(value = "filename", required = true) String fileName) throws Exception {
        return importHistoryServices.checkImportStatus(fileName, year);

    }

    @RequestMapping("/getImportHistory")
    @ResponseBody
    public ArrayList<ImportHistory> getImportHistory(@RequestParam(value = "year", required = true) int year,
                                         @RequestParam(value = "type", required = true) int type) throws Exception {
        return importHistoryServices.get(type, year);

    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public ResponseEntity test(@RequestParam(value = "year", required = false) int year,
                               @RequestParam(value = "file", required = true) MultipartFile file) {
        System.out.println("test:" + file.getOriginalFilename() + ":" + file.getSize());
        System.out.println(year);
        try {
            phonesMobile.testCSVFile(file.getInputStream());
        }
        catch (IOException e) {
            System.out.println("test: ioexception: " + e.toString());
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @RequestMapping("/sendCashToEMail")
    @ResponseBody
    public ResponseEntity sendCashToEMail(@RequestParam(value = "year", required = false) Integer year,
                                          @RequestParam(value = "month", required = false) Integer month,
                                          @RequestParam(value = "to", required = false) String to) {
        logger.info("[sendCashToEMail] year: {}, month: {}, to: {}", year, month, to);

        ArrayList<Object[]> phoneCash = phoneCashServices.getPhoneCashMonth(year, month, true);
        File sendfile = null;
        try {
            if (phoneCash != null) {
                sendfile = File.createTempFile("name", ".csv");
                phonesMobile.setCSVBom(true);
                phonesMobile.writeCsvFile(sendfile, phoneCash);

                StringBuilder subject = new StringBuilder("Отчет о начислениях");
                subject.append(" за ").append(year).append(" год, ").append(month).append(" месяц");
                if (to == null || (!to.isEmpty()))
                    to = AppProperties.getStringValue("MAIL_TO");
                StringBuilder text = new StringBuilder("Здравствуйте!")
                        .append("\n")
                        .append("\nОтчет о начислениях в прилагаемом файле.")
                        .append("\n")
                        .append("\n---")
                        .append("\nНе отвечайте на данное сообщение,")
                        .append("\nсообщение сформированно автоматически.");
                String fileName = year + "-" + month + "-cashMonth.csv";

                if (mail.sendEMail(to, subject.toString(), text.toString(), fileName, sendfile))
                    logger.info("[sendCashToEMail] MimeMessage sent");
                else {
                    logger.error("[sendCashToEMail] Create mimeMessage error");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }
            else {
                logger.error("/sendCashToEMail nothing to send");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (IOException e) {
            logger.error("[sendCashToEMail] Create temp file error", e);
        } catch (NullPointerException e) {
            logger.error("[sendCashToEMail] file: {}, ", e);

        } finally {
            if (sendfile != null)
                sendfile.delete();
        }


        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @RequestMapping(value = "/fileUploadPhoneDetail")
    @ResponseBody
    ResponseEntity fileUploadPhoneDetail(@RequestParam(value = "charset", required = false) String charset,
                                         @RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
    //ResponseEntity fileUploadPhoneDetail(@RequestParam("file") MultipartFile file) {
        System.out.println("fileUploadPhoneDetail:" + file.getOriginalFilename() + ":" + file.getSize());
        if (file == null) return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        try {
            ReadFileResult readFileResult;
            if (file.getOriginalFilename().toLowerCase().contains(".csv")) {
                if (charset != null && !charset.isEmpty())
                    phonesMobile.setCharset(charset);
                readFileResult = phonesMobile.readDetailCSV(file.getInputStream(),
                        "phoneDetailTemplateCSV");

                if (readFileResult != null && readFileResult.getRecordWrite() > 0) {
                    ImportHistory importHistory = new ImportHistory(file.getOriginalFilename(),
                            ImportHistory.TYPE_DETAIL,
                            new Timestamp(Calendar.getInstance().getTime().getTime()),
                            readFileResult.getRecordCount(), readFileResult.getRecordWrite(),
                            readFileResult.getDate());
                    importHistoryServices.add(importHistory);
                    logger.info("[fileUploadPhoneDetail CSV] loaded, record write: {}", readFileResult.getRecordWrite());
                    // System.out.println("fileUploadPhoneCash dbf: OK, exit");
                    return ResponseEntity.status(HttpStatus.OK).body(null);
                }
                else
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);


                // System.out.println("fileUploadPhoneDetail: OK, exit");
                // return ResponseEntity.status(HttpStatus.OK).body(null);
            }
            else if (file.getOriginalFilename().toLowerCase().contains(".dbf")) {
                readFileResult = phonesMobile.readDetailDBF(file.getInputStream(),
                        "phoneDetailTemplateDBF");
                if (readFileResult != null && readFileResult.getRecordWrite() > 0) {
                    ImportHistory importHistory = new ImportHistory(file.getOriginalFilename(),
                            ImportHistory.TYPE_DETAIL,
                            new Timestamp(Calendar.getInstance().getTime().getTime()),
                            readFileResult.getRecordCount(), readFileResult.getRecordWrite(),
                            readFileResult.getDate());
                    importHistoryServices.add(importHistory);
                    logger.info("[fileUploadPhoneDetail DBF] loaded, record write: {}", readFileResult.getRecordWrite());
                    // System.out.println("fileUploadPhoneCash dbf: OK, exit");
                    return ResponseEntity.status(HttpStatus.OK).body(null);
                }
                else
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        catch (Exception e) {
            System.out.println("fileUploadPhoneDetail exception: " + e.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        finally {
            stopUploadProcess = true;
        }
        // System.out.println("fileUploadPhoneDetail: not selected");
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @RequestMapping(value = "/cash/adjustPhoneCash")
    @ResponseBody
    ResponseEntity fileUploadPhoneDetail(@RequestParam(value = "year", required = true) Integer year,
                                         @RequestParam(value = "month", required = true) Integer month,
                                         @RequestParam(value = "phone") String phone,
                                         @RequestParam(value = "phonecash") PhoneCash phoneCash) throws Exception {
        //ResponseEntity fileUploadPhoneDetail(@RequestParam("file") MultipartFile file) {
        // System.out.println("fileUploadPhoneDetail:" + file.getOriginalFilename() + ":" + file.getSize());
        logger.info("[/cash/adjustPhoneCash] year: {}, month: {}, phone: {}",
                year, month, phone);
        // Timestamp monthTimestamp = new Timestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, 1);
        phoneCash.setPhone(phonesServices.findPhone(phone));
        if (phoneCash.getPhone() == null) {
            logger.info("[/cash/adjustPhoneCash] Not found phone");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Timestamp monthTS = new Timestamp(calendar.getTime().getTime());
        phoneCash.setPeople(historyServices.getPeopleAtDate(phoneCash.getPhone(), monthTS));

        phoneCash.setMonth(monthTS);
        phoneCashServices.addPhoneCash(phoneCash);

        return ResponseEntity.status(HttpStatus.OK).body(phoneCash);
    }


        @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        System.out.println("Spring exception: " + e.toString());
        logger.error("[Spring exception]", e);
    }
}


