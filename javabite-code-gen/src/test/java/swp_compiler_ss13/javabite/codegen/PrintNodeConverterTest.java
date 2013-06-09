package swp_compiler_ss13.javabite.codegen;

import static org.mockito.Matchers.any;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.ast.nodes.unary.PrintNodeJb;
import swp_compiler_ss13.javabite.codegen.converters.PrintNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class PrintNodeConverterTest {
	PrintNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new PrintNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	
	@Test
	public void testPrintLongConverter(){
		try{
			ASTNode node = Mockito.mock(PrintNode.class);
			verify(converter.icg).processNode(any(PrintNode.class));
			IdentifierData rightData = new IdentifierData("test", new LongType());
			when(converter.icg.popIdentifierData()).thenReturn(rightData);
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.PRINT_LONG, rightData.getIdentifier(),
					Quadruple.EmptyArgument, Quadruple.EmptyArgument));
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}


	@Test
	public void testPrintDoubleConverter() {
		try {
			ASTNode node = Mockito.mock(PrintNode.class);
			verify(converter.icg).processNode(any(PrintNode.class));
			IdentifierData rightData = new IdentifierData("test", new DoubleType());
			when(converter.icg.popIdentifierData()).thenReturn(rightData);
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.PRINT_DOUBLE, rightData.getIdentifier(),
					Quadruple.EmptyArgument, Quadruple.EmptyArgument));
					
			
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
		
	}
	
	@Test
	public void testPrintBooleanConverter() {
		try{
			ASTNode node = Mockito.mock(PrintNode.class);
			verify(converter.icg).processNode(any(PrintNode.class));
			IdentifierData rightData = new IdentifierData("test", new BooleanType());
			when(converter.icg.popIdentifierData()).thenReturn(rightData);
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.PRINT_BOOLEAN, rightData.getIdentifier(),
					Quadruple.EmptyArgument, Quadruple.EmptyArgument));
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
	
	
	@Test
	public void testPrintStringConverter() {
		
		try {
			ASTNode node = Mockito.mock(PrintNodeJb.class);
			verify(converter.icg).processNode(any(PrintNode.class));
			IdentifierData rightData = new IdentifierData("test", new StringType(0l));
			when(converter.icg.popIdentifierData()).thenReturn(rightData);
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.PRINT_STRING, rightData.getIdentifier(),
					Quadruple.EmptyArgument, Quadruple.EmptyArgument));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
		
	}
	
	
	@Test
	public void testPrintStructConverter() {
		try{
			ASTNode node = Mockito.mock(PrintNodeJb.class);
			verify(converter.icg).processNode(any(PrintNode.class));
			IdentifierData rightData = new IdentifierData("test", new StructType(null,
					new Member[] { new Member("",
							new LongType()) }));
			when(converter.icg.popIdentifierData()).thenReturn(rightData);
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e){
			
		}
	}
	
	@Test
	public void testPrintArrayConverter() {
		try{
			ASTNode node = Mockito.mock(PrintNodeJb.class);
			verify(converter.icg).processNode(any(PrintNode.class));
			IdentifierData rightData = new IdentifierData("test", new ArrayType(new LongType(), 0));
			when(converter.icg.popIdentifierData()).thenReturn(rightData);
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e){
			
		}
	}
	
}
