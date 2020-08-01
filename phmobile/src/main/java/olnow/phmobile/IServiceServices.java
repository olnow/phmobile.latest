package olnow.phmobile;

import java.util.ArrayList;

public interface IServiceServices extends IRootService<Service> {
    Service find(Phones phones, String name, String code);
    ArrayList<Service> get(Phones phones);
}