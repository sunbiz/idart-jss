CREATE OR REPLACE FUNCTION add_sched_visit() RETURNS trigger AS
$add_sched_visit$
DECLARE
scriptid INT;
patid INT;
pvid INT;
latestdate timestamp;
mycount INT;
BEGIN
scriptid:=NEW.prescription;
patid:=(SELECT patient FROM prescription WHERE id=scriptid);
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

RETURN NEW;
END;
$add_sched_visit$ LANGUAGE plpgsql;