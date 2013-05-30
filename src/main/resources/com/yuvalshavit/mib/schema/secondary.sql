CREATE TABLE secondary (
    sid INTEGER NOT NULL AUTO_INCREMENT,
    val3 INTEGER,
    mid $ID_TYPE$,
    KEY mid_idx (mid),
    PRIMARY KEY(sid)
) ENGINE = $ENGINE$;