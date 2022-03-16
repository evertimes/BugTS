package com.evertimes.bugts.controller.issue;

import com.evertimes.bugts.controller.utils.Session;
import com.evertimes.bugts.model.dao.MsSqlDAO;
import com.evertimes.bugts.model.dto.Commentary;
import com.evertimes.bugts.model.dto.Label;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.controlsfx.control.HyperlinkLabel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TesterIssueController implements Initializable {
    public ListView commentView;
    public TextField commentField;
    public HBox labels;

    public TextField issueID;
    public TextField projectName;
    public TextField status;
    public TextField priority;
    public TextField dateCreated;

    MsSqlDAO dao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dao = new MsSqlDAO();
        refreshIssue();
        addComments();
        addLabels();
    }

    public void sendComment(ActionEvent actionEvent) {
        dao.addCommentary(Session.testerIssue.getIssueID(),
                commentField.getText());
        commentField.setText("");
        addComments();
    }

    private void refreshIssue() {
        issueID.setText(Integer.toString(Session.testerIssue.getIssueID()));
        projectName.setText(Session.testerIssue.getProjectName());
        status.setText(Session.testerIssue.getStatusName());
        priority.setText(Session.testerIssue.getPriorityName());
        dateCreated.setText(Session.testerIssue.getDateCreated().toString());
    }

    private void addComments() {
        commentView.getItems().clear();
        List<Commentary> commentaryList = dao
                .getAllIssueCommentaries(Session.testerIssue.getIssueID());
        for (Commentary commentary : commentaryList) {
            commentView.getItems().add(commentary.getCommentary() + "\t Время: "
                    + commentary.getDateTime().toString());
        }
    }

    private void addLabels() {
        List<Label> labelList = dao
                .getAllIssueLabels(Session.testerIssue.getIssueID());
        for (Label label : labelList) {
            HyperlinkLabel hll = new HyperlinkLabel();
            hll.setText(label.getLabelName());
            hll.setTooltip(new Tooltip(label.getLabelDescription()));
            labels.getChildren().add(hll);
        }

    }
}
