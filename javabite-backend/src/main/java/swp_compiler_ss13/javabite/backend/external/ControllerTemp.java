package swp_compiler_ss13.javabite.backend.external;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.BackendModule;

/**
 * ControllerTemp class. Implementation of a temporary controller to instantiate
 * the backend and call the function "generateTargetCode()".
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class ControllerTemp {

	static List<Quadruple> tac = new ArrayList<Quadruple>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6319892294145882549L;
		{

			// add(new QuadrupleImpl(Operator.DECLARE_LONG, "#3", "!", "l"));
			// add(new QuadrupleImpl(Operator.DECLARE_LONG, "#4", "!", "t1"));
			// add(new QuadrupleImpl(Operator.ADD_LONG, "l", "t1", "l"));
			// add(new QuadrupleImpl(Operator.RETURN, "l", "!", "!"));

			// add.prog
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t1"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t2"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t3"));
			add(new QuadrupleImpl(Operator.ADD_LONG, "#10", "#23", "t1"));
			add(new QuadrupleImpl(Operator.SUB_LONG, "t1", "#23", "t1"));
			add(new QuadrupleImpl(Operator.DIV_LONG, "#100", "#2", "t2"));
			add(new QuadrupleImpl(Operator.ADD_LONG, "t1", "t2", "t1"));
			add(new QuadrupleImpl(Operator.SUB_LONG, "t1", "#30", "t1"));
			add(new QuadrupleImpl(Operator.DIV_LONG, "#-9", "#3", "t3"));
			add(new QuadrupleImpl(Operator.ADD_LONG, "t1", "t3", "t1"));
			add(new QuadrupleImpl(Operator.ASSIGN_LONG, "t1", "!", "l"));
			add(new QuadrupleImpl(Operator.RETURN, "l", "!", "!"));

			// simple_add.prog

			// add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"));
			// add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t1"));
			// add(new QuadrupleImpl(Operator.ADD_LONG, "#3", "#3", "t1"));
			// add(new QuadrupleImpl(Operator.ASSIGN_LONG, "t1", "!", "l"));
			// add(new QuadrupleImpl(Operator.RETURN, "l", "!", "!"));

			// simple_mul.prog
			// add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"));
			// add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t1"));
			// add(new QuadrupleImpl(Operator.MUL_LONG, "#3", "#3", "t1"));
			// add(new QuadrupleImpl(Operator.ASSIGN_LONG, "t1", "!", "l"));
			// add(new QuadrupleImpl(Operator.RETURN, "l", "!", "!"));

			/* Quadruple Examples */
			/*
			 * add(new QuadrupleImpl(Operator.DECLARE_LONG, "#100", "!",
			 * "long1")); add(new QuadrupleImpl(Operator.DECLARE_LONG, "#200",
			 * "!", "long2")); add(new QuadrupleImpl(Operator.SUB_LONG, "long1",
			 * "long2", "long3"));
			 * 
			 * add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "long1"));
			 * add(new QuadrupleImpl(Operator.DECLARE_LONG, "#100", "!",
			 * "long2"));
			 * 
			 * add(new QuadrupleImpl(Operator.DECLARE_DOUBLE,"!","!","double1"
			 * )); add(new
			 * QuadrupleImpl(Operator.DECLARE_DOUBLE,"#2.0","!","double2"));
			 * 
			 * add(new QuadrupleImpl(Operator.DECLARE_BOOL,"!","!","bool1"));
			 * add(new
			 * QuadrupleImpl(Operator.DECLARE_BOOL,"#FALSE","!","bool2"));
			 * 
			 * add(new QuadrupleImpl(Operator.DECLARE_STRING, "!", "!",
			 * "string1")); add(new QuadrupleImpl(Operator.DECLARE_STRING,
			 * "#\"TEST\"", "!", "string2"));
			 * 
			 * add(new QuadrupleImpl(Operator.LONG_TO_DOUBLE,
			 * "#long1","!","doubl1")); add(new
			 * QuadrupleImpl(Operator.DOUBLE_TO_LONG, "#double2", "!",
			 * "long1"));
			 * 
			 * add(new QuadrupleImpl(Operator.ASSIGN_LONG, "#3", "!", "long1"));
			 * add(new QuadrupleImpl(Operator.ASSIGN_LONG, "long2", "!",
			 * "long1"));
			 * 
			 * add(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#3.0", "!",
			 * "double1")); add(new QuadrupleImpl(Operator.ASSIGN_DOUBLE,
			 * "#double2", "!", "double1"));
			 * 
			 * add(new QuadrupleImpl(Operator.ASSIGN_BOOL, "#FALSE", "!",
			 * "bool1")); add(new QuadrupleImpl(Operator.ASSIGN_BOOL, "bool2",
			 * "!", "bool1"));
			 * 
			 * add(new QuadrupleImpl(Operator.ASSIGN_STRING, "#\"TEST\"", "!",
			 * "string1")); add(new QuadrupleImpl(Operator.ASSIGN_STRING,
			 * "string2", "!", "string1"));
			 * 
			 * add(new QuadrupleImpl(Operator.ADD_LONG, "#3", "#2", "long3"));
			 * add(new QuadrupleImpl(Operator.ADD_LONG, "long1", "long2",
			 * "long3"));
			 * 
			 * add(new QuadrupleImpl(Operator.ADD_DOUBLE, "#3.0", "#3.0",
			 * "double3")); add(new QuadrupleImpl(Operator.ADD_DOUBLE,
			 * "double1", "double2", "double3"));
			 * 
			 * add(new QuadrupleImpl(Operator.SUB_LONG, "#3", "#3", "long3"));
			 * add(new QuadrupleImpl(Operator.SUB_LONG, "long1",
			 * "long2","long3"));
			 * 
			 * add(new QuadrupleImpl(Operator.SUB_DOUBLE, "#3.0", "#3.0",
			 * "double3")); add(new QuadrupleImpl(Operator.SUB_DOUBLE,
			 * "double1", "#double2", "double3"));
			 * 
			 * add(new QuadrupleImpl(Operator.MUL_LONG, "#3", "#3", "long3"));
			 * add(new QuadrupleImpl(Operator.MUL_LONG, "long1",
			 * "long2","long3"));
			 * 
			 * add(new QuadrupleImpl(Operator.MUL_DOUBLE, "#3.0", "#3.0",
			 * "double3")); add(new QuadrupleImpl(Operator.MUL_DOUBLE,
			 * "double1", "#double2", "double3"));
			 * 
			 * add(new QuadrupleImpl(Operator.DIV_LONG, "#3", "#3", "long3"));
			 * add(new QuadrupleImpl(Operator.DIV_LONG, "long1",
			 * "long2","long3"));
			 * 
			 * add(new QuadrupleImpl(Operator.DIV_DOUBLE, "#3.0", "#3.0",
			 * "double3")); add(new QuadrupleImpl(Operator.DIV_DOUBLE,
			 * "double1", "#double2", "double3"));
			 */
		}
	};

	public static void main(final String[] args) {

		final StringBuilder sb = new StringBuilder();

		for (final Quadruple quad : ControllerTemp.tac) {
			sb.append(quad.getOperator());
			sb.append(" | ");
			sb.append(quad.getArgument1());
			sb.append(" | ");
			sb.append(quad.getArgument2());
			sb.append(" | ");
			sb.append(quad.getResult());
			sb.append("\n\n");
		}

		// System.out.println(sb.toString());

		final BackendModule backend = new BackendModule();
		final Map<String, InputStream> test = backend.generateTargetCode("Program", tac);

		for (final String key : test.keySet()) {
			System.out.println(key);
		}
	}

}
