package swp_compiler_ss13.javabite.ast.nodes.marynary;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class BlockNodeJb extends StatementNodeJb implements BlockNode {
	final List<DeclarationNode> declarations=new LinkedList<>();
	final List<StatementNode> statements=new LinkedList<>();
	SymbolTable symbolTable;
	
	@Override
	public void addDeclaration(DeclarationNode declaration) {
		declarations.add(declaration);
	}

	@Override
	public void addStatement(StatementNode statement) {
		statements.add(statement);
	}

	@Override
	public List<DeclarationNode> getDeclarationList() {
		return declarations;
	}

	@Override
	public List<StatementNode> getStatementList() {
		return statements;
	}

	@Override
	public Iterator<DeclarationNode> getDeclarationIterator() {
		return declarations.iterator();
	}

	@Override
	public Iterator<StatementNode> getStatementIterator() {
		return statements.iterator();
	}

	@Override
	public Integer getNumberOfDeclarations() {
		return declarations.size();
	}

	@Override
	public Integer getNumberOfStatements() {
		return statements.size();
	}

	@Override
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	@Override
	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable=symbolTable;
	}

}
