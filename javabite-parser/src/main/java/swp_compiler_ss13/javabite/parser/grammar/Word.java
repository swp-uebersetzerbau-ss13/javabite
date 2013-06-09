package swp_compiler_ss13.javabite.parser.grammar;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Word<T extends Symbol,NT extends Symbol> extends LinkedList<T>{
	private final Grammar<T,NT> g;
	public Word(Grammar<T,NT> g,List<T> tarr){
		super(tarr);
		this.g=g;
	}
	
	public Word(Grammar<T,NT> g,T...tarr){
		this(g,Arrays.asList(tarr));
	}
	
	public static<T extends Symbol,NT extends Symbol> Word<T,NT> getWordFromRightMostDerivation(Grammar<T,NT> g,  List<Production<T,NT>> productions ){
		// TODO: check if the productions are really in the grammar
		List<Symbol> der= new LinkedList<Symbol>();
		Queue<Production<T,NT>> prods=new ArrayDeque<>(productions);
		der.add(g.startSymbol);
		NEXT_PRODUCTION:
		while (!prods.isEmpty()){
			Production<T,NT> p=prods.poll();
			NT toDerivate=p.left;
			for (int i=der.size()-1;i>=0;i--){
				if (toDerivate.equals(der.get(i))){
					// replace
					// not the most efficient way to do it, but it's okay in the scope of unit testing
					List<Symbol> left=null;
					if (i==0)
						left=new LinkedList<Symbol>();
					else
						left=new LinkedList<Symbol>(der.subList(0, i));
					
					List<Symbol> right=null;
					if (i==der.size()-1)
						right=new LinkedList<Symbol>();
					else
						right=new LinkedList<Symbol>(der.subList(i+1, der.size()));
					der.clear();
					der.addAll(left);
					der.addAll(p.right);
					der.addAll(right);
					
					continue NEXT_PRODUCTION;
				}
			}
		}
		return new Word<T,NT>(g, (List<T>)der);
	}
}
