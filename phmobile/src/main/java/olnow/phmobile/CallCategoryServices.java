package olnow.phmobile;

public class CallCategoryServices {
    private ReferenceDAO referenceDAO = new ReferenceDAO();

    public void addCallCategory(CallCategory callCategory) {
        referenceDAO.add(callCategory);
    }

    public void updateCallCategory(CallCategory callCategory) {
        referenceDAO.updateReference(callCategory);
    }

    public CallCategory findCallCategory(int idCallCategory) {
        return (CallCategory) referenceDAO.find(idCallCategory);
    }

    //public CallCategory findCallCategory(String callCategoryName) {
    //    return callCategoryDAO.findCallCategory(callCategoryName);
    //}

    public CallCategory findCategory(String name) {
        return (CallCategory) referenceDAO.find(CallCategory.class, CallCategory_.name,
                name, CallCategory.TYPE_CATEGORYCALL);
    }

    public Description findDescription(String name) {
        return (Description) referenceDAO.find(Description.class, Description_.name,
                name, CallCategory.TYPE_DESCRIPTION);
    }

    public CallCategory findOrCreateCallCategory(String callCategoryName) {
        if (callCategoryName == null || callCategoryName.isEmpty())
            return null;
        CallCategory callCategory = findCategory(callCategoryName);
        if (callCategory == null) {
            callCategory = new CallCategory(callCategoryName);
            referenceDAO.add(callCategory);
        }
        return callCategory;
    }

    public Description findOrCreateDescription(String descriptionName) {
        if (descriptionName == null || descriptionName.isEmpty())
            return null;
        Description description = findDescription(descriptionName);
        if (description == null) {
            description = new Description(descriptionName);
            referenceDAO.add(description);
        }
        return description;
    }
}
