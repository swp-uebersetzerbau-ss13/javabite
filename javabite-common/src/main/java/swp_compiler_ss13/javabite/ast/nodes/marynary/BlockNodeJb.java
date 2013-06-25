package swp_compiler_ss13.javabite.ast.nodes.marynary;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class BlockNodeJb extends StatementNodeJb implements BlockNode {
	public BlockNodeJb() {
		super(ASTNodeType.BlockNode);
	}

	final protected Logger logger = LoggerFactory.getLogger(this.getClass());
	final protected List<DeclarationNode> declarations = new LinkedList<>();
	final protected List<StatementNode> statements = new LinkedList<>();
	SymbolTable symbolTable;

	@Override
	public void addDeclaration(DeclarationNode declaration) {
		// Do a simple trick that provivides the integer-relationship
		// in the ASTNodeJb even if DeclarationNodes are added after
		// statementNodes has been added.
		// Idea: overwrite the previous mapping
		// works because the list is longer as the previous
		// without the new declaration
		declarations.add(declaration);

		// declarations: decl_1,decl_2, ..., decl_n
		int n = declarations.size();

		// for decl : decl_1, decl_2, ..., decl_n
		for (DeclarationNode decl : declarations) {
			// decl_i in left of decl_j <=> i<j
			addChild(decl, -n);
			n--;
		}

		if (symbolTable.isDeclared(declaration.getIdentifier())) {
			logger.warn(
					"declaration already inserted ... should be catched by the semantic analysis later. dec: {}",
					declaration);
		}
		symbolTable.insert(declaration.getIdentifier(), declaration.getType());

	}

	@Override
	public void addStatement(StatementNode statement) {
		statements.add(statement);
		addChild(statement, statements.size());
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
		this.symbolTable = symbolTable;
	}

	@Override
	protected void fillNodeProperties(Properties props) {
		props.put("#decls", declarations.size());
		props.put("#stmts", statements.size());
	}

	@Override
	public List<Token> coverage() {
		List<Token> res = new LinkedList<>();
		List<Token> left = getAssociatedTokenListFromType(TokenType.LEFT_BRACE);
		List<Token> right = getAssociatedTokenListFromType(TokenType.RIGHT_BRACE);
		if (left.size() == 1) {
			res.add(left.get(0));
		}
		for (DeclarationNode de : declarations) {
			res.addAll(de.coverage());
		}
		for (StatementNode st : statements) {
			res.addAll(st.coverage());
		}
		if (right.size() == 1) {
			res.add(right.get(0));
		}
		return res;
	}

}
