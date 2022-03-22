CREATE PROC addNewIssue @ИмяПроекта nvarchar(20),
                        @Комментарий nvarchar(200),
                        @IDТестировщика int as
Declare
    @IDДефекта int = (SELECT max(IDДефекта)
                      from Дефекты) + 1;
INSERT INTO Дефекты
VALUES (@IDДефекта,
        (SELECT Проекты.IDПроекта from Проекты WHERE Проекты.ИмяПроекта = @ИмяПроекта),
        @IDТестировщика, 1, 1, SYSDATETIME())
INSERT INTO Комментарии
VALUES ((SELECT max(IDКомментария) from Комментарии) + 1,
        @Комментарий, @IDДефекта, SYSDATETIME())
GO
CREATE PROC addNewComment @IDДефекта int, @Комментарий nvarchar(200) AS
    INSERT INTO Комментарии VALUES ((SELECT MAX(IDКомментария) from Комментарии)+1,
                                    @Комментарий,@IDДефекта,SYSDATETIME())
GO
DROP PROC updateAdminIssue
GO
CREATE PROC updateAdminIssue
    @issueID int,
    @developerName nvarchar(50),
    @statusID int,
    @priorityID int
AS
UPDATE Дефекты SET Дефекты.IDСтатуса = @statusID
WHERE IDДефекта = @issueID

UPDATE Дефекты SET Дефекты.IDПриоритета = @priorityID
WHERE IDДефекта = @issueID

DECLARE @userID int = (SELECT IDПользователя from Пользователи WHERE ФИО=@developerName)

DELETE from РазработчикиРешающиеДефекты WHERE IDДефекта = @issueID

INSERT INTO РазработчикиРешающиеДефекты VALUES (@issueID,@userID,SYSDATETIME())

GO
SELECT ПВП.IDПользователя,П.ФИО from Разработчики
    join Пользователи П on Разработчики.IDПользователя = П.IDПользователя
    join ПользователиВПроектах ПВП on П.IDПользователя = ПВП.IDПользователя
    WHERE ПВП.IDПроекта = ?

SELECT РРД.IDПользователя,П.ФИО from Разработчики
    join Пользователи П on Разработчики.IDПользователя = П.IDПользователя
    join РазработчикиРешающиеДефекты РРД on Разработчики.IDПользователя = РРД.IDПользователя
WHERE РРД.IDДефекта = ?

