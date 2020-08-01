package olnow.phmobile;

import java.util.ArrayList;

public interface IServiceDAO<T> extends IRootDAO<T> {
    T find(Phones phones, String name, String code);
    ArrayList<Service> get(Phones phones);
}
