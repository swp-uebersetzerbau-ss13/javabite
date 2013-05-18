package swp_compiler_ss13.javabite.ast;

import java.util.HashMap;
import java.util.Map;

import swp_compiler_ss13.common.optimization.Liveliness;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.types.Type;

public class SymbolTableJb implements SymbolTable {
	private class SymbolTableInfo {
		String alias;
		Type type;
		Liveliness liveliness = null;
		
		SymbolTableInfo(Type type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			return "["+type+", "+alias+", "+liveliness+"]";
		}
	}
	
	private final static String TEMP_PREFIX = "tmp_";
	private SymbolTable parent = null;
	private final Map<String, SymbolTableInfo> table = new HashMap<>();

	private long ext = 0;

	@Override
	public SymbolTable getDeclaringSymbolTable(String identifier) {
		SymbolTable t = this;
		Boolean isDeclared = false;
		do {
			isDeclared = t.isDeclaredInCurrentScope(identifier);
		} while (!isDeclared && (t = t.getParentSymbolTable()) != null);
		
		return t;
	}

	@Override
	public String getIdentifierAlias(String identifier) {
		SymbolTableInfo sbInfo = table.get(identifier);
		
		if (sbInfo == null) {
			if (parent == null)
				return null;
			else
				parent.getIdentifierAlias(identifier);
		}
		
		return (sbInfo.alias != null)?sbInfo.alias:identifier;
	}
	
	@Override
	public Liveliness getLivelinessInformation(String identifier) {
		SymbolTableInfo sbInfo = table.get(identifier);
		
		if (sbInfo == null)
			return null;
		
		return sbInfo.liveliness;
	}

	@Override
	public String getNextFreeTemporary() {
		String candidate;
		
		do {
			candidate = TEMP_PREFIX + ext++;
		} while (this.isDeclared(candidate));
		
		return candidate;
	}

	@Override
	public SymbolTable getParentSymbolTable() {
		return parent;
	}

	@Override
	public SymbolTable getRootSymbolTable() {
		SymbolTable t = this;
		
		while(t.getParentSymbolTable() != null) {
			t = t.getParentSymbolTable();
		}
		
		return t;
	}

	@Override
	public Boolean insert(String identifier, Type type) {
		return table.put(identifier, new SymbolTableInfo(type)) == null;
	}

	@Override
	public Boolean isDeclared(String identifier) {
		 return getDeclaringSymbolTable(identifier) != null;
	}
	
	@Override
	public Boolean isDeclaredInCurrentScope(String identifier) {
		return table.containsKey(identifier); 
	}
	
	@Override
	public Type lookupType(String identifier) {
		SymbolTable sbTable = getDeclaringSymbolTable(identifier);
		
		if (sbTable == null) {
			return null;
		}
		
		return sbTable.lookupTypeInCurrentScope(identifier);
	}

	@Override
	public Type lookupTypeInCurrentScope(String identifier) {
		SymbolTableInfo sbInfo = table.get(identifier);
		
		if (sbInfo == null) {
			return null;
		}
		
		return sbInfo.type;
	}

	@Override
	public void putTemporary(String identifier, Type type) {
		getRootSymbolTable().insert(identifier, type);
	}

	@Override
	public Boolean remove(String identifier) {
		return table.remove(identifier) != null;
	}

	@Override
	public void setIdentifierAlias(String identifier, String alias) {
		table.get(identifier).alias = alias;
	}

	@Override
	public void setLivelinessInformation(String identifier,
			Liveliness liveliness) {
		SymbolTableInfo sbInfo = table.get(identifier);
		
		if (sbInfo == null)
			return;
		
		sbInfo.liveliness = liveliness;
	}

	public String toString(){
		return table.toString();
	}
}
