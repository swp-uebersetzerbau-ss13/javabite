package swp_compiler_ss13.javabite.backend.temp2;

import swp_compiler_ss13.javabite.backend.temp2.OperationTemplate.OperationTemplateBuilder;

import static swp_compiler_ss13.javabite.backend.Mnemonic.*;

public final class PresetTemplates {

	public static final OperationTemplate UNARY_MINUS_NUM = OperationTemplateBuilder
	    .newBuilder().add(ILOAD).add(INEG).add(ISTORE).build();

	private PresetTemplates() {
		throw new AssertionError();
	}

}
