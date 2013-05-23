package swp_compiler_ss13.javabite.parser.astGenerator;

import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.LogicUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode;
import swp_compiler_ss13.common.lexer.NumToken;
import swp_compiler_ss13.common.lexer.RealToken;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.ast.SymbolTableJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.ArithmeticBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.AssignmentNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.LogicBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.ternary.BranchNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ArithmeticUnaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.LogicUnaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.PrintNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ReturnNodeJb;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar;
import swp_compiler_ss13.javabite.types.TypeJb;

/**
 * ASTGenerator class. This class provides functions to create an AST from
 * a list of reductions. This list has to meet the following format:
 * The reductions have to ordered like if one would traverse the parsetree
 * dfe, left to right.
 * Example1:
 * If the SLR-Automaton gives the following right derivation for the word
 * aa*a+ with an appropriate grammar:
 * S->SS+, S->a, S->SS*, S->a, S->a
 * the reduction list used by this class has to meet the following format:
 * S->SS+, S->SS*, S->a, S->a, S->a
 * Example2:
 * If the SLR-Automaton gives the following right derivation for the word
 * abbcde with an appropriate grammar:
 * S->aABe, B->d, A->Abc, A->b
 * the reduction list used by this class has to meet the following format:
 * S->aABe, A->Abc, A->b, B->d
 * 
 * @TODO: remove string representation dependency 
 * @author Marco , Till
 * @since 11.05.2013
 * 
 */
public class ASTGenerator {
	// output for each step
	boolean debug=false;
	
	Logger logger=LoggerFactory.getLogger(ASTGenerator.class);
	// reduction list meeting the appropriate format
	List<TargetGrammar.Reduction> reductions;
	// stack holding blocknodes, top: current block
	Stack<BlockNode> currentBlocks;

	public ASTGenerator(List<TargetGrammar.Reduction> reductions) {
		this.reductions = reductions;
		this.currentBlocks = new Stack<BlockNode>();
	}

	public ASTJb generateAST() {
		// generate AST
		ASTJb ast = new ASTJb();

		// MS1 version
		if (reductions != null) {
			// if this is null there was a earlier error in parser 
			// TODO secure call to ASTGenerator against empty input
			BlockNode rootNode = this.useProgramProduction();
			ast.setRootNode(rootNode);
		}
		
		return ast;
	}

	private BlockNode useProgramProduction() {
		if (debug){
			logger.info("process \"programm\" \treduction on reductions {}",reductions);
		}
		
		// Generate new Block
		BlockNode root = new BlockNodeJb();
		currentBlocks.push(root);
		root.setSymbolTable(new SymbolTableJb());
		// delete next reductions' list production from it
		this.reductions.remove(0);

		// use program productions functions
		this.useDeclsProduction();
		this.useStmtsProduction();

		return this.currentBlocks.pop();
	}

	private void useDeclsProduction() {
		if (debug){
			logger.info("process \"decls\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);

		// use decls productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "decls -> decls decl":
			this.useDeclsProduction();
			DeclarationNode decl = this.useDeclProduction();
			BlockNode currentBlock = this.currentBlocks.pop();
			currentBlock.addDeclaration(decl);
			this.currentBlocks.add(currentBlock);
			break;
		case "decls ->":
			break
			;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);	
		}
		
	}

	private DeclarationNode useDeclProduction() {
		if (debug){
			logger.info("process \"decl\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		DeclarationNode decl = null;

		// use decl productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "decl -> type ID SEMICOLON":
			decl=new DeclarationNodeJb();
			decl.setType(useTypeProduction());
			String id=(((Token)thisReduction.getRightSide().get(1)).getValue());
			decl.setIdentifier(id);
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
			
		}

		return decl;
	}
	
	private Type useTypeProduction() {
		if (debug){
			logger.info("process \"type\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		Type type = null;
		
		// use decl productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "type -> LONG_SYMBOL":
			type=new TypeJb(Kind.LONG);
			break;
		case "type -> DOUBLE_SYMBOL":
			type=new TypeJb(Kind.DOUBLE);
			break;
		case "type -> STRING_SYMBOL":
			type=new TypeJb(Kind.STRING);
			break;
		case "type -> BOOL_SYMBOL":
			type=new TypeJb(Kind.BOOLEAN);
			break;
		
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
			
		}

		return type;
	}

	private void useStmtsProduction() {
		if (debug){
			logger.info("process \"stmts\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);

		// use stmts productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "stmts -> stmts stmt":
			this.useStmtsProduction();
			StatementNode stmt = this.useStmtProduction();
			BlockNode currentBlock = this.currentBlocks.pop();
			currentBlock.addStatement(stmt);
			this.currentBlocks.add(currentBlock);
			break;
		case "stmts ->":
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}
	}

	private StatementNode useStmtProduction() {
		if (debug){
			logger.info("process \"stmt\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode stmt = null;

		// use stmt productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "stmt -> assign SEMICOLON":
			stmt = this.useAssignProduction();
			break;
		case "stmt -> RETURN SEMICOLON":
			ReturnNode returnNode = new ReturnNodeJb();
			returnNode.setRightValue(null);
			stmt = returnNode;
			break;
		case "stmt -> RETURN loc SEMICOLON":
			ReturnNode returnNodeTwo = new ReturnNodeJb();
			IdentifierNode loc = (IdentifierNode) this.useLocProduction();
			returnNodeTwo.setRightValue(loc);
			stmt = returnNodeTwo;
			break;
		case "stmt -> PRINT loc SEMICOLON":
			PrintNode printNode = new PrintNodeJb();
			IdentifierNode loc2 = (IdentifierNode) this.useLocProduction();
			printNode.setRightValue(loc2);
			stmt = printNode;
			break;
		case "stmt -> IF LEFT_PARAN assign RIGHT_PARAN stmt":
			BranchNodeJb branchNode=new BranchNodeJb();
			ExpressionNode assignNode=useAssignProduction();
			StatementNode stmtNode=useStmtProduction();
			branchNode.setCondition(assignNode);
			branchNode.setStatementNodeOnTrue(stmtNode);
			stmt= branchNode;
			break;
		case "stmt -> IF LEFT_PARAN assign RIGHT_PARAN stmt ELSE stmt":
			BranchNodeJb branchNode2=new BranchNodeJb();
			ExpressionNode assignNode2=useAssignProduction();
			StatementNode stmtNodeTrue=useStmtProduction();
			StatementNode stmtNodeFalse=useStmtProduction();
			branchNode2.setCondition(assignNode2);
			branchNode2.setStatementNodeOnTrue(stmtNodeTrue);
			branchNode2.setStatementNodeOnFalse(stmtNodeFalse);
			stmt= branchNode2;
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return stmt;
	}

	private ExpressionNode useLocProduction() {
		if (debug){
			logger.info("process \"loc\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		IdentifierNode loc = null;

		// use loc productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "loc -> ID":
			BasicIdentifierNode basicId = new BasicIdentifierNodeJb();
			basicId.setIdentifier(((Token)thisReduction.getRightSide().get(0)).getValue());
			loc = basicId;
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return loc;
	}

	private ExpressionNode useAssignProduction() {
		if (debug){
			logger.info("process \"assign\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNode assign = null;

		// use assign productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "assign -> loc ASSIGNOP assign":
			IdentifierNode leftLoc = (IdentifierNode) this.useLocProduction();
			StatementNode rightAssign = useAssignProduction();
			AssignmentNode assignNode = new AssignmentNodeJb();
			assignNode.setLeftValue(leftLoc);
			assignNode.setRightValue(rightAssign);
			assign = assignNode;
			break;
		case "assign -> bool":
			assign = this.useBoolProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return assign;
	}

	private ExpressionNode useBoolProduction() {
		if (debug){
			logger.info("process \"bool\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNode bool = null;

		// use bool productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "bool -> join":
			bool = this.useJoinProduction();
			break;
		case "bool -> bool OR join":
			LogicBinaryExpressionNodeJb lben=new LogicBinaryExpressionNodeJb();
			ExpressionNode bool1 = this.useBoolProduction();
			ExpressionNode bool2 = this.useJoinProduction();
			lben.setOperator(BinaryOperator.LOGICAL_OR);
			lben.setLeftValue(bool1);
			lben.setRightValue(bool2);
			bool=lben;
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return bool;
	}

	private ExpressionNode useJoinProduction() {
		if (debug){
			logger.info("process \"join\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNode join = null;

		// use join productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "join -> equality":
			join = this.useEqualityProduction();
			break;
		case "join -> join AND equality":
			LogicBinaryExpressionNodeJb lben=new LogicBinaryExpressionNodeJb();
			ExpressionNode bool1 = this.useJoinProduction();
			ExpressionNode bool2 = this.useEqualityProduction();
			lben.setOperator(BinaryOperator.LOGICAL_AND);
			lben.setLeftValue(bool1);
			lben.setRightValue(bool2);
			join=lben;
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return join;
	}

	private ExpressionNode useEqualityProduction() {
		if (debug){
			logger.info("process \"equality\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNode equality = null;

		// use equality productions functions according to the specific
		// production
		switch (thisReduction.toString()) {
		case "equality -> rel":
			equality = this.useRelProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return equality;
	}

	private ExpressionNode useRelProduction() {
		if (debug){
			logger.info("process \"rel\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNode rel = null;

		// use rel productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "rel -> expr":
			rel = this.useExprProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return rel;
	}

	private ExpressionNode useExprProduction() {
		if (debug){
			logger.info("process \"expr\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNode expr = null;

		// use expr productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "expr -> expr PLUS term":
			ExpressionNode expr1 = (ExpressionNode) this.useExprProduction();
			ExpressionNode term1 = (ExpressionNode) this.useTermProduction();
			ArithmeticBinaryExpressionNode add = 
					new ArithmeticBinaryExpressionNodeJb();
			add.setLeftValue(expr1);
			add.setRightValue(term1);
			add.setOperator(BinaryExpressionNode.BinaryOperator.ADDITION);
			expr = add;
			break;
		case "expr -> expr MINUS term":
			ExpressionNode expr2 = (ExpressionNode) this.useExprProduction();
			ExpressionNode term2 = (ExpressionNode) this.useTermProduction();
			ArithmeticBinaryExpressionNode sub = 
					new ArithmeticBinaryExpressionNodeJb();
			sub.setLeftValue(expr2);
			sub.setRightValue(term2);
			sub.setOperator(BinaryExpressionNode.BinaryOperator.SUBSTRACTION);
			expr = sub;
			break;
		case "expr -> term":
			expr = this.useTermProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return expr;
	}

	private ExpressionNode useTermProduction() {
		if (debug){
			logger.info("process \"term\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNode term = null;

		// use term productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "term -> term TIMES unary":
			ExpressionNode term1 = (ExpressionNode) this.useTermProduction();
			ExpressionNode unary1 = (ExpressionNode) this.useUnaryProduction();
			ArithmeticBinaryExpressionNode mul = 
					new ArithmeticBinaryExpressionNodeJb();
			mul.setLeftValue(term1);
			mul.setRightValue(unary1);
			mul.setOperator(BinaryExpressionNode.BinaryOperator.MULTIPLICATION);
			term = mul;
			break;
		case "term -> term DIVIDE unary":
			ExpressionNode term2 = (ExpressionNode) this.useTermProduction();
			ExpressionNode unary2 = (ExpressionNode) this.useUnaryProduction();
			ArithmeticBinaryExpressionNode div = 
					new ArithmeticBinaryExpressionNodeJb();
			div.setLeftValue(term2);
			div.setRightValue(unary2);
			div.setOperator(BinaryExpressionNode.BinaryOperator.DIVISION);
			term = div;
			break;
		case "term -> unary":
			term = this.useUnaryProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return term;
	}

	private ExpressionNode useUnaryProduction() {
		if (debug){
			logger.info("process \"unary\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNode unary = null;

		// use unary productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "unary -> MINUS unary":
			ExpressionNode unary1 = this.useUnaryProduction();
			ArithmeticUnaryExpressionNode minus = 
					new ArithmeticUnaryExpressionNodeJb();
			minus.setRightValue(unary1);
			minus.setOperator(UnaryExpressionNode.UnaryOperator.MINUS);
			unary = minus;
			break;
		case "unary -> NOT unary":
			ExpressionNode unary2 = this.useUnaryProduction();
			LogicUnaryExpressionNode not=new LogicUnaryExpressionNodeJb();
			not.setRightValue(unary2);
			not.setOperator(UnaryExpressionNode.UnaryOperator.LOGICAL_NEGATE);
			unary = not;
			break;
		case "unary -> factor":
			unary = this.useFactorProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return unary;
	}

	private ExpressionNode useFactorProduction() {
		if (debug){
			logger.info("process \"factor\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		
		ExpressionNode factor = null;
		
		// use factor productions functions according to the specific
		// production
		switch (thisReduction.toString()) {
		case "factor -> LEFT_PARAN assign RIGHT_PARAN":
			factor = this.useAssignProduction();
			break;
		case "factor -> loc":
			factor = this.useLocProduction();
			break;
		case "factor -> NUM":
			NumToken num=(NumToken)thisReduction.getRightSide().get(0);
			Long l=num.getLongValue();
			
			LiteralNode numNode = new LiteralNodeJb();
			numNode.setLiteral(num.getValue());
			numNode.setLiteralType(new TypeJb(Type.Kind.LONG));

			factor = numNode;
			break;
		case "factor -> REAL":
			RealToken real=(RealToken)thisReduction.getRightSide().get(0);
			Double val=real.getDoubleValue();
			
			LiteralNode realNode = new LiteralNodeJb();
			realNode.setLiteral(real.getValue());
			realNode.setLiteralType(new TypeJb(Type.Kind.DOUBLE));

			factor = realNode;
			break;
		case "factor -> TRUE":
			LiteralNode trueNode = new LiteralNodeJb();
			trueNode.setLiteral("true");
			trueNode.setLiteralType(new TypeJb(Type.Kind.BOOLEAN));
			
			factor = trueNode;
			break;
		case "factor -> FALSE":
			LiteralNode falseNode = new LiteralNodeJb();
			falseNode.setLiteral("false");
			falseNode.setLiteralType(new TypeJb(Type.Kind.BOOLEAN));

			factor = falseNode;
			break;
		case "factor -> STRING":
			// Tmp get value of string ... not necessary in MS1
			Token token=(Token)thisReduction.getRightSide().get(0);
			
			LiteralNode string = new LiteralNodeJb();
			string.setLiteral(token.getValue());
			string.setLiteralType(new TypeJb(Type.Kind.STRING));
			factor = string;
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return factor;
	}
}
