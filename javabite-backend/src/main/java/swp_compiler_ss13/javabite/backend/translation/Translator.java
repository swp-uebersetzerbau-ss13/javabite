package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.*;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.*;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

import java.util.*;

import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.*;

/**
 * <h1>Translator</h1>
 * <p>
 * This class and its methods are supposed to be used to translate the tac into
 * a list of classfiles containing the target bytecode (java bytecode).
 * </p>
 * 
 * @since 27.04.2013
 */
public class Translator {

	// MethodSignature objec of the main method
	public static final MethodSignature MAIN_METHOD = new MethodSignature.Builder(
			"main").args(String[].class).build();

	// ClassSignature of the general object class
	public static final ClassSignature OBJECT_CLASS = new ClassSignature(
			Object.class);

	// list of array-set-operators
	static final EnumSet<Operator> ARRAY_SET_OPERATORS = EnumSet.of(
			Operator.ARRAY_SET_BOOLEAN, Operator.ARRAY_SET_DOUBLE,
			Operator.ARRAY_SET_LONG, Operator.ARRAY_SET_STRING);

	Collection<Classfile> classfiles;

	public Translator() {
		classfiles = new ArrayList<>();
	}

	/**
	 * <h1>generateClassfile</h1>
	 * <p>
	 * This method generates a new Classfile object using its parameter and
	 * appends it to the translator's classfile list.
	 * </p>
	 * 
	 * @since 27.04.2013
	 * 
	 * @param thisClassNameEIF
	 *            string describing this class name of the class described in
	 *            this classfile encoded in internal form
	 * @param superClassNameEIF
	 *            string describing the superclass' class name of the class
	 *            described in this classfile encoded in internal form
	 * @param isStruct
	 *            determines, whether a main classfile or a struct classfile is
	 *            supposed to be generated
	 * @param accessFlags
	 *            arbitrary amount of classfile access flags
	 * @return instance of Classfile
	 */
	private Classfile generateClassfile(final String thisClassNameEIF,
			final String superClassNameEIF, final boolean isStruct,
			final ClassfileAccessFlag... accessFlags) {
		final Classfile classfile = new Classfile(thisClassNameEIF,
				superClassNameEIF, isStruct, accessFlags);
		classfiles.add(classfile);
		return classfile;
	}

	/**
	 * <h1>translate</h1>
	 * <p>
	 * This method translates the provided tac into target bytecode, which can
	 * be executed by a jvm. Therefore it generates appropriate classfile
	 * bytecode meeting the jvm classfile specification and translates the tac
	 * statements into bytecode instructions meeting the jvm instructions
	 * specification.
	 * </p>
	 * 
	 * @since 27.04.2013
	 * 
	 * @param mainClassName
	 *            String filename of the (main)class, which always has to be
	 *            generated
	 * @param tac
	 *            List of quadruple objects
	 * @return Collection of classfile objects
	 */
	public Collection<Classfile> translate(final String mainClassName,
			List<Quadruple> tac) {

		// create a new (main)classfile / classfile with main method
		final Classfile mainClassfile = generateClassfile(mainClassName,
				OBJECT_CLASS.className, false, ClassfileAccessFlag.ACC_PUBLIC,
				ClassfileAccessFlag.ACC_SUPER);

		// add main method to this (main)classfile
		mainClassfile.addToMethodArea(MAIN_METHOD.methodName,
				MAIN_METHOD.methodDescriptor, MethodAccessFlag.ACC_PUBLIC,
				MethodAccessFlag.ACC_STATIC);

		/*
		 * ##### At this point the actual translation starts. The tac is
		 * preprocessed in several steps and finally it's being translated into
		 * the main classfile. #####
		 */

		/*
		 * parse tac for struct declarations, create classfiles for them and
		 * generate appropriate constructors to initialize the member variables
		 */
		analyzeTAC(mainClassfile, tac);

		// translate tac/program into the main classfile
		if (tac != null) {
			// "allocate" appropriate space for local variables
			tac = addVariablesToLocalVariableSpace(mainClassfile,
					MAIN_METHOD.methodName, tac);
			// search for constants and add them to the constant pool
			addConstantsToConstantPool(mainClassfile, tac);
			// translate the tac into jav bytecode instructions
			extractInstructionsFromOperations(mainClassfile,
					MAIN_METHOD.methodName, tac);
		}

		return classfiles;
	}

	/**
	 * <h1>analyzeTAC</h1>
	 * 
	 * <p>
	 * This method analyzes the provided tac and tries to find struct
	 * declarations. If it finds a declaration, it gets the whole declaration
	 * tac using the method "getStructQuadrupleCount" and then translates it
	 * into a classfile using the method "translateStructIntoClassfile".
	 * Finally, it deletes the struct declaration body (everything of the
	 * declaration after the first declaration statement), as it is not needed
	 * anymore in the further translation.
	 * </p>
	 * 
	 * @since 24.06.2013
	 * 
	 * @param mainClassfile
	 *            Classfile object of the main classfile, which is needed to
	 *            save some information of the struct translation, which is
	 *            needed in later parts of the translation
	 * @param tac
	 *            the complete program tac as list of quadruples
	 */
	private void analyzeTAC(final Classfile mainClassfile,
			final List<Quadruple> tac) {
		int listIndex = 0;

		// search for struct declarations
		for (ListIterator<Quadruple> tacIter = tac.listIterator(); tacIter
				.hasNext(); listIndex++) {

			// get the quadruple parts
			Quadruple quad = tacIter.next();
			// array name, if we find one with struct as basic type
			String arrayName = null;

			/*
			 * if we find an array declaration, get its name and skip to the
			 * arrays basic type declaration
			 */
			if (quad.getOperator() == Operator.DECLARE_ARRAY) {
				arrayName = quad.getResult();
				do {
					listIndex++;
				} while ((quad = tacIter.next()).getOperator() == Operator.DECLARE_ARRAY);
			}

			/*
			 * if the operator/ basic type is an DECLARE_STRUCT operator, a new
			 * classfile has to be generated
			 */
			if (quad.getOperator() == Operator.DECLARE_STRUCT) {

				// first, get the struct's name
				final String structName = isIgnoreParam(quad.getResult()) ? arrayName
						: quad.getResult();
				/*
				 * Then, generate the class name being used in the classfile,
				 * which is generated for the struct. We decided to name the
				 * struct classes according to their nesting prefixed with the
				 * main class' name. For instance: If we have a main class named
				 * "main" and a struct x being declared in a struct y, the name
				 * of the class being generated for x will be: "main_y_x".
				 */
				final String className = ClassSignature.joinClassName(
						mainClassfile.getClassname(), structName);

				// register struct as toplevel struct for struct resolution
				mainClassfile.addToplevelStruct(structName);
				/*
				 * Arg2 is used to store the full class name of the struct. This
				 * is needed in later parts of the translation process.
				 */
				tacIter.set(QuadrupleUtils.copyOf(quad, null, null, className,
						null));

				// get struct's tac
				final long memberVarsCount = Long
						.parseLong(removeConstantSign(quad.getArgument1()));
				List<Quadruple> structTAC = tac.subList(
						listIndex + 1,
						listIndex
								+ 1
								+ getStructQuadrupleCount(tacIter,
										memberVarsCount));

				/*
				 * translate the found struct's tac into an appropriate
				 * classfile using the already analyzed information
				 */
				translateStructIntoClassfile(mainClassfile, structTAC,
						className);

				/*
				 * delete struct body tac from program tac, structStart + 1 to
				 * keep the struct declaration
				 */
				tac.subList(listIndex + 1, listIndex + structTAC.size() + 1)
						.clear();
				tacIter = tac.listIterator(listIndex + 1);

			} else if (quad.getOperator() == Operator.PRINT_STRING) {

				// set print flag, if print declaration is found
				mainClassfile.setPrintFlag(true);
			}
		}
	}

	/**
	 * <h1>getStructQuadrupleCount</h1>
	 * 
	 * <p>
	 * This method counts, how many quadruples belong to the current struct
	 * declaration.
	 * </p>
	 * 
	 * @since 24.06.2013
	 * 
	 * @param tacIter
	 *            program iterator at the struct declaration position
	 * @param memberVarsCount
	 *            number of expected struct members to be found
	 * @return int amount of quadruples belonging to the struct tac
	 */
	private static int getStructQuadrupleCount(
			final ListIterator<Quadruple> tacIter, final long memberVarsCount) {

		// number of quadruples belonging to the current struct
		int quadCount = 0;

		// get tac of current struct declaration
		for (int i = 0; i < memberVarsCount && tacIter.hasNext(); i++, quadCount++) {
			Quadruple structQuad = tacIter.next();
			final Operator operator = structQuad.getOperator();

			switch (operator) {
			case DECLARE_ARRAY:
				structQuad = tacIter.next();
				quadCount++;
				// if it is an array, skip to the basic type declaration
				while (structQuad.getOperator() == Operator.DECLARE_ARRAY) {
					structQuad = tacIter.next();
					quadCount++;
				}
				if (structQuad.getOperator() != Operator.DECLARE_STRUCT) {
					break;
				}
			case DECLARE_STRUCT:
				// get struct's tac
				final long structMemberVarsCount = Long
						.parseLong(removeConstantSign(structQuad.getArgument1()));
				quadCount += getStructQuadrupleCount(tacIter,
						structMemberVarsCount);
			default:
				break;
			}
		}

		return quadCount;
	}

	/**
	 * <h1>translateStructIntoClassfile</h1>
	 * 
	 * <p>
	 * This method translates the provided struct tac into an appropriate
	 * classfile. In order to do this, it generates an new classfile and parses
	 * the tac again, afterwards, generating appropriate fields in the field
	 * area and fieldref constants in the constant pool, which are needed for
	 * the constructor generation at the end of this method (which is done using
	 * the method "translateStructTacIntoConstructorCode"), for every member
	 * variable of the processed struct. Many special cases have to be
	 * considered, for instance the recursive call of this method, if we find
	 * another struct declaration or an array of structs in the processed
	 * struct's tac so that new classfiles have to be generated for them. During
	 * the whole processing, all the quadruples are filtered out, which are not
	 * needed during the constructor generation, afterwards.
	 * </p>
	 * 
	 * @since 24.06.2013
	 * 
	 * @param mainClassfile
	 *            Classfile object of the main classfile, which is needed to
	 *            save some information of the struct translation, which is
	 *            needed in later parts of the translation
	 * @param structTac
	 *            list of quadruples representing the struct's body (all the
	 *            member declarations, which have to be considered for classfile
	 *            generation)
	 * @param className
	 *            the classname which has to be used for teh classfile
	 *            generation
	 */
	private void translateStructIntoClassfile(final Classfile mainClassfile,
			final List<Quadruple> structTac, final String className) {

		/*
		 * tac list for constructor code generation; at the end of this method,
		 * the struct tac is preprocessed, an appropriate classfile is created
		 * and this list contains only quadruples, which are needed for
		 * constructor code generation
		 */
		final List<Quadruple> constructorTAC = new ArrayList<>();

		/*
		 * create a new classfile for this struct using the previously specified
		 * classname
		 */
		final Classfile structClassfile = generateClassfile(className,
				OBJECT_CLASS.className, true, ClassfileAccessFlag.ACC_PUBLIC,
				ClassfileAccessFlag.ACC_SUPER);

		/*
		 * add constants of struct tac to the constant pool of the just created
		 * classfile
		 */
		addConstantsToConstantPool(structClassfile, structTac);

		/*
		 * generate field info structures in field area for every struct member,
		 * appropriate field constants in constant pool and new classfiles, if
		 * necessary (if a struct is found in this struct)
		 */
		int listIndex = 0;
		for (final ListIterator<Quadruple> tacIter = structTac.listIterator(); tacIter
				.hasNext(); listIndex++) {

			Quadruple quad = tacIter.next();
			// get members name
			final String name = quad.getResult();

			switch (quad.getOperator()) {
			case DECLARE_ARRAY:
				// temp variable for found array's tac
				int arrayDimensions = 0;

				// get rest of arrays tac
				do {
					arrayDimensions++;
					constructorTAC.add(quad);
					quad = tacIter.next();
				} while (quad.getOperator() == Operator.DECLARE_ARRAY);
				listIndex += arrayDimensions;

				final ClassSignature fieldTypeSig;

				// check found arrays basic type and act appropriatly
				if (quad.getOperator() == Operator.DECLARE_STRUCT) {
					/*
					 * BASIC TYPE STRUCT FOUND -> another classfile has to be
					 * generated
					 */

					// assemble struct name
					fieldTypeSig = new ClassSignature(arrayDimensions,
							className, name);
					quad = QuadrupleUtils.copyOf(quad, null, null,
							fieldTypeSig.baseClassName, null);
					tacIter.set(quad);

					// add struct declaration to constructor tac
					constructorTAC.add(quad);

					// get struct's tac
					final long structMemberCount = Long
							.parseLong(removeConstantSign(quad.getArgument1()));
					final int quadCount = getStructQuadrupleCount(tacIter,
							structMemberCount);
					List<Quadruple> structTACBody = structTac.subList(
							listIndex + 1, listIndex + 1 + quadCount);

					// generate classfile for structTACBody
					translateStructIntoClassfile(mainClassfile, structTACBody,
							fieldTypeSig.baseClassName);
				} else {
					// SIMPLE BASIC TYPE FOUND
					constructorTAC.add(quad);

					fieldTypeSig = new ClassSignature(
							arrayDimensions,
							JavaType.getByOperator(quad.getOperator()).classSignature.className);
				}

				// get member variable's descriptor using arrayTAC
				final FieldSignature arrayFieldSig = new FieldSignature(name,
						className, fieldTypeSig.typeClassName);
				structClassfile.addFieldToFieldArea(arrayFieldSig,
						FieldAccessFlag.ACC_PUBLIC);

				// register array of current struct with its dimensions
				mainClassfile.addStructMemberArray(
						ClassSignature.joinClassName(className, name),
						fieldTypeSig.typeClassName);

				/*
				 * generate field reference info structure in the constant pool,
				 * which will be used in the constructor for initialization
				 */
				structClassfile.addFieldrefToConstantPool(arrayFieldSig);
				break;

			case DECLARE_STRUCT:
				/*
				 * STRUCT DECLARATION FOUND -> another classfile has to be
				 * generated
				 */

				// generate classfile for structTACwithoutFirstDecl
				final ClassSignature structClassSig = new ClassSignature(
						className, name);
				quad = QuadrupleUtils.copyOf(quad, null, null,
						structClassSig.className, null);

				// change iterator and constructor tac appropriatly
				tacIter.set(quad);
				constructorTAC.add(quad);

				// get struct's tac
				final long structMemberCount = Long
						.parseLong(removeConstantSign(quad.getArgument1()));
				final int quadCount = getStructQuadrupleCount(tacIter,
						structMemberCount);
				final List<Quadruple> structTACBody = structTac.subList(
						listIndex + 1, listIndex + 1 + quadCount);

				// generate a new classfile for found struct tac
				translateStructIntoClassfile(mainClassfile, structTACBody,
						structClassSig.className);

				// register struct as substruct
				mainClassfile.addSublevelStruct(structClassSig.className);

				// get member variable's descriptor using structTAC
				final FieldSignature structFieldSig = new FieldSignature(name,
						className, structClassSig.typeClassName);
				structClassfile.addFieldToFieldArea(structFieldSig,
						FieldAccessFlag.ACC_PUBLIC);

				/*
				 * generate field reference info structure in the constant pool,
				 * which will be used in the constructor for initialization
				 */
				structClassfile.addFieldrefToConstantPool(structFieldSig);
				break;

			default:
				// getDescriptor using quad
				final FieldSignature fieldSig = new FieldSignature(name,
						className, ClassfileUtils.typeByQuadruples(quad));
				// add field to field area
				structClassfile.addFieldToFieldArea(fieldSig,
						FieldAccessFlag.ACC_PUBLIC);

				/*
				 * without initial value, initialization in constructor will not
				 * be necessary
				 */
				if (!ConstantUtils.isIgnoreParam(quad.getArgument1())) {
					// add to constructor tac
					constructorTAC.add(quad);

					/*
					 * generate field reference info structure in the constant
					 * pool, which will be used in the constructor for
					 * initialization
					 */
					structClassfile.addFieldrefToConstantPool(fieldSig);
				}
			}
		}

		/*
		 * set constructor/ generate constructor code to initialize the member
		 * variables of the previously generated struct classfile
		 */
		translateStructTacIntoConstructorCode(structClassfile, constructorTAC);
	}

	/**
	 * <h1>addConstantsToConstantPool</h1>
	 * <p>
	 * This method parses the tac and adds all constants to the provided
	 * classfile's constant pool.
	 * </p>
	 * 
	 * @since 29.04.2013
	 * 
	 * @param classFile
	 *            classfile of constant pool to add constants to
	 * @param tac
	 *            List<Quadruple> three-address-code
	 */
	private static void addConstantsToConstantPool(final Classfile classFile,
			final List<Quadruple> tac) {

		for (final Quadruple quad : tac) {

			// get the quadruple parts
			final Quadruple.Operator operator = quad.getOperator();
			final String arg1 = quad.getArgument1();
			final String arg2 = quad.getArgument2();
			final String result = quad.getResult();

			/*
			 * if the operator is an array_set-operator, it'll have to be
			 * examined separately, as argument constant types can be different
			 * from each other
			 */
			if (ARRAY_SET_OPERATORS.contains(operator)) {
				// argument 2 is a long constant
				if (isConstant(arg2)) {
					classFile.addLongToConstantPool(Long
							.parseLong(removeConstantSign(arg2)));
				}
				// result can be a constant of different types
				if (isConstant(result)) {
					switch (operator) {
					case ARRAY_SET_LONG:
						classFile.addLongToConstantPool(Long
								.parseLong(removeConstantSign(result)));
						break;
					case ARRAY_SET_DOUBLE:
						final String resMod = removeConstantSign(result);
						classFile.addDoubleToConstantPool(
								Double.parseDouble(resMod), resMod);
						break;
					case ARRAY_SET_STRING:
						classFile.addStringToConstantPool(
								removeConstantSign(result), true);
						break;
					case ARRAY_SET_BOOLEAN:
					default:
						break;
					}
				}
				continue;
			}

			// get expected type of constants
			final ConstantPoolType type = ConstantPoolType
					.getByOperator(operator);

			/*
			 * if type is equal to zero, there'll be no constants in this
			 * operation or the constants are not stored in this classfile's
			 * constant pool
			 */
			if (type == null)
				continue;

			/*
			 * check type and argument and create appropriate constant pool
			 * entry if necessary
			 */

			switch (type) {
			case LONG:
				if (isConstant(arg1)) {
					classFile.addLongToConstantPool(Long
							.parseLong(removeConstantSign(arg1)));
				}
				if (isConstant(arg2)) {
					classFile.addLongToConstantPool(Long
							.parseLong(removeConstantSign(arg2)));
				}
				if (isConstant(result)) {
					classFile.addLongToConstantPool(Long
							.parseLong(removeConstantSign(result)));
				}
				break;

			case DOUBLE:
				if (isConstant(arg1)) {
					final String arg1Mod = removeConstantSign(arg1);
					classFile.addDoubleToConstantPool(
							Double.parseDouble(arg1Mod), arg1Mod);
				}
				if (isConstant(arg2)) {
					final String arg2Mod = removeConstantSign(arg2);
					classFile.addDoubleToConstantPool(
							Double.parseDouble(arg2Mod), arg2Mod);
				}
				if (isConstant(result)) {
					final String resMod = removeConstantSign(result);
					classFile.addDoubleToConstantPool(
							Double.parseDouble(resMod), resMod);
				}
				break;

			case STRING:
				if (isConstant(arg1)) {
					classFile.addStringToConstantPool(removeConstantSign(arg1),
							true);
				}
				if (isConstant(arg2)) {
					classFile.addStringToConstantPool(removeConstantSign(arg2),
							true);
				}
				if (isConstant(result)) {
					classFile.addStringToConstantPool(
							removeConstantSign(result), true);
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * <h1>addVariablesToLocalVariableSpace</h1>
	 * <p>
	 * This method "allocates" space in the local variable space of the provided
	 * classfile's method's code attribute for all variable declarations, will
	 * convert them into assignments, if they have an initial value and will
	 * delete them if not.
	 * </p>
	 * 
	 * @since 29.04.2013
	 * @param file
	 *            instance of Classfile, which is used
	 * @param methodName
	 *            String method name being used to identify the method of the
	 *            provided classfile, in whose local variable space, space is
	 *            supposed to be "allocated"
	 * @param tac
	 *            list of quadruples which are analyzed in order to dinf
	 *            variable declarations
	 * @return List<Quadruple> the modified three-address-code
	 */
	private static List<Quadruple> addVariablesToLocalVariableSpace(
			final Classfile file, final String methodName,
			final List<Quadruple> tac) {

		for (final ListIterator<Quadruple> tacIter = tac.listIterator(); tacIter
				.hasNext();) {

			final Quadruple quad = tacIter.next();
			final String defValue;
			final String result = quad.getResult();
			String arg1 = quad.getArgument1();
			Operator op = quad.getOperator();

			switch (op) {
			case DECLARE_STRING:
				op = Operator.ASSIGN_STRING;
				defValue = ConstantUtils.DEFAULT_VALUE_STRING;
				file.addVariableToMethod(methodName, result,
						LocalVariableType.STRING);
				break;
			case DECLARE_LONG:
				op = Operator.ASSIGN_LONG;
				defValue = ConstantUtils.DEFAULT_VALUE_LONG;
				file.addVariableToMethod(methodName, result,
						LocalVariableType.LONG);
				break;
			case DECLARE_DOUBLE:
				op = Operator.ASSIGN_DOUBLE;
				defValue = ConstantUtils.DEFAULT_VALUE_DOUBLE;
				file.addVariableToMethod(methodName, result,
						ClassfileUtils.LocalVariableType.DOUBLE);
				break;
			case DECLARE_BOOLEAN:
				op = Operator.ASSIGN_BOOLEAN;
				defValue = ConstantUtils.DEFAULT_VALUE_BOOLEAN;
				file.addVariableToMethod(methodName, result,
						LocalVariableType.BOOLEAN);
				break;
			case DECLARE_REFERENCE:
				file.addVariableToMethod(methodName, result,
						ClassfileUtils.LocalVariableType.AREF);
				tacIter.remove();
				continue;
			case DECLARE_ARRAY:
				file.addVariableToMethod(methodName, result,
						ClassfileUtils.LocalVariableType.AREF);
				/*
				 * during array declaration, skip further checks until a basic
				 * type is declared/ the array declaration is finished.
				 */
				while (tacIter.hasNext()) {
					if (tacIter.next().getOperator() != Operator.DECLARE_ARRAY)
						break;
				}
				continue;
			case DECLARE_STRUCT:
				// do not delete declaration tac
				file.addVariableToMethod(methodName, result,
						ClassfileUtils.LocalVariableType.AREF);
			default:
				continue;
			}

			// modify current quadruple
			if (ConstantUtils.isIgnoreParam(arg1)) {
				arg1 = defValue;
			}

			// replace current quadruple by modified one
			tacIter.set(new QuadrupleJb(op, arg1,
					ConstantUtils.SYMBOL_IGNORE_PARAM, result));
		}

		return tac;
	}

	/**
	 * <h1>extractInstructionsFromOperations</h1>
	 * <p>
	 * This method generates instructions from the passed tac.
	 * </p>
	 * 
	 * @since 09.05.2013
	 * 
	 * @param classfile
	 *            classfile to generate bytecode sources for
	 * @param tac
	 *            list of tac-quadruples to generate bytecode with
	 */
	private static void extractInstructionsFromOperations(
			final Classfile classfile, final String methodName,
			final List<Quadruple> tac) {
		final MainBuilder pb = new MainBuilder(classfile, methodName);

		for (final Quadruple quad : tac) {

			switch (quad.getOperator()) {
			case ADD_DOUBLE:
				pb.addDouble(quad);
				break;
			case ADD_LONG:
				pb.addLong(quad);
				break;
			case ASSIGN_BOOLEAN:
				pb.assignBoolean(quad);
				break;
			case ASSIGN_DOUBLE:
				pb.assignDouble(quad);
				break;
			case ASSIGN_LONG:
				pb.assignLong(quad);
				break;
			case ASSIGN_STRING:
				pb.assignString(quad);
				break;
			case DECLARE_BOOLEAN:
				pb.declareBoolean(quad);
				break;
			case DECLARE_DOUBLE:
				pb.declareDouble(quad);
				break;
			case DECLARE_LONG:
				pb.declareLong(quad);
				break;
			case DECLARE_STRING:
				pb.declareString(quad);
				break;
			case DIV_DOUBLE:
				pb.divDouble(quad);
				break;
			case DIV_LONG:
				pb.divLong(quad);
				break;
			case DOUBLE_TO_LONG:
				pb.doubleToLong(quad);
				break;
			case LONG_TO_DOUBLE:
				pb.longToDouble(quad);
				break;
			case MUL_DOUBLE:
				pb.mulDouble(quad);
				break;
			case MUL_LONG:
				pb.mulLong(quad);
				break;
			case RETURN:
				pb.returnLong(quad);
				break;
			case SUB_DOUBLE:
				pb.subDouble(quad);
				break;
			case SUB_LONG:
				pb.subLong(quad);
				break;
			case AND_BOOLEAN:
				pb.andBoolean(quad);
				break;
			case OR_BOOLEAN:
				pb.orBoolean(quad);
				break;
			case NOT_BOOLEAN:
				pb.notBoolean(quad);
				break;
			case COMPARE_LONG_E:
				pb.compareLongE(quad);
				break;
			case COMPARE_LONG_G:
				pb.compareLongG(quad);
				break;
			case COMPARE_LONG_L:
				pb.compareLongL(quad);
				break;
			case COMPARE_LONG_GE:
				pb.compareLongGE(quad);
				break;
			case COMPARE_LONG_LE:
				pb.compareLongLE(quad);
				break;
			case COMPARE_DOUBLE_E:
				pb.compareDoubleE(quad);
				break;
			case COMPARE_DOUBLE_G:
				pb.compareDoubleG(quad);
				break;
			case COMPARE_DOUBLE_L:
				pb.compareDoubleL(quad);
				break;
			case COMPARE_DOUBLE_GE:
				pb.compareDoubleGE(quad);
				break;
			case COMPARE_DOUBLE_LE:
				pb.compareDoubleLE(quad);
				break;
			case LABEL:
				pb.label(quad);
				break;
			case BRANCH:
				pb.branch(quad);
				break;
			case PRINT_STRING:
				pb.printString(quad);
				break;
			case DECLARE_ARRAY:
				pb.declareArray(quad);
				break;
			case ARRAY_GET_LONG:
				pb.arrayGetLong(quad);
				break;
			case ARRAY_GET_DOUBLE:
				pb.arrayGetDouble(quad);
				break;
			case ARRAY_GET_BOOLEAN:
				pb.arrayGetBoolean(quad);
				break;
			case ARRAY_GET_STRING:
				pb.arrayGetString(quad);
				break;
			case ARRAY_GET_REFERENCE:
				pb.arrayGetReference(quad);
				break;
			case ARRAY_SET_LONG:
				pb.arraySetLong(quad);
				break;
			case ARRAY_SET_DOUBLE:
				pb.arraySetDouble(quad);
				break;
			case ARRAY_SET_BOOLEAN:
				pb.arraySetBoolean(quad);
				break;
			case ARRAY_SET_STRING:
				pb.arraySetString(quad);
				break;
			case BOOLEAN_TO_STRING:
				pb.booleanToString(quad);
				break;
			case LONG_TO_STRING:
				pb.longToString(quad);
				break;
			case DOUBLE_TO_STRING:
				pb.doubleToString(quad);
				break;
			case STRUCT_GET_LONG:
				pb.structGetLong(quad);
				break;
			case STRUCT_GET_DOUBLE:
				pb.structGetDouble(quad);
				break;
			case STRUCT_GET_BOOLEAN:
				pb.structGetBoolean(quad);
				break;
			case STRUCT_GET_STRING:
				pb.structGetString(quad);
				break;
			case STRUCT_GET_REFERENCE:
				pb.structGetReference(quad);
				break;
			case STRUCT_SET_LONG:
				pb.structSetLong(quad);
				break;
			case STRUCT_SET_DOUBLE:
				pb.structSetDouble(quad);
				break;
			case STRUCT_SET_BOOLEAN:
				pb.structSetBoolean(quad);
				break;
			case STRUCT_SET_STRING:
				pb.structSetString(quad);
				break;
			case CONCAT_STRING:
				pb.concatString(quad);
				break;
			case DECLARE_STRUCT:
				pb.declareStruct(quad);
				break;
			default:
				break;
			}
		}

		final Program pr = pb.build();
		classfile.addInstructionsToMethod("main", pr.toInstructionsArray());
	}

	/**
	 * <h1>translateStructIntoConstructorCode</h1>
	 * 
	 * This method translates the initialization code of a struct classfile into
	 * bytecode. Struct classfiles contain nothing but a default constructor,
	 * which initializes all fields of that struct.
	 */
	private static void translateStructTacIntoConstructorCode(
			final Classfile structClassfile,
			final List<Quadruple> constructorTAC) {
		final StructBuilder pb = new StructBuilder(structClassfile, "<init>");

		for (final Quadruple quad : constructorTAC) {
			switch (quad.getOperator()) {
			case DECLARE_ARRAY:
				pb.fieldDeclareArray(quad);
				break;
			case DECLARE_STRUCT:
				pb.fieldDeclareStruct(quad);
				break;
			case DECLARE_BOOLEAN:
				pb.fieldDeclareBoolean(quad);
				break;
			case DECLARE_STRING:
				pb.fieldDeclareString(quad);
				break;
			case DECLARE_DOUBLE:
				pb.fieldDeclareDouble(quad);
				break;
			case DECLARE_LONG:
				pb.fieldDeclareLong(quad);
				break;
			default:
				break;
			}
		}

		final Program pr = pb.build();
		final Instruction InstrAload = new Instruction(Mnemonic.ALOAD_0);
		final Instruction InstrInvokespecial = new Instruction(
				Mnemonic.INVOKESPECIAL, structClassfile.getConstructorIndex());
		structClassfile.addInstructionsToMethod("<init>", InstrAload,
				InstrInvokespecial);
		structClassfile.addInstructionsToMethod("<init>",
				pr.toInstructionsArray());
	}
}
