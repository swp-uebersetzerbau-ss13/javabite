package swp_compiler_ss13.javabite.parser;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.parser.Parser;
import swp_compiler_ss13.common.parser.ReportLog;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.parser.astGenerator.ASTGenerator;
import swp_compiler_ss13.javabite.parser.grammar.exceptions.AmbiguityInDerivationGrammarException;
import swp_compiler_ss13.javabite.parser.grammar.exceptions.WordNotInLanguageGrammarException;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.Reduction;
import swp_compiler_ss13.javabite.parser.targetgrammar.semantic_check.ASTAnalyzer;

/**
 * Responsible to convert the token stream to an AST.
 * Additionally some semantic errors are catched.
 * @author Till
 *
 */
public class ParserJb implements Parser {
	Logger log = LoggerFactory.getLogger(ParserJb.class);
	
	/**
	 * use to report necessary actions
	 */
	protected ReportLog reportLog;
	/**
	 * the lexer as the source for the token stream
	 */
	protected Lexer lexer;
	/**
	 * our grammar, that derives the token stream.
	 */
	protected TargetGrammar grammar=new TargetGrammar();
	
	
	/**
	 * The basic functionality. If the source code delivered by the lexer 
	 * is syntactically and semantically correct, this method delivers a 
	 * correct AST
	 */
	@Override
	public ASTJb getParsedAST() {
		// read the whole program in the tokenSequence
		List<Token> tokenSeq=getTokenList();
		// convert it to the necessary format
		TargetGrammar.SourceCode sourceCode= grammar.new SourceCode(tokenSeq);
		// get the derivation of the tokenStream ( as left-to-right top down)
		List<Reduction> derivationSeq=null;
		try{
			derivationSeq=grammar.derivateDFLeftToRight(sourceCode);
		} catch(WordNotInLanguageGrammarException | AmbiguityInDerivationGrammarException e){
			log.warn("Grammer throws exeception {}", e.getClass());
			Token prob=e.getRelatedToken();
			reportLog.reportError(prob.getValue(), prob.getLine(), prob.getColumn(), "Can not proceed AST build with Token '" + prob.getValue() + "' at this position.");
		}
		// use the ASTGenerator to derive the AST from the derivation
		ASTGenerator astGen=new ASTGenerator(derivationSeq);
		// generate the necessary AST
		ASTJb astJb=astGen.generateAST();
		
		ASTAnalyzer analyzer = new ASTAnalyzer(reportLog);
		analyzer.analyse(astJb);
		return astJb;
	}

	/**
	 * set the lexer as the source of the token stream
	 */
	@Override
	public void setLexer(Lexer lexer) {
		this.lexer=lexer;
	}

	/**
	 * set the reportLog as a notifier in case 
	 *  something noticeable happens
	 */
	@Override
	public void setReportLog(ReportLog reportLog) {
		this.reportLog=reportLog;
	}
	
	/**
	 * returns the token List as a sequence in the
	 * given order
	 * @return
	 */
	private List<Token> getTokenList(){
		List<Token> res=new LinkedList<Token>();
		Token token;
		// add every token but the EOF-token
		while ((token=lexer.getNextToken()).getTokenType()!=TokenType.EOF) res.add(token);
		// add the EOF-token
		res.add(token);
		return res;
	}
}