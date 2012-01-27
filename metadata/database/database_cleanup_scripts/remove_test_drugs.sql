-- Please note that this script will only work on drugs that are not used in prescriptions. Thus, if any patient(s) 
-- already received the drug that you would like to delete, then you will not be able to delete the drug.
-- This script is specifically created to remove test drugs where the drug in question was not dispensed or prescribed.
CREATE OR REPLACE FUNCTION delete_drug(ids VARCHAR)
RETURNS VOID AS $$

DECLARE 
	Tid int;
		
BEGIN
	SELECT ID INTO Tid FROM DRUG WHERE NAME = ids;
	RAISE NOTICE 'DRUG "%" SELECTED ', Tid;

	
	DELETE 
	FROM PACKAGEDDRUGS
	WHERE STOCK IN (
		SELECT ID FROM STOCK
		WHERE DRUG  = Tid);
	RAISE NOTICE 'PACKAGEDDRUGS deleted ';

	DELETE 
	FROM PACKAGE
	WHERE ID IN (
		SELECT PARENTPACKAGE FROM PACKAGEDDRUGS
		WHERE STOCK IN (
		SELECT ID FROM STOCK
		WHERE DRUG  = Tid))
		AND PACKAGEID LIKE 'destroyedStock';
	RAISE NOTICE 'PACKAGE deleted ';
	
	DELETE
	FROM STOCK
	WHERE DRUG = Tid;	
	RAISE NOTICE 'STOCK deleted ';

	DELETE 
	FROM CHEMICALDRUGSTRENGTH
	WHERE DRUG = Tid;
	RAISE NOTICE 'CHEML DRUG STRENGTH deleted ';

	DELETE 
	FROM DRUG
	WHERE DRUG.ID = Tid;
	RAISE NOTICE 'DRUG deleted ';
	
	RETURN;
END;
$$ LANGUAGE plpgsql;

-- NB: Usage  - use the name of te drug, eg '[D4T] Stavudine 30mg'. 
-- Just uncomment the line below and replace Tenofovir_placebo 90 with the drug that you would like to delete
--SELECT delete_drug('Tenofovir_placebo 90');   


DROP FUNCTION delete_drug(character varying);