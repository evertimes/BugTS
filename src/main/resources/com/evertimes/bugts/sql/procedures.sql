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
