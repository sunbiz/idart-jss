A short guide to using the export. 

A template should be available with all possible columns. Most of the columns are self explanatory. 

CLINIC NAMES
------------
If the clinic name in the import file does not match and existing Clinic in iDART then
a new Clinic will be created.

CAPTURING ADDRESS
----------------
The Address fields are usually used for separate parts of the address however, you 
wish to capture more than 1 address (postal and residential), then a full address can 
be captured in each field. e.g. postal in Address 1 and residential in Address 2.

CAPTURING NEXT OF KIN
------------------------
Since iDART only saves the treatment supporter name and contact details, 
the extra fields (next of kin relationship and next of kin address) will be saved 
in the existing fields. For example, if the next of kin name is "john doe" and the 
relationship is mother then the treatment supporter name will be "John Doe (mother)". 
Likewise, the address is saved with the contact number.

EPISODE START DATE
------------------ 
If the "Episode Start Date" column is not present the import will use the current
date as the episode start date.

ARV START DATE
-------------- 
Sometimes the ARV start date is recorded but the episode start date is not. 
In this case, if you would like to set the episode start date to the ARV start date, 
create a column called "Episode Start Date" in the template and copy 
the ARV start date Values into this column.

COLUMN HEADINGS
--------------- 
The column headings in the template are NOT case sensitive. However, they are 
spelling sensitive so be careful.

USING OPENOFFICE
---------------- 
If you do not have access to Microsoft excel, use open office to create the import file. 
When you have completed, save the file as an excel document. The import should then
 be able to read the file. The import will only read .xls files
 
CAPTURING EXTRA DATA 
-----------------------
There are some extra fields available such as the "episode start notes" and  
"address 3", so if want to capture extra data, you can use these fields. 
For anything else, it will need to be added via changes in the source code.

ADDITIONAL ERRORS
-----------------
There may be additional error information in the iDART log file. Particularly
regarding dates.