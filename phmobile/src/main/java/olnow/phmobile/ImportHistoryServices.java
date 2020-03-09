package olnow.phmobile;

import org.hibernate.NonUniqueResultException;

import java.util.ArrayList;

public class ImportHistoryServices extends RootServices<ImportHistory> {
    private ImportHistoryDAO importHistoryDAO = new ImportHistoryDAO();

    public boolean checkImportStatus(String fileName, int year) {
        ImportHistory importHistory;
        try {
            importHistory = importHistoryDAO.find(fileName, year);
        }
        catch (NonUniqueResultException e) {
            return true;
        }
        return importHistory != null;
    }

    public ArrayList<ImportHistory> get(int type, int year) {
        return importHistoryDAO.get(type, year);
    }
}
