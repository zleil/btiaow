package com.btiao.infomodel;

import org.neo4j.graphdb.RelationshipType;

public class RelTypeNeo4j implements RelationshipType {
	public RelTypeNeo4j(RelType r) {
		relType = r;
	}

	public String name() {
		return relType.name;
	}

	public RelType relType;
}
