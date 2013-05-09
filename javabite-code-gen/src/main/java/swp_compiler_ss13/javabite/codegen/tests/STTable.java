package swp_compiler_ss13.javabite.codegen.tests;

import java.util.HashMap;

import swp_compiler_ss13.common.optimization.Liveliness;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.types.Type;

public class STTable implements SymbolTable {

	private SymbolTable parent = null;
	private HashMap<String, Type> symbolTable;
	private long ext;

	public STTable(){
		this.symbolTable = new HashMap<String, Type>();
	}
	
	/**
	 * 
	 * @param parent
	 */
	public STTable(SymbolTable parent) {
		this.parent = parent;
		this.symbolTable = new HashMap<String, Type>();
	}
				
	@Override
	public void setLivelinessInformation(String identifier,
			Liveliness liveliness) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Boolean remove(String identifier) {
		return symbolTable.remove(identifier) != null;
	}
	
	@Override
	public void putTemporary(String identifier, Type type) {
		if(this.parent != null){
			parent.putTemporary(identifier, type);
			}else{
			insert(identifier, type);
		}
	}

	@Override
	public SymbolTable getParentSymbolTable() {
		return parent;
	}

	@Override
	public Boolean isDeclared(String identifier) {
		return symbolTable.containsKey(identifier);
	}

	@Override
	public Type lookupType(String identifier) {
		return symbolTable.get(identifier);
	}

	@Override
	public void insert(String identifier, Type type) {
		if(!isDeclared(identifier)){
			symbolTable.put(identifier, type);
		}
	}

	@Override
	public Liveliness getLivelinessInformation(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNextFreeTemporary() {
		String temp;
		do{
		temp = "tmp" + ext;
		ext++;
		}while(isDeclared(temp));
		return temp;
		}

}
