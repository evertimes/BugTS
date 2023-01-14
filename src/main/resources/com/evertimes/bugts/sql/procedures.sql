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
INSERT INTO Комментарии
VALUES ((SELECT MAX(IDКомментария) from Комментарии) + 1,
        @Комментарий, @IDДефекта, SYSDATETIME())
GO
CREATE PROC updateAdminIssue @issueID int,
                             @developerName nvarchar(50),
                             @statusID int,
                             @priorityID int
AS
UPDATE Дефекты
SET Дефекты.IDСтатуса = @statusID
WHERE IDДефекта = @issueID

UPDATE Дефекты
SET Дефекты.IDПриоритета = @priorityID
WHERE IDДефекта = @issueID

DECLARE
    @userID int = (SELECT IDПользователя
                   from Пользователи
                   WHERE ФИО = @developerName)
print(@userID)
DELETE
from РазработчикиРешающиеДефекты
WHERE IDДефекта = @issueID

INSERT INTO РазработчикиРешающиеДефекты
VALUES (@issueID, @userID, SYSDATETIME())
GO
CREATE PROC getAllUsers AS
SELECT Пользователи.IDПользователя, ФИО, Телефон, ДомашнийАдрес, N'Разработчик' as 'Роль'
FROM Пользователи
         JOIN Разработчики Р on Пользователи.IDПользователя = Р.IDПользователя
UNION
SELECT Пользователи.IDПользователя, ФИО, Телефон, ДомашнийАдрес, N'Тестировщик' as 'Роль'
FROM Пользователи
         JOIN Тестировщики Т on Пользователи.IDПользователя = Т.IDПользователя
UNION
SELECT Пользователи.IDПользователя, ФИО, Телефон, ДомашнийАдрес, N'Администратор' as 'Роль'
FROM Пользователи
         JOIN Администраторы А on Пользователи.IDПользователя = А.IDПользователя
GO
CREATE PROC addNewUser @FullName nvarchar(50),
                       @Phone nvarchar(20),
                       @Role nvarchar(20), @Home nvarchar(100)
AS
Declare
    @NextIndex int = (SELECT MAX(IDПользователя)
                      from Пользователи) + 1;
INSERT INTO Пользователи
VALUES (@NextIndex, @FullName, @Phone, @Home)
    IF (@Role = N'Разработчик')
        INSERT INTO Разработчики VALUES (@NextIndex, 0)
    ELSE
        IF (@Role = N'Тестировщик')
            INSERT INTO Тестировщики VALUES (@NextIndex, 0)
        ELSE
            IF (@Role = N'Администратор')
                INSERT INTO Администраторы VALUES (@NextIndex)
    INSERT INTO LogPass VALUES (@NextIndex,'0')
GO
CREATE PROC getProjectUsers @projectID int AS
Select users.IDПользователя, ФИО, Телефон, ДомашнийАдрес, Роль
from (SELECT Пользователи.IDПользователя, ФИО, Телефон, ДомашнийАдрес, N'Разработчик' as 'Роль'
      FROM Пользователи
               JOIN Разработчики Р on Пользователи.IDПользователя = Р.IDПользователя
      UNION
      SELECT Пользователи.IDПользователя, ФИО, Телефон, ДомашнийАдрес, N'Тестировщик' as 'Роль'
      FROM Пользователи
               JOIN Тестировщики Т on Пользователи.IDПользователя = Т.IDПользователя
      UNION
      SELECT Пользователи.IDПользователя, ФИО, Телефон, ДомашнийАдрес, N'Администратор' as 'Роль'
      FROM Пользователи
               JOIN Администраторы А on Пользователи.IDПользователя = А.IDПользователя) as users
         JOIN ПользователиВПроектах on users.IDПользователя = ПользователиВПроектах.IDПользователя
WHERE IDПроекта = @projectID
GO
    CREATE PROC addNewProject @projectName nvarchar(20), @projectDesc nvarchar(200) AS
    Declare
        @nextIndex int = (SELECT max(IDПроекта)
                          from Проекты) + 1
    INSERT INTO Проекты
    VALUES (@nextIndex, @projectName, @projectDesc, N'Начало работ', SYSDATETIME())

