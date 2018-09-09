
CREATE TRIGGER chec_empty ON admin
INSTEAD of INSERT
AS
BEGIN
    DECLARE @ano VARCHAR(10)
    DECLARE @pw VARCHAR(20)
    DECLARE @name VARCHAR(10)
    SELECT @ano = ano FROM inserted
    SELECT @pw = pw FROM inserted
    SELECT @name = name FROM inserted
    IF(@ano = '' OR @ano is null OR @pw = '' OR @pw is null OR @name = '' OR @name is null)
        print ('Insert refused')
    ELSE
        INSERT into admin SELECT * FROM inserted
END
