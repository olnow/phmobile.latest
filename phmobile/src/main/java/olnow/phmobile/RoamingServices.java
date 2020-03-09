package olnow.phmobile;

import java.util.ArrayList;

public class RoamingServices {
    private RoamingDAO roamingDAO = new RoamingDAO();

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
    }

    public Roaming findOrCreateRoaming(String roamingName) {
        if (roamingName == null || roamingName.isEmpty())
            return null;
        Roaming roaming = findRoaming(roamingName);
        if (roaming == null) {
            roaming = new Roaming(roamingName);
            roamingDAO.addRoaming(roaming);
        }
        return roaming;
    }
}