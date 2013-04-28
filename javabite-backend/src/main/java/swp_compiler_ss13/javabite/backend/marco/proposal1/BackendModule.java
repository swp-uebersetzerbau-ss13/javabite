package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

/**
 * BackendImpl class. Implementation of the interface "Backend".
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class BackendModule implements Backend {
	
	private TACOptimizer tacOptimizer;
	private Translator translator;
	private TargetCodeOptimizer targetCodeOptimizer;
	
	public BackendModule() {
		this.tacOptimizer = new TACOptimizer();
		this.translator = new Translator();
		this.targetCodeOptimizer = new TargetCodeOptimizer();
	}
	
	@Override
	public Map<String, InputStream> generateTargetCode(List<Quadruple> tac) {
		
		// TAC Optimizer
		
		
		// Translator
		List<Classfile> classfileList = this.translator.translate(tac);
		// ##### BeginTmpOutput #####
		this.dirtyPrint(classfileList);
		// ##### EndTmpOutput #####
		
		
		// Target Code Optimizer
		
		return null;
	}
	
	
	
	
	
	
	
	// DELETE LATER!!!!
	public void dirtyPrint(List<Classfile> classfileList) {
		Iterator<Classfile> classfileListIterator = classfileList.iterator();
		while(classfileListIterator.hasNext()) {
			Classfile classfile = classfileListIterator.next();
			System.out.println(classfile.getName() + ": ");
			
			ArrayList<Byte> bytes = classfile.getBytes();
			Iterator<Byte> bytesIterator = bytes.iterator();
			
			int i = 0;
			while(bytesIterator.hasNext()) {
				Byte byteVal = bytesIterator.next();
				String tmp = Integer.toHexString(((byteVal + 256)%256));
				if (tmp.length() < 2) {
					tmp = "0" + tmp;
				}
				
				System.out.print(tmp + " ");
				
				i++;
				if (i == 16) {
					System.out.println("");
					i = 0;
				}
			}
		}
	}
}
