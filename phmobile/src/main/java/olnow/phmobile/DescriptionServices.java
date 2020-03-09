package olnow.phmobile;


public class DescriptionServices extends RootServices<Description> {
    private ReferenceDAO referenceDAO = new ReferenceDAO();

    public Description findDescription(String name) {
        return (Description) referenceDAO.find(Description.class, Description_.name,
                name, CallCategory.TYPE_DESCRIPTION);
    }

    public Description findOrCreate(String name) {
        if (name == null || name.isEmpty())
            return null;
        Description description = findDescription(name);
        if (description == null) {
            description = new Description(name);
            add(description);
        }
        return description;
    }

    public Description findOrCreate(String name, int direction) {
        if (name == null || name.isEmpty())
            return null;
        Description description = findDescription(name);
        if (description == null) {
            description = new Description(name, direction);
            add(description);
        }
        return description;
    }
}
