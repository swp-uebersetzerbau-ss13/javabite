package swp_compiler_ss13.javabite.parser.targetgrammar;

import static swp_compiler_ss13.javabite.parser.grammar.Utils.list;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
		
		// program -> decls stmts
		grammar.addProduction(program, 
				list(decls,stmts)
				);
		
		// block -> {decls stmts}
		grammar.addProduction(block, 
				list(t(TokenType.LEFT_BRACE),decls,stmts,t(TokenType.RIGHT_BRACE))
				);
		
		// decls -> decls decl | \epsilon 
		grammar.addProduction(decls, 
				list(decls,decl),
				list(eps));
		
		// decl -> type *id*;
		grammar.addProduction(decl, 
				list(type,t(TokenType.ID),t(TokenType.SEMICOLON)));
		
		// type -> type [*num*] | *basic* | *record* {decls}
		grammar.addProduction(type,
				// type [*num*]
				// TODO: this kind of parenthesis does not exist  
				// *basic*
				list(t(TokenType.DOUBLE_SYMBOL)),
				list(t(TokenType.LONG_SYMBOL)),
				list(t(TokenType.STRING_SYMBOL)),
				list(t(TokenType.BOOL_SYMBOL))
				// *record {decls}*
				// TODO: record symbol does not exist
				);
		
		// stmts -> stmts stmt | \epsilon
		grammar.addProduction(stmts, 
				list(stmts,stmt),
				list(eps));
		
		/* stmt -> 	assign;
	     | 			*if*(assign) stmt
	     | 			*if*( assign ) stmt *else* stmt
	     | 			*while*( assign ) stmt
	     | 			*do* stmt *while* ( assign );
	     | 			*break*;
	     | 			*return*;
	     | 			*return* loc;
	     | 			*print* loc;
	     | 			block
	    **/
		grammar.addProduction(stmt,
				// assign
				list(assign,t(TokenType.SEMICOLON)),
				// *if* (assign) stmt
				list(t(TokenType.IF),t(TokenType.LEFT_PARAN),assign,t(TokenType.RIGHT_PARAN),stmt),
				// *if* (assign) stmt *else* stmt
				list(t(TokenType.IF),t(TokenType.LEFT_PARAN),assign,t(TokenType.RIGHT_PARAN),stmt,t(TokenType.ELSE),stmt),
				// *while* (assign) stmt
				list(t(TokenType.WHILE),t(TokenType.LEFT_PARAN),assign,t(TokenType.RIGHT_PARAN),stmt),
				// *do* stmt *while* (assign);
				list(t(TokenType.DO),stmt,t(TokenType.WHILE),t(TokenType.LEFT_PARAN),assign,t(TokenType.RIGHT_PARAN),t(TokenType.SEMICOLON)),
				// break;
				list(t(TokenType.BREAK),t(TokenType.SEMICOLON)),
				// return;
				list(t(TokenType.RETURN),t(TokenType.SEMICOLON)),
				// return loc;
				list(t(TokenType.RETURN),loc,t(TokenType.SEMICOLON)),
				// *print* loc;
				list(t(TokenType.PRINT),loc,t(TokenType.SEMICOLON)),
				// block			
				list(block)
				);
		
		// loc -> loc [assign] | *id* | loc.*id*
		grammar.addProduction(loc,
				// TODO: loc [assign] 
				list(t(TokenType.ID))
				// TODO: loc.*id* 
				);
		
		// assign -> loc=assign | bool
		grammar.addProduction(assign,
				list(loc,t(TokenType.ASSIGNOP),assign),
				list(bool));
		
		// bool -> bool||join | join
		grammar.addProduction(bool,
				list(bool,t(TokenType.OR),join),
				list(join));
		
		// join -> join&&equality | equality
		grammar.addProduction(join,
				list(join,t(TokenType.AND),equality),
				list(equality));
		
		// equality -> equality==rel | equality!=rel | rel
		grammar.addProduction(equality,
				list(equality,t(TokenType.EQUALS),rel),
				list(equality,t(TokenType.NOT_EQUALS),rel),
				list(rel));
		
		// rel -> expr<expr | expr<=expr | expr>=expr | expr>expr | expr
		grammar.addProduction(rel,
				list(expr,t(TokenType.LESS),expr),
				list(expr,t(TokenType.LESS_OR_EQUAL),expr),
				list(expr,t(TokenType.GREATER),expr),
				list(expr,t(TokenType.GREATER_EQUAL),expr),
				list(expr));
		
		// expr -> expr+term | expr-term | term
		grammar.addProduction(expr,
				list(expr,t(TokenType.PLUS),term),
				list(expr,t(TokenType.MINUS),term),
				list(term));
		
		// term -> term*unary | term/unary | unary
		grammar.addProduction(term,
				list(term,t(TokenType.TIMES),unary),
				list(term,t(TokenType.DIVIDE),unary),
				list(unary));
		
		// unary -> !unary | -unary | factor
		grammar.addProduction(unary, 
				list(t(TokenType.NOT),unary), 
				list(t(TokenType.MINUS),unary), 
				list(factor));
		
		// factor -> (assign) | loc | *num* | *real* | *true* | *false* | *string*
		grammar.addProduction(factor,
				list(t(TokenType.LEFT_PARAN),assign,t(TokenType.RIGHT_PARAN)),
				list(loc),
				list(t(TokenType.NUM)), 
				list(t(TokenType.REAL)),
				list(t(TokenType.TRUE)),
				list(t(TokenType.FALSE)),
				list(t(TokenType.STRING))
				);
		
		automaton=new SLRAutomaton<>(grammar);
	}
	
	/**
	 * derivate given sourcecode and retuns a list of productions in the reverse order. 
	 * The derivation is always the rightmost derivation
	 * @param sc the given word
	 * @return the concrete derivation if the word is in the language. Otherwise undetermined result.
	 */
	public List<Reduction> derivateDFRightToLeft(SourceCode sc){
		List<Production<Terminal, NonTerminal>> productions= automaton.getDerivationASsSequence(sc);
		List<Reduction> reductions=new LinkedList<>();
		for (Production<Terminal,NonTerminal> p : productions) reductions.add(new Reduction(p));	
		return reductions;
	}
	
	
	public List<Reduction> derivateDFLeftToRight(SourceCode sc){
		List<Reduction> rtl=derivateDFRightToLeft(sc);
		HashMap<NonTerminal,Stack<Node>> seen=new HashMap<>();
		for (Reduction reduction : rtl) seen.put(reduction.left_side,new Stack<Node>());
		Node n=null;
		for (Reduction production : rtl){
			n = new Node();
			n.nt=production.left_side;
			n.edges=production.right_side;
			n.reduction=production;
			Node[] new_children=new Node[n.edges.size()];
			for (int i=n.edges.size()-1;i>=0;i--){
				Object o=n.edges.get(i);
				if (o instanceof Token){
					// add primitive Tree
					Token tok=(Token)o;
					Leaf leaf= new Leaf();
					leaf.t=tok;
					leaf.edges.add(tok);
					new_children[i]=leaf;
				}
				else{
					NonTerminal nt=(NonTerminal)o;
					// add more complex tree
					// get the last produced
					Node append=seen.get(nt).pop();
					new_children[i]=append;
				}
				n.children=Arrays.asList(new_children);
			}
			seen.get(n.nt).add(n);
		}
		return n.getLeftToRight();
	}
	
	
	class Leaf extends Node{
		Token t;
		@Override
		List<Reduction> getLeftToRight() {
			return new LinkedList<>();
		}
		
	}
	class Node{
		@Override
		public String toString() {
			return nt+"[edges=" + edges + ", children=" + children +"]";
		}
		Reduction reduction;
		List<Node> children=new LinkedList<>();
		List<Object> edges=new LinkedList<>();
		NonTerminal nt;
		List<Reduction> getLeftToRight(){
			
			List<Reduction> res=new LinkedList<>();
			res.add(this.reduction);
			for (Node child : children) res.addAll(child.getLeftToRight());
			// simple node
			return res;
			
		}
		
	}
	
	
	
	/**
	 * Represents a concrete production related to our grammar
	 * @author Till
	 *
	 */
	public class Reduction {
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
			String res= left_side.toString()+"->";
			for (Object o :right_side){
				if (o instanceof Token){
					res+=((Token)o).getTokenType();
				}
				else{
					res+=o;
				}
			}
			return res;
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
			for (Token tok : tarr) if(tok.getTokenType()!=TokenType.COMMENT) add(new Terminal(tok));
		}
		
	}
	
	
}
