/*
 * Copyright 2008-2019 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 * FileId: 0/Z5eAbO270z183sLLqlnELbYfeThdam
 */
package net.shopxx.dao.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.MemberDao;
import net.shopxx.entity.GroupBuying;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberAttribute;
import net.shopxx.entity.Order;

/**
 * Dao - 会员
 * 
 * @author SHOP++ Team
 * @version 6.1
 */
@Repository
public class MemberDaoImpl extends BaseDaoImpl<Member, Long> implements MemberDao {

	@Override
	public Page<Member> findPage(Member.RankingType rankingType, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		if (rankingType != null) {
			switch (rankingType) {
			case POINT:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("point")));
				break;
			case BALANCE:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("balance")));
				break;
			case AMOUNT:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("amount")));
				break;
			}
		}
		return super.findPage(criteriaQuery, pageable);
	}

	@Override
	public Long count(Date beginDate, Date endDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("createdDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date>get("createdDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	@Override
	public List<Member> search(String keyword, Set<Member> excludes, Integer count) {
		if (StringUtils.isEmpty(keyword)) {
			return Collections.emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.like(root.<String>get("username"), "%" + keyword + "%")));
		if (CollectionUtils.isNotEmpty(excludes)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.not(root.in(excludes)));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, count, null, null);
	}

	@Override
	public void clearAttributeValue(MemberAttribute memberAttribute) {
		if (memberAttribute == null || memberAttribute.getType() == null || memberAttribute.getPropertyIndex() == null) {
			return;
		}

		String propertyName;
		switch (memberAttribute.getType()) {
		case TEXT:
		case SELECT:
		case CHECKBOX:
			propertyName = Member.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
			break;
		default:
			propertyName = String.valueOf(memberAttribute.getType());
			break;
		}
		String jpql = "update Member mem set mem." + propertyName + " = null";
		entityManager.createQuery(jpql).executeUpdate();
	}

	@Override
	public BigDecimal totalBalance() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(criteriaBuilder.sum(root.<BigDecimal>get("balance")));
		BigDecimal result = entityManager.createQuery(criteriaQuery).getSingleResult();
		return result != null ? result : BigDecimal.ZERO;
	}

	@Override
	public BigDecimal frozenTotalAmount() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(criteriaBuilder.sum(root.<BigDecimal>get("frozenAmount")));
		BigDecimal result = entityManager.createQuery(criteriaQuery).getSingleResult();
		return result != null ? result : BigDecimal.ZERO;
	}

	@Override
	public Long grantedGroupBuyingParticipants(GroupBuying groupBuying, List<Order.Status> statuses) {
		Assert.notNull(groupBuying, "[Assertion failed] - groupBuying is required; it must not be null");

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		Root<Member> root = criteriaQuery.from(Member.class);
		criteriaQuery.select(root);

		Subquery<Order> subquery = criteriaQuery.subquery(Order.class);
		Root<Order> subqueryRoot = subquery.from(Order.class);
		subquery.select(subqueryRoot);
		Predicate subRestrictions = criteriaBuilder.and(criteriaBuilder.conjunction(), criteriaBuilder.equal(subqueryRoot.get("member"), root));

		if (groupBuying != null) {
			subRestrictions = criteriaBuilder.and(subRestrictions, criteriaBuilder.equal(subqueryRoot.get("groupBuying"), groupBuying));
		}
		if (CollectionUtils.isNotEmpty(statuses)) {
			subRestrictions = criteriaBuilder.and(subRestrictions, subqueryRoot.get("status").in(statuses));
		}
		subquery.where(subRestrictions);

		criteriaQuery.where(criteriaBuilder.exists(subquery));
		return super.count(criteriaQuery);
	}

	@Override
	public int updateRealnameStatus(Member member) {
		String jpql = "update Member mem set mem.attributeValue7 = "+member.getAttributeValue8()+" where mem.id = "+member.getId();
		return entityManager.createQuery(jpql).executeUpdate();
	}

}