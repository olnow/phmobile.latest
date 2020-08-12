package olnow.phmobile;


import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.Map;

public interface IServiceServices extends IRootService<Service> {
    Service find(Phones phones, String name, String code);
    ArrayList<Service> get(Phones phones);
    void addService(Map<String, String> maps) throws NotFoundException;
}