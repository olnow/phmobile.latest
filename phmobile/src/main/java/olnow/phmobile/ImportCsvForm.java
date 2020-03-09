package olnow.phmobile;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ImportCsvForm {
    @FXML private TextField mTextFiledCSVPath, mTextFiledTemplate;
    @FXML private TextArea mTextArea, mTextParseResult;
    @FXML private DatePicker mDatePicker;
    private boolean btnOKPreseed = false;
    private String separator = ";";
    private File csvFile;
    private String extension = "*.csv";
    private final String dbfExtension = "*.dbf";
    private boolean openDialog = true;


    public void setTemplate(String template) {
        mTextFiledTemplate.setText(template);
    }

    public String getTemplate() { return mTextFiledTemplate.getText(); }

    public void setExtensionDBF() {
        extension = dbfExtension;
        mDatePicker.setVisible(true);
    }

    public void setDialogTypeOpen(boolean openDialog) {
        this.openDialog = openDialog;
    }

    public Date getDate() {
        Date date = java.sql.Date.valueOf(mDatePicker.getValue());
        return date;
    }

    @FXML
    private void onClickBtnSelectFile() {
        FileChooser select_file = new FileChooser();
        FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("Import file ", extension);

        select_file.getExtensionFilters().add(csv);
        select_file.setTitle("Open file");
        if (!mTextFiledCSVPath.getText().equals(""))
            select_file.setInitialFileName(mTextFiledCSVPath.getText());
        Stage stage = (Stage) mTextFiledCSVPath.getScene().getWindow();
        if (openDialog)
            csvFile = select_file.showOpenDialog(stage);
        else
            csvFile = select_file.showSaveDialog(stage);
        if (csvFile != null) {
            mTextFiledCSVPath.setText(csvFile.getPath());
            refreshCSV();
            if (extension.equals(dbfExtension)) {
                LocalDate currentDate = LocalDate.now();
                try {
                    String fileName = csvFile.getName().split("\\.")[0];
                    String stmonth = fileName.substring(fileName.length() - 2);
                    LocalDate importDate = LocalDate.of(currentDate.getYear(),
                            Integer.parseInt(stmonth),
                            1);
                    mDatePicker.setValue(importDate);
                } catch (Exception e) {
                    System.out.println("ImportCsvForm: onClickBtnSelectFile: Convert month:" + e.toString());
                }
            }
        }
    }

    @FXML private void onClickBtnRefresh() {
        refreshCSV();
    }

    private void refreshCSV() {
        mTextArea.setText("");
        mTextParseResult.setText("");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(csvFile));
            String line = reader.readLine();
            byte maxreadline = 7;
            byte linenum = 0;
            boolean endfile = false;
            String[] template_values = mTextFiledTemplate.getText().split(";");
            while (line != null && linenum < maxreadline) {
                mTextArea.appendText(linenum + ": " + line + "\n");

                String[] values = line.split(separator);

                Map<String, String> templatemap = new HashMap<>();

                String newline;
                boolean compare;
                if ((compare = (mTextFiledTemplate.getText().lastIndexOf(";") == mTextFiledTemplate.getText().length()-1)
                    &&(line.lastIndexOf(";") == line.length()-1))
                    || endfile || !compare) {
                    //phones.addParce(line, separator, phonesTemplate);
                    for (int i=0; i<template_values.length ; i++) {
                        if (template_values[i]!=null && !template_values[i].equals(""))
                            templatemap.put(template_values[i], PhonesServices.trimSymvols(values[i]));
                    }

                    if (!templatemap.containsKey("phone") && (!templatemap.containsKey("fio")))
                        mTextParseResult.appendText("Line " + linenum + " spipped\n");
                    else
                        mTextParseResult.appendText(linenum + ": " + templatemap.toString()+ "\n");

                    line = reader.readLine();
                    continue;
                }
                /*
                if (!templatemap.containsKey("phone") && (!templatemap.containsKey("fio")))
                    mTextParseResult.appendText("Line " + linenum + " spipped\n");
                else
                    mTextParseResult.appendText(linenum + ": " + templatemap.toString()+ "\n");
                */
                newline = reader.readLine();
                if (newline == null) {
                    endfile = true;
                }
                else
                    line = line.concat(newline);

                linenum++;
                //line = reader.readLine();
            }
            if (reader!=null)
                reader.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @FXML
    private void initialize() {
        mDatePicker.setValue(LocalDate.now());
    }

    public void show() {
        /*try {
            Parent importcsv = FXMLLoader.load(getClass().getClassLoader().getResource("importcsvform.fxml"));
            stage = new Stage();//(Stage) mTablePhones.getScene().getWindow();
            stage.setScene(new Scene(importcsv, 400, 200));
            stage.showAndWait();
        }
        catch (Exception e) { System.out.println(e); }
        //stage.showAndWait();
        */
    }

    @FXML private void onClickBtnOK () {
        btnOKPreseed = true;
        Stage stage = (Stage) mTextArea.getScene().getWindow();
        stage.close();
    }

    @FXML private void onClickBtnCancel () {
        btnOKPreseed = false;
        Stage stage = (Stage) mTextArea.getScene().getWindow();
        stage.close();
    }

    public boolean getOKPressed() {
        return btnOKPreseed;
    }

    public File getCSVFile() {
        if (csvFile != null) {
            return csvFile;
        }
        return null;
    }
}
