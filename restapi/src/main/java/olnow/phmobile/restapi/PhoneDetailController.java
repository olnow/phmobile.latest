package olnow.phmobile.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import olnow.phmobile.PhoneDetailServices;

import java.util.concurrent.CompletableFuture;

@Controller
// @EnableAsync
class PhoneDetailController {
    private static final Logger logger = LoggerFactory.getLogger(PhoneDetailController.class);
    @Autowired
    private PhoneDetailServices phoneDetailServices = new PhoneDetailServices();

    private boolean stopAnalizeProcess = false;
    private CompletableFuture<Object[]> result;

    // @Autowired
    // private
    // TestAsync testAsync;

    @RequestMapping("/analizePhoneDetailTariffsMonth")
    @ResponseBody
    private Object[] analizePhoneDetailTariffsMonth(@RequestBody IntProgressWrapper month) {
        System.out.println("/analizePhoneDetailTariffsMonth:" + month.getProgress().getProgress());
        Object[] res = phoneDetailServices.analizePhoneDetailTariffsMonth(2019, month.getProgress().getProgress());
        stopAnalizeProcess = true;
        return res;
    }

    @RequestMapping("/analizePhoneDetailYear")
    @ResponseBody
    private Object[] analizePhoneDetailYear(@RequestBody IntProgressWrapper year) throws Exception {
        System.out.println("/analizePhoneDetailYear:" + year.getProgress().getProgress());
        Object[] res = phoneDetailServices.analizePhoneDetailTariffsYearLocal(year.getProgress().getProgress());
        stopAnalizeProcess = true;
        return res;
    }

    @RequestMapping("/getDetailAnalizeYear")
    @ResponseBody
    private Object[] getDetailAnalizeYear(@RequestParam(value = "year", required = true) int year) throws Exception {
        logger.info("[getDetailAnalizeYear] start, year: {}", year);
        Object[] res = phoneDetailServices.analizePhoneDetailTariffsYearLocal(year);
        logger.info("[getDetailAnalizeYear] end");
        stopAnalizeProcess = true;
        return res;
    }

    /*
    @RequestMapping("/getDetailAnalizeYearFuture")
    @ResponseBody
    private CompletableFuture<Object[]> getDetailAnalizeYearFuture(@RequestParam(value = "year", required = true) int year) throws Exception {
        logger.info("[getDetailAnalizeYearFuture] year: {}, async: {}, async2: {}", year, testAsync);
        result = testAsync.loadDetailAnalizeYearFuture(year);
        logger.info("[getDetailAnalizeYearFuture] started root");
        stopAnalizeProcess = !result.isDone();
        return result;
    }

    @RequestMapping("/getDetailAnalizeProgressFuture")
    @ResponseBody
    IntProgress getDetailAnalizeProgressFuture(@RequestBody IntProgressWrapper progress) {
        System.out.println("getDetailAnalizeProgress: " + progress.getProgress().getProgress());//) + ": " + phonesMobile.getFileReadProgress());
        int i = 0;
        //if (progress.getProgress().getProgress() == 0)
        //    stopAnalizeProcess = false;
        if (result == null) return new IntProgress();
        stopAnalizeProcess = !result.isDone();
        while (!stopAnalizeProcess && progress.getProgress().getProgress() == (i = testAsync.getProgress())) {
            try {
                Thread.sleep(600);
                //System.out.println("sleep: " + progress.getProgress().getProgress() + ": " + i);
            }
            catch (InterruptedException e) {
                System.out.println("getDetailAnalizeProgress exception: " + e.toString());
            }
        }
        if (stopAnalizeProcess)
            stopAnalizeProcess = false;
        IntProgress res = new IntProgress();
        res.setProgress(i);
        return res;
    }
    */

    @RequestMapping("/getDetailAnalizeProgress")
    @ResponseBody
    IntProgress getDetailAnalizeProgress(@RequestBody IntProgressWrapper progress) {
        System.out.println("getDetailAnalizeProgress: " + progress.getProgress().getProgress());//) + ": " + phonesMobile.getFileReadProgress());
        int i = 0;
        if (progress.getProgress().getProgress() == 0)
            stopAnalizeProcess = false;
        //if (result == null) return new IntProgress();
        //stopAnalizeProcess = !result.isDone();
        while (!stopAnalizeProcess && progress.getProgress().getProgress() == (i = phoneDetailServices.getProgress())) {
            try {
                Thread.sleep(600);
                // System.out.println("sleep: " + progress.getProgress().getProgress() + ": " + i);
            }
            catch (InterruptedException e) {
                System.out.println("getDetailAnalizeProgress exception: " + e.toString());
            }
        }
        if (stopAnalizeProcess)
            stopAnalizeProcess = false;
        IntProgress res = new IntProgress();
        res.setProgress(i);
        return res;
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        System.out.println("PhoneDetail: Spring exception: " + e.toString());
        logger.error(this.getClass().getName(), e);
    }
}
