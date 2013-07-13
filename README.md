javabite
========
Javabite is a compiler for a language, mostly named **'PROG'**, described by the related repository 
<https://github.com/swp-uebersetzerbau-ss13/common>.

## What can you find in this repository?
This repository contains implementations for all interfaces defined by the common-repository mentioned above, a CLI compiler utilizing the implemented compiler modules and a minimalitic IDE-like GUI with visualization of datastructures of the compilation process like the Abstract Syntax Tree (AST) and the Three Address Code (TAC). 

The compiler is developed in **JAVA** and generates **target code** for the **JVM 1.7**. The compiler is modulazied into five parts:

1. Lexer
     + The lexer tokenize the input into word of the [grammar of **PROG**](https://github.com/swp-uebersetzerbau-ss13/common/wiki/Grammar)
2. Parser
     + The parser converts the token stream provided by the lexer into an AST.
3. Semantical Analyzer
     + The semantical analyzer checks if the generated AST is wellformed considering the [semantic defined for the grammer](https://github.com/swp-uebersetzerbau-ss13/common/wiki/Grammar#semantic)
4. Intermediate Code Generator
     + The intermediate code generator takes this check AST and converts it into the [intermediate TAC](https://github.com/swp-uebersetzerbau-ss13/common/wiki/Grammar#semantic)
5. Backend
     + The backend converts the TAC into the target code which is **Java ByteCode** for *Javabite* 

All modules are also implemented against the same interfaces by the second team working in <https://github.com/swp-uebersetzerbau-ss13/fuc>. They provide alternative implementations of the frontend modules (all but Backend) and a backend module targeting the [LLVM](http://llvm.org/)

## Quickstart
If you want to try the compiler, execute this command on the shell: 

```
git clone https://github.com/swp-uebersetzerbau-ss13/javabite.git
cd javabite
git submodule init
git submodule update
gradlew buildCompiler
java -jar bin\javabite-compiler-0.2.jar
```

This starts the GUI. Examples of **PROG** programs can be found in this repository and in common. Look for files with suffix '.prog'.

## Module Descriptions
This section describes the functionality, implementation and structure of the different modules.

### Lexer
The task of the lexer module is to tokenize the incoming sourcecode. This tokenization breaks down the words of the sourcecode into a set of tokens, which are strings. Additionally the lexer adds a context to each token, for example, whether the token is of the type NUM or REAL. With such tokens other compiler modules like the parser can create an AST more easily. The lexer is build with a pattern matcher, that works with the *regular expression engine* of *Java* to tokenize the sourcecode. This implementation can be easily updated, because of the fact that only the regular expressions for each token have to be updated. We also can add new tokens by just adding a new token type and a regular expression for it. The list of tokens in the **PROG** grammar can be found in the [TokenType definition](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-lexer/src/main/java/swp_compiler_ss13/javabite/lexer/JavabiteTokenType.java) and the used regular expressions are available in the [JavabiteTokenType definition](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-lexer/src/main/java/swp_compiler_ss13/javabite/lexer/JavabiteTokenType.java)

Then lexer implementation is named [**swp_compiler_ss13.javabite.lexer.LexerJb**](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-lexer/src/main/java/swp_compiler_ss13/javabite/lexer/LexerJb.java).

### Parser
The parser logic has been implemented based on the fundamental knowledge gained from the course *Compiler Design*. Regardless, it was necessary to tailor some of the learned techniques to our needs. This documentation gives in particular a introduction into the deflexion of the *default approach*. 
First, we name the important classes and their basic functionality. Thereafter, we show where we had to change the prototype-implementation presented in various lectures. At last, we'll outline how the interaction  between the modules happens and how a typical application of the parser works. 
Long story short, we've successfully implemented a SLR parser

#### Modules and Classes
There exist several important packages and classes, each of them providing a special kind of functionality.
* **swp_compiler_ss13.javabite.parser.grammar.Grammar<T,NT>** is the most important class of the parser. This class represents a grammar with **T** as terminals and **NT** as NonTerminals, respectively. The implementation provides functions like closure-computation, first- and follow-set determination.  
* **swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar** is a special subclass of **Grammar**. This class represents the exact programmatic translation of the [grammar of interest](https://github.com/swp-uebersetzerbau-ss13/common/wiki/Grammar). 
* **swp_compiler_ss13.javabite.parser.grammar.SLRAutomaton** is a automaton, which can derive the concrete derivation of a given word with a look ahead of 1.
* **swp_compiler_ss13.javabite.parser.astGenerator.ASTGenerator** has been developed to translate a derivation ( bottom-up-left-to-right). The result is a Abstract Syntax tree

#### Mentionable Modifications
Some approaches have to be fitted or completely replaced to use it in our implementation with the given grammar. We note some of them:
* It's not traceable to construct the ast when the translation is given in a right-most fashion. We had to translate the right-most derivation in the left-most derivation first in the **TargetGrammar** class.
* The LRAutomaton had to be modified in a way, that the automaton is able to deal with epsilon productions. Another way is to remove the epsilon-productions from the grammar, what results in another grammar. We did want to keep the grammar. In the most common literature, the automaton has not to deal with epsilon productions since they are removed before. Thus, the automaton had to be adjusted.
* The closure, first- and follow set computation can be very complex for large grammars. We decide to differ from a pure functional implementation and implemented cached and/ or pre-computed values as often as possible. This decreases the readability but increases the performance dramatically.

#### Application of the Modules
A typical application can be drafted as follows:

1. A word of the grammar is given in form of a sequence of tokens
2. We abstract from the concrete token and just consider the type ( e.g. "myInt" and "myDouble" as a ID is treated the same way). 
3. We use the abstract token stream to get a concrete derivation of the word with the help of TargetGrammar.
4. Luckily, we piggy-bagged the information in the abstract token. Now, we have the derivation with the concrete tokens ( e.g. the tokens "myInt" and "myDouble" is again distinguishable).
5. We use the TargetGrammar to _rotate_ the derivation to gain a left-to-right fashioned derivation.
6. We use a semantic translation to construct the ast from the derivation in _ASTGenerator_.

### Semantic Analyser
Like the implementation of the parser, we tried to follow as often as possible the prototype approach of the compiler design course. Unfortunately, this not as frequently as in the parser possible. In the next sections, we describe the important modules and after that, a short outline of the use of the semantic analyzer.

#### Modules and Classes
There are just a few important classes, whose are quickly described:
* **swp_compiler_ss13.javabite.semantic.SemanticAnalyzerJb** simply checks for simple division by zero and uses the ASTTypeChecker ( described below) to guarantee type-correctness.
* **swp_compiler_ss13.javabite.semantic.ASTTypeChecker** is a very simple S-Attributed check. Implemented by traversing and caching. 


#### Mentionable Modifications
The best-practice approach would be to create a L-Attributed grammar to check e.g. for invalid break-statement positions.
To implement a simple evaluation scheme, we decided to use a L-Attributed grammar with a limited dependency scheme to ease the evaluation approach. The big advantage is that we do not have to use a dynamic scheme based on the dependency graph.
We distinguish between S and L' attributes. *S attributes* depend just on the attributes of the subtree, as expected with synthesized attributes. 
*L' attributes* depend on L' attributes of the parent or is a immediate determined value ( like the type of a literal).
Using this scheme, it was possible to evaluate all attributes with a simple left-to-right depth-first-traversal, where at a node the L' attributes computed instantly. After the procedure have been invoked recursive on the whole subtree, the synthesized attributes are evaluated.
With this scheme, it is possible to evaluate synthesized attributes like the type and L' attributes like the validness of a position for a break-statement ( context-sensitive).

#### Application of the Modules
A typical application can be drafted as follows:

1. Create a SemanticAnalyzer
2. analyze the ast of interest with it
3. interpret the results and errors thrown by the reportErrorLog.

### Intermediate Code Generator
The intermediate code generator converts the provided AST into a list of quadruple which is the TAC. The implementation for the javabite team [**swp_compiler_ss13.javabite.codegen.IntermediateCodeGeneratorJb**](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-code-gen/src/main/java/swp_compiler_ss13/javabite/codegen/IntermediateCodeGeneratorJb.java) delegates the conversion of the AST nodes to specialized converters for each node type. The converters calls the generator for processing subnodes, generates necessary quadruples and add them to the quadruple list.

For the conversion process the converters can access helper methods provided by the generator. The helper methods provided unique temporary variable names and unique names for jump labels. Furthermore there are mechanismns for scope management and mapping from AST to TAC name space.

### Backend
The backend generates the *Java Bytecode* from the TAC. The main class is [**swp_compiler_ss13.javabite.backend.BackendModule**](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-backend/src/main/java/swp_compiler_ss13/javabite/backend/BackendJb.java)

Basically the javabite-backend consists of three main components � 

1. a three-address-code optimizer 
  * [swp_compiler_ss13.javabite.backend.translation.TACOptimizer](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-backend/src/main/java/swp_compiler_ss13/javabite/backend/translation/TACOptimizer.java)
2. the actual translator being responsible for translating the three-address-code into java bytecode files 
  * [swp_compiler_ss13.javabite.backend.translation.Translator](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-backend/src/main/java/swp_compiler_ss13/javabite/backend/translation/TargetCodeOptimizer.java)
3. and a target code optimizer, which is supposed to optimize the generated bytecode.
  * [swp_compiler_ss13.javabite.backend.translation.TargetCodeOptimizer](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-backend/src/main/java/swp_compiler_ss13/javabite/backend/translation/TargetCodeOptimizer.java)

The optimization components are not implemented, yet, as they are optional.

#### Translator

The translator translates the three-address-code into java bytecode in several steps:

swp_compiler_ss13.javabite.backend.translation.Translator.translate(String, List<Quadruple>)

1. First, it generates a new classfile object (swp_compiler_ss13.javabite.backend.classfile.Classfile), initializes it with all necessary information considering the jvm specification for classfiles and adds a new 'main'-method object to it, which will be used to run the generated code.
2. Then, the translator iterates three times over the three-address-code extracting different information.
3. During the first iteration it looks for variable declarations and allocates appropriate space for every found one in the code attribute of the previously generated classfile's main-method.
4. During the second iteration the translator looks for all constants in the three-address-code being prefixed with the symbol '#' and adds appropriate constant pool info structures meeting the jvm specification for those ones to the previously generated classfile's constant pool.
5. During the third iteration, the actual translation takes place and the three-address-code is translated into instruction objects being modeled considering the jvm specification for those ones. The generated code is added to the code attribute of the previously generated classfile's main-method, afterwards.
6. Finally, the translator returns a Collection of classfile objects.

[JVM Specification - http://docs.oracle.com/javase/specs/jvms/se7/html/](http://docs.oracle.com/javase/specs/jvms/se7/html/)

### Compiler
There are three ways to work with the compiler: via JUnit-test, via CLI and via GUI. All of these implementations base on [**swp_compiler_ss13.javabite.compiler.AbstractJavabiteCompiler**](https://github.com/swp-uebersetzerbau-ss13/javabite/blob/master/javabite-common/src/main/java/swp_compiler_ss13/javabite/compiler/AbstractJavabiteCompiler.java).

The *AbstractJavabiteCompiler* loads all necessary module via the [Java ServiceLoader API](http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html) and select the implementation like defined in the javabite.properties file, which gets generated if not existing on startup.

The compilation process is implemented based on the requirements of the common interfaces and after each phase of the compilation process (each module providing a phase), a notification method is called which can abort the compilation process.

### IDE Environment
The Javabite IDE is not only a wrapper for the command line interface. With the IDE files can be edited and compiled without always typing in the command line. The following list shows the main functionalities of the Javabite compiler:

* opening, editing and saving files
* compiling and running the sourcecode
* syntax highlighting and TokenType mouseover for sourcecode
* viewing report logs containing warning and error messages found while compiling
* viewing compiler internal logs
* display the sourcecode as an abstract syntax tree
* display the sourcecode as an three address code in quadruple


## Project Structure
The project consists of eigth projects, each responsible for another part of the compiler:

1. common
     + common contains the language specification, tests to check for interface conformance of a implementation and the common interfaces
     + this a *git submodule* of the common repository
2.  javabite-common
     + contains implementation of the project common classes
3. javabite-lexer
     + contains lexer for the common grammar
4. javabite-parser
     + contains parser for the common grammar
5. javabite-semantic
     + contains semantical analyzer for the common grammar
6. javabite-code-gen
     + contains the generation for the three address code
7. javabite-backend
     + conatins javabite-backend generating *Java ByteCode*
8. javabite-compiler
     + contains the main application with user interface

### Setup of development environment

The following is needed:
+ a recent git installation

Recommended is:
+ Eclipse
+ a gradle installation (you get one automatically by executing the gradlew-command)
+ Gradle Integration for Eclipse (available through Eclipse Marketplace)

If another IDE integration plugin in the build.gralde is needed, please open issue.

1. Checkout the repository to a location you want. Execute:

 ```
 git submodule init
 git submodule update
 ```
2. Go to the "Import..." in eclipse and select Gradle->Gradle Project.
3. Select the repository location and press "Build Model"
4. Select all projects
5. Press "finish"

Eclipse will setup repository for you.

### Runtime test

They can be executed with: `gradlew :javabite-compiler:test`

### Cross testing

Cross-testing is not automated yet. Manually they can be executed by adding the jars to the :javabite:test-task classpath and change the javabite.properties in test-resources.
