--------------------------------------
-- USE:
--  The diffs are ordered by datamodel version number.
--------------------------------------

---------------------------------------
-- iDART Datamodel version 3.0.1
-- Simon Kelly            04/07/2007
-- Convert patient activity to episodes.
---------------------------------------
DROP FUNCTION IF EXISTS diffprocedure(old_db_version VARCHAR, new_db_version VARCHAR);

CREATE OR REPLACE FUNCTION diffprocedure(old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --
		
		CREATE TABLE episode
		(
		id int4 NOT NULL,
		startdate timestamp NOT NULL,
		stopdate timestamp,
		startreason varchar,
		stopreason varchar,
		startnotes varchar,
		stopnotes varchar,
		patient int4,
		index int4,
		CONSTRAINT episode_pkey PRIMARY KEY (id),
		CONSTRAINT patient_fkey FOREIGN KEY (patient)
			REFERENCES patient (id) MATCH SIMPLE
			ON UPDATE NO ACTION ON DELETE NO ACTION
		)
		WITHOUT OIDS;
		
		INSERT INTO episode (id, startdate, stopdate, startreason, stopreason, startnotes, stopnotes, patient, index)
		SELECT nextval ('public.hibernate_sequence'), startdate, stopdate, activationreason, deactivationreason, activationnotes, deactivationnotes, id, 0 from patient where startdate is not null;
		
		ALTER TABLE patient DROP COLUMN startdate;
		ALTER TABLE patient DROP COLUMN stopdate;
		ALTER TABLE patient DROP COLUMN activationreason;
		ALTER TABLE patient DROP COLUMN	deactivationreason;
		ALTER TABLE patient DROP COLUMN activationnotes;
		ALTER TABLE patient DROP COLUMN deactivationnotes;
  		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.0.0', '3.0.1');

---------------------------------------
-- iDART Datamodel version 3.0.2
-- Rashid Lambada            13/07/2007
-- Stock take changes
---------------------------------------

CREATE OR REPLACE FUNCTION diffprocedure(old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --
		
		CREATE TABLE stocktake (
   		id integer NOT NULL,
    	enddate timestamp without time zone,
    	startdate timestamp without time zone,
    	stocktakenumber character varying(255),
    	open boolean
		);
		
		ALTER TABLE ONLY stocktake
    	ADD CONSTRAINT stocktake_pkey PRIMARY KEY (id);
    	
    	alter table stocktake
		add constraint unique_stockTakeNumber unique(stockTakeNumber);
		
		CREATE TABLE stockadjustment (
    	id integer NOT NULL,
    	capturedate timestamp without time zone,
    	stock integer NOT NULL,
   	 	notes character varying(255),
   	 	stocktake integer,
    	stockcount integer,
    	adjustedvalue integer,
    	finalised boolean
		);
		
		ALTER TABLE ONLY stockadjustment
  		ADD CONSTRAINT stockadjustment_pkey PRIMARY KEY (id);
		ALTER TABLE ONLY stockadjustment
   		ADD CONSTRAINT stock_fkey FOREIGN KEY (stock) REFERENCES stock(id);
   		ALTER TABLE ONLY stockadjustment
   		ADD CONSTRAINT stockTake_fkey FOREIGN KEY (stocktake) REFERENCES stocktake(id);
   		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.0.1', '3.0.2');

---------------------------------------
-- iDART Datamodel version 3.0.3
-- Melissa Loudon 13/07/07
-- Stock Levels Table
---------------------------------------

CREATE OR REPLACE FUNCTION diffprocedure(old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --
		
		create table StockLevel (
    id int4 not null ,
    batch int4 not null,
    fullContainersRemaining int4,
    loosePillsRemaining int4
);

alter table StockLevel 
    add constraint stockLevel_pkey
	primary key(id);

alter table StockLevel 
    add constraint batch_fkey 
    foreign key (batch) 
    references Stock;

alter table stocklevel
	add constraint unique_batch 
	unique(batch);


   		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.0.2', '3.0.3');

-----------------------------------
-- Clean up - Keep this section at the very bottom of diff script
-----------------------------------

---------------------------------------
-- iDART Datamodel version 3.1.0
-- Simon Kelly 14/11/2008
-- Release 3.1.0
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

SELECT diffprocedure('3.0.3', '3.1.0');

DROP FUNCTION IF EXISTS diffprocedure(old_db_version VARCHAR, new_db_version VARCHAR);
