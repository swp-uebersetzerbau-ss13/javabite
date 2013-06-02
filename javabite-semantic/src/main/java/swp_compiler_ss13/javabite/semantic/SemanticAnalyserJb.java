package swp_compiler_ss13.javabite.semantic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.semanticAnalysis.SemanticAnalyser;

public class SemanticAnalyserJb implements SemanticAnalyser {
	Logger log = LoggerFactory.getLogger(SemanticAnalyserJb.class);
	
	private ReportLog reportLog;

	@Override
	public void setReportLog(ReportLog log) {
		reportLog = log;
	}

	@Override
	public AST analyse(AST ast) {
		throw new RuntimeException("SemanticAnalyserJb analyse not implemented");
	}
}