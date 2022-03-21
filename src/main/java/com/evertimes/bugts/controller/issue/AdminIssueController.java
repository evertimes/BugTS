package com.evertimes.bugts.controller.issue;

import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.Commentary;
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
    public ComboBox status;
    public ComboBox priority;
    public TextField dateAssigned;
    public ComboBox assignee;

    private MsSqlDAO dao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dao = new MsSqlDAO();
        addLabels();
        refreshIssue();
        addComments();
        addDevs();
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
        status.getItems().add(Session.adminIssue.getStatusName());
        status.getSelectionModel().select(0);
        priority.getItems().add(Session.adminIssue.getPriorityName());
        priority.getSelectionModel().select(0);
        dateAssigned.setText(Session.adminIssue.getDateRegistered().toString());
    }

    private void addDevs(){
        assignee.getItems().addAll(dao.getProjectDevs(Session.adminIssue.getProjectName()));
        assignee.getSelectionModel().select(dao.getCurrentDev(Session.adminIssue.getIssueID()));
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
}
