package olnow.phmobile;

import java.sql.Timestamp;

public class ReadFileResult {
    private int recordCount;
    private int recordWrite;
    private Timestamp date;

    public ReadFileResult(int recordCount, int recordWrite) {
        this.recordCount = recordCount;
        this.recordWrite = recordWrite;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
