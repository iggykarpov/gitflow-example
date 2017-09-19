package org.finra.esched.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.finra.esched.dao.IBaseHibernateDAO;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Required;

/**
 * Data access object (DAO) for domain model
 * 
 * @author MyEclipse Persistence Tools
 */
public class BaseHibernateDAO implements IBaseHibernateDAO {

	private EntityManager entityManager;

	@Required
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public Session getSession() throws HibernateException {
        Session session = getEntityManager().unwrap(Session.class);
        return session;
    }

}