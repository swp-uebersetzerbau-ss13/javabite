package swp_compiler_ss13.javabite.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class JavaClassProcess {
	private final static boolean NO_VERIFY_NEEDED = true;
	private final static String NO_VERIFY = "-noverify";
	
	Process p;

	public JavaClassProcess(File classFile) {
		if (classFile == null)
			throw new NullPointerException();

		if (!classFile.exists())
			throw new JavaClassProcessRuntimeException(
					"JavaClassProcess can not be started. File does not exists.");

		if (classFile.isDirectory())
			throw new JavaClassProcessRuntimeException(
					"JavaClassProcess can not be started. Need a file not a directory.");

		if (!classFile.getName().endsWith(".class"))
			throw new JavaClassProcessRuntimeException(
					"JavaClassProcess can not be started. File has not expected extension.");

		String classPath = classFile.getParent();
		String className = classFile.getName();
		className = className.substring(0, className.lastIndexOf('.'));

		String javaExecutablePath = System.getProperty("java.home")
				+ File.separator + "bin" + File.separator + "java";
		ProcessBuilder processBuilder = new ProcessBuilder(javaExecutablePath,
				"-cp", classPath, NO_VERIFY_NEEDED?NO_VERIFY:"", className);
		try {
			p = processBuilder.redirectErrorStream(true).start();
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			throw new JavaClassProcessRuntimeException(e.getMessage(), e);
		}
	}

	public Integer getReturnValue() {
		return p.exitValue();
	}

	public String getProcessOutput() {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(p.getInputStream(), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	public InputStream getInputstream() {
		return p.getInputStream();
	}
}
