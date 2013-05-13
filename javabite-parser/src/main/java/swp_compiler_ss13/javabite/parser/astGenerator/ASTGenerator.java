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
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
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
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ArithmeticUnaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
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
		BlockNode rootNode = this.useProgramProduction();
		ast.setRootNode(rootNode);

		return ast;
	}

	private BlockNode useProgramProduction() {
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
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);

		// use decls productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "decls->declsdecl":
			this.useDeclsProduction();
			DeclarationNode decl = this.useDeclProduction();
			BlockNode currentBlock = this.currentBlocks.pop();
			currentBlock.addDeclaration(decl);
			this.currentBlocks.add(currentBlock);
			break;
		case "decls->":
			break
			;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);	
		}
		
	}

	private DeclarationNode useDeclProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		DeclarationNode decl = null;

		// use decl productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "decl->typeIDSEMICOLON":
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
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		Type type = null;

		// use decl productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "type->LONG_SYMBOL":
			type=new TypeJb(Kind.LONG);
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
			
		}

		return type;
	}

	private void useStmtsProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);

		// use stmts productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "stmts->stmtsstmt":
			this.useStmtsProduction();
			StatementNode stmt = this.useStmtProduction();
			BlockNode currentBlock = this.currentBlocks.pop();
			currentBlock.addStatement(stmt);
			this.currentBlocks.add(currentBlock);
			break;
		case "stmts->":
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}
	}

	private StatementNode useStmtProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode stmt = null;

		// use stmt productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "stmt->assignSEMICOLON":
			stmt = this.useAssignProduction();
			break;
		case "stmt->return":
			ReturnNode returnNode = new ReturnNodeJb();
			returnNode.setRightValue(null);
			stmt = returnNode;
			break;
		case "stmt->RETURNlocSEMICOLON":
			ReturnNode returnNodeTwo = new ReturnNodeJb();
			IdentifierNode loc = (IdentifierNode) this.useLocProduction();
			returnNodeTwo.setRightValue(loc);
			stmt = returnNodeTwo;
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return stmt;
	}

	private StatementNode useLocProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		IdentifierNode loc = null;

		// use loc productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "loc->ID":
			BasicIdentifierNode basicId = new BasicIdentifierNodeJb();
			basicId.setIdentifier(((Token)thisReduction.getRightSide().get(0)).getValue());
			loc = basicId;
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return loc;
	}

	private StatementNode useAssignProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode assign = null;

		// use assign productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "assign->locASSIGNOPassign":
			IdentifierNode leftLoc = (IdentifierNode) this.useLocProduction();
			StatementNode rightAssign = this.useAssignProduction();
			AssignmentNode assignNode = new AssignmentNodeJb();
			assignNode.setLeftValue(leftLoc);
			assignNode.setRightValue(rightAssign);
			assign = assignNode;
			break;
		case "assign->bool":
			assign = this.useBoolProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return assign;
	}

	private StatementNode useBoolProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode bool = null;

		// use bool productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "bool->join":
			bool = this.useJoinProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return bool;
	}

	private StatementNode useJoinProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode join = null;

		// use join productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "join->equality":
			join = this.useEqualityProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return join;
	}

	private StatementNode useEqualityProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode equality = null;

		// use equality productions functions according to the specific
		// production
		switch (thisReduction.toString()) {
		case "equality->rel":
			equality = this.useRelProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return equality;
	}

	private StatementNode useRelProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode rel = null;

		// use rel productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "rel->expr":
			rel = this.useExprProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return rel;
	}

	private StatementNode useExprProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode expr = null;

		// use expr productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "expr->exprPLUSterm":
			ExpressionNode expr1 = (ExpressionNode) this.useExprProduction();
			ExpressionNode term1 = (ExpressionNode) this.useTermProduction();
			ArithmeticBinaryExpressionNode add = 
					new ArithmeticBinaryExpressionNodeJb();
			add.setLeftValue(expr1);
			add.setRightValue(term1);
			add.setOperator(BinaryExpressionNode.BinaryOperator.ADDITION);
			expr = add;
			break;
		case "expr->exprMINUSterm":
			ExpressionNode expr2 = (ExpressionNode) this.useExprProduction();
			ExpressionNode term2 = (ExpressionNode) this.useTermProduction();
			ArithmeticBinaryExpressionNode sub = 
					new ArithmeticBinaryExpressionNodeJb();
			sub.setLeftValue(expr2);
			sub.setRightValue(term2);
			sub.setOperator(BinaryExpressionNode.BinaryOperator.SUBSTRACTION);
			expr = sub;
			break;
		case "expr->term":
			expr = this.useTermProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return expr;
	}

	private StatementNode useTermProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode term = null;

		// use term productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "term->termTIMESunary":
			ExpressionNode term1 = (ExpressionNode) this.useTermProduction();
			ExpressionNode unary1 = (ExpressionNode) this.useUnaryProduction();
			ArithmeticBinaryExpressionNode mul = 
					new ArithmeticBinaryExpressionNodeJb();
			mul.setLeftValue(term1);
			mul.setRightValue(unary1);
			mul.setOperator(BinaryExpressionNode.BinaryOperator.MULTIPLICATION);
			term = mul;
			break;
		case "term->termDIVIDEunary":
			ExpressionNode term2 = (ExpressionNode) this.useTermProduction();
			ExpressionNode unary2 = (ExpressionNode) this.useUnaryProduction();
			ArithmeticBinaryExpressionNode div = 
					new ArithmeticBinaryExpressionNodeJb();
			div.setLeftValue(term2);
			div.setRightValue(unary2);
			div.setOperator(BinaryExpressionNode.BinaryOperator.DIVISION);
			term = div;
			break;
		case "term->unary":
			term = this.useUnaryProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return term;
	}

	private StatementNode useUnaryProduction() {
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		StatementNode unary = null;

		// use unary productions functions according to the specific production
		switch (thisReduction.toString()) {
		case "unary->-unary":
			ExpressionNode unary1 = (ExpressionNode) this.useUnaryProduction();
			ArithmeticUnaryExpressionNode minus = 
					new ArithmeticUnaryExpressionNodeJb();
			minus.setRightValue(unary1);
			minus.setOperator(UnaryExpressionNode.UnaryOperator.MINUS);
			unary = minus;
			break;
		case "unary->factor":
			unary = this.useFactorProduction();
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return unary;
	}

	private StatementNode useFactorProduction() {
		
		// get next reductions' list production and delete it from it
		TargetGrammar.Reduction thisReduction = this.reductions.get(0);
		this.reductions.remove(0);
		
		StatementNode factor = null;
		
		// use factor productions functions according to the specific
		// production
		switch (thisReduction.toString()) {
		case "factor->LEFT_PARANassignRIGHT_PARAN":
			factor = this.useAssignProduction();
			break;
		case "factor->loc":
			factor = this.useLocProduction();
			break;
		case "factor->NUM":
			NumToken num=(NumToken)thisReduction.getRightSide().get(0);
			Long l=num.getLongValue();
			
			LiteralNode numNode = new LiteralNodeJb();
			numNode.setLiteral(l.toString());
			numNode.setLiteralType(new TypeJb(Type.Kind.LONG));

			factor = numNode;
			break;
		case "factor->REAL":
			RealToken real=(RealToken)thisReduction.getRightSide().get(0);
			Double val=real.getDoubleValue();
			
			LiteralNode realNode = new LiteralNodeJb();
			realNode.setLiteral(val.toString());
			realNode.setLiteralType(new TypeJb(Type.Kind.DOUBLE));

			factor = realNode;
			break;
		case "factor->true":
			LiteralNode trueNode = new LiteralNodeJb();
			trueNode.setLiteral("true");
			trueNode.setLiteralType(new TypeJb(Type.Kind.BOOLEAN));

			factor = trueNode;
			break;
		case "factor->false":
			LiteralNode falseNode = new LiteralNodeJb();
			falseNode.setLiteral("false");
			falseNode.setLiteralType(new TypeJb(Type.Kind.BOOLEAN));

			factor = falseNode;
			break;
		case "factor->string":
			// Tmp get value of string ... not necessary in MS1
			TargetGrammar.Reduction stringVal = this.reductions.get(0);
			this.reductions.remove(0);

			LiteralNode string = new LiteralNodeJb();
			string.setLiteral(stringVal.toString());
			string.setLiteralType(new TypeJb(Type.Kind.STRING));

			factor = string;
			break;
		default:
			logger.error("thisReduction : {} , matches no case",thisReduction);
		}

		return factor;
	}
}
