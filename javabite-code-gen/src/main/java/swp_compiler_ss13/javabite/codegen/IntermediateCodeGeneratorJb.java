package swp_compiler_ss13.javabite.codegen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.codegen.converters.ArithmeticBinaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.ArithmeticUnaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.ArrayIdentifierNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.AssignmentNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.BasicIdentifierNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.BlockNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.BranchNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.DeclarationNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.LiteralNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.LogicBinaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.LogicUnaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.PrintNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.RelationExpressionNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.ReturnNodeConverter;
import swp_compiler_ss13.javabite.codegen.converters.StructIdentifierNodeConverter;

public class IntermediateCodeGeneratorJb implements
		Ast2CodeConverterCompatibleGenerator {
	private final static Logger log = LoggerFactory
			.getLogger(IntermediateCodeGeneratorJb.class);

	private static final Class<?>[] converterClasses = {
			ArithmeticBinaryExpressionNodeConverter.class,
			ArithmeticUnaryExpressionNodeConverter.class,
			ArrayIdentifierNodeConverter.class,
			AssignmentNodeConverter.class, BasicIdentifierNodeConverter.class,
			BlockNodeConverter.class, BranchNodeConverter.class,
			DeclarationNodeConverter.class, LiteralNodeConverter.class,
			LogicBinaryExpressionNodeConverter.class,
			LogicUnaryExpressionNodeConverter.class, PrintNodeConverter.class,
			RelationExpressionNodeConverter.class, ReturnNodeConverter.class,
			StructIdentifierNodeConverter.class};

	private static final String IDENTIFIER_GENERATION_PREFIX = "TMP";
	private long identifierGenerationCounter = 0;

	private static final String LABEL_PREFIX = "LABEL";
	private long labelCounter = 0;
	
	private static final String REFERENCE_NAME = "ref";
	private long referenceGeneratorCounter = 0;
	
	List<Quadruple> quadruples = new ArrayList<>(1000);

	/**
	 * Set of used identifiers (in TAC is can't be use twice)
	 */
	final Set<String> usedIds = new HashSet<>();

	/**
	 * Contains mapping of identifiers which were used twice for the current
	 * scope
	 */
	final Deque<Map<String, IdentifierData>> blockScopes = new ArrayDeque<>();

	/**
	 * Contains identifiers for inter-converter-communication
	 */
	final Deque<IdentifierData> identifierDataStack = new ArrayDeque<>();

	/**
	 * Contains break labels for loop scope
	 */
	final Deque<String> breakLabelStack = new ArrayDeque<>();

	/**
	 * s All available converters for the AST
	 */
	private final Map<ASTNode.ASTNodeType, Ast2CodeConverter> converters = new HashMap<>();

	public IntermediateCodeGeneratorJb() {
		initConverters();
	}

	@Override
	public List<Quadruple> generateIntermediateCode(final AST ast)
			throws IntermediateCodeGeneratorException {
		reset();
		processNode(ast.getRootNode());
		return new ArrayList<>(quadruples);
	}

	@Override
	public void addQuadruple(Quadruple quadruple) {
		quadruples.add(quadruple);
	}

	@Override
	public void processNode(ASTNode node)
			throws IntermediateCodeGeneratorException {
		Ast2CodeConverter converter = converters.get(node.getNodeType());

		if (converter == null)
			throw new IntermediateCodeGeneratorException(
					"No converter available for type: " + node.getNodeType());

		converter.convert(node);
	}

	@Override
	public void enterNewScope() {
		blockScopes.push(new HashMap<String, IdentifierData>());
	}

	@Override
	public void leaveCurrentScope() {
		blockScopes.pop();
	}

	@Override
	public IdentifierData generateTempIdentifier(Type type)
			throws IntermediateCodeGeneratorException {
		IdentifierData data = generateIdentifierMapping(
				generateTacIdentifier(null), type);
		addQuadruple(QuadrupleFactoryJb.generateDeclaration(data).get(0));
		return data;
	}

	@Override
	public IdentifierData generateIdentifierMapping(String astIdentifier,
			Type type) throws IntermediateCodeGeneratorException {
		IdentifierData data = new IdentifierData(
				generateTacIdentifier(astIdentifier), type);
		blockScopes.peek().put(astIdentifier, data);
		return data;
	}

	@Override
	public IdentifierData lookupIdentifierData(String astIdentifier)
			throws IntermediateCodeGeneratorException {
		for (Iterator<Map<String, IdentifierData>> it = blockScopes.iterator(); it
				.hasNext();) {
			Map<String, IdentifierData> current = it.next();
			IdentifierData result = current.get(astIdentifier);
			if (result != null) {
				return result;
			}
		}

		throw new IntermediateCodeGeneratorException();
	}

	@Override
	public void pushIdentifierData(IdentifierData data) {
		identifierDataStack.push(data);
	}

	@Override
	public IdentifierData popIdentifierData() {
		return identifierDataStack.pop();
	}

	private String generateTacIdentifier(String astIdentifier)
			throws IntermediateCodeGeneratorException {
		String tacIdentifier = astIdentifier;
		while (tacIdentifier == null || usedIds.contains(tacIdentifier)) {
			// TODO: this solution add a limitation for the code generator
			tacIdentifier = IDENTIFIER_GENERATION_PREFIX
					+ identifierGenerationCounter++;

			if (identifierGenerationCounter == 0) {
				throw new IntermediateCodeGeneratorException(
						"Can not generate enough identifier");
			}
		}
		usedIds.add(tacIdentifier);
		return tacIdentifier;
	}

	/**
	 * initialize all known nodeConverters
	 */
	private void initConverters() {
		try {
			for (Class<?> converterClass : converterClasses) {
				Ast2CodeConverter converter = (Ast2CodeConverter) converterClass
						.newInstance();
				converter.setIcgJb(this);
				converters.put(converter.getNodeType(), converter);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Unexpected error in instatiation of IntermediateCodeGeneratorJb");
			e.printStackTrace();
		}
	}

	/**
	 * reset the generator for a new run
	 */
	private void reset() {
		quadruples.clear();
		usedIds.clear();
		blockScopes.clear();
		identifierDataStack.clear();
		identifierGenerationCounter = 0;
		breakLabelStack.clear();
		labelCounter = 0;
		referenceGeneratorCounter = 0;
	}

	@Override
	public String getNewLabel() {
		return LABEL_PREFIX + labelCounter++;
	}

	@Override
	public void enterLoop(String breakLabel) {
		breakLabelStack.push(breakLabel);
	}

	@Override
	public void leaveLoop() {
		breakLabelStack.pop();
	}

	@Override
	public String getCurrentBreakLabel() {
		return breakLabelStack.peek();
	}

	@Override
	public String getNewReference() {
		String ref = REFERENCE_NAME + referenceGeneratorCounter++;
		addQuadruple(QuadrupleFactoryJb.generateReferenceDeclaring(ref));
		return ref;
	}
}