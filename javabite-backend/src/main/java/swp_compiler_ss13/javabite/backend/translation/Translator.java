package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.*;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;
import swp_compiler_ss13.javabite.backend.utils.QuadrupleUtils;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.isConstant;
import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.removeConstantSign;

/**
 * <h1>Translator</h1>
 * <p>
 * This class and its methods are supposed to be used to translate the tac into
 * a list of classfiles containing the target bytecode.
 * </p>
 * 
 * @author Marco
 * @since 27.04.2013
 */
public class Translator {

	public static final String FILE_EXTENSION_CLASS = ".class";

	public static final MethodSignature MAIN_METHOD = new MethodSignature(
			"main", (String) null, void.class, String[].class);

	public static final ClassfileUtils.ClassSignature OBJECT_CLASS = new ClassfileUtils.ClassSignature(
			Object.class);

	Collection<Classfile> classfiles;

	public Translator() {
		classfiles = new ArrayList<>();
	}

	/**
	 * BOOLEAN <h1>generateNewFile</h1>
	 * <p>
	 * This method generates a new Classfile object using its parameter name and
	 * appends it to the translator's classfile list.
	 * </p>
	 * 
	 * @since 27.04.2013
	 * 
	 * @param name
	 *            string describing the classfile's name
	 * @param thisClassNameEIF
	 *            string describing this class name of the class described in
	 *            this classfile encoded in internal form
	 * @param superClassNameEIF
	 *            string describing the superclass' class name of the class
	 *            described in this classfile encoded in internal form
	 * @param isStruct
	 *            determines, whether a main classfile or a struct classfile is
	 *            supposed to be generated
	 * @return instance of a class implementing the Classfile interface
	 */
	private Classfile generateClassfile(final String name,
			final String thisClassNameEIF, final String superClassNameEIF,
			final boolean isStruct, final ClassfileAccessFlag... accessFlags) {
		final Classfile classfile = new Classfile(name, thisClassNameEIF,
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
	 */
	public Collection<Classfile> translate(final String mainClassName,
			List<Quadruple> tac) {

		// some initialization
		final String classFileName = mainClassName + FILE_EXTENSION_CLASS;

		// create a new (main)classfile / classfile with main method
		final Classfile mainClassfile = generateClassfile(classFileName,
				mainClassName, OBJECT_CLASS.getClassNameAsContainer(), false,
				ClassfileAccessFlag.ACC_PUBLIC, ClassfileAccessFlag.ACC_SUPER);

		// add main method to this (main)classfile
		mainClassfile.addMethodToMethodArea(MAIN_METHOD.methodName,
				MAIN_METHOD.methodDescriptor, MethodAccessFlag.ACC_PUBLIC,
				MethodAccessFlag.ACC_STATIC);

		// parse tac for struct declarations and create classfiles for them
		// TODO einkommentieren, wenn es geht
		generateClassfilesForStructsInTAC(mainClassfile, tac, mainClassName);

		// translate tac/program into the main method's code
		if (tac != null) {
			tac = addVariablesToLocalVariableSpace(mainClassfile,
					MAIN_METHOD.methodName, tac);
			addConstantsToConstantPool(mainClassfile, tac);
			extractInstructionsFromOperations(mainClassfile,
					MAIN_METHOD.methodName, tac);
		}

		return classfiles;
	}

	/**
	 * <h1>generateClassfilesForStructsInTAC</h1>
	 * 
	 * TODO implementation and javadoc - search for structs in tac and build new
	 * classes use new translate method or change translate method to
	 * translate(...,..., false)
	 * 
	 * @since 24.06.2013
	 */
	private void generateClassfilesForStructsInTAC(
			final Classfile mainClassfile, final List<Quadruple> tac,
			final String basicClassName) {
		int listIndex = -1;

		// search for struct declarations
		for (ListIterator<Quadruple> tacIter = tac.listIterator(); tacIter
				.hasNext();) {
			listIndex++;

			// get the quadruple parts
			Quadruple quad = tacIter.next();
			final Quadruple.Operator operator = quad.getOperator();
			final String structName = quad.getResult();

			if (operator == Operator.DECLARE_ARRAY) {
				do {
					listIndex++;
				} while ((quad = tacIter.next()).getOperator() == Operator.DECLARE_ARRAY);
			}

			/*
			 * if the operator is an DECLARE_STRUCT-operator, a new classfile
			 * has to be generated
			 */
			if (quad.getOperator() == Operator.DECLARE_STRUCT) {

				// generate appropriate classfile
				// TODO normalize struct name
				final String className = basicClassName + "_" + structName;

				// register struct as toplevel struct for struct resolution
				mainClassfile.addToplevelStruct(structName);
				// argument2 is misused to store the actual class name of the
				// struct
				tacIter.set(QuadrupleUtils.copyQuadruple(quad, null, null,
						className, null));

				// temp variable for found structs' tac
				List<Quadruple> structTAC;

				// get struct's tac
				final long memberVarsCount = Long
						.parseLong(removeConstantSign(quad.getArgument1()));
				structTAC = getStructsTac(tacIter, memberVarsCount);
				final int structEnd = listIndex + structTAC.size();

				translateStructIntoClassfile(mainClassfile, structTAC,
						className);

				/*
				 * delete struct tac from tac, structStart + 1 to keep the
				 * struct declaration
				 */
				tac.subList(listIndex + 1, structEnd + 1).clear();
				tacIter = tac.listIterator(listIndex + 1);
			}
		}
	}

	/**
	 * <h1>getStructsTac</h1>
	 * 
	 * getStructsTac TODO javadoc
	 * 
	 * @since 24.06.2013
	 */
	private static List<Quadruple> getStructsTac(
			final ListIterator<Quadruple> tacIter, final long memberVarsCount) {

		// temp variable for found structs' tac
		final List<Quadruple> structTAC = new ArrayList<>();

		// get tac
		for (int i = 0; i < memberVarsCount; i++) {
			Quadruple structQuad = tacIter.next();
			final Operator operator = structQuad.getOperator();

			switch (operator) {
			case DECLARE_ARRAY:
				structTAC.add(structQuad);
				structQuad = tacIter.next();
				while (structQuad.getOperator() == Operator.DECLARE_ARRAY) {
					structTAC.add(structQuad);
					structQuad = tacIter.next();
				}
				if (structQuad.getOperator() == Operator.DECLARE_STRUCT) {
					structTAC.add(structQuad);
					// get struct's tac
					final long structMemberVarsCount = Long
							.parseLong(removeConstantSign(structQuad
									.getArgument1()));
					structTAC.addAll(getStructsTac(tacIter,
							structMemberVarsCount));
				} else {
					structTAC.add(structQuad);
				}
				break;
			case DECLARE_STRUCT:
				structTAC.add(structQuad);
				// get struct's tac
				final long structMemberVarsCount = Long
						.parseLong(removeConstantSign(structQuad.getArgument1()));
				structTAC.addAll(getStructsTac(tacIter, structMemberVarsCount));
				break;
			default:
				structTAC.add(structQuad);
			}
		}

		return structTAC;
	}

	/**
	 * <h1>translateStructIntoClassfile</h1>
	 * 
	 * translateStructIntoClassfile TODO javadoc
	 * 
	 * @since 24.06.2013
	 */
	private void translateStructIntoClassfile(final Classfile mainClassfile,
			final List<Quadruple> structTac, final String className) {

		// tac list for constructor code generation
		final List<Quadruple> constructorTAC = new ArrayList<>();

		// generate classfile
		final String classFileName = className + FILE_EXTENSION_CLASS;

		// create a new (main)classfile / classfile with main method
		final Classfile structClassfile = generateClassfile(classFileName,
				className, OBJECT_CLASS.getClassNameAsContainer(), true,
				ClassfileAccessFlag.ACC_PUBLIC, ClassfileAccessFlag.ACC_SUPER);

		// add constants to constant pool
		addConstantsToConstantPool(structClassfile, structTac);

		/*
		 * generate field info structures in field are, field constants in
		 * constant pool and new classfiles, if necessary
		 */
		if (structTac != null) {
			for (final ListIterator<Quadruple> tacIter = structTac
					.listIterator(); tacIter.hasNext();) {

				final Quadruple quad = tacIter.next();
				final String name = quad.getResult();
				final Operator op = quad.getOperator();
				final String arg1 = quad.getArgument1();

				String descriptor;

				switch (op) {
				case DECLARE_ARRAY:
					// temp variable for found array's tac
					final List<Quadruple> arrayTAC = new ArrayList<>();
					arrayTAC.add(quad);
					constructorTAC.add(quad);
					int dimensions = 1;

					// get rest of arrays tac
					Quadruple arrayQuad = tacIter.next();
					while (arrayQuad.getOperator() == Operator.DECLARE_ARRAY) {
						arrayTAC.add(arrayQuad);
						constructorTAC.add(arrayQuad);
						arrayQuad = tacIter.next();
						dimensions++;
					}

					if (arrayQuad.getOperator() == Operator.DECLARE_STRUCT) {
						arrayTAC.add(arrayQuad);
						constructorTAC.add(arrayQuad);

						List<Quadruple> structTACwithoutFirstDecl;

						// get struct's tac
						final long structMemberVarsCount = Long
								.parseLong(removeConstantSign(arrayQuad
										.getArgument1()));

						structTACwithoutFirstDecl = getStructsTac(tacIter,
								structMemberVarsCount);
						arrayTAC.addAll(structTACwithoutFirstDecl);

						// generate classfile for structTACwithoutFirstDecl
						final String structClassName = className + "_" + name;
						translateStructIntoClassfile(mainClassfile,
								structTACwithoutFirstDecl, structClassName);
					} else {
						arrayTAC.add(arrayQuad);
						constructorTAC.add(arrayQuad);
					}

					// getDescriptor using arrayTAC
					descriptor = ClassfileUtils.typeByQuadruples(arrayTAC);
					structClassfile.addFieldToFieldArea(name, descriptor,
							FieldAccessFlag.ACC_PUBLIC);

					// register array of current struct with its dimensions
					mainClassfile.addStructMemberArray(className + "_" + name,
							descriptor);

					/*
					 * generate field reference info structure in the constant
					 * pool, which will be used in the constructor for
					 * initialization
					 */
					structClassfile.addFieldrefConstantToConstantPool(name,
							descriptor, className);
					break;
				case DECLARE_STRUCT:
					// temp variables for found struct's tac
					// TODO remoav
					final List<Quadruple> structTAC = new ArrayList<>();

					structTAC.add(quad);
					constructorTAC.add(quad);

					// get struct's tac
					final long structMemberVarsCount = Long
							.parseLong(removeConstantSign(quad.getArgument1()));
					final List<Quadruple> structTACwithoutFirstDecl = getStructsTac(
							tacIter, structMemberVarsCount);
					structTAC.addAll(structTACwithoutFirstDecl);

					// generate classfile for structTACwithoutFirstDecl
					final String structClassName = className + "_" + name;
					translateStructIntoClassfile(mainClassfile,
							structTACwithoutFirstDecl, structClassName);

					// register struct as substruct
					mainClassfile.addSublevelStruct(structClassName);

					// getDescriptor using structTAC
					// TODO delete
					// descriptor = ClassfileUtils.typeByQuadruples(structTAC);
					descriptor = "L" + structClassName + ";";
					structClassfile.addFieldToFieldArea(name, descriptor,
							FieldAccessFlag.ACC_PUBLIC);

					/*
					 * generate field reference info structure in the constant
					 * pool, which will be used in the constructor for
					 * initialization
					 */
					structClassfile.addFieldrefConstantToConstantPool(name,
							descriptor, className);
					break;
				default:
					// getDescriptor using quad
					descriptor = ClassfileUtils.typeByQuadruples(quad);
					// add field to field area
					structClassfile.addFieldToFieldArea(name, descriptor,
							FieldAccessFlag.ACC_PUBLIC);

					/*
					 * without initial value, initialization in constructor will
					 * not be necessary
					 */
					if (!ConstantUtils.isIgnoreParam(arg1)) {
						// add to constructor tac
						constructorTAC.add(quad);

						/*
						 * generate field reference info structure in the
						 * constant pool, which will be used in the constructor
						 * for initialization
						 */
						structClassfile.addFieldrefConstantToConstantPool(name,
								descriptor, className);
					}
				}
			}
		}

		// set constructor
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
			 * examined separately
			 */
			if (operator.name().startsWith("ARRAY_SET")) {
				// argument 2 is a long constant
				if (isConstant(arg2)) {
					classFile.addLongConstantToConstantPool(Long
							.parseLong(removeConstantSign(arg2)));
				}
				// result can be a constant of different types
				if (isConstant(result)) {
					switch (operator) {
					case ARRAY_SET_LONG:
						classFile.addLongConstantToConstantPool(Long
								.parseLong(removeConstantSign(result)));
						break;
					case ARRAY_SET_DOUBLE:
						final String resMod = removeConstantSign(result);
						classFile.addDoubleConstantToConstantPool(
								Double.parseDouble(resMod), resMod);
						break;
					case ARRAY_SET_STRING:
						classFile
								.addStringConstantToConstantPool(removeConstantSign(result));
						break;
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
					classFile.addLongConstantToConstantPool(Long
							.parseLong(removeConstantSign(arg1)));
				}
				if (isConstant(arg2)) {
					classFile.addLongConstantToConstantPool(Long
							.parseLong(removeConstantSign(arg2)));
				}
				break;

			case DOUBLE:
				if (isConstant(arg1)) {
					final String arg1Mod = removeConstantSign(arg1);
					classFile.addDoubleConstantToConstantPool(
							Double.parseDouble(arg1Mod), arg1Mod);
				}
				if (isConstant(arg2)) {
					final String arg2Mod = removeConstantSign(arg2);
					classFile.addDoubleConstantToConstantPool(
							Double.parseDouble(arg2Mod), arg2Mod);
				}
				break;

			case STRING:
				if (type == ConstantPoolType.STRING && isConstant(arg1)) {
					classFile
							.addStringConstantToConstantPool(removeConstantSign(arg1));
				}
				if (type == ConstantPoolType.STRING && isConstant(arg2)) {
					classFile
							.addStringConstantToConstantPool(removeConstantSign(arg2));
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
	 * classfile's provided method's code attribute for all variable
	 * declarations and will convert them into assignments, if they have an
	 * initial value and will delete them if not.
	 * </p>
	 * 
	 * @since 29.04.2013
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
				file.addVariableToMethodsCode(methodName, result,
						LocalVariableType.STRING);
				break;
			case DECLARE_LONG:
				op = Operator.ASSIGN_LONG;
				defValue = ConstantUtils.DEFAULT_VALUE_LONG;
				file.addVariableToMethodsCode(methodName, result,
						LocalVariableType.LONG);
				break;
			case DECLARE_DOUBLE:
				op = Operator.ASSIGN_DOUBLE;
				defValue = ConstantUtils.DEFAULT_VALUE_DOUBLE;
				file.addVariableToMethodsCode(methodName, result,
						ClassfileUtils.LocalVariableType.DOUBLE);
				break;
			case DECLARE_BOOLEAN:
				op = Operator.ASSIGN_BOOLEAN;
				defValue = ConstantUtils.DEFAULT_VALUE_BOOLEAN;
				file.addVariableToMethodsCode(methodName, result,
						LocalVariableType.BOOLEAN);
				break;
			case DECLARE_REFERENCE:
				file.addVariableToMethodsCode(methodName, result,
						ClassfileUtils.LocalVariableType.AREF);
				tacIter.remove();
				continue;
			case DECLARE_ARRAY:
				file.addVariableToMethodsCode(methodName, result,
						ClassfileUtils.LocalVariableType.AREF);
				/*
				 * during array declaration, skip further checks until a basic
				 * type is declared/ the array declaration is finished.
				 */
				while (tacIter.next().getOperator() == Operator.DECLARE_ARRAY) {

				}
				continue;
			case DECLARE_STRUCT:
				// do not delete declaration tac
				file.addVariableToMethodsCode(methodName, result,
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
		classfile
				.addInstructionsToMethodsCode("main", pr.toInstructionsArray());
	}

	/**
	 * <h1>translateStructIntoConstructorCode</h1> TODO EIke
	 */
	private static void translateStructTacIntoConstructorCode(
			final Classfile structClassfile,
			final List<Quadruple> constructorTAC) {
		final StructBuilder pb = new StructBuilder(structClassfile, "<init>");

		for (final Quadruple quad : constructorTAC) {
			switch (quad.getOperator()) {
			case DECLARE_ARRAY:
				pb.declareArray(quad);
				break;
			case DECLARE_STRUCT:
				pb.declareStruct(quad);
				break;
			case DECLARE_BOOLEAN:
			case ASSIGN_BOOLEAN:
				pb.declareBoolean(quad);
				break;
			case DECLARE_STRING:
			case ASSIGN_STRING:
				pb.declareString(quad);
				break;
			case DECLARE_DOUBLE:
			case ASSIGN_DOUBLE:
				pb.declareDouble(quad);
				break;
			case DECLARE_LONG:
				pb.declareLong(quad);
				break;
			default:
				break;
			}
		}

		final Program pr = pb.build();
		final Instruction InstrAload = new Instruction(Mnemonic.ALOAD_0);
		final Instruction InstrInvokespecial = new Instruction(
				Mnemonic.INVOKESPECIAL, structClassfile.getConstructorIndex());
		structClassfile.addInstructionToMethodsCode("<init>", InstrAload);
		structClassfile.addInstructionToMethodsCode("<init>",
				InstrInvokespecial);
		structClassfile.addInstructionsToMethodsCode("<init>",
				pr.toInstructionsArray());
	}
}
