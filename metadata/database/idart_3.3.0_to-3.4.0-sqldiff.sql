--------------------------------------
-- USE:
--  The diffs are ordered by datamodel version number.
--------------------------------------

---------------------------------------
-- iDART Datamodel version 3.3.1
-- Rashid 24 June 2008
-- removed iDARTsites and include stockCenters
---------------------------------------
CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
	constraintname varchar;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --

		--move pharmacy details to simpledomain for use in reports
	
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'), 'pharmacy_detail','pharmacist', pharmacist from pharmacy;
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'), 'pharmacy_detail','assistant_pharmacist', pharmacist2 from pharmacy;
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'), 'pharmacy_detail','pharmacy_name', name from idartsite where id in (select id from pharmacy);
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'), 'pharmacy_detail','pharmacy_street', street from pharmacy;
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'), 'pharmacy_detail','pharmacy_city', city from idartsite where id in (select id from pharmacy);
		INSERT INTO simpledomain(id, description, name, value) select nextval('hibernate_sequence'), 'pharmacy_detail','pharmacy_contact_no', contactNo from pharmacy;
	
	-- update clinic table --
	
		alter table clinic add column clinicName varchar(255);
		alter table clinic add column city varchar(255);
		alter table clinic add column modified character(1);
	
		-- Update new columns with relevent information --

		UPDATE clinic
		SET    clinicName = clin.name, city = clin.city, modified = clin.modified
		FROM   (SELECT site.name as name , site.city as city, site.modified, site.id as clinId 
		from idartSite as site) AS clin
		WHERE id = clinId;




		
		-- add new constraints to table
		alter table clinic add constraint unique_ClinicName Unique(clinicName);
		
	-- end of update Clinic table to stockCenter table --
	
	
	-- update siteUser table  --
		-- rename table to clinicUser
		alter table siteUser rename to clinicUser;
	
		-- rename column
		alter table clinicUser rename column siteId to clinicId;
	
		-- drop old constraints --
		alter table clinicUser drop constraint siteuser_pkey;
		alter table clinicUser drop constraint site_fkey;
	
		-- remove pharmacy entries
		delete from clinicUser where clinicId in 
		(select id from pharmacy );

		-- create new constraint
		alter table clinicUser add constraint clinicUser_pkey PRIMARY KEY (userId, clinicId);
		alter table clinicUser add constraint clinic_fkey FOREIGN KEY(clinicId) REFERENCES Clinic(id);
		alter table clinicUser add constraint unique_ClinicUser Unique(userId, clinicId);
	
		
	-- update pharmacy table to stockCenter table --
		-- remove all entries in pharmacy table --
		delete from pharmacy;
		
		-- restructure table --
		alter table pharmacy drop column pharmacist2;
		alter table pharmacy drop column pharmacist;
		alter table pharmacy drop column contactNo;
		alter table pharmacy drop column street;
		alter table pharmacy add column stockCenterName varchar(255);
		alter table pharmacy add column preferred boolean;
	
		--drop pk constraint to clinic
		select into constraintname conname 
		from pg_constraint where conrelid = (select oid from pg_class where relname like 'pharmacy')
		and contype like 'p';
	
		if (found) then
		execute 'alter table pharmacy drop constraint '||constraintname;
		else
		RAISE NOTICE 'No primary key constraint in pharmacy';
		end if;
	
		-- add new constraints to table
		alter table pharmacy add constraint stockCenter_pkey PRIMARY KEY (id);
		alter table pharmacy add constraint unique_StockCenterName Unique(stockCenterName);
	
		-- rename table to stockCenter
		alter table pharmacy rename to stockCenter;
	
		-- set the preferred stockCenter
		update stockCenter set preferred = true, 
		stockCenterName = (select i.id from idartsite i, 
		stockCenter c where c.id = i.id);
		
	
		-- Add stockCenter for each clinic --
		insert into stockCenter(id, stockCenterName, preferred)
			(select clinic.id, clinic.clinicName, false 
			from clinic as clinic);
	
		-- delete idartSite table
		drop table idartSite;
	
	-- end of update pharmacy table to stockCenter table --
		
	-- Stock table queries --
		--rename column to mive stock from clinic to stockCenter
		alter table stock rename column clinic to stockCenter;
	
		--drop fk constraint to clinic
		select into constraintname conname 
		from pg_constraint where conrelid = (select oid from pg_class where relname like 'stock')
		and confrelid = (select oid from pg_class where relname like 'clinic')
		and contype like 'f';
	
		if (found) then
		execute 'alter table stock drop constraint '||constraintname;
		else
		RAISE NOTICE 'No foreign key constraint on stock references clinic';
		end if;
	
		
	
		--add fk constraint to pharmacy
		ALTER TABLE stock ADD FOREIGN KEY (stockCenter) REFERENCES stockCenter (id);
	
	-- End of Stock Table Queries --

		


		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.3.0','3.3.1');

---------------------------------------------------------------
-- iDART Datamodel version 3.3.2
-- Rashid 24/06/2008
-- updates from 3.2
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
		
		-- Alter packageDrugInfotmp table
		alter table packageDrugInfotmp add column qtyInHand varchar(255);
		alter table packageDrugInfotmp add column summaryQtyInHand varchar(255);
		alter table packageDrugInfotmp add column qtyInLastBatch varchar(255);
		alter table packagedruginfotmp rename column patientname to patientFirstName;
		alter table packagedruginfotmp add column patientLastName varchar(255);
		
		-- Add UnitPrice column to stock
		ALTER TABLE stock ADD COLUMN UnitPrice numeric;
		
		-- Add new constraints to patientid column
		ALTER TABLE patient ALTER COLUMN patientid SET NOT NULL;
		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.3.1','3.3.2');



---------------------------------------------------------------
-- iDART Datamodel version 3.3.3
-- Munaf 04/07/2008
-- added to allow drug table to store "Take 0.5 lozenges 5 times a day"
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

	ALTER TABLE Drug ALTER COLUMN defaultAmnt TYPE float4;


		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.3.2','3.3.3');


---------------------------------------------------------------
-- iDART Datamodel version 3.3.4
-- Rashid 21/08/2008
-- set the message field type to text in the logging table
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

		ALTER TABLE Logging ALTER COLUMN message TYPE text;


		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.3.3','3.3.4');

---------------------------------------------------------------
-- iDART Datamodel version 3.3.5
-- Simon 14/01/2009
-- rename arv_start_date to ARV Start Date
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

		update attributetype set name = 'ARV Start Date' where name = 'arv_start_date';

		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.3.4','3.3.5');

-----------------------------------
-- Clean up - Keep this section at the very bottom of diff script
-----------------------------------
---------------------------------------
-- iDART Datamodel version 3.4.0
-- Simon Kelly 14/11/2008
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

SELECT diffprocedure('3.3.5','3.4.0');

DROP FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR);

