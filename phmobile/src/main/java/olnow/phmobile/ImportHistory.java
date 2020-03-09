package olnow.phmobile;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "import_history")
public class ImportHistory {
    public static final int TYPE_CASH = 1;
    public static final int TYPE_DETAIL = 2;

    @Id
    @Column(name = "idimport_history")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idImportHistory;

    @Column(name = "filename")
    private String fileName;

    @Column
    private int type;

    @Column(name = "import_date")
    private Timestamp importDate;

    @Column(name = "record_count")
    private int recordCount;

    @Column(name = "record_write")
    private int recordWrite;

    @Column
    private Timestamp month;

    public ImportHistory() {
    }

    public ImportHistory(String fileName, Timestamp importDate, int recordCount, int recordWrite, Timestamp month) {
        this.fileName = fileName;
        this.importDate = importDate;
        this.recordCount = recordCount;
        this.recordWrite = recordWrite;
        this.month = month;
    }

    public ImportHistory(String fileName, int type, Timestamp importDate, int recordCount, int recordWrite, Timestamp month) {
        this.fileName = fileName;
        this.type = type;
        this.importDate = importDate;
        this.recordCount = recordCount;
        this.recordWrite = recordWrite;
        this.month = month;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Timestamp getImportDate() {
        return importDate;
    }

    public void setImportDate(Timestamp importDate) {
        this.importDate = importDate;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getRecordWrite() {
        return recordWrite;
    }

    public void setRecordWrite(int recordWrite) {
        this.recordWrite = recordWrite;
    }

    public Timestamp getMonth() {
        return month;
    }

    public void setMonth(Timestamp month) {
        this.month = month;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
