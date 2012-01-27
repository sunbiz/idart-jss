-- existing chemical compounds (excluding combinations)
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (1,31); -- Abacavir
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (2,27); -- Didanosine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (3,40); -- Efavirenz
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (4,30); -- Lamivudine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (5,38); -- Nevirapine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (6,18); -- Ritonavir
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (7,29); -- Stavudine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (8,26); -- Zidovudine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (9,21); -- Lopinavir
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (10,19); -- Nelfinavir
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (11,32); -- Tenofovir

-- new chemical compounds
insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Metisazone'); -- atccode_id=0
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 0 from chemicalcompound where name = 'Metisazone';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'ACV', 'Aciclovir'); -- atccode_id=1
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 1 from chemicalcompound where name = 'Aciclovir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'IDU', 'Idoxuridine'); -- atccode_id=2
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 2 from chemicalcompound where name = 'Idoxuridine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Vidarabine'); -- atccode_id=3
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 3 from chemicalcompound where name = 'Vidarabine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'RBV', 'Ribavirin'); -- atccode_id=4
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 4 from chemicalcompound where name = 'Ribavirin';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'GCV', 'Ganciclovir'); -- atccode_id=5
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 5 from chemicalcompound where name = 'Ganciclovir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'FCV', 'Famciclovir'); -- atccode_id=6
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 6 from chemicalcompound where name = 'Famciclovir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'VACV', 'Valaciclovir'); -- atccode_id=7
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 7 from chemicalcompound where name = 'Valaciclovir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'CDV', 'Cidofovir'); -- atccode_id=8
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 8 from chemicalcompound where name = 'Cidofovir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'PCV', 'Penciclovir'); -- atccode_id=9
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 9 from chemicalcompound where name = 'Penciclovir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Valganciclov'); -- atccode_id=10
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 10 from chemicalcompound where name = 'Valganciclov';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'BVDU', 'Brivudine'); -- atccode_id=11
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 11 from chemicalcompound where name = 'Brivudine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'RH', 'Rimantadine'); -- atccode_id=12
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 12 from chemicalcompound where name = 'Rimantadine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Tromantadine'); -- atccode_id=13
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 13 from chemicalcompound where name = 'Tromantadine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'FOS', 'Foscarnet'); -- atccode_id=14
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 14 from chemicalcompound where name = 'Foscarnet';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Fosfonet'); -- atccode_id=15
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 15 from chemicalcompound where name = 'Fosfonet';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'SAQ', 'Saquinavir'); -- atccode_id=16
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 16 from chemicalcompound where name = 'Saquinavir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'IND', 'Indinavir'); -- atccode_id=17
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 17 from chemicalcompound where name = 'Indinavir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'APV', 'Amprenavir'); -- atccode_id=20
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 20 from chemicalcompound where name = 'Amprenavir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'FPV', 'Fosamprenavir'); -- atccode_id=22
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 22 from chemicalcompound where name = 'Fosamprenavir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'ATV', 'Atazanavir'); -- atccode_id=23
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 23 from chemicalcompound where name = 'Atazanavir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'TPV', 'Tipranavir'); -- atccode_id=24
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 24 from chemicalcompound where name = 'Tipranavir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'DRV', 'Darunavir'); -- atccode_id=25
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 25 from chemicalcompound where name = 'Darunavir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Zalcitabine'); -- atccode_id=28
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 28 from chemicalcompound where name = 'Zalcitabine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'ADV', 'Adefovir'); -- atccode_id=33
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 33 from chemicalcompound where name = 'Adefovir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Emtricitabine'); -- atccode_id=34
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 34 from chemicalcompound where name = 'Emtricitabine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'ETV', 'Entecavir'); -- atccode_id=35
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 35 from chemicalcompound where name = 'Entecavir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Telbivudine'); -- atccode_id=36
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 36 from chemicalcompound where name = 'Telbivudine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Clevudine'); -- atccode_id=37
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 37 from chemicalcompound where name = 'Clevudine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'DLV', 'Delavirdine'); -- atccode_id=39
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 39 from chemicalcompound where name = 'Delavirdine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Etravirine'); -- atccode_id=41
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 41 from chemicalcompound where name = 'Etravirine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Zanamivir'); -- atccode_id=42
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 42 from chemicalcompound where name = 'Zanamivir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Oseltamivir'); -- atccode_id=43
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 43 from chemicalcompound where name = 'Oseltamivir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Moroxydine'); -- atccode_id=51
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 51 from chemicalcompound where name = 'Moroxydine';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'LSZ', 'Lysozyme'); -- atccode_id=52
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 52 from chemicalcompound where name = 'Lysozyme';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'INPX', 'Inosine pranobex'); -- atccode_id=53
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 53 from chemicalcompound where name = 'Inosine pranobex';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Pleconaril'); -- atccode_id=54
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 54 from chemicalcompound where name = 'Pleconaril';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'ENF', 'Enfuvirtide'); -- atccode_id=55
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 55 from chemicalcompound where name = 'Enfuvirtide';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Raltegravir'); -- atccode_id=56
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 56 from chemicalcompound where name = 'Raltegravir';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), NULL, 'Maraviroc'); -- atccode_id=57
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 57 from chemicalcompound where name = 'Maraviroc';

insert into chemicalcompound (id, acronym, name) values(nextval('hibernate_sequence'), 'MBV', 'Maribavir'); -- atccode_id=58
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 58 from chemicalcompound where name = 'Maribavir';

-- combinations
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (8,44); -- (Zidovudine) and Lamivudine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (4,44); -- Zidovudine and (Lamivudine)

insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (4,45); -- (Lamivudine) and Abacavir
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (1,45); -- Lamivudine and (Abacavir)

insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (11,46); -- (Tenofovir) and Emtricitabine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 46 from chemicalcompound where name = 'Emtricitabine'; -- Tenofovir and (Emtricitabine)

insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (8,47); -- (Zidovudine), Lamivudine and Abacavir
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (4,47); -- Zidovudine, (Lamivudine) and Abacavir
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (1,47); -- Zidovudine, Lamivudine and (Abacavir)

insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (8,48); -- (Zidovudine), Lamivudine and Nevirapine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (4,48); -- Zidovudine, (Lamivudine) and Nevirapine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (5,48); -- Zidovudine, Lamivudine and (Nevirapine)

insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) select id, 49 from chemicalcompound where name = 'Emtricitabine'; -- (Emtricitabine), Tenofovir and Efavirenz
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (11,49); -- Emtricitabine, (Tenofovir) and Efavirenz
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (3,49); -- Emtricitabine, Tenofovir and (Efavirenz)

insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (7,50); -- (Stavudine), Lamivudine and Nevirapine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (4,50); -- Stavudine, (Lamivudine) and Nevirapine
insert into atccode_chemicalcompound (chemicalcompound_id, atccode_id) values (5,50); -- Stavudine, Lamivudine and (Nevirapine)
