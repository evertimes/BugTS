package com.evertimes.bugts.controller;

import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.Runner;
import com.evertimes.bugts.model.dao.ConnectionPool;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public TextField loginField;
    public TextField passwordField;
    public TextField connectionField;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onLoginButtonClick() {
        ConnectionPool.connectionString = connectionField.getText();
        try {
            int id = Integer.parseInt(loginField.getText());
            tryLogin(id);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Неверный логин или пароль, попробуйте еще раз", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void tryLogin(int id) {
        MsSqlDAO dao = new MsSqlDAO();
        if (!dao.isReady) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не удается подключиться к БД", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        try {
            Session.userId = id;
            if (id == 0 && passwordField.getText().equals("0")) {
                Runner.setRoot("zero-view");
                return;
            }
            String realPass = dao.getPassword(id);
            if (!realPass.equals(passwordField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Неверный логин или пароль, попробуйте еще раз", ButtonType.OK);
                alert.showAndWait();
            } else {
                switch (dao.getUserRole(id)) {
                    case Developer:
                        Runner.setRoot("developer-view");
                        break;
                    case Tester:
                        Runner.setRoot("tester-view");
                        break;
                    case Administrator:
                        Runner.setRoot("admin-view");
                        break;
                    case Unknown:
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } catch (SQLException e) {
            System.err.println("XMLEXC!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectionField.setText("jdbc:sqlserver://Andrey\\MSSQLSERVER;database=BugTracker;user=sa;password=1234");
    }
}