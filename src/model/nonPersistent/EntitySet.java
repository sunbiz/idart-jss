/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package model.nonPersistent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class EntitySet {

	private String name;
	private List<Integer> entityIds;
	private Class entity;

	public EntitySet() {
		entityIds = new ArrayList<Integer>();
	}

	public EntitySet(List<Integer> entityIds) {
		this.entityIds = entityIds;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the patientIds.
	 */
	public List<Integer> getEntityIds() {
		return entityIds;
	}

	/**
	 * @param patientIds The patientIds to set.
	 */
	public void setEntityIds(List<Integer> entityIds) {
		this.entityIds = entityIds;
	}

	/**
	 * @return the current PatientSet
	 */
	public EntitySet copyEntityIds(Collection<Integer> entityIdsToCopy) {
		this.entityIds = new ArrayList<Integer>(entityIdsToCopy);
		return this;
	}

	public void add(Integer entityId) {
		if (!entityIds.contains(entityId)) {
			entityIds.add(entityId);
		}
	}

	public boolean remove(Integer entityId) {
		return entityIds.remove(entityId);
	}

	public boolean removeAllIds(Collection<Integer> entityIdSet) {
		return entityIds.removeAll(entityIdSet);
	}

	public boolean contains(Integer entityId) {
		return entityIds.contains(entityId);
	}

	public EntitySet copy() {
		EntitySet ret = new EntitySet();
		ret.entityIds.addAll(entityIds);
		return ret;
	}

	/**
	 * Does not change this PatientSet object
	 *
	 * @return the intersection between this and other
	 */
	public EntitySet intersect(EntitySet other) {
		EntitySet ret = copy();
		ret.entityIds.retainAll(other.entityIds);
		return ret;
	}

	/**
	 * Does not change this PatientSet object
	 *
	 * @return the union between this and other
	 */
	public EntitySet union(EntitySet other) {
		EntitySet ret = new EntitySet();
		Set<Integer> set = new HashSet<Integer>(this.entityIds);
		set.addAll(other.entityIds);
		ret.copyEntityIds(set);
		return ret;
	}

	/**
	 * Does not change this PatientSet object
	 * @return this set *minus* all members of the other set
	 */
	public EntitySet subtract(EntitySet other) {
		EntitySet ret = copy();
		ret.entityIds.removeAll(other.entityIds);
		return ret;
	}

	public EntitySet getPage(int startPostition, int pageSize) {
		int size = size();
		if (startPostition > size) {
			startPostition = size;
		}
		int endPosition = startPostition + pageSize;
		if (endPosition > size) {
			endPosition = size;
		}
		List<Integer> subList = entityIds.subList(startPostition, endPosition);
		EntitySet page = new EntitySet(subList);
		return page;

	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		int soFar = 0;
		for (Integer entityId : entityIds) {
			ret.append(entityId).append("\n");
			if (++soFar > 20) {
				ret.append("...");
				break;
			}
		}
		return ret.toString();
	}

	public static EntitySet parseCommaSeparatedEntityIds(String s) {
		EntitySet ret = new EntitySet();
		for (StringTokenizer st = new StringTokenizer(s, ","); st.hasMoreTokens(); ) {
			String id = st.nextToken();
			ret.add(new Integer(id.trim()));
		}
		return ret;
	}

	public String toCommaSeparatedEntityIds() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Integer> i = entityIds.iterator(); i.hasNext(); ) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	// For bean-style calls
	public String getCommaSeparatedEntityIds() {
		return toCommaSeparatedEntityIds();
	}

	public int size() {
		return entityIds.size();
	}

	public int getSize() {
		return size();
	}

	public boolean isEmpty() {
		return entityIds.isEmpty();
	}

}
