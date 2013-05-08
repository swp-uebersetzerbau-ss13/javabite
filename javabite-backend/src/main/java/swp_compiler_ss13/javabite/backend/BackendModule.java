package swp_compiler_ss13.javabite.backend;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.translation.TACOptimizer;
import swp_compiler_ss13.javabite.backend.translation.TargetCodeOptimizer;
import swp_compiler_ss13.javabite.backend.translation.Translator;


/**
 * BackendImpl class. Implementation of the interface "Backend".
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class BackendModule implements Backend
{

	@SuppressWarnings("unused")
	private TACOptimizer tacOptimizer;
	private Translator translator;
	@SuppressWarnings("unused")
	private TargetCodeOptimizer targetCodeOptimizer;

	public BackendModule() {
		this.tacOptimizer = new TACOptimizer();
		this.translator = new Translator();
		this.targetCodeOptimizer = new TargetCodeOptimizer();
	}

	@Override
	public Map<String, InputStream> generateTargetCode(List<Quadruple> tac) {

		// TAC Optimizer
		// ### currently empty ###
		// Translator
		Collection<IClassfile> classfiles = this.translator.translate(tac);
		// Target Code Optimizer
		// ### currently empty ###

		Map<String, InputStream> targetCodeS = createTargetCodeStreams(classfiles);
		
		// simple visualization 
		visualizeTargetCode(targetCodeS);
		
		return targetCodeS;
	}
	
	private Map<String, InputStream> createTargetCodeStreams(Collection<IClassfile> classfiles) {
		Map<String, InputStream> targetCodeIS = new HashMap<>();
		
		for(IClassfile classfile : classfiles) {
			targetCodeIS.put(classfile.getName(), classfile.generateInputstream());
		}
		
		return targetCodeIS;
	}

	/*
	 * print content of inputstreams to console
	 */
	private void visualizeTargetCode(Map<String, InputStream> targetCodeIS) {
		
		for(String classname : targetCodeIS.keySet()) {

			StringBuilder sb = new StringBuilder();
			sb.append("Classname : "+ classname + "\n");
			sb.append("Content : \n\n");
			
			ByteArrayInputStream  is = (ByteArrayInputStream) targetCodeIS.get(classname);			
			DataInputStream dis = new DataInputStream(is);
			
			int i = 0;
			byte b;
			try {
				while((b = (byte) dis.read()) != -1) {
					String tmp = Integer.toHexString(((b + 256) % 256));
					if (tmp.length() < 2) {
						sb.append(0).append(tmp).append(" ");
					} else {
						sb.append(tmp).append(" ");
					}
				
					i++;
					if (i == 16) {
						sb.append("\n");
						i = 0;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(sb.toString());
		}
	}


}
