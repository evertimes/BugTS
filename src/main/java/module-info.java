module com.evertimes.bugts {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.microsoft.sqlserver.jdbc;

    opens com.evertimes.bugts to javafx.fxml;
    exports com.evertimes.bugts.model.dto;
    exports com.evertimes.bugts;
    exports com.evertimes.bugts.model.dto.issue;
    exports com.evertimes.bugts.controller;
    opens com.evertimes.bugts.controller to javafx.fxml;
    exports com.evertimes.bugts.model.dao;
    opens com.evertimes.bugts.model.dao to javafx.fxml;
    exports com.evertimes.bugts.controller.issue;
    opens com.evertimes.bugts.controller.issue to javafx.fxml;
    opens com.evertimes.bugts.model.dto.issue to javafx.base;
    exports com.evertimes.bugts.controller.main;
    opens com.evertimes.bugts.controller.main to javafx.fxml;
    opens com.evertimes.bugts.model.dto to javafx.base, javafx.fxml;
    exports com.evertimes.bugts.controller.utils;
    opens com.evertimes.bugts.controller.utils to javafx.fxml;
}