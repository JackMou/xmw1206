/*
 * Copyright 2008-2019 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 * FileId: boLf1JRS9bThO6ceVQgFTSZ78HH8exxO
 */
package net.shopxx.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.AftersalesDao;
import net.shopxx.entity.Aftersales;
import net.shopxx.entity.Member;
import net.shopxx.entity.OrderItem;
import net.shopxx.entity.Store;

/**
 * Dao - 售后
 *
 * @author SHOP++ Team
 * @version 6.1
 */
@Repository
public class AftersalesDaoImpl extends BaseDaoImpl<Aftersales, Long> implements AftersalesDao {

	@Override
	public List<Aftersales> findList(List<OrderItem> orderItems) {
		String jpql = "select aftersales from Aftersales aftersales where aftersales in(select aftersalesItem.aftersales from AftersalesItem aftersalesItem where aftersalesItem.orderItem in(:orderItems))";
		TypedQuery<Aftersales> query = entityManager.createQuery(jpql, Aftersales.class);
		return query.setParameter("orderItems", orderItems).getResultList();
	}

	@Override
	public Page<Aftersales> findPage(Aftersales.Type type, Aftersales.Status status, Member member, Store store, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Aftersales> criteriaQuery = criteriaBuilder.createQuery(Aftersales.class);
		Root<Aftersales> root = criteriaQuery.from(Aftersales.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		Class<? extends Aftersales> clazz = type != null ? type.getClazz() : null;
		if (clazz != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.type(), clazz));
		}
		if (status != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		}
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if (store != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("store"), store));
		}
		criteriaQuery.where(restrictions);

		return super.findPage(criteriaQuery, pageable);
	}

}