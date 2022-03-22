package com.evertimes.bugts.controller.issue;

import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.*;
import com.evertimes.bugts.model.dto.Label;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HyperlinkLabel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminIssueController implements Initializable {
    public ListView commentView;
    public TextField commentField;
    public HBox labels;

    public TextField issueID;
    public TextField projectName;
    public TextField testerName;
    public ComboBox<Status> status;
    public ComboBox<Priority> priority;
    public TextField dateAssigned;
    public ComboBox<ProjectDev> assignee;
    public ComboBox labelsToAdd;

    private MsSqlDAO dao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dao = new MsSqlDAO();
        addLabels();
        refreshIssue();
        addComments();
        addDevs();
        addPriorities();
        addStatuses();
    }


    public void sendComment(ActionEvent actionEvent) {
        dao.addCommentary(Session.adminIssue.getIssueID(),
                commentField.getText());
        commentField.setText("");
        addComments();
    }

    private void refreshIssue() {
        issueID.setText(Integer.toString(Session.adminIssue.getIssueID()));
        projectName.setText(Session.adminIssue.getProjectName());
        testerName.setText(Session.adminIssue.getTesterName());
        dateAssigned.setText(Session.adminIssue.getDateRegistered().toString());
    }

    private void addDevs() {
        assignee.getItems().addAll(dao.getProjectDevs(Session.adminIssue.getProjectName()));
        assignee.getSelectionModel().select(dao.getCurrentDev(Session.adminIssue.getIssueID()));
    }

    private void addPriorities() {
        List<Priority> priorities = dao.getAllPriorities();
        priority.getItems().addAll(priorities);
        for (Priority priorityObj : priorities) {
            if (priorityObj.getPriorityName().equals(Session.adminIssue.getPriorityName())) {
                priority.getSelectionModel().select(priorityObj);
                break;
            }
        }
    }

    private void addStatuses() {
        List<Status> statuses = dao.getAllStatuses();
        status.getItems().addAll(statuses);
        for (Status statusObj : statuses) {
            if (statusObj.getStatusName().equals(Session.adminIssue.getStatusName())) {
                status.getSelectionModel().select(statusObj);
                break;
            }
        }
    }

    private void addComments() {
        commentView.getItems().clear();
        List<Commentary> commentaryList = dao
                .getAllIssueCommentaries(Session.adminIssue.getIssueID());
        for (Commentary commentary : commentaryList) {
            commentView.getItems().add(commentary.getCommentary() + "\t Время: "
                    + commentary.getDateTime().toString());
        }
    }

    private void addLabels() {
        List<Label> labelList = dao
                .getAllIssueLabels(Session.adminIssue.getIssueID());
        for (Label label : labelList) {
            HyperlinkLabel hll = new HyperlinkLabel();
            hll.setText(label.getLabelName());
            hll.setTooltip(new Tooltip(label.getLabelDescription()));
            labels.getChildren().add(hll);
        }

    }

    public void updateIssue(ActionEvent actionEvent) {
        System.out.println("LOG controller " + status.getSelectionModel().getSelectedItem().getStatusID());
        dao.updateAdminIssue(Session.adminIssue.getIssueID(),
                assignee.getSelectionModel().getSelectedItem().getUserName(),
                status.getSelectionModel().getSelectedItem().getStatusID(),
                priority.getSelectionModel().getSelectedItem().getPriorityID());
    }

    public void addLabel(ActionEvent actionEvent) {
    }
}
