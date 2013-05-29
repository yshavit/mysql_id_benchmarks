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

`Main`:

- `mid`, one of the types above
- `val1` and `val2`, both ints
- two keys, one one each of the two val fields

`Secondary`:

- `sid`, int auto\_increment
- `val3`, int
- `mid`, same type as `Main.mid`.
- key on `mid`

The tests are:

- insert N rows into `Main`
- update the two val columns for each row in `Main`
- join `Main` and `Secondary` M times
- delete N rows from `Main`