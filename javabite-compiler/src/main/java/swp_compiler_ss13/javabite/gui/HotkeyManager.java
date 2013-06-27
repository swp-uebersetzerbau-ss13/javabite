package swp_compiler_ss13.javabite.gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class HotkeyManager implements KeyEventDispatcher {

	@Override
	public boolean dispatchKeyEvent(KeyEvent arg0) {
		
		//save, strg+s
		if(arg0.getKeyCode() == 83 && arg0.isControlDown())
		{
			System.out.println("Save file!!");
		}
		
		return false;
	}

}
