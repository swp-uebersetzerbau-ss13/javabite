package swp_compiler_ss13.javabite.backend.marco.proposal1;

/**
 * ControllerTemp class. Implementation of a temporary controller
 * to instantiate the backend and call the function "generateTargetCode()".
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class ControllerTemp {

	public static void main(String[] args) {
		
		BackendModule backend = new BackendModule();
		backend.generateTargetCode(null);
		
	}

}
