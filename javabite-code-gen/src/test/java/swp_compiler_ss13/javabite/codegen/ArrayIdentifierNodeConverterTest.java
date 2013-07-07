package swp_compiler_ss13.javabite.codegen;
import org.junit.Before;
import org.mockito.Mockito;

import swp_compiler_ss13.javabite.codegen.converters.ArrayIdentifierNodeConverter;
public class ArrayIdentifierNodeConverterTest {
        
        ArrayIdentifierNodeConverter converter;
           
    @Before
    public void setUp() throws Exception {
        converter = new ArrayIdentifierNodeConverter();
        converter.icg = Mockito
                .mock(IntermediateCodeGeneratorJb.class);
    }
}