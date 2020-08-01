package olnow.phmobile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

@Repository
public class HistoryServices {
   private HistoryDAO history = new HistoryDAO();
   //PhonesServices phonesSrvc = new PhonesServices();
   private HistoryTypeServices historyTypeSrvc = new HistoryTypeServices();

   @Autowired
   IServiceServices serviceServices;

   public ArrayList<History> getAllHistory() {
       return this.history.getHistory();
   }
   public void updateHistory(History hist) { history.updateHistory(hist); }
   public void addHistory(History hist) { history.addHistory(hist); }
   public History getActiveHistory(Phones phone) { return history.getActiveHistory(phone); }
   public ArrayList<History> getHistory(Phones phone) { return history.getHistory(phone); }
   public People getPeopleAtDate(Phones phone, Timestamp month) { return history.getPeopleAtDate(phone, month); }

   public void GenerateHistory(Phones phone) {
      History hist;
      if ((hist = getActiveHistory(phone)) == null && phone.getPeople() != null) {
         hist = new History();
         Calendar startyear = Calendar.getInstance();
         startyear.set(Calendar.MONTH, 0);
         startyear.set(Calendar.DAY_OF_MONTH, 1);
         startyear.set(Calendar.MINUTE, 0);
         startyear.set(Calendar.HOUR_OF_DAY, 0);
         startyear.set(Calendar.SECOND, 0);
         startyear.set(Calendar.MILLISECOND, 0);
         Timestamp timestamp = new Timestamp(startyear.getTime().getTime());
         HistoryType histType = historyTypeSrvc.findHistoryType(HistoryTypeServices.TYPE_NEW_PHONE);
         hist.setDate(timestamp);
         hist.setPhone(phone);
         hist.setPeople(phone.getPeople());
         hist.setType(histType);
         addHistory(hist);
      }
   }

   public ArrayList<Object> getLastHistoryWithCashAnalize() {
      return history.getLastHistoryWithCashAnalize();
   }

   public ArrayList<Object> getLastHistoryWithCashAnalizeAndServices() {
      ArrayList<Object> ph = getLastHistoryWithCashAnalize();
      if (ph != null) {
         // ServiceServices serviceServices; // = new ServiceServices();
         ph.forEach(o -> {
            Object[] array = (Object[]) o;
            if (array[0] != null)
               ((Phones) array[0]).setServices(serviceServices.get(((Phones) array[0])));
         });
      }
      return ph;
      //return history.getLastHistoryWithCashAnalizeAndServices();
   }
}
