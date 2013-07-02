package swp_compiler_ss13.javabite.gui.bytecode;

import java.io.File;
import java.io.IOException;

import javax.swing.Action;

import org.apache.commons.lang.NotImplementedException;
import org.gjt.jclasslib.browser.BrowserComponent;
import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.config.window.BrowserPath;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.io.ClassFileReader;

public class SimpleBrowserService implements BrowserServices {

	ClassFile classfile;

	public SimpleBrowserService(File classfile) {
		try {
			this.classfile = ClassFileReader.readFromClassPath(new String[] {}, "",
					classfile.getName());
		} catch (InvalidByteCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ClassFile getClassFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activate() {
		throw new NotImplementedException();
	}

	@Override
	public BrowserComponent getBrowserComponent() {
		throw new NotImplementedException();
	}

	@Override
	public Action getActionBackward() {
		throw new NotImplementedException();
	}

	@Override
	public Action getActionForward() {
		throw new NotImplementedException();
	}

	@Override
	public void openClassFile(String className, BrowserPath browserPath) {
		// TODO: implementation
	}

	@Override
	public boolean canOpenClassFiles() {
		return true;
	}

	@Override
	public void showURL(String urlSpec) {
		throw new NotImplementedException();
	}

}
