package swp_compiler_ss13.javabite.codegen;
import org.junit.Before;
import org.mockito.Mockito;
import swp_compiler_ss13.javabite.codegen.converters.WhileNodeConverter;
public class WhileNodeConverterTest {
        
        WhileNodeConverter converter;
           
    @Before
    public void setUp() throws Exception {
        converter = new WhileNodeConverter();
        converter.icg = Mockito
                .mock(IntermediateCodeGeneratorJb.class);
    }
}