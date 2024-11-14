CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA "user_management";

CREATE TABLE USER_INFO (
    ID UUID DEFAULT "user_management".uuid_generate_v4(),
    USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    USER_NAME VARCHAR(50) NOT NULL,
    FIRST_NAME VARCHAR(50) NOT NULL,
    MIDDLE_NAME VARCHAR(50),
    LAST_NAME VARCHAR(50),
    EMAIL VARCHAR(50) NOT NULL,
    COUNTRY_CODE VARCHAR(10),
    MOBILE_NO VARCHAR(50),
    HASHED_PASSWORD VARCHAR(100) NOT NULL,
    SECRET VARCHAR(400) NOT NULL,
    EMAIL_VERIFIED BOOLEAN NOT NULL,
    MFA_ENABLED BOOLEAN NOT NULL,
    IS_ACTIVE BOOLEAN NOT NULL,
    CREATED_ON TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATED_BY VARCHAR(100) NOT NULL,
    UPDATED_ON TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_BY VARCHAR(100) NOT NULL,
    CONSTRAINT USER_PKEY PRIMARY KEY (ID)
);