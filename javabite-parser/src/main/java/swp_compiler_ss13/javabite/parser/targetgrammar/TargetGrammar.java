package swp_compiler_ss13.javabite.parser.targetgrammar;

import static swp_compiler_ss13.javabite.parser.grammar.Utils.list;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.parser.grammar.Grammar;
import swp_compiler_ss13.javabite.parser.grammar.Production;
import swp_compiler_ss13.javabite.parser.grammar.SLRAutomaton;
import swp_compiler_ss13.javabite.parser.grammar.Symbol;
import swp_compiler_ss13.javabite.parser.grammar.Word;

/**
 * This class represents our grammar defined in the specification.
 * @author Till
 *
 */
public class TargetGrammar {
	Logger logger=LoggerFactory.getLogger(TargetGrammar.class);
	
	/**
	 * just necessary to build the grammar rules
	 */
	private Map<TokenType, Terminal> uniqueTerminals= new HashMap<>();
	/**
	 * singleton regarding the TokenType
	 * @param tt the wanted type
	 * @return the singleton token
	 */
	private Terminal t(TokenType tt){
		Terminal t=uniqueTerminals.get(tt);
		if (t== null) {
			t= new Terminal(tt);
			uniqueTerminals.put(tt, t);
		}
		return t;
	}
	
	
	// necessary nonTerminals and Terminals
	NonTerminal artificial_start= new NonTerminal("E'");
	Terminal eps=new Terminal("\\eps");
	
	// Nonterminals of our grammar
	NonTerminal program=new NonTerminal("program");
	NonTerminal block=new NonTerminal("block");
	NonTerminal decls=new NonTerminal("decls");
	NonTerminal decl=new NonTerminal("decl");
	NonTerminal type=new NonTerminal("type");
	NonTerminal stmts=new NonTerminal("stmts");
	NonTerminal stmt=new NonTerminal("stmt");
	NonTerminal assign=new NonTerminal("assign");
	NonTerminal loc=new NonTerminal("loc");
	NonTerminal bool=new NonTerminal("bool");
	NonTerminal join=new NonTerminal("join");
	NonTerminal equality=new NonTerminal("equality");
	NonTerminal rel=new NonTerminal("rel");
	NonTerminal expr=new NonTerminal("expr");
	NonTerminal term=new NonTerminal("term");
	NonTerminal unary=new NonTerminal("unary");
	NonTerminal factor=new NonTerminal("factor");
	
	// Our grammar
	Grammar<Terminal,NonTerminal> grammar;
	// Our automaton to derive words
	SLRAutomaton<Terminal, NonTerminal> automaton;
	
	/**
	 * the regular constructor. Defines the grammar and builds the automaton
	 */
	public TargetGrammar(){
		grammar=new Grammar<Terminal,NonTerminal>(program,artificial_start,t(TokenType.EOF),eps);
		
		grammar.addProduction(program, list(decls,stmts));
		grammar.addProduction(block, list(t(TokenType.LEFT_BRACE),decls,stmts,t(TokenType.RIGHT_BRACE)));
		grammar.addProduction(decls, list(decls,decl),list(eps));
		grammar.addProduction(decl, list(type,t(TokenType.ID),t(TokenType.SEMICOLON)));
		grammar.addProduction(type, list(t(TokenType.DOUBLE_SYMBOL)),list(t(TokenType.LONG_SYMBOL)));
		grammar.addProduction(stmts, list(stmts,stmt),list(eps));
		grammar.addProduction(stmt, list(assign,t(TokenType.SEMICOLON)),list(t(TokenType.RETURN),t(TokenType.ID),t(TokenType.SEMICOLON)));
		grammar.addProduction(loc,list(t(TokenType.ID)));
		grammar.addProduction(assign,list(loc,t(TokenType.ASSIGNOP),expr),list(bool));
		grammar.addProduction(bool,list(bool,t(TokenType.OR),join),list(join));
		grammar.addProduction(join,list(join,t(TokenType.AND),equality),list(equality));
		grammar.addProduction(equality,list(equality,t(TokenType.EQUALS),rel),list(equality,t(TokenType.NOT_EQUALS),rel),list(rel));
		grammar.addProduction(rel,list(expr,t(TokenType.LESS),expr),list(expr,t(TokenType.LESS_OR_EQUAL),expr),list(expr,t(TokenType.GREATER),expr),list(expr,t(TokenType.GREATER_EQUAL),expr),list(expr));
		grammar.addProduction(expr,list(expr,t(TokenType.PLUS),term),list(expr,t(TokenType.MINUS),term),list(term));
		grammar.addProduction(term,list(term,t(TokenType.TIMES),unary),list(term,t(TokenType.DIVIDE),unary),list(unary));
		grammar.addProduction(unary, list(t(TokenType.MINUS),unary), list(factor));
		grammar.addProduction(factor, list(t(TokenType.NUM)), list(t(TokenType.REAL)),list(loc),list(t(TokenType.LEFT_PARAN),assign,t(TokenType.RIGHT_PARAN)));
		
		automaton=new SLRAutomaton<>(grammar);
	}
	
	/**
	 * derivate given sourcecode and retuns a list of productions in the reverse order. 
	 * The derivation is always the rightmost derivation
	 * @param sc the given word
	 * @return the concrete derivation if the word is in the language. Otherwise undetermined result.
	 */
	public List<Reduction> derivate(SourceCode sc){
		List<Production<Terminal, NonTerminal>> productions= automaton.getDerivationASsSequence(sc);
		List<Reduction> reductions=new LinkedList<>();
		for (Production<Terminal,NonTerminal> p : productions) reductions.add(new Reduction(p));	
		return reductions;
	}
	
	/**
	 * Represents a concrete production related to our grammar
	 * @author Till
	 *
	 */
	public class Reduction{
		List<Object> right_side=new LinkedList<>();
		NonTerminal left_side;
		Reduction(Production<Terminal,NonTerminal> production) {
			for (Symbol symbol : production.right){
				if (symbol instanceof NonTerminal)
					right_side.add(symbol);
				else
					right_side.add(((Terminal)symbol).getToken());
			}
			left_side=production.left;
		}
		public String toString(){
		return left_side+"->"+right_side;
		}
		
		/**
		 * the right-handed value
		 * @return the list of terminals and nonterminals at the left ( Interfaces: @NonTerminal and @Token)
		 */
		public List<Object> getRightSide(){
			return right_side;
		}
		/**
		 * returns the left-handed value
		 * @return the nonterminal at the left
		 */
		public NonTerminal getLeftSide(){
			return left_side;
		}
	}
	
	/**
	 * Represents a word in the language
	 * @author Till
	 *
	 */
	public class SourceCode extends Word<Terminal, NonTerminal>{

		/**
		 * 
		 */
		private static final long serialVersionUID = -6560981223942917205L;

		/**
		 * creates the word and converts it to the necessary class
		 * @param tarr the word
		 */
		public SourceCode(List<Token> tarr) {
			super(grammar, new LinkedList<Terminal>());
			for (Token tok : tarr) add(new Terminal(tok));
		}
		
	}
	
	
}
