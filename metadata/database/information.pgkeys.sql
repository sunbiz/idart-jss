/* This returns the integers from 1 to INDEX_MAX_KEYS/FUNC_MAX_ARGS */
DROP FUNCTION IF EXISTS _pg_keypositions();
CREATE FUNCTION _pg_keypositions() RETURNS SETOF integer
    LANGUAGE sql
    IMMUTABLE
    AS 'select g.s
        from generate_series(1,current_setting(''max_index_keys'')::int,1)
        as g(s)';
 
/* This returns the integers from 1 to INDEX_MAX_KEYS/FUNC_MAX_ARGS */
DROP FUNCTION IF EXISTS information_schema._pg_keypositions();
CREATE FUNCTION information_schema._pg_keypositions() RETURNS SETOF integer
    LANGUAGE sql
    IMMUTABLE
    AS 'select g.s
        from generate_series(1,current_setting(''max_index_keys'')::int,1)
        as g(s)';