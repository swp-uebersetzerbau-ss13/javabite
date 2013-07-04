package swp_compiler_ss13.javabite.codegen;
import org.junit.Before;
import org.mockito.Mockito;
import swp_compiler_ss13.javabite.codegen.converters.StructIdentifierNodeConverter;
public class StructIdentifierNodeConverterTest {
        
        StructIdentifierNodeConverter converter;
           
    @Before
    public void setUp() throws Exception {
        converter = new StructIdentifierNodeConverter();
        converter.icg = Mockito
                .mock(IntermediateCodeGeneratorJb.class);
    }
}