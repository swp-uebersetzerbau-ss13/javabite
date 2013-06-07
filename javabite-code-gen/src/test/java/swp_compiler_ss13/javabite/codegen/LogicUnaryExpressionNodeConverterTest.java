package swp_compiler_ss13.javabite.codegen;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.unary.LogicUnaryExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.codegen.converters.LogicUnaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;


public class LogicUnaryExpressionNodeConverterTest {
	
	
	LogicUnaryExpressionNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new LogicUnaryExpressionNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}

	@Test
	public void testLogicUnaryExpressionLong(){
		try {
			ASTNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(LogicUnaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test", new LongType()));
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(new IdentifierData("tmp", new LongType()));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	@Test
	public void testLogicUnaryExpressionDouble(){
		try {
			ASTNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(LogicUnaryExpressionNode.class));
			IdentifierData oldData = new IdentifierData("test", new DoubleType());
			when(converter.icg.popIdentifierData()).thenReturn(oldData);
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
			.thenReturn(new IdentifierData("tmp", new DoubleType()));
			verify(converter).convert(node);
			fail();
			
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	@Test
	public void testLogicUnaryExpressionBoolean(){
		try {
			ASTNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(LogicUnaryExpressionNode.class));
			IdentifierData oldData = new IdentifierData("test", new BooleanType());
			when(converter.icg.popIdentifierData()).thenReturn(oldData);
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("tmp", new BooleanType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.NOT_BOOLEAN, "test1",
					Quadruple.EmptyArgument, "test2"));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	@Test
	public void testLogicUnaryExpressionStruct(){
		try {
			ASTNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(LogicUnaryExpressionNode.class));
			IdentifierData oldData = new IdentifierData("test", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) }));
			when(converter.icg.popIdentifierData()).thenReturn(oldData);
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
			.thenReturn(new IdentifierData("tmp", new DoubleType()));
			verify(converter).convert(node);
			fail();
			
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	@Test
	public void testLogicUnaryExpressionArray(){
		try {
			ASTNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(LogicUnaryExpressionNode.class));
			IdentifierData oldData = new IdentifierData("test", new ArrayType(new LongType(), 0));
			when(converter.icg.popIdentifierData()).thenReturn(oldData);
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
			.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
			verify(converter).convert(node);
			fail();
			
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}		
		
}
