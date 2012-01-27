UPDATE form SET actionlanguage2 = 'Sela' 
			WHERE actionlanguage2 = 'Tata';
			
		UPDATE form SET actionlanguage2 = 'Qaba',
			actionlanguage3 = 'Wend aan' 
			WHERE form = 'Ointment'
			OR form = 'Lotion'
			OR form = 'Cream';
			
		UPDATE form SET actionlanguage2 = 'Galela',
			actionlanguage3 = 'Gebruik',
			formlanguage2 = 'amathontsi',
			formlanguage3 = 'druppel(s)'
			WHERE form = 'Ear drops'
			OR form = 'Nose drops'
			OR form = 'Eye drops';

		UPDATE form SET actionlanguage1 = 'Insert',
			actionlanguage2 = 'Sebenzisa', 
			actionlanguage3 = 'Steek in',
			dispinstructions1 = 'Vaginal Cream / Isithambiso sangaphantsi'
			WHERE form = 'Vaginal Cream';

		UPDATE form SET actionlanguage3 = 'Neem',
			formlanguage3 = 'ml'
			WHERE form = 'Syrup'
			OR form = 'Suspesion';
			
		INSERT INTO simpledomain(id, name, value) 
			SELECT nextval('hibernate_sequence'), 
			'activation_reason', 'Restart ART';
			
		INSERT INTO simpledomain(id, name, value) select nextval('hibernate_sequence'), 'reason_for_update', 'Breast Enlargement';
		INSERT INTO simpledomain(id, name, value) select nextval	('hibernate_sequence'), 'reason_for_update', 'Lactic Acidosis';
		INSERT INTO simpledomain(id, name, value) select nextval('hibernate_sequence'), 'reason_for_update', 'Peripheral Neuropathy';
		INSERT INTO simpledomain(id, name, value) select nextval('hibernate_sequence'), 'reason_for_update', 'Post Natal';
		INSERT INTO simpledomain(id, name, value) select nextval('hibernate_sequence'), 'reason_for_update', 'Pregnancy';
		INSERT INTO simpledomain(id, name, value) select nextval('hibernate_sequence'), 'reason_for_update', 'Renal Failure';
		INSERT INTO simpledomain(id, name, value) select nextval('hibernate_sequence'), 'reason_for_update', 'Strength Change';
		INSERT INTO simpledomain(id, name, value) select nextval('hibernate_sequence'), 'reason_for_update', 'Virological Failure';

		UPDATE chemicalcompound set acronym = 'LPV' where name = 'Lopinavir';
