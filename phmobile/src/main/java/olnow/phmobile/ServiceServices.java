package olnow.phmobile;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServiceServices implements RootServicesInterface {
    ServiceDAO serviceRootDAO = new ServiceDAO();
    PhonesServices phonesServices = new PhonesServices();
    Logger logger = LoggerFactory.getLogger(ServiceServices.class);

    @Override
    public RootDAO getRootDAO() {
        return serviceRootDAO;
    }

    public Service find(Phones phones, String name, String code) {
        return serviceRootDAO.find(phones, name, code);
    }

    public ArrayList<Service> get(Phones phones) {
        return serviceRootDAO.get(phones);
    }

    public void addService(Map<String, String> maps) throws NotFoundException {
        if (maps == null)
            return;

        Phones phones = phonesServices.findPhone(maps.get("phone"));
        if (phones == null) {
            logger.error("Phone not found: {} ", maps.get("phone"));
            throw new NotFoundException("Phone not found: " + maps.get("phone"));
        }
        int state = -1;
        if (maps.get("status") != null && !maps.get("status").isEmpty()) {
            logger.debug(maps.get("status"), maps.get("status"), maps.get("status").equals(Service.STATE_ACTIVE_NAME));
            if (maps.get("status").equals(Service.STATE_ACTIVE_NAME))
                state = Phones.STATE_ACTIVE;
            else
                state = Phones.STATE_INACTIVE;
        }
        if (phones.getState() != state) {
            phones.setState(Phones.STATE_ACTIVE);
            phonesServices.updatePhone(phones);
        }
        //phone;;;status;status_date;status_cause;description;tariff_date_start;
        // tariif_date_end;tariff_code;phone_date_start
        Service service;
        if (maps.get("tariff_code") != null && !maps.get("tariff_code").isEmpty() &&
            maps.get("description") != null && !maps.get("description").isEmpty()) {

            if ((service = find(phones, maps.get("description"), maps.get("tariff_code"))) == null)
                service = new Service(phones, maps.get("description"), maps.get("tariff_code"));
            //phones.setTariff(maps.get("tariff_code"));
        } else {
            logger.error("Empty import data for service: {} ", maps.get("phone"));
            throw new NotFoundException("Empty import data for service: " + maps.get("phone"));
        };
        Timestamp dateStart, dateEnd;
        if (maps.get("tariff_date_start") != null && !maps.get("tariff_date_start").isEmpty()) {
            try {
                DateFormat parser = new SimpleDateFormat("dd.MM.yyyy");
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date timeParsed = parser.parse(maps.get("tariff_date_start")); //.replaceAll("/", ".") + " " + maps.get("time"));
                dateStart = new Timestamp(timeParsed.getTime());
                service.setDatestart(dateStart);
            } catch (Exception e) {
                logger.error("Error in dateStart parsing: {}", maps.get("tariff_date_start"), e);
            }
        }
        if (maps.get("tariff_date_end") != null && !maps.get("tariff_date_end").isEmpty()) {
            try {
                DateFormat parser = new SimpleDateFormat("dd.MM.yyyy");
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date timeParsed = parser.parse(maps.get("tariff_date_end")); //.replaceAll("/", ".") + " " + maps.get("time"));
                dateEnd = new Timestamp(timeParsed.getTime());
                service.setDateend(dateEnd);
            } catch (Exception e) {
                logger.error("Error in dateEnd parsing: {}", maps.get("tariff_date_end"), e);
            }
        }
        if (service != null)
            addOrUpdate(service);
    }


}
