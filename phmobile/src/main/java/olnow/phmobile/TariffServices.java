package olnow.phmobile;

import olnow.phmobile.dao.TariffDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

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

@Repository
@Qualifier("TariffServices")
public class TariffServices extends RootServiceImpl<Tariff> implements IRootService<Tariff> {
    // private IRootDAO rootTariffDAO; // = new TariffDAO();

    @Autowired
    TariffServices(TariffDAO rootDAO) {
        super(rootDAO);
        // rootTariffDAO = rootDAO;
    }

    //@Override
    // public TariffDAO getRootDAO() {
    //    return rootDAO;
    //}

    /*
    @Override
    public ArrayList<Tariff> get() {
        return rootDAO.get();
    }

    public ArrayList<Tariff> getSorted() {
        return rootDAO.get(Tariff_.name);
    }*/
}