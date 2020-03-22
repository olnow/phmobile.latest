package olnow.phmobile.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import olnow.phmobile.*;

import java.util.ArrayList;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
class PeopleController {
    private PeopleServices peopleServices = new PeopleServices();
    //PhonesServices phonesServices = new PhonesServices();
    //PhonesMobile phonesMobile = new PhonesMobile();
    private Logger logger = LoggerFactory.getLogger(PeopleController.class);

    @Autowired
    private
    ADUtils adUtils;

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/updatePeople")
    @ResponseBody
    ResponseEntity updatePeople(@RequestBody PeopleWrapper people) {
        System.out.println("updatePeople: " + people.getPeople().getIdpeople());
        peopleServices.updatePeople(people.getPeople());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/updatePeopleAndHistory")
    @ResponseBody
    ResponseEntity updatePeopleAndHistory(@RequestBody HistoryWrapper historyWrapper) {
        logger.info("[updatePeopleAndHistory] people: {}, history: {}",
                //historyWrapper);
                historyWrapper.getHistory().getPeople(), historyWrapper.getHistory());
        peopleServices.updatePeople(historyWrapper.getHistory().getPeople());

        HistoryServices historyServices = new HistoryServices();
        HistoryTypeServices historyTypeServices = new HistoryTypeServices();

        if (historyWrapper.getHistory().getIdhistory() > 0) {
            historyServices.updateHistory(historyWrapper.getHistory());
        }
        else {
            History history = new History();
            history.setPhone(historyWrapper.getHistory().getPhone());
            history.setPeople(historyWrapper.getHistory().getPeople());
            history.setDate(historyWrapper.getHistory().getDatestart());
            HistoryType histType = historyTypeServices.findHistoryType(HistoryTypeServices.TYPE_NEW_PHONE);
            history.setType(histType);
            historyServices.addHistory(history);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @GetMapping("/getPeopleList")
    @ResponseBody
    ArrayList<People> getPeopleList() {
        //peopleServices.getPeople();
        return peopleServices.getPeople();
    }

    @PostMapping("/people/findAD")
    @ResponseBody
    ArrayList<People> findPeopleAD(@RequestParam(name = "fio", required = true) String fio) {
        ArrayList<People> peopleArrayList = adUtils.find(fio);
        if (peopleArrayList == null) return null;
        return peopleServices.findPeopleAndSetID(peopleArrayList);
    }

    @PostMapping("/people/findLocal")
    @ResponseBody
    ArrayList<People> findPeopleLocal(@RequestParam(name = "fio", required = true) String fio) {
        return peopleServices.get(fio);
    }

}
