INSERT INTO PERMISSION (NAME, DESCRIPTION) VALUES('ALL', 'Full system access (super admin)');
INSERT INTO "ROLE" (NAME, DESCRIPTION) VALUES('SUPER_ADMIN', 'Unrestricted system access');

-- Now assign the permission to the role using subqueries
INSERT INTO ROLE_PERMISSION (ROLE_ID, PERMISSION_ID)
SELECT
    (SELECT ROLE_ID FROM "ROLE" WHERE NAME = 'SUPER_ADMIN'),
    (SELECT PERMISSION_ID FROM PERMISSION WHERE NAME = 'ALL')
FROM DUAL;


INSERT INTO USER_ACCOUNT (FIRST_NAME, LAST_NAME, PASSWORD, EMAIL, PHONE_NUMBER)
VALUES ('System', 'Admin', '$2a$10$DBpSBJgxNIqeLV1sdN6UVuRpG1S2EMCpnPxyP8B6PCwaMDrUFZ64O', 'superadmin@company.com', '+10000000001');


-- Now assign the role to the user using subqueries
INSERT INTO USER_ACCOUNT_ROLE (USER_ID, ROLE_ID)
SELECT
    (SELECT USER_ID FROM USER_ACCOUNT WHERE EMAIL = 'superadmin@company.com'),
    (SELECT ROLE_ID FROM "ROLE" WHERE NAME = 'SUPER_ADMIN')
FROM DUAL;
