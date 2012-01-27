--------------------------------------
-- USE:
--  The diffs are ordered by datamodel version number.
--------------------------------------

---------------------------------------
-- iDART Datamodel version 3.1.1
-- Melissa Loudon 13/10/07
-- Add 2nd dispensing instructions column to form table
---------------------------------------

CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --
		

alter table Form add column dispInstructions2 varchar;
update Form set dispInstructions2 = '';



   		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.1.0', '3.1.1');


---------------------------------------
-- iDART Datamodel version 3.1.2
-- Rashid Limbada 13/10/07
-- Add extra columns to package table for 
-- return package feature
---------------------------------------

CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --

		alter table Package
		add dateReturned timestamp without time zone,
		add stockReturned boolean,
		add packageReturned boolean,
		add reasonForPackageReturn character varying(255);
		
		update Package
		set stockReturned = false;
		update Package
		set packageReturned = false;
		
		insert into simpledomain(
		            id, description, name, value)
		    values (nextval ('hibernate_sequence'), 'Package Return Reason', 'packageReturnReason','No longer receiving treatment at clinic' );

		insert into simpledomain(
		            id, description, name, value)
		    values (nextval ('hibernate_sequence'), 'Package Return Reason', 'packageReturnReason','Change of Drugs' );

		insert into simpledomain(
		            id, description, name, value)
		    values (nextval ('hibernate_sequence'), 'Package Return Reason', 'packageReturnReason','Package lost in transit ' );

		insert into simpledomain( id, description, name, value)
		    values (nextval ('hibernate_sequence'), 'Package Return Reason', 'packageReturnReason', 'Missed Appointment' );

  		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.1.1', '3.1.2');

---------------------------------------
-- iDART Datamodel version 3.1.3
-- Renato Bacelar da Silveira 01/11/07
-- Add Patient attribute tables
-- patientattribute & attribute_type
---------------------------------------

CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --

    create table PatientAttribute (
        id int4 not null,
        value varchar(255),
        patient int4,
        type_id int4,
        primary key (id)
    );


    create table AttributeType (
        id int4 not null,
        dataType varchar(255),
        description varchar(255),
        name varchar(255),
        primary key (id)
    );

  alter table PatientAttribute 
        add constraint FKE2D1B9374E29EEE7 
        foreign key (patient) 
        references Patient;

    alter table PatientAttribute 
        add constraint FKE2D1B9377A191F53 
        foreign key (type_id) 
        references AttributeType;

-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.1.2', '3.1.3');

---------------------------------------
-- iDART Datamodel version 3.1.4
-- Rashid Limbada 06/11/07
-- Remove duplicates from Regimen and 
-- RegimenDrug tables 
---------------------------------------
CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --
		
		-- Fix duplicate prolem in Regimen and RegimenDrugs Tables
		--Create temp tables to hold the data
			create table Reg as
			select distinct * from Regimen;
			
			create table RegDrug as
			select distinct * from RegimenDrugs;
			
		--Drop tables with duplicates
			drop table RegimenDrugs;
			drop table Regimen;
			
		--Rename temp tables to orignal tables
			Alter Table Reg Rename To Regimen;
			Alter Table RegDrug Rename To RegimenDrugs;
			
		--Add primary keys
			Alter Table Regimen
			add constraint regimen_fkey
			primary key(id);
			
			Alter Table RegimenDrugs
			add constraint regimenDrugs_fkey
			primary key(id);
			
			
		--Add other constraints
			alter table RegimenDrugs
			add constraint regimen_fkey
			foreign key(regimen)
			references Regimen;
			
			alter table RegimenDrugs
			add constraint drug_fkey
			foreign key(drug)
			references Drug;
			
			alter table Regimen
			add constraint unique_regimen
			Unique(regimenName, drugGroup);
			
			alter table RegimenDrugs
			add constraint unique_regimenDrug
			Unique(regimen, drug);
		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.1.3', '3.1.4');

---------------------------------------
-- iDART Datamodel version 3.1.5
-- Melissa Loudon 10/12/07
-- Clinic and Pharmacy inherit from iDartSite
---------------------------------------
CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --
		
		--add the iDartSite table
		
		create table iDartSite 
		(id int4 not null,
		name varchar(255), 
		city varchar(255),
		modified char(1));

		alter table iDartSite add constraint iDartSite_pkey PRIMARY KEY (id);
		
		--alter the pharmacy and clinic tables to inherit from iDartSite
		insert into iDartSite  (select id, clinic, city, modified from clinic);
		insert into iDartSite  (select id, pharmacyName, city, 'T' from pharmacy);

		alter table clinic drop column city;
		alter table clinic drop column modified;
		alter table pharmacy drop column city;
		alter table clinic drop column clinic;
		alter table pharmacy drop column pharmacyName;
	
		--SiteUser table
		create table SiteUser (siteId int4 not null,
		userId int4 not null);
		
		--Add clinic users
		insert into siteuser(siteId, userId)
		(select site.id, us.id
		from idartsite as site, users as us
		where us.role like ('Clinic - '||site.name));
		
		--Add pharmacy users
		insert into siteuser(siteId, userId)
		(select pharm.id, us.id
		from pharmacy as pharm, users as us
		where us.role like 'Pharmacy');

		


		
		
		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.1.4', '3.1.5');

---------------------------------------
-- iDART Datamodel version 3.1.6
-- Rashid Limbada 24/01/08
-- Access to main Clinic to all users
---------------------------------------
CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --

		
--Allow access to main clinic to all users
				insert into siteuser(siteId, userId)
					(select site.id, us.id
					from idartsite as site, users as us, clinic as c
					where c.mainClinic = true and
					c.id = site.id);
				
--This brings duplicates in cases where users already have access to main clinic
--Create temp tables to hold the data
					create table sUser as
			select distinct * from SiteUser;	
				
--Drop tables with duplicates
				drop table SiteUser;
				
--Rename temp tables to orignal tables
				Alter Table sUser Rename To SiteUser;


		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.1.5', '3.1.6');

---------------------------------------
-- iDART Datamodel version 3.1.7
-- Rashid Limbada 09/06/08
-- Added keys to siteUser database
---------------------------------------
CREATE OR REPLACE FUNCTION diffprocedure (old_db_version VARCHAR, new_db_version VARCHAR) RETURNS boolean AS $$
 DECLARE
	doupdate boolean;
 BEGIN
	SELECT (REPLACE(value, '.', '0') = REPLACE(old_db_version, '.', '0')) into doupdate FROM simpledomain WHERE name = 'database_version';
	IF doupdate THEN
		RAISE NOTICE 'Updating to %', new_db_version;
		-- START YOUR QUERY(IES) HERE --

		-- Add primary key to table
		ALTER TABLE ONLY siteUser
    		ADD CONSTRAINT siteUser_pkey PRIMARY KEY (siteId, userId);

		-- Add foreign key for site to table
		ALTER TABLE ONLY siteUser
		    ADD CONSTRAINT site_fkey FOREIGN KEY (siteId) REFERENCES idartSite(id);

		-- Add foreign key for user to table		
		ALTER TABLE ONLY siteUser
		    ADD CONSTRAINT user_fkey FOREIGN KEY (userId) REFERENCES users(id);

		-- END YOUR QUERY(IES) HERE --
		UPDATE simpledomain SET value = new_db_version WHERE name = 'database_version';
	END IF;
	RETURN doupdate;
 END;
$$ LANGUAGE plpgsql;

SELECT diffprocedure('3.1.6', '3.1.7');

-----------------------------------
-- Clean up - Keep this section at the very bottom of diff script
-----------------------------------
---------------------------------------
-- iDART Datamodel version 3.3.0
-- Simon Kelly 14/11/2008
-- Release 3.3.0
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

SELECT diffprocedure('3.1.7', '3.3.0');

DROP FUNCTION diffprocedure(old_db_version VARCHAR, new_db_version VARCHAR);
