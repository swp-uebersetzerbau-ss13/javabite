package org.gradle.api.tasks;

import java.util.List;

import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.ajoberstar.gradle.git.tasks.*
import org.eclipse.jgit.api.*
import org.eclipse.jgit.api.errors.*

class GitSubmoduleInit extends GitBase {
	List<String> modules
	@TaskAction
	void pullRepo() {
		SubmoduleInitCommand cmd = getGit().submoduleInit()
		modules.each {cmd.addPath(it)}
		try {
			cmd.call()
		} catch (InvalidRemoteException e) {
			throw new GradleException("Invalid remote repository.", e)
		} catch (TransportException e) {
			throw new GradleException("Problem with transport.", e)
		} catch (WrongRepositoryStateException e) {
			throw new GradleException("Invalid repository state.", e)
		} catch (GitAPIException e) {
			throw new GradleException("Problem with pull.", e)
		}
	}
	
	@Input
	public void getModules() {
		this.modules = modules?:[];
	}
	
	public void setModules(List<String> modules) {
		this.modules = modules;
	}
}