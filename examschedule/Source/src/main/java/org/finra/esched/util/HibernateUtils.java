package org.finra.esched.util;

import org.hibernate.proxy.HibernateProxy;

public class HibernateUtils {

  /**
	 * Transforms a hibernate proxy to the entity's class
	 * 
	 * @param entity
	 * @return entity
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unProxy(T entity) {
		if (entity instanceof HibernateProxy) {
	        entity =  (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
	                .getImplementation();
	    } 
		
		return entity;
	}
	
	/**
	 * Transforms a hibernate proxy object to the desired clazz, if possible.  <br />
	 * For when the entity is an instance of clazz. 
	 * 
	 * @param entity
	 * @param clazz
	 * @return entity cast to clazz
	 */
	@SuppressWarnings("unchecked")
	public static <T, Y> Y unProxyToClass(T entity, Class<Y> clazz) {
		return (Y) HibernateUtils.unProxy(entity);
	}
}