javabite
========
Javabite is a compiler for a language described by the related repository 
<https://github.com/swp-uebersetzerbau-ss13/common>.

## How-To build the project
Depending on the operation system there are script files which setup everything necessary
to build the javabite-compiler.

For Windows: 
	"gradlew.bat buildCompiler"

For sh-compatible: 
	"./gradlew buildCompiler"

For ant: 
	The build.xml execute these commands depending on your OS by default.
	The build itself will be executed by gradle. 

The fully build compiler can be found in bin/javabite-compiler-0.1.jar

It compiles the file provided as first command line argument.

## Note for MS1
After build like described in "How-To build the project" the examples can be executed 
by these commands.

´´´
	java -jar javabite-compiler-0.1.jar ../common/examples/ms1/<progname>.prog 
´´´

If build is successful the generated program can be started with

´´´
	java <progname>
´´´

The Javabite group expects for MS1 that all variables are implicitly initialized.
Therefore the 'error_undef_return.prog'-example will be compilable. If the result of
discussion of MS2 with the other project group is, that the semantic is otherwise,
this will be changed.

## Project Structure
The project consists of six projects, each responsible for another part of the compiler:

1. common
 * common contains the language specification, tests to check for interface conformance 
 of a implementation and the common interfaces
 * ! project is referenced as submodule ! - please see remarks 
2. javabite-backend
 * javabite-backend implements a javabyte-generating backend for the compiler-framework
3. javabite-code-gen
 * generation for the three address code
4. javabite-lexer
 * lexer for the common grammar
5. javabite-parser
 * parser for the common grammar
6. javabite-compiler
 * main application with user interface which provide the compiler
7. javabite-common
 * implementation of the project common classes
 
## Submodule
The common-project has its own git repository (all the others are part of the javabite 
repository). The javabite-repository is setted up so that it will automatically download 
the referenced common-repository.

This means that the submodule is a git repository inside of another git-repository. 
Changes inside of it will not be commited with commits of the parent repository.
Its imported to understand, that the submodule reference a certain commit and if 
a newer commit of common should be used it has to be pulled and than the reference
must be updated.

You will have to call 
```
> git submodule update
```
to get the main referenced version.


## How to change common
**Attention**: If there is something unclear below here, better create an issue. We will try
to describe it in more details. If you only want a newer commit of common than you can
also create an issue and we will update to it. 

If you change, commit or update yourself in this submodule, you will possible safe a some
problems which to solve you will need further knowledge.

**Advice**: Checkout common a second time and change this one.

To change common you have to go inside common repository and switch to a branch, eg.:

```
> git checkout master
```

Than you finished your changes, commit and push as always. If everyone using javabite
should use your new commit, you will have to commit and push the main-repo.

## Setup

The following is needed: 
* a recent git installation
* a gradle installation

Recommended is:
* Eclipse
* Gradle Integration for Eclipse (available through Eclipse Marketplace)

If another IDE integration plugin in the build.gralde is needed, please open issue.

1. Checkout the repository to a location you want.
 * If you do this on console you will need to use:
 ```
 git submodule init
 git submodule update
 ```
2. Go to the "Import..." in eclipse and select Gradle->Gradle Project.
3. Select the repository location and press "Build Model"
4. Select all projects
5. Press "finish"

Eclipse will setup repository for you.

## Development remarks
Please remember to add new sources within package swp_compiler_ss13.javabite.<project-short-name>
The common interfaces can be found within swp_compiler_ss13.common
