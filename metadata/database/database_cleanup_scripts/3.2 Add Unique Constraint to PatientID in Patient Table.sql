-- Identify patients with duplicate patientId's --

select * from patient patient1, patient patient2 
where patient1.id != patient2.id and patient1.patientid = patient2.patientid;


-- Add new constraints to patientid column
		
ALTER TABLE patient ADD CONSTRAINT patientid_unique_key UNIQUE (patientid);
		
-- END YOUR QUERY(IES) HERE --
		