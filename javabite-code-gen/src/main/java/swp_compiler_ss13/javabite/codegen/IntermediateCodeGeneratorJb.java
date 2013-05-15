package swp_compiler_ss13.javabite.codegen;

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
public class IntermediateCodeGeneratorJb implements IntermediateCodeGenerator {

	/**
	 * The list of generated quadruples
	 */
	public static List<Quadruple> quadruples;
	
	/**
	 * List of used variable (identifier) names
	 */
	static List<String> usedVariableNames;
	
	/**
	 * The stack of the latest Identifier names.
	 */
	static Stack<Map<String, String>> latestIdentifierNames;
	
	/**
	 * The stack of symbol tables
	 */
	static Stack<SymbolTable> symbolTable;
	
	/**
	 * temporary result outputs, used while loading a result of a variable
	 */
	static Stack<String> temporaryResultOutputs;
	
	/**
	 * temporary types, used while loading a result of a variable
	 */
	static Stack<Type> temporaryTypes;
	
	/**
	* Constructor for the intermediate code generator
	*/
	public IntermediateCodeGeneratorJb() {
		quadruples = new LinkedList<>();
		usedVariableNames = new LinkedList<>();
		latestIdentifierNames = new Stack<>();
		symbolTable = new Stack<>();
		temporaryResultOutputs = new Stack<>();
		temporaryTypes = new Stack<>();
	}


	/**
	 * generates the intermediate code for the given AST 
	 * @param ast
	 */
	@Override
	public List<Quadruple> generateIntermediateCode(AST ast)
			throws IntermediateCodeGeneratorException {
		BlockNodeJb program = (BlockNodeJb) ast.getRootNode();
		new BlockNodeCG().convert(program);
		return quadruples;
	}

	/**
	 * handles the the node
	 * @param node
	 * @throws IntermediateCodeGeneratorException
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
			new BlockNodeCG().convert((BlockNodeJb)node);
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
			String newVariableName = symbolTable.peek().getNextFreeTemporary();
			symbolTable.peek().putTemporary(newVariableName, type);
			usedVariableNames.add(newVariableName);
			latestIdentifierNames.peek().put(identifier, newVariableName);
			quadruples.add(QuadrupleFactoryJb.declaration(newVariableName, type));
			return newVariableName;
		}
		 //if the identifier name not in the usedVariableNames,
		 //add the new identifier name into the usedVariablenNames
		else {
			usedVariableNames.add(identifier);
			latestIdentifierNames.peek().put(identifier, identifier);
			quadruples.add(QuadrupleFactoryJb.declaration(identifier, type));
			return identifier;
		}
	}

		
	/**
	* Get the given identifier and return its current name 
	* (if renaming was done)
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
		Stack<Map<String, String>> namesInBlocks = (Stack<Map<String, String>>) IntermediateCodeGeneratorJb.latestIdentifierNames.clone();
		String id="";
		while (!namesInBlocks.isEmpty()) {
		Map<String, String> currentIdentifiers = namesInBlocks.pop();
			if (currentIdentifiers.containsKey(identifier)) {
				id= currentIdentifiers.get(identifier);
				break;
			}
		}
		if(id==""){
			throw new IntermediateCodeGeneratorException("Identifier "+id+" is not declared!");
		}
		return id;
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
		// find a name for the temporary identifier
		String identifier = symbolTable.peek().getNextFreeTemporary();
		// add the temporary identifier in the list of used variables
		usedVariableNames.add(identifier);
		// add the temporary identifier in the list of latest identifiers
		latestIdentifierNames.peek().put(identifier, identifier);
		// set the temporary identifier with its type in symbol table
		symbolTable.peek().putTemporary(identifier, type);		
		// generate the quadruple for its declaration and add it to the list
		Quadruple declarationQuadruple = QuadrupleFactoryJb.declaration(identifier, type);
		IntermediateCodeGeneratorJb.quadruples.add(declarationQuadruple);
		return identifier;
	}

}
