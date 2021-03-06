package swp_compiler_ss13.javabite.parser.astGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode;
import swp_compiler_ss13.common.lexer.NumToken;
import swp_compiler_ss13.common.lexer.RealToken;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.ast.SymbolTableJb;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.IdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.ArithmeticBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.AssignmentNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.DoWhileNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.LogicBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.RelationExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.WhileNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BreakNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.ternary.BranchNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ArithmeticUnaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ArrayIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.LogicUnaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.PrintNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ReturnNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.StructIdentifierNodeJb;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar;

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
	Stack<BlockNodeJb> currentBlocks;

	// 
	List<Object> nonDisposedTokenConsumer=new LinkedList<>();
	
	public ASTGenerator(List<TargetGrammar.Reduction> reductions) {
		this.reductions = reductions;
		this.currentBlocks = new Stack<>();
	}

	public ASTJb generateAST() {
		// generate AST
		ASTJb ast = new ASTJb();

		BlockNode rootNode = this.useBlockProduction();
		ast.setRootNode(rootNode);
	
		
		return ast;
	}

	private BlockNodeJb useBlockProduction() {
		if (debug){
			logger.info("process \"programm\" \treduction on reductions {}",reductions);
		}
		
		// get parent-scope
		SymbolTableJb newSymbolTable=new SymbolTableJb();
		if (currentBlocks.size()>=1){
			SymbolTable parentSymbolTable=currentBlocks.peek().getSymbolTable();
			newSymbolTable.setParentSymbolTable(parentSymbolTable);
		}
		
		// Generate new Block
		BlockNodeJb root = new BlockNodeJb();
		currentBlocks.push(root);
		root.setSymbolTable(newSymbolTable);
		// delete next reductions' list production from it
		this.reductions.remove(0);

		// use program productions functions
		this.useDeclsProduction();
		this.useStmtsProduction();

		return this.currentBlocks.pop();
	}
	
	private BlockNodeJb useBlockProductionDeclarationsOnly() {
		if (debug){
			logger.info("process \"programmDeclarationsOnly\" \treduction on reductions {}",reductions);
		}
		
		// get parent-scope
		SymbolTableJb newSymbolTable=new SymbolTableJb();
		if (currentBlocks.size()>=1){
			SymbolTable parentSymbolTable=currentBlocks.peek().getSymbolTable();
			newSymbolTable.setParentSymbolTable(parentSymbolTable);
		}
		
		// Generate new Block
		BlockNodeJb root = new BlockNodeJb();
		currentBlocks.push(root);
		root.setSymbolTable(newSymbolTable);

		// use program productions functions
		this.useDeclsProduction();

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
			DeclarationNodeJb decl = this.useDeclProduction();
			BlockNodeJb currentBlock = this.currentBlocks.peek();
			currentBlock.addDeclaration(decl);
			break;
		case "decls ->":
			break
			;
		default:
			logger.error("[Decls] thisReduction : {} , matches no case",thisReduction);	
		}
		
	}

	private DeclarationNodeJb useDeclProduction() {
		if (debug){
			logger.info("process \"decl\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		DeclarationNodeJb decl = null;

		// use decl productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "decl -> type ID SEMICOLON":
			decl=new DeclarationNodeJb();
			DeclarationNodeJb type_wrapper=useDeclarationTypeProduction();
			decl.setType(type_wrapper.getType());
			decl.addToCoverage(type_wrapper.coverage());
			decl.addToCoverage((Token)thisReduction.getRightSide().get(1));
			decl.addToCoverage((Token)thisReduction.getRightSide().get(2));
			String id=(((Token)thisReduction.getRightSide().get(1)).getValue());
			decl.setIdentifier(id);
			break;
		default:
			logger.error("[Decl] thisReduction : {} , matches no case",thisReduction);
			
		}
		return decl;
	}
	
	private DeclarationNodeJb useDeclarationTypeProduction() {
		if (debug){
			logger.info("process \"type\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		DeclarationNodeJb type_wrapper=new DeclarationNodeJb();
		Type type = null;
		List<Token> additionals=new LinkedList<>();
		
		// use decl productions functions according to the specific production
		switch (thisReduction.toString()) {
		// primitive "basic" cases
		case "type -> LONG_SYMBOL":
			type=new LongType();
			additionals.add((Token)thisReduction.getRightSide().get(0));
			break;
		case "type -> DOUBLE_SYMBOL":
			type=new DoubleType();
			additionals.add((Token)thisReduction.getRightSide().get(0));
			break;
		case "type -> STRING_SYMBOL":
			type=new StringType(-1l);
			additionals.add((Token)thisReduction.getRightSide().get(0));
			break;
		case "type -> BOOL_SYMBOL":
			additionals.add((Token)thisReduction.getRightSide().get(0));
			type=new BooleanType();
			break;
		case "type -> type LEFT_BRACKET NUM RIGHT_BRACKET":
			DeclarationNodeJb type_wrapper_arr=useDeclarationTypeProduction();
			Type arrayType=type_wrapper_arr.getType();
			NumToken num=(NumToken)thisReduction.getRightSide().get(2);
			
			LiteralNodeJb numNode = new LiteralNodeJb();
			numNode.setLiteral(num.getValue());
			numNode.setLiteralType(new LongType());
			
			additionals.addAll(type_wrapper_arr.coverage());
			additionals.add((Token)thisReduction.getRightSide().get(1));
			additionals.add((Token)thisReduction.getRightSide().get(2));
			additionals.add((Token)thisReduction.getRightSide().get(3));
			
			type=new ArrayType(arrayType, Integer.parseInt(numNode.getLiteral()));
			break;
		case "type -> RECORD_SYMBOL LEFT_BRACE decls RIGHT_BRACE":
			// delegate to blockProduction to avoid code redundancy
			BlockNodeJb recordDecls=useBlockProductionDeclarationsOnly();
			ArrayList<Member> membersList=new ArrayList<>();
			// extract member types and fill membersList
			for (DeclarationNode decl : recordDecls.getDeclarationList()){
				membersList.add(new Member(decl.getIdentifier(),decl.getType()));
			}
			// set tokens
			additionals.add((Token)thisReduction.getRightSide().get(0));
			additionals.add((Token)thisReduction.getRightSide().get(1));
			additionals.addAll(recordDecls.coverage());
			additionals.add((Token)thisReduction.getRightSide().get(3));
			
			type=new StructType(membersList.toArray(new Member[membersList.size()]));
			break;
		default:
			logger.error("[Type] thisReduction : {} , matches no case",thisReduction);			
		}
		type_wrapper.addToCoverage(additionals);
		
		
		type_wrapper.setType(type);

		return type_wrapper;
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
			BlockNodeJb currentBlock = this.currentBlocks.pop();
			currentBlock.addStatement(stmt);
			this.currentBlocks.add(currentBlock);
			break;
		case "stmts ->":
			break;
		default:
			logger.error("[Stmts] thisReduction : {} , matches no case",thisReduction);
			throw new RuntimeException();
		}
	}

	private StatementNodeJb useStmtProduction() {
		if (debug){
			logger.info("process \"stmt\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNodeJb stmt = null;

		// use stmt productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "stmt -> assign SEMICOLON":
			stmt = this.useAssignProduction();
			break;
		case "stmt -> RETURN SEMICOLON":
			ReturnNodeJb returnNode = new ReturnNodeJb();
			returnNode.setRightValue(null);
			stmt = returnNode;
			break;
		case "stmt -> RETURN loc SEMICOLON":
			ReturnNodeJb returnNodeTwo = new ReturnNodeJb();
			IdentifierNodeJb loc = this.useLocProduction();
			returnNodeTwo.setRightValue(loc);
			stmt = returnNodeTwo;
			break;
		case "stmt -> PRINT loc SEMICOLON":
			PrintNodeJb printNode = new PrintNodeJb();
			IdentifierNodeJb loc2 = this.useLocProduction();
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
		case "stmt -> BREAK SEMICOLON":
            BreakNodeJb breakNode = new BreakNodeJb();
			stmt=breakNode;
			break;
		case "stmt -> block":
			BlockNodeJb blockNode=useBlockProduction();
			stmt=blockNode;
			break;
		case "stmt -> WHILE LEFT_PARAN assign RIGHT_PARAN stmt":
			WhileNodeJb whileNode=new WhileNodeJb();
			ExpressionNodeJb whileCondition=useAssignProduction();
			StatementNodeJb whileStatement=useStmtProduction();
			whileNode.setCondition(whileCondition);
			whileNode.setLoopBody(whileStatement);
			stmt=whileNode;
			break;
		case "stmt -> DO stmt WHILE LEFT_PARAN assign RIGHT_PARAN SEMICOLON":
			DoWhileNodeJb doWhileNode=new DoWhileNodeJb();
			StatementNodeJb doWhileStatement=useStmtProduction();
			ExpressionNodeJb doWhileCondition=useAssignProduction();
			doWhileNode.setCondition(doWhileCondition);
			doWhileNode.setLoopBody(doWhileStatement);
			stmt=doWhileNode;
			break;
		default:
			logger.error("[Stmts] thisReduction : {} , matches no case",thisReduction);
		}
		stmt.putAllTokens(thisReduction.getRightSide());
		return stmt;
	}

	private IdentifierNodeJb useLocProduction() {
		if (debug){
			logger.info("process \"loc\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		IdentifierNodeJb loc = null;

		// use loc productions functions according to the specific production
		switch (thisReduction.toString()) {
		
		case "loc -> ID":
			BasicIdentifierNodeJb basicId = new BasicIdentifierNodeJb();
			basicId.setIdentifier(((Token)thisReduction.getRightSide().get(0)).getValue());
			loc = basicId;
			break;
		case "loc -> loc LEFT_BRACKET assign RIGHT_BRACKET":
			ArrayIdentifierNodeJb arrayId = new ArrayIdentifierNodeJb();
			IdentifierNodeJb arrayLoc=useLocProduction();
			ExpressionNodeJb indexNode=useAssignProduction();
			arrayId.setIdentifierNode(arrayLoc);
			arrayId.setIndexNode(indexNode);
			loc = arrayId;
			break;
		case "loc -> loc DOT ID":
			StructIdentifierNodeJb structIdentifier=new StructIdentifierNodeJb();
			IdentifierNodeJb structLoc = useLocProduction();
			BasicIdentifierNodeJb structId= new BasicIdentifierNodeJb();
			structId.setIdentifier(((Token)thisReduction.getRightSide().get(2)).getValue());
			structIdentifier.setIdentifierNode(structLoc);
			structIdentifier.setFieldName(structId.getIdentifier());
			loc = structIdentifier;
			break;
			
		default:
			logger.error("[Loc] thisReduction : {} , matches no case",thisReduction);
		}
		loc.putAllTokens(thisReduction.getRightSide());
		return loc;
	}

	private ExpressionNodeJb useAssignProduction() {
		if (debug){
			logger.info("process \"assign\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNodeJb assign = null;

		// use assign productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "assign -> loc ASSIGNOP assign":
			IdentifierNodeJb leftLoc = this.useLocProduction();
			StatementNodeJb rightAssign = useAssignProduction();
			AssignmentNodeJb assignNode = new AssignmentNodeJb();
			assignNode.setLeftValue(leftLoc);
			assignNode.setRightValue((ExpressionNode)rightAssign);
			assign = assignNode;
			break;
		case "assign -> bool":
			assign = this.useBoolProduction();
			break;
		default:
			logger.error("[Assign] thisReduction : {} , matches no case",thisReduction);
		}
		assign.putAllTokens(thisReduction.getRightSide());
        return assign;
	}

	private ExpressionNodeJb useBoolProduction() {
		if (debug){
			logger.info("process \"bool\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNodeJb bool = null;

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
			logger.error("[Bool] thisReduction : {} , matches no case",thisReduction);
		}
		bool.putAllTokens(thisReduction.getRightSide());
        return bool;
	}

	private ExpressionNodeJb useJoinProduction() {
		if (debug){
			logger.info("process \"join\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNodeJb join = null;

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
			logger.error("[Join] thisReduction : {} , matches no case",thisReduction);
		}
		join.putAllTokens(thisReduction.getRightSide());
		return join;
	}

	private ExpressionNodeJb useEqualityProduction() {
		if (debug){
			logger.info("process \"equality\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNodeJb equality = null;

		// use equality productions functions according to the specific
		// production
		switch (thisReduction.toString()) {
		case "equality -> rel":
			equality = this.useRelProduction();
			break;
		case "equality -> equality EQUALS rel":
			RelationExpressionNodeJb equalNode=new RelationExpressionNodeJb();
			equalNode.setOperator(BinaryOperator.EQUAL);
			ExpressionNodeJb exp1=useEqualityProduction();
			ExpressionNodeJb rel1=useRelProduction();
			equalNode.setLeftValue(exp1);
			equalNode.setRightValue(rel1);
			equality=equalNode;
			break;
		case "equality -> equality NOT_EQUALS rel":
			RelationExpressionNodeJb equalNode1=new RelationExpressionNodeJb();
			equalNode1.setOperator(BinaryOperator.INEQUAL);
			ExpressionNodeJb exp2=useEqualityProduction();
			ExpressionNodeJb rel2=useRelProduction();
			equalNode1.setLeftValue(exp2);
			equalNode1.setRightValue(rel2);
			equality=equalNode1;
			break;
		default:
			logger.error("[Equality] thisReduction : {} , matches no case",thisReduction);
		}
		equality.putAllTokens(thisReduction.getRightSide());
		return equality;	
	}

	private ExpressionNodeJb useRelProduction() {
		if (debug){
			logger.info("process \"rel\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNodeJb rel = null;

		// use rel productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "rel -> expr":
			rel = this.useExprProduction();
			break;
		case "rel -> expr LESS expr":
			RelationExpressionNodeJb lben1=new RelationExpressionNodeJb();
			lben1.setLeftValue(useExprProduction());
			lben1.setRightValue(useExprProduction());
			lben1.setOperator(BinaryOperator.LESSTHAN);
			rel=lben1;
			break;
		case "rel -> expr LESS_OR_EQUAL expr":
			RelationExpressionNodeJb lben2=new RelationExpressionNodeJb();
			lben2.setLeftValue(useExprProduction());
			lben2.setRightValue(useExprProduction());
			lben2.setOperator(BinaryOperator.LESSTHANEQUAL);
			rel=lben2;
			break;
		case "rel -> expr GREATER expr":
			RelationExpressionNodeJb lben3=new RelationExpressionNodeJb();
			lben3.setLeftValue(useExprProduction());
			lben3.setRightValue(useExprProduction());
			lben3.setOperator(BinaryOperator.GREATERTHAN);
			rel=lben3;
			break;
		case "rel -> expr GREATER_EQUAL expr":
			RelationExpressionNodeJb lben4=new RelationExpressionNodeJb();
			lben4.setLeftValue(useExprProduction());
			lben4.setRightValue(useExprProduction());
			lben4.setOperator(BinaryOperator.GREATERTHANEQUAL);
			rel=lben4;
			break;
		default:
			logger.error("[Rel] thisReduction : {} , matches no case",thisReduction);
		}
		rel.putAllTokens(thisReduction.getRightSide());
		return rel;
	}

	private ExpressionNodeJb useExprProduction() {
		if (debug){
			logger.info("process \"expr\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNodeJb expr = null;

		// use expr productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "expr -> expr PLUS term":
			ExpressionNodeJb expr1 = this.useExprProduction();
			ExpressionNodeJb term1 = this.useTermProduction();
			ArithmeticBinaryExpressionNodeJb add = 
					new ArithmeticBinaryExpressionNodeJb();
			add.setLeftValue(expr1);
			add.setRightValue(term1);
			add.setOperator(BinaryExpressionNode.BinaryOperator.ADDITION);
			expr = add;
			break;
		case "expr -> expr MINUS term":
			ExpressionNodeJb expr2 = this.useExprProduction();
			ExpressionNodeJb term2 = this.useTermProduction();
			ArithmeticBinaryExpressionNodeJb sub = 
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
			logger.error("[Expr] thisReduction : {} , matches no case",thisReduction);
		}
		expr.putAllTokens(thisReduction.getRightSide());
		return expr;
	}

	private ExpressionNodeJb useTermProduction() {
		if (debug){
			logger.info("process \"term\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNodeJb term = null;

		// use term productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "term -> term TIMES unary":
			ExpressionNodeJb term1 =  this.useTermProduction();
			ExpressionNodeJb unary1 =  this.useUnaryProduction();
			ArithmeticBinaryExpressionNodeJb mul = 
					new ArithmeticBinaryExpressionNodeJb();
			mul.setLeftValue(term1);
			mul.setRightValue(unary1);
			mul.setOperator(BinaryExpressionNode.BinaryOperator.MULTIPLICATION);
			term = mul;
			break;
		case "term -> term DIVIDE unary":
			ExpressionNodeJb term2 =  this.useTermProduction();
			ExpressionNodeJb unary2 =  this.useUnaryProduction();
			ArithmeticBinaryExpressionNodeJb div = 
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
			logger.error("[Term] thisReduction : {} , matches no case",thisReduction);
		}
		term.putAllTokens(thisReduction.getRightSide());
		return term;
	}

	private ExpressionNodeJb useUnaryProduction() {
		if (debug){
			logger.info("process \"unary\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		ExpressionNodeJb unary = null;

		// use unary productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "unary -> MINUS unary":
			ExpressionNodeJb unary1 = this.useUnaryProduction();
			ArithmeticUnaryExpressionNodeJb minus = 
					new ArithmeticUnaryExpressionNodeJb();
			minus.setRightValue(unary1);
			minus.setOperator(UnaryExpressionNode.UnaryOperator.MINUS);
			unary = minus;
			break;
		case "unary -> NOT unary":
			ExpressionNodeJb unary2 = this.useUnaryProduction();
			LogicUnaryExpressionNodeJb not=new LogicUnaryExpressionNodeJb();
			not.setRightValue(unary2);
			not.setOperator(UnaryExpressionNode.UnaryOperator.LOGICAL_NEGATE);
			unary = not;
			break;
		case "unary -> factor":
			unary = this.useFactorProduction();
			break;
		default:
			logger.error("[Unary] thisReduction : {} , matches no case",thisReduction);
		}
		unary.putAllTokens(thisReduction.getRightSide());
		return unary;
	}

	private ExpressionNodeJb useFactorProduction() {
		if (debug){
			logger.info("process \"factor\" \treduction on reductions {}",reductions);
		}
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		
		ExpressionNodeJb factor = null;
		
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
			
			LiteralNodeJb numNode = new LiteralNodeJb();
			numNode.setLiteral(num.getValue());
			numNode.setLiteralType(new LongType());

			factor = numNode;
			break;
		case "factor -> REAL":
			RealToken real=(RealToken)thisReduction.getRightSide().get(0);
			
			LiteralNodeJb realNode = new LiteralNodeJb();
			realNode.setLiteral(real.getValue());
			realNode.setLiteralType(new DoubleType());

			factor = realNode;
			break;
		case "factor -> TRUE":
			LiteralNodeJb trueNode = new LiteralNodeJb();
			trueNode.setLiteral("true");
			trueNode.setLiteralType(new BooleanType());
			
			factor = trueNode;
			break;
		case "factor -> FALSE":
			LiteralNodeJb falseNode = new LiteralNodeJb();
			falseNode.setLiteral("false");
			falseNode.setLiteralType(new BooleanType());

			factor = falseNode;
			break;
		case "factor -> STRING":
			// Tmp get value of string ... not necessary in MS1
			Token token = (Token) thisReduction.getRightSide().get(0);
			
			LiteralNodeJb string = new LiteralNodeJb();
			string.setLiteral(token.getValue());
			string.setLiteralType(new StringType(-1L));
			factor = string;
			break;
		default:
			logger.error("[Factor] thisReduction : {} , matches no case",thisReduction);
		}
		factor.putAllTokens(thisReduction.getRightSide());
		return factor;
	}
}
