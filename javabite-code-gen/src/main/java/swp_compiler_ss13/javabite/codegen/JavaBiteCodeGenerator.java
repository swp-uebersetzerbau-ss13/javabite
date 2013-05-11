package swp_compiler_ss13.javabite.codegen;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;

/**
 * 
 * @author Alpin Sahin und Florian Mercks
 *
 */
public class JavaBiteCodeGenerator implements IntermediateCodeGenerator {

	/**
	* The generated quadruples
	*/
	public static List<Quadruple> quadruples;
	
	/**
	* List of used names. This is needed for single static assignment.
	*/
	static List<String> usedNames;
	
	/**
	* The stack of identifier renames
	*/
	static Stack<Map<String, String>> currentScopeRenames;
	
	/**
	* The stack of symbol tables
	*/
	static Stack<SymbolTable> currentSymbolTable;
	
	/**
	* Store for intermediate results
	*/
	static Stack<String> intermediateResults;
	
	static Stack<Type> intermediateTypes;
	
	/**
	* Constructor for the intermediate code generator
	*/
	public JavaBiteCodeGenerator() {
		quadruples = new LinkedList<>();
		usedNames = new LinkedList<>();
		currentScopeRenames = new Stack<>();
		currentSymbolTable = new Stack<>();
		intermediateResults = new Stack<>();
		intermediateTypes = new Stack<>();
	}


	
	@Override
	public List<Quadruple> generateIntermediateCode(AST ast)
			throws IntermediateCodeGeneratorException {
		BlockNodeJb program = (BlockNodeJb) ast.getRootNode();
		BlockNodeCG.convert(program);
		return quadruples;
	}

	/**
	* call the method that handles the node in the AST
	*
	* @param node
	* The node to handle
	 * @return 
	* @throws IntermediateCodeGeneratorException
	* An error occurred
	*/
	public static void differentiateNode(ASTNodeJb node) throws IntermediateCodeGeneratorException {
		switch (node.getNodeType()) {
		case ArithmeticBinaryExpressionNode:
			//TODO
			break;
		case ArithmeticUnaryExpressionNode:
			new ArithmeticBinaryExpressionNodeCG().convert((ArithmeticBinaryExpressionNode) node);
			break;
		case ArrayIdentifierNode:
			//TODO
			break;
		case AssignmentNode:
			new AssignmentNodeCG().convert((AssignmentNode) node);
			break;
		case BasicIdentifierNode:
			new BasicIdentifierNodeCG().convert((BasicIdentifierNode)node);
			break;
		case BlockNode:
			BlockNodeCG.convert((BlockNodeJb)node);
			break;
		case BranchNode:
			//TODO
			break;
		case BreakNode:
			//TODO
			break;
		case DeclarationNode:
			new DeclarationNodeCG().convert((DeclarationNode) node);
			break;
		case DoWhileNode:
			//TODO
			break;
		case LiteralNode:
			new LiteralNodeCG().convert((LiteralNode) node);
			break;
		case LogicBinaryExpressionNode:
			//TODO
			break;
		case LogicUnaryExpressionNode:
			//TODO
			break;
		case PrintNode:
			//TODO
			break;
		case RelationExpressionNode:
			//TODO
			break;
		case ReturnNode:
			//TODO
			break;
		case StructIdentifierNode:
			//TODO			
			break;
		case WhileNode:
			//TODO
			break;
		default:
			break;
		}
	}



	public static String saveIdentifier(String identifier,
			Type type) throws IntermediateCodeGeneratorException {
		if (!usedNames.contains(identifier)) {
			usedNames.add(identifier);
			currentScopeRenames.peek().put(identifier, identifier);
			quadruples.add(QuadrupleFactory.declaration(identifier, type));
			return identifier;
		} else {
			// rename is required to keep single static assignment
			String newName = currentSymbolTable.peek().getNextFreeTemporary();
			currentSymbolTable.peek().putTemporary(newName, type);
			usedNames.add(newName);
			currentScopeRenames.peek().put(identifier, newName);
			quadruples.add(QuadrupleFactory.declaration(newName, type));
			return newName;
		}
	}

		
	/**
	* Load the given identifier and return its actual name (if renaming was
	* done)
	*
	* @param id
	* The identifier name to load
	* @return The actual name of the identifier
	* @throws IntermediateCodeGeneratorException
	* Identifier was not found
	*/
	public static String loadIdentifier(String id) throws IntermediateCodeGeneratorException {
		@SuppressWarnings("unchecked")
		Stack<Map<String, String>> renameScopes = (Stack<Map<String, String>>) JavaBiteCodeGenerator.currentScopeRenames.clone();
		try {
			while (true) {
			Map<String, String> renamedIds = renameScopes.pop();
				if (renamedIds.containsKey(id)) {
					return renamedIds.get(id);
				}
			}
		} catch (EmptyStackException e) {
		throw new IntermediateCodeGeneratorException("Undeclared variable found: " + id);
		}
	}

	
	/**
	* Create a new temporary value and save it to the internal store of
	* variables
	*
	* @param type
	* The type of the new variable
	* @return The name of the new variable
	* @throws IntermediateCodeGeneratorException
	* An error occurred
	*/
	public static String createAndSaveTemporaryIdentifier(Type type) throws IntermediateCodeGeneratorException {
		String id = currentSymbolTable.peek().getNextFreeTemporary();
		currentSymbolTable.peek().putTemporary(id, type);
		usedNames.add(id);
		currentScopeRenames.peek().put(id, id);
		JavaBiteCodeGenerator.quadruples.add(QuadrupleFactory.declaration(id, type));
		return id;
	}

}
