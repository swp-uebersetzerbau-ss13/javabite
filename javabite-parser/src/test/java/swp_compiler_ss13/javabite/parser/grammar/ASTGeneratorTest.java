package swp_compiler_ss13.javabite.parser.grammar;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.lexer.LexerJb;
import swp_compiler_ss13.javabite.parser.astGenerator.ASTGenerator;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.Reduction;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.SourceCode;
import swp_compiler_ss13.javabite.token.TokenJb;

public class ASTGeneratorTest {
	ASTGenerator instance;
	Logger logger=LoggerFactory.getLogger(this.getClass());
	TargetGrammar syn=new TargetGrammar();
	
	
	@Test
	public void testSimpleDeclaration(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		logger.info("res : {}",resAsReadableString(res));
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
		logger.info("root node ->"+ast.getRootNode());
	}
	
	
	@Test
	public void testSimpleDoubleDeclaration(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		logger.info("res : {}",resAsReadableString(res));
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
		logger.info("root node ->"+ast.getRootNode());
	}
	
	
	@Test
	public void testSimpleStatement(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.REAL,"2.0"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		logger.info("res : {}",resAsReadableString(res));
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
		logger.info("root node ->"+ast.getRootNode());
	}
	
	
	@Test
	public void testSimpleProgram(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.REAL,"2.0"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		logger.info("res : {}",resAsReadableString(res));
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
		logger.info("root node ->"+ast.getRootNode());
	}
	
	@Test
	public void testLessSimpleProgram(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.REAL,"2.0"));
		tList.add(new TokenJb(TokenType.DIVIDE,"/"));
		tList.add(new TokenJb(TokenType.REAL,"2"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		logger.info("res : {}",resAsReadableString(res));
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
		logger.info("root node ->"+ast.getRootNode());
	}
	
	
	@Test
	public void testLexerIntegrationVeryComplex(){
		String toCheck="" +
				"long i;" +
				"long j;" +
				"i=(j=2);" +
				"j=2;" +
				"return j;";
		LexerJb lex=new LexerJb();
		lex.setSourceStream(new ByteArrayInputStream(toCheck.getBytes()));
		List<Token> tList=new LinkedList<>();
		Token t;
		do{
			t=lex.getNextToken();
			tList.add(t);
		} while (t.getTokenType()!=TokenType.EOF);
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
		logger.info("root node ->"+ast.getRootNode());
	}
	
	
	
	private String toReadableString(TargetGrammar.Reduction reduction){
		StringBuilder strb=new StringBuilder();
		strb.append(reduction.getLeftSide());
		strb.append("-> {");
		boolean first=true;
		for (Object o : reduction.getRightSide()){
			if (!first) strb.append(", ");
			else first=false;
			if (o instanceof Token){
				Token tok=(Token)o;
				strb.append(tok.getTokenType());
				strb.append("[");
				strb.append(tok.getValue());
				strb.append("]");
			}
			else strb.append(o);
		}
		strb.append(" }");
		return strb.toString();
	}

	
	private String resAsReadableString(List<Reduction> res){
		StringBuilder strb=new StringBuilder();
		boolean first=true;
		for (Reduction prod : res) {
			if (!first) strb.append(", ");
			else first=false;
			strb.append(toReadableString(prod));
		}
		return strb.toString();
	}
}
