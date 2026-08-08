package org.the4thlaw.commons.services.importing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseRelationsHolder {
    protected final Map<String, List<Map<String, String>>> allRelations = new HashMap<>();

	public Map<String, List<Map<String, String>>> getAllRelations() {
        return allRelations;
    }

	protected static Map<String, String> join(String col1, String val1, String col2, String val2) {
		HashMap<String, String> columns = new HashMap<>();
		columns.put(col1, val1);
		columns.put(col2, val2);
		return columns;
	}
}
