package com.evertimes.bugts.controller.main;

import com.evertimes.bugts.Runner;
import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.Label;
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
    public TableView assignedTable;
    public TableColumn issueId;
    public TableColumn testerName;
    public TableColumn projectName;
    public TableColumn statusName;
    public TableColumn priorityName;
    public TableColumn dateAssigned;
    public TableColumn moreButton;
    public TableView<Label> labelTable;
    public TableColumn<Label,Integer> labelID;
    public TableColumn<Label,String> labelName;
    public TableColumn<Label,String> labelDescription;
    public TextField newLabelName;
    public TextField newLabelDesc;
    public TableColumn deleteLabel;
    HashSet<Label> editedLabels = new HashSet<>();
    HashSet<Label> removedLabels = new HashSet<>();
    HashSet<Label> addedLabels = new HashSet<>();
    MsSqlDAO dao = new MsSqlDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelID.setCellValueFactory(new PropertyValueFactory<>("labelID"));
        labelName.setCellValueFactory(new PropertyValueFactory<>("labelName"));
        labelDescription.setCellValueFactory(new PropertyValueFactory<>("labelDescription"));
        deleteLabel.setCellFactory(removeLabel);
        labelTable.setEditable(true);
        labelName.setCellFactory(TextFieldTableCell.forTableColumn());
        labelDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        labelTable.getItems().addAll(dao.getAllLabels());
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

    public void changeName(TableColumn.CellEditEvent<Label,String> cellEditEvent) {
        Label label = labelTable.getSelectionModel().getSelectedItem();
        label.setLabelName(cellEditEvent.getNewValue());
        editedLabels.add(label);
    }

    public void changeDesc(TableColumn.CellEditEvent<Label,String> cellEditEvent) {
        Label label = labelTable.getSelectionModel().getSelectedItem();
        label.setLabelDescription(cellEditEvent.getNewValue());
        editedLabels.add(label);
    }

    public void saveLabels(ActionEvent actionEvent) {
        for (Label label: editedLabels) {
            dao.updateLabel(label.getLabelID(),
                    label.getLabelName(),
                    label.getLabelDescription());
        }
        editedLabels.clear();
        for (Label label: removedLabels) {
            dao.removeLabel(label.getLabelID());
        }
        removedLabels.clear();
        for (Label label: addedLabels) {
            dao.addLabel(label.getLabelID(), label.getLabelName(), label.getLabelDescription());
        }
        addedLabels.clear();
    }

    public void addLabel(ActionEvent actionEvent) {
        int length = labelTable.getItems().size();
        int id = labelTable.getItems().get(length-1).getLabelID()+1;
        Label label = new Label(id,newLabelName.getText(),newLabelDesc.getText());
        labelTable.getItems().add(label);
        addedLabels.add(label);
        labelTable.refresh();
    }
}




