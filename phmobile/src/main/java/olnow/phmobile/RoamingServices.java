package olnow.phmobile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("RoamingServices")
public class RoamingServices extends RootServiceImpl<Roaming> implements IRoamingServices {

    @Autowired
    RoamingServices(IRoamingDAO roamingDAO) {
        super(roamingDAO);
    }
    // private RoamingDAO roamingDAO = new RoamingDAO();

    /*
    public void addRoaming(Roaming roaming) {
        roamingDAO.addRoaming(roaming);
    }

    public void updateRoaming(Roaming roaming) {
        roamingDAO.updateRoaming(roaming);
    }

    public Roaming findRoaming(int idroaming) {
        return roamingDAO.findRoaming(idroaming);
    }

    public Roaming findRoaming(String roamingname) {
        return (Roaming) roamingDAO.findRoaming(Roaming.class, Roaming_.roaming, roamingname);
    }

    public ArrayList<Roaming> getRoaming() {
        return roamingDAO.getRoaming();
    }*/

    public Roaming findOrCreateRoaming(String roamingName) {
        if (roamingName == null || roamingName.isEmpty())
            return null;
        Roaming roaming = find(Roaming_.roaming, roamingName);
        if (roaming == null) {
            roaming = new Roaming(roamingName);
            rootDAO.add(roaming);
        }
        return roaming;
    }
}