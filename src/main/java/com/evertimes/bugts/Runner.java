package com.evertimes.bugts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Runner extends Application {
    private static Scene scene;
    private static Stage stageAccessor;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Runner.class.getResource("login-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 420, 300);
        stage.setTitle("Система остлеживания ошибок");
        stageAccessor = stage;
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent getRoot() {
        return scene.getRoot();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader;
        if (fxml.compareTo("login-view") == 0) {
            stageAccessor.setWidth(420);
            stageAccessor.setHeight(300);
            stageAccessor.setTitle("Система остлеживания ошибок");
            fxmlLoader = new FXMLLoader(Runner.class.getResource(fxml + ".fxml"));
        } else if (fxml.compareTo("developer-view") == 0) {
            stageAccessor.setTitle("Консоль разработчика");
            stageAccessor.setWidth(900);
            stageAccessor.setHeight(500);
            URL myUrl = Runner.class.getResource(fxml + ".fxml");
            fxmlLoader = new FXMLLoader(myUrl);
        } else if (fxml.compareTo("tester-view") == 0) {
            stageAccessor.setTitle("Консоль тестировщика");
            stageAccessor.setWidth(650);
            stageAccessor.setHeight(550);
            URL myUrl = Runner.class.getResource(fxml + ".fxml");
            fxmlLoader = new FXMLLoader(myUrl);
        } else if (fxml.compareTo("admin-view") == 0) {
            stageAccessor.setTitle("Консоль администратора");
            stageAccessor.setWidth(900);
            stageAccessor.setHeight(500);
            URL myUrl = Runner.class.getResource(fxml + ".fxml");
            fxmlLoader = new FXMLLoader(myUrl);
        } else if (fxml.compareTo("zero-view") == 0) {
            stageAccessor.setTitle("Управление");
            stageAccessor.setWidth(900);
            stageAccessor.setHeight(500);
            URL myUrl = Runner.class.getResource(fxml + ".fxml");
            fxmlLoader = new FXMLLoader(myUrl);
        }else {
            throw new UnsupportedOperationException("no such view");
        }
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}