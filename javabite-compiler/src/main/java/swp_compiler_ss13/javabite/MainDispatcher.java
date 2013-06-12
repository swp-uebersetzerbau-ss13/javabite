package swp_compiler_ss13.javabite;

import java.awt.EventQueue;
import java.io.File;

import swp_compiler_ss13.javabite.compiler.JavabiteCliCompiler;
import swp_compiler_ss13.javabite.gui.MainFrame;
import swp_compiler_ss13.javabite.runtime.JavaClassProcess;

public class MainDispatcher {
	public static void main(final String[] args) {
		System.out.println("Javabite-Compiler for PROG");

		if (args.length < 1) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						MainFrame frame = new MainFrame();
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return;
		}

		if (args.length < 2) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						MainFrame frame = new MainFrame(new File(args[0]));
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return;
		}

		File file = new File(args[1]);
		System.out.println(file.getAbsolutePath());
		JavabiteCliCompiler compiler = new JavabiteCliCompiler();
		switch (args[0]) {
		case "compile":
			compiler.compile(file);
			break;
		case "run":
			if (!compiler.isJavaBackend()) {
				System.out.println("'run' only works with a Java backend"); 
				return;
			}
			File out = compiler.compile(file);
			if (out == null) {
				return;
			}
			JavaClassProcess p = compiler.execute(out);
			System.out.println(p.getProcessOutput());
			System.out.println("Return-Code: " + p.getReturnValue());
			break;
		default:
			showHelp();
		}
	}

	private static void showHelp() {
		System.out.println("Help:");
		// TODO: write help
	}
}
