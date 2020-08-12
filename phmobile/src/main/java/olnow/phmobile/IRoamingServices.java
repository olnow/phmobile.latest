package olnow.phmobile;

import javax.persistence.metamodel.SingularAttribute;

public interface IRoamingServices extends IRootService<Roaming> {
    // Object findRoaming(SingularAttribute attr, String name);
    Roaming findOrCreateRoaming(String roamingName);
}
