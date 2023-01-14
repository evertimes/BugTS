package com.evertimes.bugts.controller.main;

import com.evertimes.bugts.Runner;
import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.Label;
import com.evertimes.bugts.model.dto.Project;
import com.evertimes.bugts.model.dto.User;
import com.evertimes.bugts.model.dto.issue.DeveloperIssue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Border;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

public class ZeroController implements Initializable {
    public TableColumn issueId;
    public TableColumn testerName;
    public TableColumn projectName;
    public TableColumn statusName;
    public TableColumn priorityName;
    public TableColumn dateAssigned;
    public TableColumn moreButton;
    public TableView<Label> labelTable;
    public TableColumn<Label, Integer> labelID;
    public TableColumn<Label, String> labelName;
    public TableColumn<Label, String> labelDescription;
    public TextField newLabelName;
    public TextField newLabelDesc;
    public TableColumn deleteLabel;
    public TableColumn userID;
    public TableColumn fullName;
    public TableColumn phone;
    public TableColumn role;
    public TableColumn homeAddress;
    public TableColumn changeUser;
    public TableView<User> userTable;
    public TextField newFullName;
    public TextField newPhone;
    public ComboBox newRole;
    public TextField newHomeAddress;
    public TableView projectTable;
    public TableColumn projectID;
    public TableColumn projectDescription;
    public TableColumn projectStart;
    public TableColumn projectSettings;
    public TextField newProjName;
    public TextField newProjDesc;
    HashSet<Label> editedLabels = new HashSet<>();
    HashSet<Label> removedLabels = new HashSet<>();
    HashSet<Label> addedLabels = new HashSet<>();
    MsSqlDAO dao = new MsSqlDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Labels
        labelID.setCellValueFactory(new PropertyValueFactory<>("labelID"));
        labelName.setCellValueFactory(new PropertyValueFactory<>("labelName"));
        labelDescription.setCellValueFactory(new PropertyValueFactory<>("labelDescription"));
        deleteLabel.setCellFactory(removeLabel);
        labelTable.setEditable(true);
        labelName.setCellFactory(TextFieldTableCell.forTableColumn());
        labelDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        refreshLabels();
        //Users
        userID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));
        homeAddress.setCellValueFactory(new PropertyValueFactory<>("homeAddress"));
        refreshUsers();
        projectSettings.setCellFactory(projectCb);
        projectID.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        projectDescription.setCellValueFactory(new PropertyValueFactory<>("projectDescription"));
        projectStart.setCellValueFactory(new PropertyValueFactory<>("projectStart"));
        refreshProjects();
    }

    private void refreshProjects() {
        projectTable.getItems().clear();
        projectTable.getItems().addAll(dao.getAllProjects());
    }

    public void exit(ActionEvent actionEvent) throws IOException, SQLException {
        Runner.setRoot("login-view");

    }

    private void refreshLabels() {
        labelTable.getItems().clear();
        labelTable.getItems().addAll(dao.getAllLabels());
    }

    private void refreshUsers() {
        userTable.getItems().clear();
        userTable.getItems().addAll(dao.getAllUsers());
    }

    public void changeName(TableColumn.CellEditEvent<Label, String> cellEditEvent) {
        Label label = labelTable.getSelectionModel().getSelectedItem();
        label.setLabelName(cellEditEvent.getNewValue());
        editedLabels.add(label);
    }

    public void changeDesc(TableColumn.CellEditEvent<Label, String> cellEditEvent) {
        Label label = labelTable.getSelectionModel().getSelectedItem();
        label.setLabelDescription(cellEditEvent.getNewValue());
        editedLabels.add(label);
    }

    public void saveLabels(ActionEvent actionEvent) {
        for (Label label : editedLabels) {
            dao.updateLabel(label.getLabelID(),
                    label.getLabelName(),
                    label.getLabelDescription());
        }
        editedLabels.clear();
        for (Label label : removedLabels) {
            dao.removeLabel(label.getLabelID());
        }
        removedLabels.clear();
        for (Label label : addedLabels) {
            dao.addLabel(label.getLabelID(), label.getLabelName(), label.getLabelDescription());
        }
        addedLabels.clear();
    }

    public void addLabel(ActionEvent actionEvent) {
        int length = labelTable.getItems().size();
        int id = labelTable.getItems().get(length - 1).getLabelID() + 1;
        if (!newLabelName.getText().equals("")) {
            Label label = new Label(id, newLabelName.getText(), newLabelDesc.getText());
            labelTable.getItems().add(label);
            addedLabels.add(label);
            labelTable.refresh();
        }
    }


    public void addNewUser(ActionEvent actionEvent) {
        if (!newFullName.getText().equals("") && !newPhone.getText().equals("")
                && !newRole.getSelectionModel().getSelectedItem()
                .toString().equals("")
                && !newHomeAddress.getText().equals("")) {
            dao.addNewUser(newFullName.getText(), newPhone.getText(),
                    newRole.getSelectionModel().getSelectedItem().toString(),
                    newHomeAddress.getText());
        }
        refreshUsers();
    }

    Callback<TableColumn<Label, Void>, TableCell<Label, Void>> removeLabel = new Callback<>() {
        @Override
        public TableCell<Label, Void> call(final TableColumn<Label, Void> param) {
            return new TableCell<Label, Void>() {

                private final Button btn = new Button("Удалить");


                {
                    btn.setBorder(Border.EMPTY);
                    btn.setAlignment(Pos.CENTER);
                    btn.setOnAction((ActionEvent event) -> {
                        Label data = getTableView().getItems().get(getIndex());
                        removedLabels.add(data);
                        labelTable.getItems().remove(data);
                        labelTable.refresh();
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
    Callback<TableColumn<User, Void>, TableCell<User, Void>> changeUserCb = new Callback<>() {
        @Override
        public TableCell<User, Void> call(final TableColumn<User, Void> param) {
            return new TableCell<User, Void>() {

                private final Button btn = new Button("Изменить");


                {
                    btn.setBorder(Border.EMPTY);
                    btn.setAlignment(Pos.CENTER);
                    btn.setOnAction((ActionEvent event) -> {
                        User data = getTableView().getItems().get(getIndex());
                        Stage dialog = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(Runner.class.getResource("zero-user-view.fxml"));
                        try {
                            Scene scene = new Scene(fxmlLoader.load(), 740, 400);
                            dialog.setScene(scene);
                            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.showAndWait();
                            refreshUsers();
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
    Callback<TableColumn<Project, Void>, TableCell<Project, Void>> projectCb = new Callback<>() {
        @Override
        public TableCell<Project, Void> call(final TableColumn<Project, Void> param) {
            return new TableCell<Project, Void>() {

                private final Button btn = new Button("Подробнее");


                {
                    btn.setBorder(Border.EMPTY);
                    btn.setAlignment(Pos.CENTER);
                    btn.setOnAction((ActionEvent event) -> {
                        Project data = getTableView().getItems().get(getIndex());
                        Session.project = data;
                        Stage dialog = new Stage();
                        dialog.setTitle(data.getProjectName());
                        FXMLLoader fxmlLoader = new FXMLLoader(Runner.class.getResource("zero-project-view.fxml"));
                        try {
                            Scene scene = new Scene(fxmlLoader.load(), 940, 400);
                            dialog.setScene(scene);
                            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
                            dialog.initModality(Modality.APPLICATION_MODAL);
                            dialog.showAndWait();
                            refreshUsers();
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

    public void addNewProject(ActionEvent actionEvent) {
        if (!newProjName.getText().equals("") && !newProjDesc.equals("")) {
            dao.addNewProject(newProjName.getText(), newProjDesc.getText());
            refreshProjects();
        }
    }
}




