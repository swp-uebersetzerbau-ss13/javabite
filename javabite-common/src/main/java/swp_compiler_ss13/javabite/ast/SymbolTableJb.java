package swp_compiler_ss13.javabite.ast;

import java.util.HashMap;
import java.util.Map;

import swp_compiler_ss13.common.optimization.Liveliness;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.types.Type;

public class SymbolTableJb implements SymbolTable {
	private final static String TEMP_PREFIX = "tmp_";
	
	private SymbolTable parent = null;
	private final Map<String, SymbolTableInfo> table = new HashMap<>();
	private long ext = 0;

	@Override
	public SymbolTable getParentSymbolTable() {
		return parent;
	}

	@Override
	public Boolean isDeclared(String identifier) {
		return table.containsKey(identifier); 
	}
	
	/**
	 * Checks if the given identifier is already declared in this table or 
	 * parent.
	 * @param identifier
	 * @return
	 */
	public Boolean isDeclaredScope(String identifier) {
		SymbolTable t = this;
		Boolean isDeclared = false;
		do {
			isDeclared = t.isDeclared(identifier);
		} while (!isDeclared && (t = t.getParentSymbolTable()) != null);
		
		return isDeclared; 
	}

	@Override
	public Type lookupType(String identifier) {
		SymbolTableInfo sbInfo = table.get(identifier);
		
		if (sbInfo == null) {
			return null;
		}
		
		return sbInfo.type;
	}

	@Override
	public void insert(String identifier, Type type) {
		table.put(identifier, new SymbolTableInfo(type));
	}

	@Override
	public Boolean remove(String identifier) {
		return table.remove(identifier) != null;
	}

	@Override
	public void setLivelinessInformation(String identifier,
			Liveliness liveliness) {
		SymbolTableInfo sbInfo = table.get(identifier);
		
		if (sbInfo == null)
			return;
		
		sbInfo.liveliness = liveliness;
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
		} while (this.isDeclaredScope(candidate));
		
		return candidate;
	}

	@Override
	public void putTemporary(String identifier, Type type) {
		getRootTable().insert(identifier, type);
	}
	
	private SymbolTable getRootTable() {
		SymbolTable t = this;
		
		while(t.getParentSymbolTable() != null) {
			t = t.getParentSymbolTable();
		}
		
		return t;
	}
	
	private class SymbolTableInfo {
		Type type;
		Liveliness liveliness = null;
		
		SymbolTableInfo(Type type) {
			this.type = type;
		}
		@Override
		public String toString() {
			return "["+type+", "+liveliness+"]";
		}
	}
	
	public String toString(){
		return table.toString();
	}
}
