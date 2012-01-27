-- This script will rename patient id's that are duplicated. The most recent one will have
-- "-duplicate" appended to it.

-- This script will need to be run multiple times if there are more than 2 patients with
-- the same patientid or idnum

-- Once this script has been run the patient merge function can be used to compare
-- the duplicate patients and merge them if necessary.

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
				
UPDATE patient 
SET 
   idnum = idnum||'-duplicate'
WHERE 
   id in (SELECT id
		  FROM patient a
		  WHERE a.id = (SELECT max(id) FROM patient
				WHERE idnum= a.idnum
				AND idnum IS NOT NULL
				AND idnum != ''
				GROUP BY idnum
				HAVING count(*) >1));
