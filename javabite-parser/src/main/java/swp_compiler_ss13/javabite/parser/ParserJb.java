package swp_compiler_ss13.javabite.parser;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.parser.Parser;
import swp_compiler_ss13.common.parser.ReportLog;

public class ParserJb implements Parser {
	protected ReportLog reportLog;
	protected Lexer lexer;
	
	@Override
	public AST getParsedAST() {
		throw new NotImplementedException();
	}

	@Override
	public void setLexer(Lexer lexer) {
		this.lexer=lexer;
	}

	@Override
	public void setReportLog(ReportLog reportLog) {
		this.reportLog=reportLog;
	}
}