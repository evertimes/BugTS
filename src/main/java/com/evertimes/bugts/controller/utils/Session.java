package com.evertimes.bugts.controller.utils;

import com.evertimes.bugts.model.dto.Project;
import com.evertimes.bugts.model.dto.Role;
import com.evertimes.bugts.model.dto.issue.AdminIssue;
import com.evertimes.bugts.model.dto.issue.DeveloperIssue;
import com.evertimes.bugts.model.dto.issue.TesterIssue;

public class Session {
    public static int userId;
    public static Role userRole;
    public static DeveloperIssue developerIssue;
    public static AdminIssue adminIssue;
    public static TesterIssue testerIssue;
    public static Project project;
}
