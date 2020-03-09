package olnow.phmobile;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;

public class DateSelectForm {
    boolean btnOKPreseed = false;
    @FXML private GridPane rootGrid;
    @FXML private DatePicker mDatePicker;
    @FXML private TextField mTextFiledCSVPath;
    @FXML private HBox mHBoxSelectCSV;
    File csvFile;
    private boolean openDialogType = true;

    public Date getDate() {
        Date date = java.sql.Date.valueOf(mDatePicker.getValue());
        return date;
    }

    public void setDialogTypeOpen(boolean openDialogType) {
        this.openDialogType = openDialogType;
    }

    @FXML
    private void initialize() {
        mDatePicker.setValue(LocalDate.now());
    }

    @FXML private void onClickBtnOK () {
        btnOKPreseed = true;
        Stage stage = (Stage) rootGrid.getScene().getWindow();
        stage.close();
    }

    @FXML private void onClickBtnCancel () {
        btnOKPreseed = false;
        Stage stage = (Stage) rootGrid.getScene().getWindow();
        stage.close();
    }

    public boolean getOKPressed() {

        return btnOKPreseed;
    }

    @FXML
    private void onClickBtnSelectFile() {
        FileChooser select_file = new FileChooser();
        FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("Export csv", "*.csv");
        select_file.getExtensionFilters().add(csv);
        select_file.setTitle("Export csv");
        if (!mTextFiledCSVPath.getText().equals(""))
            select_file.setInitialFileName(mTextFiledCSVPath.getText());
        Stage stage = (Stage) mTextFiledCSVPath.getScene().getWindow();
        if (openDialogType)
            csvFile = select_file.showOpenDialog(stage);
        else
            csvFile = select_file.showSaveDialog(stage);
        if (csvFile != null) {
            mTextFiledCSVPath.setText(csvFile.getPath());
            //refreshCSV();
        }
    }

    public File getCSVFile() {
        if (csvFile != null) {
            return csvFile;
        }
        return null;
    }

    public void showFileSelect() {
        mHBoxSelectCSV.setVisible(true);
    }
}
