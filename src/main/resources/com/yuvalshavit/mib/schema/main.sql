CREATE TABLE main (
    mid $ID_TYPE$,
    val1 VARCHAR(32) CHARACTER SET utf8 COLLATE utf8_bin,
    val2 INTEGER,
    KEY val1_idx (val1),
    KEY val2_idx (val2),
    PRIMARY KEY (mid)
) ENGINE = $ENGINE$;