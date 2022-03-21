package com.evertimes.bugts.controller.main;

import com.evertimes.bugts.Runner;
import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.issue.AdminIssue;
import com.evertimes.bugts.model.dto.issue.DeveloperIssue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    public TableView notAssignedTable;
    public TableColumn issueId;
    public TableColumn testerName;
    public TableColumn projectName;
    public TableColumn statusName;
    public TableColumn priorityName;
    public TableColumn moreButton;
    public TableColumn dateRegistered;

    MsSqlDAO dao = new MsSqlDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        issueId.setCellValueFactory(new PropertyValueFactory<>("issueID"));
        testerName.setCellValueFactory(new PropertyValueFactory<>("testerName"));
        projectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        statusName.setCellValueFactory(new PropertyValueFactory<>("statusName"));
        priorityName.setCellValueFactory(new PropertyValueFactory<>("priorityName"));
        dateRegistered.setCellValueFactory(new PropertyValueFactory<>("dateRegistered"));
        moreButton.setCellFactory(cellFactory);
        try {
            seeNewIssues(new ActionEvent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) throws IOException, SQLException {
        Runner.setRoot("login-view");

    }

    Callback<TableColumn<AdminIssue, Void>, TableCell<AdminIssue, Void>> cellFactory = new Callback<>() {
        @Override
        public TableCell<AdminIssue, Void> call(final TableColumn<AdminIssue, Void> param) {
            return new TableCell<AdminIssue, Void>() {

                private final Button btn = new Button("Подробнее...");


                {
                    btn.setBorder(Border.EMPTY);
                    btn.setAlignment(Pos.CENTER);
                    btn.setOnAction((ActionEvent event) -> {
                        AdminIssue data = getTableView().getItems().get(getIndex());
                        Session.adminIssue = data;
                        Stage dialog = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(Runner.class.getResource("admin-issue-view.fxml"));
                        try {
                            Scene scene = new Scene(fxmlLoader.load(), 740, 500);
                            dialog.setScene(scene);
                            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.showAndWait();
                            seeNewIssues(new ActionEvent());
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            };
        }
    };

    public void seeNewIssues(ActionEvent actionEvent) throws SQLException {
        notAssignedTable.getItems().clear();
        List<AdminIssue> issues = dao.getNewIssues(Session.userId);
        for (AdminIssue issue : issues) {
            notAssignedTable.getItems().add(issue);
        }
    }

    public void seeWaitIssues(ActionEvent actionEvent) throws SQLException {
        notAssignedTable.getItems().clear();
        List<AdminIssue> issues = dao.getWaitIssues(Session.userId);
        for (AdminIssue issue : issues) {
            notAssignedTable.getItems().add(issue);
        }
    }

    public void seeClosedIssues(ActionEvent actionEvent) throws SQLException {
        notAssignedTable.getItems().clear();
        List<AdminIssue> issues = dao.getClosedIssues(Session.userId);
        for (AdminIssue issue : issues) {
            notAssignedTable.getItems().add(issue);
        }
    }

    public void seeAllIssues(ActionEvent actionEvent) throws SQLException {
        notAssignedTable.getItems().clear();
        List<AdminIssue> issues = dao.getAllIssues(Session.userId);
        for (AdminIssue issue : issues) {
            notAssignedTable.getItems().add(issue);
        }
    }
}
