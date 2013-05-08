package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
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
public class BackendModule implements IBackend
{

	private TACOptimizer tacOptimizer;
	private Translator translator;
	private TargetCodeOptimizer targetCodeOptimizer;

	public BackendModule() {
		this.tacOptimizer = new TACOptimizer();
		this.translator = new Translator();
		this.targetCodeOptimizer = new TargetCodeOptimizer();
	}

	@Override
	public Map<String, InputStream> generateTargetCode(List<IQuadruple> tac) {

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
