package org.celllife.idart.database.hibernate.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class TransactionalCommand {
	
	private final Session session;

	public TransactionalCommand(Session session) {
		this.session = session;
	}
	
	public void run(){
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			
			executeInTransaction();
			
			session.flush();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
		}
	}
	
	public Session getSession() {
		return session;
	}

	protected abstract void executeInTransaction();
	
}
