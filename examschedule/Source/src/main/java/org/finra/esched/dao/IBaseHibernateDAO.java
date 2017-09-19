package org.finra.esched.dao;


import javax.persistence.EntityManager;

import org.hibernate.Session;


/**
 * Data access interface for domain model
 * @author MyEclipse Persistence Tools
 */
public interface IBaseHibernateDAO {
	
	 Session getSession();
	 EntityManager getEntityManager();
}