package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.codegen.converters.RelationNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class RelationNodeConverterTest {
	
RelationNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new RelationNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}

	
	//LESS
	@Test
	public void testRelationLong_EQUAL(){
		try {
			ASTNode node = Mockito.mock(RelationExpressionNode.class);
	
			verify(converter.icg).processNode(any(RelationExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
			
			verify(converter.icg).processNode(any(RelationExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
			
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(new IdentifierData("tmp", new LongType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_E,
					"tmp1", "tmp2", "tmp3"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	
		

}
