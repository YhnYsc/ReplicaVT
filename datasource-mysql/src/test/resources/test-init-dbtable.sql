-- Test Column MetaData
CREATE TABLE test_table(
   col_id         INTEGER      NOT NULL DEFAULT 0,
   col_tinyid     TINYINT      NOT NULL DEFAULT 0,
   col_char       CHAR(10)     NOT NULL DEFAULT '',
   col_varchar    VARCHAR(10)  NOT NULL DEFAULT '',
   col_nvarchar   NVARCHAR(10) NOT NULL DEFAULT '',
   col_blob       BLOB         NULL,
   col_text       TEXT         NULL,
   col_decimal    DECIMAL(10,3)   NOT NULL DEFAULT 0,
   col_double     DOUBLE       NOT NULL DEFAULT 0,
   col_time       TIME NOT NULL DEFAULT (CURRENT_TIME),
   col_date       DATE NOT NULL DEFAULT (CURRENT_DATE),
   col_ts0        TIMESTAMP    NOT NULL DEFAULT NOW(),
   col_ts6        TIMESTAMP(6) NOT NULL DEFAULT NOW(6),
   PRIMARY KEY (col_id, col_tinyid)
);

-- Test Reference ssTables
CREATE TABLE test_son_table(
   col_son_id     INTEGER      NOT NULL DEFAULT 0,
   col_parent_id  INTEGER      NOT NULL DEFAULT 0,
   col_ts         TIMESTAMP    NOT NULL DEFAULT NOW(),
   PRIMARY KEY (col_son_id),
   FOREIGN KEY (col_parent_id) REFERENCES test_table(col_id)
);
CREATE TABLE test_leaf_table(
    col_leaf_id    INTEGER      NOT NULL DEFAULT 0,
    col_parent_id  INTEGER      NOT NULL DEFAULT 0,
    col_ts         TIMESTAMP    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (col_leaf_id),
    FOREIGN KEY (col_parent_id) REFERENCES test_table(col_id)
);

-- Test Event Group table
CREATE TABLE EventGroup_LALILULELO(
    TableGroup     VARCHAR(255) NOT NULL DEFAULT '',
    TableName      VARCHAR(255) NOT NULL DEFAULT '',
    TableVersion   Integer NOT NULL DEFAULT 1,
    SeqNo          BIGINT NOT NULL AUTO_INCREMENT,
    Operation      CHAR(1) NOT NULL DEFAULT '',
    Mode           CHAR(1) NOT NULL DEFAULT '',
    PayloadKeyOld  TEXT NULL,
    PayloadKeyNew  TEXT NULL,
    Payload        LONGTEXT NULL,
    PayloadCmpOld  TEXT NULL,
    PayloadCmpNew  TEXT NULL,
    EventFrom      VARCHAR(100) NOT NULL DEFAULT '',
    CreateTs       TIMESTAMP(6) NOT NULL DEFAULT NOW(6),
    UpdateTs       TIMESTAMP(6) NOT NULL DEFAULT NOW(6),
    INDEX(SeqNo)
);

INSERT INTO EventGroup_LALILULELO(TableGroup, SeqNo) VALUES('TG1', 1);
INSERT INTO EventGroup_LALILULELO(TableGroup, SeqNo) VALUES('TG1', 2);
INSERT INTO EventGroup_LALILULELO(TableGroup, SeqNo) VALUES('TG1', 3);
INSERT INTO EventGroup_LALILULELO(TableGroup, SeqNo) VALUES('TG1', 4);
INSERT INTO EventGroup_LALILULELO(TableGroup, SeqNo) VALUES('TG1', 5);
INSERT INTO EventGroup_LALILULELO(TableGroup, SeqNo) VALUES('TG1', 6);