package olnow.phmobile;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;

@Repository
@Qualifier("TariffDAO")
public class TariffDAO extends RootDAOImpl<Tariff> implements IRootDAO<Tariff> {
    TariffDAO() {
        super(Tariff.class);
    }

    /*
    @Override
    public ArrayList<Tariff> get() {

        return get(); //get("Tariff");
    }

    @Override
    public ArrayList<Tariff> getSorted(SingularAttribute orderBy) {
        return get(Tariff.class, Tariff_.name);
    }*/
}
