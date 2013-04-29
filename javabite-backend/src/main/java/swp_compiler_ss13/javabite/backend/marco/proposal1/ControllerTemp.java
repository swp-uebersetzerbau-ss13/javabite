package swp_compiler_ss13.javabite.backend.marco.proposal1;


import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.javabite.backend.marco.proposal1.Quadruple.Operator;

/**
 * ControllerTemp class. Implementation of a temporary controller
 * to instantiate the backend and call the function "generateTargetCode()".
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class ControllerTemp {

	static List<QuadrupleImpl> tac = new ArrayList<QuadrupleImpl>(){ 
		/**
		 * 
		 */
		private static final long serialVersionUID = 6319892294145882549L;
		{
			/* Variable declaration */
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "long1"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "#1l", "!", "long2"));
			
			add(new QuadrupleImpl(Operator.DECLARE_DOUBLE,"!","!","doble1" ));
			add(new QuadrupleImpl(Operator.DECLARE_DOUBLE,"#2.0","!","double2"));
			
			add(new QuadrupleImpl(Operator.DECLARE_BOOL,"!","!","bool1"));
			add(new QuadrupleImpl(Operator.DECLARE_BOOL,"#FALSE","!","bool2"));
			
			add(new QuadrupleImpl(Operator.DECLARE_STRING, "!", "!", "string1"));
			add(new QuadrupleImpl(Operator.DECLARE_STRING, "#\"TEST\"", "!", "string2"));
			
			add(new QuadrupleImpl(Operator.LONG_TO_DOUBLE, "#long1","!","doubl1"));
			add(new QuadrupleImpl(Operator.DOUBLE_TO_LONG, "#double2", "!", "long1"));
			
			add(new QuadrupleImpl(Operator.ASSIGN_LONG, "#3l", "!", "long1"));
			add(new QuadrupleImpl(Operator.ASSIGN_LONG, "long2", "!", "long1"));
			
			add(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#3.0", "!", "double1"));
			add(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#double2", "!", "double1"));			
			
			add(new QuadrupleImpl(Operator.ASSIGN_BOOL, "#FALSE", "!", "bool1"));
			add(new QuadrupleImpl(Operator.ASSIGN_BOOL, "bool2", "!", "bool1"));
			
			add(new QuadrupleImpl(Operator.ASSIGN_STRING, "#\"TEST\"", "!", "string1"));
			add(new QuadrupleImpl(Operator.ASSIGN_STRING, "string2", "!", "string1"));
			
			add(new QuadrupleImpl(Operator.ADD_LONG, "#3l", "#2l", "long3"));
			add(new QuadrupleImpl(Operator.ADD_LONG, "long1", "long2", "long3"));
			
			add(new QuadrupleImpl(Operator.ADD_DOUBLE, "#3.0", "#3.0", "double3"));
			add(new QuadrupleImpl(Operator.ADD_DOUBLE, "double1", "double2", "double3"));
		
			add(new QuadrupleImpl(Operator.SUB_LONG, "#3l", "#3l", "long3"));
			add(new QuadrupleImpl(Operator.SUB_LONG, "long1", "long2","long3"));
			
			add(new QuadrupleImpl(Operator.SUB_DOUBLE, "#3.0", "#3.0", "double3"));
			add(new QuadrupleImpl(Operator.SUB_DOUBLE, "double1", "#double2", "double3"));
			
			add(new QuadrupleImpl(Operator.MUL_LONG, "#3l", "#3l", "long3"));
			add(new QuadrupleImpl(Operator.MUL_LONG, "long1", "long2","long3"));
			
			add(new QuadrupleImpl(Operator.MUL_DOUBLE, "#3.0", "#3.0", "double3"));
			add(new QuadrupleImpl(Operator.MUL_DOUBLE, "double1", "#double2", "double3"));
			
			add(new QuadrupleImpl(Operator.DIV_LONG, "#3l", "#3l", "long3"));
			add(new QuadrupleImpl(Operator.DIV_LONG, "long1", "long2","long3"));
			
			add(new QuadrupleImpl(Operator.DIV_DOUBLE, "#3.0", "#3.0", "double3"));
			add(new QuadrupleImpl(Operator.DIV_DOUBLE, "double1", "#double2", "double3"));
		}
	};

	
	public static void main(String[] args) {
		
		StringBuilder sb = new StringBuilder();
		
		for(QuadrupleImpl quad : ControllerTemp.tac) {
			sb.append(quad.getOperator()); 
			sb.append(" | ");
			sb.append(quad.getArgument1());
			sb.append(" | ");
			sb.append(quad.getArgument2());
			sb.append(" | ");
			sb.append(quad.getResult());
			sb.append("\n\n");
		}
		
		System.out.println(sb.toString());
		
		BackendModule backend = new BackendModule();
		backend.generateTargetCode(null);
	}

}
