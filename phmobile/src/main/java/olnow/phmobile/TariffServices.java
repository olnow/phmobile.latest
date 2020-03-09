package olnow.phmobile;

import java.util.ArrayList;

/*
public class TariffServices extends RootServices<Tariff> {
    private TariffDAO rootDAO = new TariffDAO();

    @Override
    public ArrayList<Tariff> get() {
        return rootDAO.get();
    }

    public ArrayList<Tariff> getSorted() {
        return rootDAO.getSorted();
    }
}*/

public class TariffServices implements RootServicesInterface<Tariff> {
    private TariffDAO rootDAO = new TariffDAO();

    @Override
    public TariffDAO getRootDAO() {
        return rootDAO;
    }

    @Override
    public ArrayList<Tariff> get() {
        return getRootDAO().get();
    }

    public ArrayList<Tariff> getSorted() {
        return getRootDAO().getSorted();
    }
}