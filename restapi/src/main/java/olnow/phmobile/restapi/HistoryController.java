package olnow.phmobile.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import olnow.phmobile.*;

import java.util.ArrayList;

@RestController
class HistoryController {
    private HistoryServices historyServices = new HistoryServices();
    private Logger logger = LoggerFactory.getLogger(HistoryController.class);

    @RequestMapping("/getActiveHistory")
    @ResponseBody
    History getActiveHistory(@RequestBody PhoneWrapper phone) {
        System.out.println("/getActiveHistory:" + phone.getPhone());
        History res;
        if ((res = historyServices.getActiveHistory(phone.getPhone())) == null) {
            HistoryTypeServices historyTypeServices = new HistoryTypeServices();
            HistoryType historyType =
                    historyTypeServices.findHistoryType(HistoryTypeServices.TYPE_NEW_PHONE);
            res = new History();
            res.setPhone(phone.getPhone());
            if (phone.getPhone().getPeople() != null)
                res.setPeople(phone.getPhone().getPeople());
            res.setType(historyType);
        }
        return res;
    }

    @RequestMapping("/getLastHistoryWithCashAnalize")
    @ResponseBody
    private ArrayList<Object> getLastHistoryWithCashAnalize() {
        logger.info("[getLastHistoryWithCashAnalize]");
        return historyServices.getLastHistoryWithCashAnalizeAndServices();
    }
}
