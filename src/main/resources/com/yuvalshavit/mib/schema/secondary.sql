CREATE TABLE secondary (
    sid INTEGER AUTO_INCREMENT PRIMARY KEY,
    val3 INTEGER,
    mid $ID_TYPE$,
    KEY mid_idx (mid)
) ENGINE = $ENGINE$;