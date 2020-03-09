package olnow.phmobile;

public class HistoryTypeServices {
    private HistoryTypeDAO historyTypeDAO = new HistoryTypeDAO();
    public final static int TYPE_NEW_PHONE = 1;
    public final static int TYPE_BLOCK_PHONE = 2;

    public void addHistoryType(HistoryType historyType) { historyTypeDAO.addHistoryType(historyType); }
    public void updateHistoryType(HistoryType historyType) { historyTypeDAO.updateHistoryType(historyType); }
    public HistoryType findHistoryType(int idhistorytype) { return historyTypeDAO.findHistoryType(idhistorytype); }

}
