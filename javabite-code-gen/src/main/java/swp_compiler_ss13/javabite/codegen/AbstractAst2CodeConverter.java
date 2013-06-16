package swp_compiler_ss13.javabite.codegen;


public abstract class AbstractAst2CodeConverter implements Ast2CodeConverter {
	protected Ast2CodeConverterCompatibleGenerator icg;
	
	@Override
	public void setIcgJb(Ast2CodeConverterCompatibleGenerator icg) {
		this.icg = icg;
	}
}
