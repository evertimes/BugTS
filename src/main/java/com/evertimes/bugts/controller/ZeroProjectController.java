package com.evertimes.bugts.controller;

import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ZeroProjectController implements Initializable {
    public TableView userTable;
    public TableColumn userID;
    public TableColumn fullName;
    public TableColumn role;
    public TableColumn phone;
    public TableColumn homeAddress;
    public TableColumn changeUser;
    public TextField userId;
    private MsSqlDAO dao = new MsSqlDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));
        homeAddress.setCellValueFactory(new PropertyValueFactory<>("homeAddress"));
        refreshUsers();
    }

    private void refreshUsers() {
        userTable.getItems().clear();
        userTable.getItems().addAll(dao.getProjectUsers(Session.project.getProjectId()));
    }

    public void addUser(ActionEvent actionEvent) {
        try {
            dao.addUserToProject(Integer.parseInt(userId.getText()), Session.project.getProjectId());
            refreshUsers();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не удается добавить.",
                    ButtonType.OK);
            alert.showAndWait();
        }
    }
}
