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
	static List<String> usedVariableNames;
	
	/**
	* The stack of the latest Identifier names.
	*/
	static Stack<Map<String, String>> latestIdentifierNames;
	
	/**
	* The stack of symbol tables
	*/
	static Stack<SymbolTable> latestSymbolTable;
	
	/**
	* Store for intermediate results
	*/
	static Stack<String> temporaryResultOutputs;
	
	static Stack<Type> temporaryTypes;
	
	/**
	* Constructor for the intermediate code generator
	*/
	public JavaBiteCodeGenerator() {
		quadruples = new LinkedList<>();
		usedVariableNames = new LinkedList<>();
		latestIdentifierNames = new Stack<>();
		latestSymbolTable = new Stack<>();
		temporaryResultOutputs = new Stack<>();
		temporaryTypes = new Stack<>();
	}


	/**
	 * generates the intermediate code for the given AST 
	 */
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


	/**
	 * declare the identifier
	 * @param identifier
	 * @param type
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	public static String addIdentifier(String identifier,
			Type type) throws IntermediateCodeGeneratorException {
		
		/* looks up if the identifier name is already used
		 * create a temporary variable that keeps a new name for the variable,
		 * if the identifier has been used
		 */
		if (usedVariableNames.contains(identifier)) {
			// rename is required to keep single static assignment
			String newVariableName = latestSymbolTable.peek().getNextFreeTemporary();
			latestSymbolTable.peek().putTemporary(newVariableName, type);
			usedVariableNames.add(newVariableName);
			latestIdentifierNames.peek().put(identifier, newVariableName);
			quadruples.add(QuadrupleFactory.declaration(newVariableName, type));
			return newVariableName;
		}
		 //if the identifier name not in the usedVariableNames,
		 //add the new identifier name into the usedVariablenNames
		else {
			usedVariableNames.add(identifier);
			latestIdentifierNames.peek().put(identifier, identifier);
			quadruples.add(QuadrupleFactory.declaration(identifier, type));
			return identifier;
		}
	}

		
	/**
	* Load the given identifier and return its actual name (if renaming was
	* done)
	*
	* @param identifier
	* The identifier name to load
	* @return The actual name of the identifier
	* @throws IntermediateCodeGeneratorException
	* Identifier was not found
	*/
	public static String getIdentifier(String identifier) throws IntermediateCodeGeneratorException {
		//Copy the all used names from latestIdentifierNames to a temporary variable
		@SuppressWarnings("unchecked")
		Stack<Map<String, String>> namesInBlocks = (Stack<Map<String, String>>) JavaBiteCodeGenerator.latestIdentifierNames.clone();
		try {
			while (true) {
			Map<String, String> currentIdentifiers = namesInBlocks.pop();
				if (currentIdentifiers.containsKey(identifier)) {
					return currentIdentifiers.get(identifier);
				}
			}
		} 
		catch (EmptyStackException e) {
			throw new IntermediateCodeGeneratorException(identifier + " is not declared!");
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
	public static String createAndAddTemporaryIdentifier(Type type) throws IntermediateCodeGeneratorException {
		String identifier = latestSymbolTable.peek().getNextFreeTemporary();
		latestSymbolTable.peek().putTemporary(identifier, type);
		usedVariableNames.add(identifier);
		latestIdentifierNames.peek().put(identifier, identifier);
		JavaBiteCodeGenerator.quadruples.add(QuadrupleFactory.declaration(identifier, type));
		return identifier;
	}

}
