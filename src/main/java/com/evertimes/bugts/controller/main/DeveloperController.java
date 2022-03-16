package com.evertimes.bugts.controller.main;

import com.evertimes.bugts.Runner;
import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.issue.DeveloperIssue;
import javafx.event.ActionEvent;
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

public class DeveloperController implements Initializable {
    public TableView assignedTable;
    public TableColumn issueId;
    public TableColumn testerName;
    public TableColumn projectName;
    public TableColumn statusName;
    public TableColumn priorityName;
    public TableColumn dateAssigned;
    public TableColumn moreButton;

    MsSqlDAO dao = new MsSqlDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        issueId.setCellValueFactory(new PropertyValueFactory<>("issueID"));
        testerName.setCellValueFactory(new PropertyValueFactory<>("testerName"));
        projectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        statusName.setCellValueFactory(new PropertyValueFactory<>("statusName"));
        priorityName.setCellValueFactory(new PropertyValueFactory<>("priorityName"));
        dateAssigned.setCellValueFactory(new PropertyValueFactory<>("dateAssigned"));
        moreButton.setCellFactory(cellFactory);
        try {
            refreshIssues();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) throws IOException, SQLException {
        Runner.setRoot("login-view");

    }

    private void refreshIssues() throws SQLException {
        assignedTable.getItems().clear();
        List<DeveloperIssue> issues = dao.getMyIssues(Session.userId);
        for (DeveloperIssue issue : issues) {
            assignedTable.getItems().add(issue);
        }
    }

    Callback<TableColumn<DeveloperIssue, Void>, TableCell<DeveloperIssue, Void>> cellFactory = new Callback<>() {
        @Override
        public TableCell<DeveloperIssue, Void> call(final TableColumn<DeveloperIssue, Void> param) {
            return new TableCell<DeveloperIssue, Void>() {

                private final Button btn = new Button("Подробнее...");


                {
                    btn.setBorder(Border.EMPTY);
                    btn.setAlignment(Pos.CENTER);
                    btn.setOnAction((ActionEvent event) -> {
                        DeveloperIssue data = getTableView().getItems().get(getIndex());
                        Session.developerIssue = data;
                        Stage dialog = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(Runner.class.getResource("developer-issue-view.fxml"));
                        try {
                            Scene scene = new Scene(fxmlLoader.load(), 740, 400);
                            dialog.setScene(scene);
                            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.showAndWait();
                            refreshIssues();
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
}




