package swp_compiler_ss13.javabite.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JavaClassProcess {
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
				"-cp", classPath, "-noverify", className);
		try {
			p = processBuilder.start();
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			throw new JavaClassProcessRuntimeException(e.getMessage(), e);
		}
	}

	public Integer getReturnValue() {
		return p.exitValue();
	}

	public String getProcessOutput() {
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw new JavaClassProcessRuntimeException(e.getMessage(), e);
		}
		return sb.toString();
	}

	public InputStream getInputstream() {
		return p.getInputStream();
	}
}
