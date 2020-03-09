package olnow.phmobile;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class PeopleSelectForm {
    PeopleServices peopleSrvc = new PeopleServices();
    boolean btnOKPreseed = false;
    @FXML private TextField mTextFieldSearchFIO;
    @FXML private ChoiceBox<People> mChoicePeople;
    @FXML private GridPane rootGrid;
    @FXML private DatePicker mDatePicker;
    @FXML private ChoiceBox mChoiceDepartment;
    @FXML private TextField mTextFieldDepartment;
    @FXML private ChoiceBox mChoicePosition;
    @FXML private TextField mTextFieldPosition, mTextFieldAccount;
    @FXML private CheckBox mCheckServiceAccount;

    public People getPeople() {
        if (mChoicePeople.getSelectionModel().getSelectedIndex() >= 0)
            return mChoicePeople.getSelectionModel().getSelectedItem();
        return null;
    }

    public Date getDate() {
        Date date = java.sql.Date.valueOf(mDatePicker.getValue());
        return date;
    }

    public void setDateStart(Date date) {
        //LocalDate localDate = LocalDate.now(ZoneId.of("UTC"));
        //localDate.
        //mDatePicker.setValue();
    }

    private void updatePeopleList() {
        ArrayList<People> people = peopleSrvc.getPeople();
        if (people != null && !people.isEmpty()) {
            mChoicePeople.getItems().setAll(people);
        }
        else mChoicePeople.getItems().clear();
        ArrayList<String> departments = peopleSrvc.getDepartments();
        if (departments != null && !departments.isEmpty()) {
            mChoiceDepartment.getItems().setAll(departments);
        }
        else mChoiceDepartment.getItems().clear();
        ArrayList<String> positions = peopleSrvc.getPositions();
        if (positions != null && !positions.isEmpty()) {
            mChoicePosition.getItems().setAll(positions);
        }
        else mChoicePosition.getItems().clear();

    }

    @FXML
    private void initialize() {
        mChoicePeople.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<People>() {
            @Override
            public void changed(ObservableValue<? extends People> observable, People oldValue, People newValue) {
                onChoicePeopleScroll(null);
            }
        });

        mChoiceDepartment.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (mChoiceDepartment != null && mChoiceDepartment.getItems() != null)
                    mTextFieldDepartment.setText(mChoiceDepartment.getSelectionModel().getSelectedItem().toString());
            }
        });

        mChoicePosition.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (mChoicePosition != null && mChoicePosition.getItems() != null)
                    mTextFieldPosition.setText(mChoicePosition.getSelectionModel().getSelectedItem().toString());
            }
        });
        updatePeopleList();
        mTextFieldSearchFIO.requestFocus();
        mDatePicker.setValue(LocalDate.now());
    }

    public void selectPeople(People people) {
        mChoicePeople.getSelectionModel().select(people);
    }

    @FXML private void onClickBtnOK () {
        if (mChoicePeople.getSelectionModel().getSelectedIndex() >= 0)
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

    @FXML private void onActionSearch() {
        peopleSrvc.setPeopleFilter(mTextFieldSearchFIO.getText());
        updatePeopleList();
        if (mChoicePeople.getItems().size() == 1) {
            mChoicePeople.getSelectionModel().selectFirst();
        }
        mChoicePeople.requestFocus();
    }

    @FXML private void onChoicePeopleScroll(KeyEvent event) {
        People people = mChoicePeople.getSelectionModel().getSelectedItem();
        if (people == null)
            return;
        mTextFieldDepartment.setText(people.getDepartment());
        mTextFieldPosition.setText(people.getPosition());
        mChoiceDepartment.getSelectionModel().select(people.getDepartment());
        mChoicePosition.getSelectionModel().select(people.getPosition());
        mTextFieldAccount.setText(people.getAccount());
    }

    @FXML private void onClickBtnUpdate() {
        People people = mChoicePeople.getSelectionModel().getSelectedItem();
        if (people == null)
            return;
        if (mTextFieldDepartment.getText() != null && !mTextFieldDepartment.getText().isEmpty())
            people.setDepartment(mTextFieldDepartment.getText());
        if (mTextFieldPosition.getText() != null && !mTextFieldPosition.getText().isEmpty())
            people.setPosition(mTextFieldPosition.getText());
        if (mTextFieldAccount.getText() != null && !mTextFieldAccount.getText().isEmpty())
            people.setAccount(mTextFieldAccount.getText());
        people.setServiceaccount(mCheckServiceAccount.isSelected() ? Phones.STATE_ACTIVE : Phones.STATE_INACTIVE);
        peopleSrvc.updatePeople(people);
    }

    @FXML private void onClickBtnAdd() {
        People people = new People(mTextFieldSearchFIO.getText(),
                mTextFieldAccount.getText(),
                mTextFieldDepartment.getText(),
                mTextFieldPosition.getText(),
                mCheckServiceAccount.isSelected() ? Phones.STATE_ACTIVE : Phones.STATE_INACTIVE);
        peopleSrvc.addPeople(people);
        mChoicePeople.getItems().add(people);
        mChoicePeople.getSelectionModel().select(people);
    }
}
