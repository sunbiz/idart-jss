-- This script will rename patient id's that are duplicated. The most recent one will have
-- "-duplicate" appended to it.

UPDATE patient 
SET 
   patientid = patientid||'-duplicate'
WHERE 
   id in (SELECT id
		  FROM patient a
		  WHERE a.id = (SELECT max(id) FROM patient
				WHERE patientid = a.patientid
				AND patientid IS NOT NULL
				AND patientid != ''	
				GROUP BY patientid
				HAVING count(*) >1));