-- Remove the first name and surname
update patient set firstnames = 'Joe' where sex='M';
update patient set firstnames = 'Jane' where sex='F';
update patient set firstnames = 'Unknown' where sex='U';
update patient set lastname = 'Soap';

-- Remove the Address
update patient set address1 = 'Address Line 1';
update patient set address2 = 'Address Line 2';
update patient set address3 = 'Address Line 3';

-- Remove the ID Number
update patient set idnum = 8000000000000;

-- Remove the Phone Numbers
update patient set cellphone = '0820000000';
update patient set workphone = '0530000000';
update patient set homephone = '0210000000';

-- Remove the Next Of Kin Details
update patient set nextofkinname = 'Next Of Kin Name';
update patient set nextofkinphone = '0820000000';

-- Set user passwords to 123 --
update users set cl_password = '123';

