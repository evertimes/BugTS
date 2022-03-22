package com.evertimes.bugts.controller.issue;

import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.Commentary;
import com.evertimes.bugts.model.dto.Label;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.controlsfx.control.HyperlinkLabel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DeveloperIssueController implements Initializable {
    public ListView commentView;
    public TextField commentField;
    public VBox labels;

    public TextField issueID;
    public TextField projectName;
    public TextField testerName;
    public TextField status;
    public TextField priority;
    public TextField dateAssigned;

    public Button resolve;
    public Button cancelResolve;
    public Button sendDouble;
    public Button notReproduce;

    private MsSqlDAO dao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dao = new MsSqlDAO();
        addLabels();
        refreshIssue();
        addComments();
        if (status.getText().equals("Назначен")) {
            resolve.setDisable(false);
            cancelResolve.setDisable(false);
            sendDouble.setDisable(false);
            notReproduce.setDisable(false);
        } else {
            resolve.setDisable(true);
            cancelResolve.setDisable(true);
            sendDouble.setDisable(true);
            notReproduce.setDisable(true);
        }
    }

    private void setButtonDisabled() {
        resolve.setDisable(true);
        cancelResolve.setDisable(true);
        sendDouble.setDisable(true);
        notReproduce.setDisable(true);
    }

    public void sendComment(ActionEvent actionEvent) {
        dao.addCommentary(Session.developerIssue.getIssueID(),
                commentField.getText());
        commentField.setText("");
        addComments();
    }

    private void refreshIssue() {
        issueID.setText(Integer.toString(Session.developerIssue.getIssueID()));
        projectName.setText(Session.developerIssue.getProjectName());
        testerName.setText(Session.developerIssue.getTesterName());
        status.setText(Session.developerIssue.getStatusName());
        priority.setText(Session.developerIssue.getPriorityName());
        dateAssigned.setText(Session.developerIssue.getDateAssigned().toString());
    }

    private void addComments() {
        commentView.getItems().clear();
        List<Commentary> commentaryList = dao
                .getAllIssueCommentaries(Session.developerIssue.getIssueID());
        for (Commentary commentary : commentaryList) {
            commentView.getItems().add(commentary.getCommentary() + "\t Время: "
                    + commentary.getDateTime().toString());
        }
    }

    private void addLabels() {
        List<Label> labelList = dao
                .getAllIssueLabels(Session.developerIssue.getIssueID());
        for (Label label : labelList) {
            HyperlinkLabel hll = new HyperlinkLabel();
            hll.setText(label.getLabelName());
            hll.setTooltip(new Tooltip(label.getLabelDescription()));
            labels.getChildren().add(hll);
        }

    }

    public void resolve(ActionEvent actionEvent) {
        status.setText("Исправлен");
        dao.setIssueStatus(Session.developerIssue.getIssueID(),3);
        setButtonDisabled();
    }

    public void cancelResolve(ActionEvent actionEvent) {
        status.setText("Не исправлен");
        dao.setIssueStatus(Session.developerIssue.getIssueID(),4);
        setButtonDisabled();
    }

    public void sendDouble(ActionEvent actionEvent) {
        status.setText("Дубль");
        dao.setIssueStatus(Session.developerIssue.getIssueID(),5);
        setButtonDisabled();
    }

    public void notReproduce(ActionEvent actionEvent) {
        status.setText("Не воспроизводимо");
        dao.setIssueStatus(Session.developerIssue.getIssueID(),6);
        setButtonDisabled();
    }
}
