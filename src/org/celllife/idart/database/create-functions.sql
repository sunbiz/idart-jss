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