package olnow.phmobile;

import java.util.ArrayList;

public class TariffDAO extends RootDAO<Tariff> {
    @Override
    public ArrayList<Tariff> get() {
        return get("Tariff");
    }

    public ArrayList<Tariff> getSorted() {
        return get(Tariff.class, Tariff_.name);
    }
}
