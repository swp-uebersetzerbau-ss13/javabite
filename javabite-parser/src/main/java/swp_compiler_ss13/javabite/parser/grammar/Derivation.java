package swp_compiler_ss13.javabite.parser.grammar;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this class represents a concrete representation ( syntax tree ) of a word
 * @author Till
 *
 * @param <T>
 * @param <NT>
 */
public class Derivation <T extends Symbol,NT extends Symbol, G extends Grammar<T,NT>>{
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	public Derivation() {
		
	}
	
	
	private class SymbolNode<R>{
		R value;
		NonTerminalNode parent;
	}
	private class TerminalLeaf extends SymbolNode<T>{
		
	}
	private class NonTerminalNode extends SymbolNode<NT>{
		final List<SymbolNode<Symbol>> children=new LinkedList<>();
		void addChildren(List<SymbolNode<Symbol>> list){
			this.children.addAll(list);
			for (SymbolNode<Symbol> child : list) {
				if (child.parent==null){
					child.parent=NonTerminalNode.this;
				}
				else if (!child.parent.equals(NonTerminalNode.this)){
					logger.error("this must not happen : {} should be parent of {}, but {} is parent",NonTerminalNode.this,child,child.parent);
				}
			}
		}
		
	}
	
	class LRBuilder{
		SymbolNode startNode;
		LRBuilder(){
			
		}
	}
}
