--------------------------------------
-- USE:
--  The diffs are ordered by datamodel version number.
--------------------------------------

---------------------------------------------------------------
-- iDART Datamodel version 3.4.1
-- Simon 25/02/2009
-- PVSTAT
---------------------------------------------------------------
CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
	constraintname varchar;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --

CREATE TABLE patientstattypes
(
  id integer NOT NULL,
  statname character varying(30),
  statformat character varying(1),
  CONSTRAINT pk_patientstattype PRIMARY KEY (id)
);

INSERT INTO patientstattypes(id, statname, statformat) select nextval('hibernate_sequence'), 'CD4 Count', 'N';
INSERT INTO patientstattypes(id, statname, statformat) select nextval('hibernate_sequence'), 'Viral Load', 'N';
INSERT INTO patientstattypes(id, statname, statformat) select nextval('hibernate_sequence'), 'CD4 Percentage', 'N';
INSERT INTO patientstattypes(id, statname, statformat) select nextval('hibernate_sequence'), 'ALT', 'C';


CREATE TABLE patientstatistic
(
  id integer NOT NULL,
  entry_id integer,
  patient_id integer,
  datetested date,
  daterecorded date,
  patientstattype_id integer,
  statnumeric double precision,
  stattext character(255),
  statdate date,
  CONSTRAINT pk_patientstatistic PRIMARY KEY (id)
);

ALTER TABLE patientstatistic OWNER TO postgres;

CREATE TABLE patientvisitreason
(
  id integer,
  reasonforvisit character varying(50)
);

ALTER TABLE patientvisitreason OWNER TO postgres;

INSERT INTO patientvisitreason(id, reasonforvisit) select nextval('hibernate_sequence'),'Scheduled Visit';
INSERT INTO patientvisitreason(id, reasonforvisit) select nextval('hibernate_sequence'),'Not Feeling Well';
INSERT INTO patientvisitreason(id, reasonforvisit) select nextval('hibernate_sequence'),'Counselling';
INSERT INTO patientvisitreason(id, reasonforvisit) select nextval('hibernate_sequence'),'Advice';
INSERT INTO patientvisitreason(id, reasonforvisit) select nextval('hibernate_sequence'),'Other';

CREATE TABLE patientvisit
(
  id integer NOT NULL,
  patient_id integer,
  dateofvisit date,
  isscheduled character varying(1),
  patientvisitreason_id integer,
  diagnosis character varying(255),
  notes character varying(255),
  CONSTRAINT pk_patientvisit PRIMARY KEY (id)
);


CREATE OR REPLACE FUNCTION add_sched_visit() RETURNS TRIGGER AS 
$add_sched_visit$
DECLARE
firstdash INT;
seconddash INT;
patstring VARCHAR(30);
patid INT;
pvid INT;
latestdate timestamp;
mycount INT;
BEGIN
firstdash:=position('-' in NEW.packageid);
IF (firstdash>0) THEN
patstring:=substring(NEW.packageid from firstdash+1);
seconddash:=position('-' in patstring);
patstring:=substring(patstring from 1 for seconddash-1);
patid:=(SELECT id FROM patient WHERE patientid=patstring);
pvid:=(SELECT id FROM patientvisitreason WHERE reasonforvisit='Scheduled Visit');
mycount:=(SELECT Count(*) FROM patientvisit WHERE patient_id=patid and patientvisitreason_id=pvid);
latestdate:=(SELECT dateofvisit FROM patientvisit WHERE patient_id=patid and patientvisitreason_id=pvid ORDER BY dateofvisit DESC LIMIT 1); 
IF (latestdate < (now()-interval '24 hours')) THEN
INSERT INTO patientvisit(id, patient_id, dateofvisit, isscheduled, patientvisitreason_id, diagnosis, notes)
    VALUES (nextval('hibernate_sequence'), patid, NEW.datereceived, 'Y', pvid, '', 'Scheduled Visit to Receive Package');
END IF;
IF (mycount=0) THEN
INSERT INTO patientvisit(id, patient_id, dateofvisit, isscheduled, patientvisitreason_id, diagnosis, notes)
    VALUES (nextval('hibernate_sequence'), patid, NEW.datereceived, 'Y', pvid, '', 'Scheduled Visit to Receive Package');
END IF;
END IF;

RETURN NEW;
END;
$add_sched_visit$ LANGUAGE plpgsql;
CREATE TRIGGER add_sched_visit AFTER INSERT ON package FOR EACH ROW EXECUTE PROCEDURE add_sched_visit();

CREATE OR REPLACE FUNCTION del_sched_visit() RETURNS TRIGGER AS 
$del_sched_visit$
DECLARE
firstdash INT;
seconddash INT;
patstring VARCHAR(30);
patid INT;
pvid INT;
visitdate timestamp;
mycount INT;
delid INT;
BEGIN
firstdash:=position('-' in OLD.packageid);
IF (firstdash>0) THEN
patstring:=substring(OLD.packageid from firstdash+1);
seconddash:=position('-' in patstring);
patstring:=substring(patstring from 1 for seconddash-1);
patid:=(SELECT id FROM patient WHERE patientid=patstring);
pvid:=(SELECT id FROM patientvisitreason WHERE reasonforvisit='Scheduled Visit');
visitdate:=(SELECT dateofvisit FROM patientvisit where patient_id =patid and patientvisitreason_id=pvid order by dateofvisit desc LIMIT 1);
delid:=(SELECT id FROM patientvisit where patient_id =patid and patientvisitreason_id=pvid order by dateofvisit desc LIMIT 1);
IF ((date_part('month',visitdate)=date_part('month',OLD.datereceived)) AND (date_part('year',visitdate)=date_part('year',OLD.datereceived)) AND (date_part('day',visitdate)=date_part('day',OLD.datereceived))) THEN
DELETE FROM patientvisit where id=delid;
END IF;
END IF;
RETURN OLD;
END;
$del_sched_visit$ LANGUAGE plpgsql;
CREATE TRIGGER del_sched_visit BEFORE DELETE ON package FOR EACH ROW EXECUTE PROCEDURE del_sched_visit();		
				
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.4.0','3.4.1');

---------------------------------------------------------------
-- iDART Datamodel version 3.4.2
-- Simon 28-05-2009
-- Added regimens to simple domain
---------------------------------------------------------------
CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
	constraintname varchar;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --

		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'),'Regimen 1A', 'regimen', '1A';
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'),'Regimen 1B', 'regimen', '1B';
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'),'Regimen 2', 'regimen', '2';
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'),'Mixed Regimen', 'regimen', 'Mixed';
				
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.4.1','3.4.2');

---------------------------------------------------------------
-- iDART Datamodel version 3.4.3
-- Rashid 03-09-2009
-- Added regimens 1C and 1D to simple domain
-- Added episode start reason "Down Referred" to simple domain
---------------------------------------------------------------
CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
	constraintname varchar;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --

		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'),'Regimen 1C', 'regimen', '1C';
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'),'Regimen 1D', 'regimen', '1D';
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'),'', 'activation_reason', 'Down Referred';
				
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.4.2','3.4.3');

-----------------------------------
-- Clean up - Keep this section at the very bottom of diff script
-----------------------------------

---------------------------------------
-- iDART Datamodel version 3.5.0
-- Simon Kelly 17/04/2009
-- Release 3.4.0
---------------------------------------

CREATE OR REPLACE FUNCTION diffprocedure(old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --
  		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.4.3','3.5.0');

DROP FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR);

