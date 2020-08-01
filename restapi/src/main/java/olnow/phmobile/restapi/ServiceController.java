package olnow.phmobile.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import olnow.phmobile.PhonesMobile;
import olnow.phmobile.IServiceServices;

import java.io.IOException;

@RestController
class ServiceController {
    private Logger logger = LoggerFactory.getLogger(ServiceController.class);
    private PhonesMobile phonesMobile = new PhonesMobile();

    @Autowired
    private IServiceServices serviceServices; // = new ServiceServices();

    @PostMapping("/loadServices")
    @ResponseBody
    public ResponseEntity loadServices(@RequestParam(value = "file", required = true) MultipartFile file,
                                           @RequestParam(value = "charset", required = false) String charset) {
        logger.info("/loadServices file: {}, size: {}", file.getOriginalFilename(), file.getSize());
        try {
            if (charset != null && !charset.isEmpty())
                phonesMobile.setCharset(charset);
            phonesMobile.setUpdateMonthInfo(false);
            phonesMobile.readCSV(file.getInputStream(), serviceServices,
                    "addService", "phonesTariff");
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        catch (IOException e) {
            logger.error("IO Exception, file {}", file.getOriginalFilename(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
