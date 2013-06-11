package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.codegen.converters.AssignmentNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class AssignmentNodeConverterTest {
	AssignmentNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new AssignmentNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	
	@Test
	public void testAssignmentNodeConverter_Long(){
		try {
			ASTNode node = Mockito.mock(AssignmentNode.class);
	
			verify(converter.icg).processNode(any(AssignmentNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ASSIGN_LONG,
					"test2", Quadruple.EmptyArgument,
					"test1"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("test1", new LongType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	
	@Test
	public void testAssignmentNodeConverter_Double(){
		try {
			ASTNode node = Mockito.mock(AssignmentNode.class);
	
			verify(converter.icg).processNode(any(AssignmentNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ASSIGN_DOUBLE,
					"test2", Quadruple.EmptyArgument,
					"test1"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("test1", new LongType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	@Test
	public void testAssignmentNodeConverter_Boolean(){
		try {
			ASTNode node = Mockito.mock(AssignmentNode.class);
	
			verify(converter.icg).processNode(any(AssignmentNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ASSIGN_BOOLEAN,
					"test2", Quadruple.EmptyArgument,
					"test1"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("test1", new LongType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	
	@Test
	public void testAssignmentNodeConverter_String(){
		try {
			ASTNode node = Mockito.mock(AssignmentNode.class);
	
			verify(converter.icg).processNode(any(AssignmentNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ASSIGN_STRING,
					"test2", Quadruple.EmptyArgument,
					"test1"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("test1", new LongType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	@Test
	public void testAssignmentNodeConverter_Array(){
		try {
			ASTNode node = Mockito.mock(AssignmentNode.class);
	
			verify(converter.icg).processNode(any(AssignmentNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	@Test
	public void testAssignmentNodeConverter_Struct(){
		try {
			ASTNode node = Mockito.mock(AssignmentNode.class);
	
			verify(converter.icg).processNode(any(AssignmentNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
}
