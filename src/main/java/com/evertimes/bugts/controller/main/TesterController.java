package com.evertimes.bugts.controller.main;

import com.evertimes.bugts.Runner;
import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.issue.TesterIssue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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

public class TesterController implements Initializable {
    public TableView foundTable;
    public TableColumn hiddenCol;
    public TableColumn issueId;
    public TableColumn projectName;
    public TableColumn statusName;
    public TableColumn priorityName;
    public TableColumn dateCreated;
    public TableColumn moreButton;
    public ComboBox projectNamePrompt;
    public TextArea commentaryPrompt;

    MsSqlDAO dao = new MsSqlDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        issueId.setCellValueFactory(new PropertyValueFactory<>("issueID"));
        projectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        statusName.setCellValueFactory(new PropertyValueFactory<>("statusName"));
        priorityName.setCellValueFactory(new PropertyValueFactory<>("priorityName"));
        dateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        moreButton.setCellFactory(cellFactory);
        try {
            refreshIssues();
            projectNamePrompt.setItems(FXCollections.observableList(dao.getMyProjectsNames(Session.userId)));
            projectNamePrompt.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) throws IOException, SQLException {
        Runner.setRoot("login-view");

    }

    private void refreshIssues() throws SQLException {
        foundTable.getItems().clear();
        int id = Session.userId;
        List<TesterIssue> issues = dao.getMyFoundIssues(id);
        for (TesterIssue issue : issues) {
            foundTable.getItems().add(issue);
        }
    }

    Callback<TableColumn<TesterIssue, Void>, TableCell<TesterIssue, Void>> cellFactory = new Callback<>() {
        @Override
        public TableCell<TesterIssue, Void> call(final TableColumn<TesterIssue, Void> param) {
            return new TableCell<TesterIssue, Void>() {

                private final Button btn = new Button("Подробнее...");


                {
                    btn.setBorder(Border.EMPTY);
                    btn.setAlignment(Pos.CENTER);
                    btn.setOnAction((ActionEvent event) -> {
                        TesterIssue data = getTableView().getItems().get(getIndex());
                        Session.testerIssue = data;
                        Stage dialog = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(Runner.class.getResource("tester-issue-view.fxml"));
                        try {
                            Scene scene = new Scene(fxmlLoader.load(), 740, 400);
                            dialog.setScene(scene);
                            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.showAndWait();
                        } catch (IOException e) {
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

    public void addNewIssue(ActionEvent actionEvent) throws SQLException {
        String projectName = projectNamePrompt.getValue().toString();
        String commentary = commentaryPrompt.getText();
        dao.addNewIssue(projectName, commentary, Session.userId);
        refreshIssues();
    }
}
