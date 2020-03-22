package olnow.phmobile.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import olnow.phmobile.Contracts;
import olnow.phmobile.ContractsServices;

import java.util.ArrayList;

@RestController
class Options {
    private Logger logger = LoggerFactory.getLogger(Options.class);
    private ContractsServices contractsServices = new ContractsServices();

    @GetMapping("/getOptions")
    public ResponseEntity getOptions() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/getContracts")
    @ResponseBody
    ArrayList<Contracts> getContracts() {
        logger.info(this.getClass().getName(),"/getContracts");
        return contractsServices.get();
    }
}
