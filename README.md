MySQL CRUD benchmarks for id types
==================================

Simple experiment to determine how costly different kinds of PKs are in MySQL.

For now, we'll test the following:

- integer auto\_increment
- varchar(32), storing a variant-4 [UUID](http://en.wikipedia.org/wiki/Universally_unique_identifier#Version_4_.28random.29)
  - utf8 encoding
  - latin1 encoding
- char(32), storing the same, and also with those encodings
- binary(16), storing the same
- varchar(32) storing ints ("1", "2", etc) as a kindasorta control group for the varchar tests

We'll be working with two tables, `Main` and `Secondary`. They are defined as:

Main:

- `mid`, one of the types above
- `val1`, a varchar(32) storing a uuid
- `val2`, an int
- two keys, one one each of the two val fields

Secondary:

- `sid`, int auto\_increment
- `val3`, int
- `mid`, same type as `Main.mid`.
- key on `mid`

The tests are:

- insert N rows into `Main`
- update the two val columns for each row in `Main`
- join `Main` and `Secondary` M times
- delete N rows from `Main`

Compiling and running
=====================

    mvn clean package
    
    java \
        -Drows=<rows> \
        -Dthreads=<threads> \
        -Duser=<mysql_user> \
        -Dschema=<mysql_schema> \
        [ -Dpassword=<mysql_password> ] \
        -Dprovider=<providers> \
        -jar target/mysql-id-benchmarks-1.0-SNAPSHOT-jar-with-dependencies.jar

where:

- `<rows>` is the number of rows to insert into the main table
- `<threas>` is the number of threads to use; each will have its own JDBC connection
- `<mysql_user>` is the mysql user
- `<mysql_schema>` is the mysql schema (aka database) to connect to
- `<mysql_password>` is the user's password; defaults to `""`
- `<providers>` is a comma-delimited list of the PK providers to use, or `all`. Your options are:
    - `auto_inc` : PK column will be an auto-incremented int
    - `varchar_utf8` : PK column will be a `VARCHAR(36)` in utf8, storing UUIDs
    - `varchar_latin1` : Same, but in Latin1
    - `char_latin1` : Same, but `CHAR(36)`
    - `binary` : PK column will be a `BINARY(16)`, storing UUIDs
