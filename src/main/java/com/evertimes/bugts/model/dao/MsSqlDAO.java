package com.evertimes.bugts.model.dao;

import com.evertimes.bugts.model.dto.*;
import com.evertimes.bugts.model.dto.issue.AdminIssue;
import com.evertimes.bugts.model.dto.issue.DeveloperIssue;
import com.evertimes.bugts.model.dto.issue.TesterIssue;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MsSqlDAO {
    //TODO: Разработчик
    //Получение всех дефектов для пользователя
    //Получение закрытых дефектов
    //Сортировка по приоритету
    //TODO: Администратор
    //Получение новых дефектов.
    //Присовоение меток
    //Получение всех дефектов (все в удобном виде);
    //Получение всех пользователей
    //Создание пользователя
    //TODO: Тестировщик
    //Добавление нового дефекта
    //TODO: Все
    //Добавление комментария
    Connection connection;

    public MsSqlDAO() {
        connection = ConnectionPool.getConnection();
    }

    public Role getUserRole(int id) throws SQLException {
        ResultSet rs = connection.createStatement()
                .executeQuery("SELECT * " +
                        "FROM Разработчики " +
                        "WHERE IDПользователя = " + id);
        if (rs.next()) {
            rs.close();
            return Role.Developer;
        } else {
            rs.close();
            rs = connection.createStatement()
                    .executeQuery("SELECT * " +
                            "FROM Тестировщики " +
                            "WHERE IDПользователя = " + id);
            if (rs.next()) {
                rs.close();
                return Role.Tester;
            } else {
                rs.close();
                rs = connection.createStatement()
                        .executeQuery("SELECT * " +
                                "FROM Администраторы " +
                                "WHERE IDПользователя = " + id);
                if (rs.next()) {
                    return Role.Administrator;
                } else {
                    rs.close();
                    return Role.Unknown;
                }
            }
        }
    }

    public List<DeveloperIssue> getMyIssues(int id) throws SQLException {
        List<DeveloperIssue> issues = new ArrayList<>();
        try (
                PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(
                        "SELECT Дефекты.IDДефекта,ИмяПроекта,ФИО,ИмяСтатуса,ИмяПриоритета,ДатаИВремяНазначения from Дефекты " +
                                "join Проекты on Дефекты.IDПроекта=Проекты.IDПроекта " +
                                "join Пользователи on Дефекты.IDТестировщика = Пользователи.IDПользователя " +
                                "join Статусы on Дефекты.IDСтатуса=Статусы.IDСтатуса " +
                                "join Приоритеты on Дефекты.IDПриоритета = Приоритеты.IDПриоритета " +
                                "join РазработчикиРешающиеДефекты РРД on Дефекты.IDДефекта = РРД.IDДефекта " +
                                "WHERE РРД.IDПользователя=?"
                );
        ) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    issues.add(new DeveloperIssue(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getTimestamp(6).toLocalDateTime()));
                }
            }
            return issues;
        }
    }

    public List<TesterIssue> getMyFoundIssues(int id) throws SQLException {
        List<TesterIssue> issues = new ArrayList<>();
        try (
                PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(
                        "SELECT Дефекты.IDДефекта, " +
                                "ИмяПроекта, " +
                                "ИмяСтатуса, " +
                                "ИмяПриоритета, " +
                                "ДатаИВремяРегистрации " +
                                "from Дефекты " +
                                "join Проекты on Дефекты.IDПроекта = Проекты.IDПроекта " +
                                "join Статусы on Дефекты.IDСтатуса = Статусы.IDСтатуса " +
                                "join Приоритеты on Дефекты.IDПриоритета = Приоритеты.IDПриоритета " +
                                "WHERE Дефекты.IDТестировщика = ?"
                );
        ) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    issues.add(new TesterIssue(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getTimestamp(5).toLocalDateTime()));
                }
            }
            return issues;
        }
    }

    public List<User> getAllUsers() {
        throw new RuntimeException("Not implemented");
    }

    public List<Commentary> getAllIssueCommentaries(int issueId) {
        List<Commentary> commentaries = new ArrayList<>();
        try (PreparedStatement statement = connection
                .prepareStatement("SELECT " +
                        "Комментарий,ВремяКомментария " +
                        "FROM Комментарии WHERE IDДефекта=? " +
                        "ORDER BY ВремяКомментария")) {
            statement.setInt(1, issueId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    commentaries.add(new Commentary(rs.getString(1),
                            rs.getTimestamp(2).toLocalDateTime()));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return commentaries;
    }

    public List<Label> getAllIssueLabels(int issueId) {
        List<Label> labels = new ArrayList<>();
        try (PreparedStatement statement = connection
                .prepareStatement("" +
                        "SELECT Метки.IDМетки,ИмяМетки,ОписаниеМетки " +
                        "FROM Метки " +
                        "join ДефектыСМетками ДСМ " +
                        "on Метки.IDМетки = ДСМ.IDМетки " +
                        "where IDДефекта = ?")) {
            statement.setInt(1, issueId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    labels.add(new Label(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3)));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return labels;
    }

    public void addNewIssue(String projectName,
                            String commentaryText,
                            int testerID) throws SQLException {
        try (CallableStatement callable = connection
                .prepareCall("{call addNewIssue (?,?,?)}");) {
            callable.setString(1, projectName);
            callable.setString(2, commentaryText);
            callable.setInt(3, testerID);
            callable.executeUpdate();
        }
    }

    public List<String> getMyProjectsNames(int userID) throws SQLException {
        List<String> myProjectNames = new ArrayList<>();
        try (PreparedStatement statement = connection
                .prepareStatement("SELECT ИмяПроекта " +
                        "from Проекты join ПользователиВПроектах ПВП " +
                        "on Проекты.IDПроекта = ПВП.IDПроекта " +
                        "WHERE IDПользователя = ?")) {
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    myProjectNames.add(rs.getString(1));
                }
            }
        }
        return myProjectNames;
    }

    public void assignIssue(int issueId, int userId) {

    }

    public List<AdminIssue> getNewIssues(int adminID) throws SQLException {
        List<AdminIssue> issues = new ArrayList<>();
        try (
                PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(
                        "SELECT Дефекты.IDДефекта, " +
                                "ИмяПроекта, " +
                                "ФИО, " +
                                "ИмяСтатуса, " +
                                "ИмяПриоритета, " +
                                "ДатаИВремяРегистрации " +
                                "from Дефекты " +
                                "join Проекты on Дефекты.IDПроекта = Проекты.IDПроекта " +
                                "join Пользователи on Дефекты.IDТестировщика = Пользователи.IDПользователя " +
                                "join Статусы on Дефекты.IDСтатуса = Статусы.IDСтатуса " +
                                "join Приоритеты on Дефекты.IDПриоритета = Приоритеты.IDПриоритета " +
                                "WHERE Дефекты.IDСтатуса = 1 AND " +
                                "Дефекты.IDПроекта " +
                                "IN (SELECT IDПроекта " +
                                "from ПользователиВПроектах " +
                                "WHERE IDПользователя = ?)")
        ) {
            statement.setInt(1, adminID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    issues.add(new AdminIssue(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getTimestamp(6).toLocalDateTime()));
                }
            }

        }
        return issues;
    }

    public List<AdminIssue> getAllIssues(int adminID) throws SQLException {
        List<AdminIssue> issues = new ArrayList<>();
        try (
                PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(
                        "SELECT Дефекты.IDДефекта, " +
                                "ИмяПроекта, " +
                                "ФИО, " +
                                "ИмяСтатуса, " +
                                "ИмяПриоритета, " +
                                "ДатаИВремяРегистрации " +
                                "from Дефекты " +
                                "join Проекты on Дефекты.IDПроекта = Проекты.IDПроекта " +
                                "join Пользователи on Дефекты.IDТестировщика = Пользователи.IDПользователя " +
                                "join Статусы on Дефекты.IDСтатуса = Статусы.IDСтатуса " +
                                "join Приоритеты on Дефекты.IDПриоритета = Приоритеты.IDПриоритета " +
                                "WHERE Дефекты.IDПроекта " +
                                "IN (SELECT IDПроекта " +
                                "from ПользователиВПроектах " +
                                "WHERE IDПользователя = ?)");
        ) {
            statement.setInt(1, adminID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    issues.add(new AdminIssue(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getTimestamp(6).toLocalDateTime()));
                }
            }
        }
        return issues;
    }

    public List<AdminIssue> getWaitIssues(int adminID) throws SQLException {
        List<AdminIssue> issues = new ArrayList<>();
        try (
                PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(
                        "SELECT Дефекты.IDДефекта, " +
                                "ИмяПроекта, " +
                                "ФИО, " +
                                "ИмяСтатуса, " +
                                "ИмяПриоритета, " +
                                "ДатаИВремяРегистрации " +
                                "from Дефекты " +
                                "join Проекты on Дефекты.IDПроекта = Проекты.IDПроекта " +
                                "join Пользователи on Дефекты.IDТестировщика = Пользователи.IDПользователя " +
                                "join Статусы on Дефекты.IDСтатуса = Статусы.IDСтатуса " +
                                "join Приоритеты on Дефекты.IDПриоритета = Приоритеты.IDПриоритета " +
                                "WHERE Дефекты.IDСтатуса BETWEEN 3 AND 6 " +
                                "AND Дефекты.IDПроекта " +
                                "IN (SELECT IDПроекта " +
                                "from ПользователиВПроектах " +
                                "WHERE IDПользователя = ?)");
        ) {
            statement.setInt(1, adminID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    issues.add(new AdminIssue(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getTimestamp(6).toLocalDateTime()));
                }
            }
        }
        return issues;
    }

    public List<AdminIssue> getClosedIssues(int adminID) throws SQLException {
        List<AdminIssue> issues = new ArrayList<>();
        try (
                PreparedStatement statement = ConnectionPool.getConnection().prepareStatement(
                        "SELECT Дефекты.IDДефекта, " +
                                "ИмяПроекта, " +
                                "ФИО, " +
                                "ИмяСтатуса, " +
                                "ИмяПриоритета, " +
                                "ДатаИВремяРегистрации " +
                                "from Дефекты " +
                                "join Проекты on Дефекты.IDПроекта = Проекты.IDПроекта " +
                                "join Пользователи on Дефекты.IDТестировщика = Пользователи.IDПользователя " +
                                "join Статусы on Дефекты.IDСтатуса = Статусы.IDСтатуса " +
                                "join Приоритеты on Дефекты.IDПриоритета = Приоритеты.IDПриоритета " +
                                "WHERE Дефекты.IDСтатуса=7 " +
                                "AND Дефекты.IDПроекта " +
                                "IN (SELECT IDПроекта " +
                                "from ПользователиВПроектах " +
                                "WHERE IDПользователя = ?)");
        ) {
            statement.setInt(1, adminID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    issues.add(new AdminIssue(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getTimestamp(6).toLocalDateTime()));
                }
            }
        }
        return issues;
    }


    public void addCommentary(int issueID, String commentary) {
        try (CallableStatement procedure = connection
                .prepareCall("{call addNewComment (?,?)}")) {
            procedure.setInt(1, issueID);
            procedure.setString(2, commentary);
            procedure.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<ProjectDev> getProjectDevs(String projectName){
        List<ProjectDev> projectDevs = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement("" +
                "SELECT ПВП.IDПользователя,П.ФИО " +
                "from Разработчики " +
                "join Пользователи П on Разработчики.IDПользователя = П.IDПользователя " +
                "join ПользователиВПроектах ПВП on П.IDПользователя = ПВП.IDПользователя " +
                "join Проекты on ПВП.IDПроекта = Проекты.IDПроекта " +
                "WHERE Проекты.ИмяПроекта = ?")) {
            ps.setString(1,projectName);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) {
                    projectDevs.add(new ProjectDev(rs.getInt(1),
                            rs.getString(2)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projectDevs;
    }

    public ProjectDev getCurrentDev(int issueID){
        try(PreparedStatement ps = connection.prepareStatement("" +
                "SELECT РРД.IDПользователя,Пользователи.ФИО " +
                "from Пользователи " +
                "    join РазработчикиРешающиеДефекты РРД " +
                "on Пользователи.IDПользователя = РРД.IDПользователя " +
                "WHERE РРД.IDДефекта = ?")){
            ps.setInt(1,issueID);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new ProjectDev(rs.getInt(1),rs.getString(2));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void addNewUser(User user, Role role) {

    }

    public void assignUserToProject() {

    }

    public void getMyProjects() {

    }

    public List<DeveloperIssue> getUnassignedIssues() {
        return null;
    }

    public void assignIssueToDeveloper(int issueId, int developerId) {

    }
}
