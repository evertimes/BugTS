package com.evertimes.bugts.controller;

import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.Runner;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    public TextField loginField;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onLoginButtonClick() {
        int id = Integer.parseInt(loginField.getText());
        tryLogin(id);
    }

    private void tryLogin(int id) {
        try {
            Session.userId = id;
            switch (new MsSqlDAO().getUserRole(id)) {
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
        } catch (IOException e) {
            System.err.println("IO EXCEPTION!");
        } catch (SQLException e) {
            System.err.println("XMLEXC!");
        }
    }
}