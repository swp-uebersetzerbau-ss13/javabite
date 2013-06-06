package swp_compiler_ss13.javabite.backend;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.byteArrayToHexString;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.intToByteArray;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortToByteArray;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.ArrayType;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.InfoTag;
import swp_compiler_ss13.javabite.backend.translation.Translator;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

/**
 * <h1>Program</h1>
 * <p>
 * Representation of a program instruction block. Contains a list of operations
 * which consist of instructions.
 * </p>
 * 
 * @author eike
 * @since 02.05.2013 23:41:39
 * 
 */
public class Program {

	private final List<Operation> operations;

	private Program(final List<Operation> operations) {
		this.operations = operations;
	}

	/**
	 * Returns this programs operations
	 * 
	 * @return the operations
	 */
	public List<Operation> toOperationsList() {
		return operations;
	}

	/**
	 * Returns this programs instructions.
	 * 
	 * @return the instructions
	 */
	public Instruction[] toInstructionsArray() {
		int icount = 0;
		for (final Operation op : operations) {
			icount += op.getInstructionCount();
		}
		final Instruction[] instructions = new Instruction[icount];
		int currIndex = 0;
		if (operations != null) {
			for (final Operation op : operations) {

				System.arraycopy(op.getInstructions(), 0, instructions,
						currIndex, op.getInstructionCount());
				currIndex += op.getInstructionCount();
			}
		}
		return instructions;
	}

	/**
	 * Returns this programs bytes
	 * 
	 * @return the byte array
	 */
	public byte[] toByteArray() {
		final ByteBuffer bb = ByteBuffer.allocate(getByteCount());
		if (operations != null) {
			for (final Operation operation : operations) {
				bb.put(operation.toByteArray());
			}
		}
		return bb.array();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if (operations != null) {
			for (final Operation operation : operations) {
				sb.append(operation.toString());
			}
		}
		return sb.toString();
	}

	/**
	 * Returns this programs hex string
	 * 
	 * @return the hex string
	 */
	public String toHexString() {
		return byteArrayToHexString(toByteArray());
	}

	/**
	 * Returns this programs operations count
	 * 
	 * @return the operator count
	 */
	public int getOperationsCount() {
		return operations.size();
	}

	/**
	 * Returns this programs byte count
	 * 
	 * @return the byte count
	 */
	public int getByteCount() {
		int count = 0;
		if (operations != null) {
			for (final Operation operation : operations) {
				count += operation.getByteCount();
			}
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (operations == null ? 0 : operations.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Program))
			return false;
		final Program other = (Program) obj;
		if (operations == null) {
			if (other.operations != null)
				return false;
		} else if (!operations.equals(other.operations))
			return false;
		return true;
	}

	/**
	 * <h1>Builder</h1>
	 * <p>
	 * This class provides a builder pattern implementation for the program
	 * class.
	 * </p>
	 * 
	 * @author eike
	 * @since May 18, 2013 12:29:51 AM
	 */
	public static class Builder {

		private static final Pattern P_CONST_SIGN = Pattern.compile("#.*?");

		// the list of operations of this program
		private final List<Operation> operations;
		// the classfile instance of this program
		private final IClassfile classfile;
		// the method name of this program
		private final String methodName;
		// saves all jump targets (labels) for jump instruction creation
		private final Map<String, Instruction> jumpTargets;
		// saves all jump instructions for later offset calculation
		private final List<JumpInstruction> jumpInstructions;
		// sizes of last array declaration, in reverse order on the stack
		private final Stack<String> arrayDimensions;
		// label name of last label
		private final Stack<String> labelNames;
		// determines, whether the System exit method has already been added/
		// a return statement is present in the tac
		private boolean returnFlag;
		// indicates last instruction was a label, denoting a jump location
		private boolean labelFlag;
		// index of methodref info of system exit method in constant pool
		private short systemExitIndex;
		// index of fieldref info of system out in constant pool
		private short systemOutIndex;
		// name of last array seen
		private String arrayName;

		public Builder(final IClassfile classfile, final String methodName) {
			operations = new ArrayList<>();
			jumpTargets = new HashMap<>();
			jumpInstructions = new ArrayList<>();
			arrayDimensions = new Stack<>();
			labelNames = new Stack<>();
			this.classfile = classfile;
			this.methodName = methodName;
			returnFlag = false;
			systemExitIndex = 0;
			systemOutIndex = 0;
		}

		private Builder add(final Operation operation) {
			operations.add(operation);
			if (labelFlag) {
				labelFlag = false;
				while (!labelNames.isEmpty()) {
					jumpTargets.put(labelNames.pop(),
							operation.getInstruction(0));
				}
			}
			return this;
		}

		/**
		 * Builds the current program instance and returns it.
		 * 
		 * @return the program instance built by this builder instance
		 */
		public Program build() {
			// check, whether there is a return instruction in the end
			// if not, set it
			if (!returnFlag) {
				addReturnOp();
			}

			// calculate offsets for jumping
			int currentOffset = 0;
			for (final Operation op : operations) {
				for (final Instruction in : op.getInstructions()) {
					in.setOffset(currentOffset);
					currentOffset += in.getByteCount();
				}
			}

			// caluclate jump offset for every jump, set as argument of jump
			for (final JumpInstruction in : jumpInstructions) {
				Instruction target = in.getTargetInstruction();
				if (target == null) {
					target = getJumpTarget(in.getTargetLabel());
				}
				assert target != null;
				final int offset = target.getOffset() - in.getOffset();
				if (offset > Short.MAX_VALUE) {
					in.setMnemonic(Mnemonic.GOTO_W);
					in.setArguments(intToByteArray(offset));
				} else {
					in.setArguments(shortToByteArray((short) offset));
				}
			}

			return new Program(operations);
		}

		private boolean isBoolean(String s) {
			s = s.trim();
			return s.equals(Translator.CONST_TRUE)
					|| s.equals(Translator.CONST_FALSE);
		}

		private boolean isConstant(final String s) {
			return s.startsWith(Translator.SYM_CONST);
		}

		private String removeConstantSign(final String s) {
			final Matcher m = P_CONST_SIGN.matcher(s);
			return m.replaceAll("");
		}

		private Instruction getJumpTarget(final String s) {
			return jumpTargets.get(s);
		}

		// INSTRUCTION CREATORS ------------------------------------------------

		/**
		 * Creates a load instruction, that can be added to a program flow. This
		 * operation can distinguish between constants and variables, but needs
		 * information on the size of the variable to be loaded.
		 * 
		 * @param arg1
		 *            argument containing constant or variable name
		 * @param constType
		 *            type of variable/constant to load
		 * @param varLoadOp
		 *            load operation to execute if arg1 is a variable
		 */
		private Instruction loadOp(final String arg1, final InfoTag constType,
				final Mnemonic varLoadOp) {
			if (isBoolean(arg1)) {
				if (isConstant(arg1)) {
					final short value = (short) (Translator.CONST_TRUE
							.equals(arg1.toUpperCase()) ? 1 : 0);
					return new Instruction(Mnemonic.ICONST(value),
							ByteUtils.shortToByteArray(value));
				} else {
					final byte index = classfile.getIndexOfVariableInMethod(
							methodName, arg1);
					return new Instruction(Mnemonic.ILOAD(index), index);
				}
			} else if (isConstant(arg1) && constType != null) {
				final short index = classfile.getIndexOfConstantInConstantPool(
						constType, removeConstantSign(arg1));
				assert index > 0;
				if (constType.isWide()) {
					return new Instruction(Mnemonic.LDC2_W,
							ByteUtils.shortToByteArray(index));
				} else if (index >= 256) {
					return new Instruction(Mnemonic.LDC_W,
							ByteUtils.shortToByteArray(index));
				} else {
					return new Instruction(Mnemonic.LDC, (byte) index);
				}
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(
						methodName, arg1);
				assert index > 0;
				return new Instruction(Mnemonic.getMnemonic(varLoadOp.name(),
						index), index);
			}
		}

		/**
		 * creates a store operation, which stores the value on the stack into a
		 * variable identified by the result-string.
		 * 
		 * @param result
		 *            name of variable to store value in
		 * @param storeOp
		 *            operation to use for storing value
		 * @return new instruction
		 */
		private Instruction storeOp(final String result, final Mnemonic storeOp) {
			final byte index = classfile.getIndexOfVariableInMethod(methodName,
					result);
			// return new Instruction(1, Mnemonic.getMnemonic(storeOp, index),
			// index);
			return new Instruction(Mnemonic.getMnemonic(storeOp.name(), index),
					index);
		}

		// GENERIC OPERATIONS --------------------------------------------------

		/**
		 * Assigns a number (constant or variable) to a variable. If needed, the
		 * number is converted into another format. This convertion is done by
		 * the passed convert operation, if not null.
		 * 
		 * @param q
		 *            quadruple of operation
		 * @param constType
		 *            type of constant (if is constant)
		 * @param loadOp
		 *            operation to perform for loading variables
		 * @param convertOp
		 *            operation to perform for converting the number
		 * @param storeOp
		 *            operation to perform to store the number
		 * @return this builders instance
		 */
		private Builder assign(final Quadruple q, final InfoTag constType,
				final Mnemonic loadOp, final Mnemonic convertOp,
				final Mnemonic storeOp) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(loadOp(q.getArgument1(), constType, loadOp));
			if (convertOp != null) {
				op.add(convertOp);
			}
			op.add(storeOp(q.getResult(), storeOp));
			return add(op.build());
		}

		/**
		 * loads two numbers, performes a caluclation on them and stores the
		 * result back into a variable.
		 * 
		 * @param q
		 *            quadruple of operation
		 * @param constType
		 *            type of constants (if any constants)
		 * @param loadOp
		 *            operation to perform for loading variables
		 * @param calcOp
		 *            operation to perform for calculation
		 * @param storeOp
		 *            operation to perform to store the result
		 * @return this builders instance
		 */
		private Builder calculateNumber(final Quadruple q,
				final InfoTag constType, final Mnemonic loadOp,
				final Mnemonic calcOp, final Mnemonic storeOp) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(loadOp(q.getArgument1(), constType, loadOp));
			op.add(loadOp(q.getArgument2(), constType, loadOp));
			op.add(calcOp);
			op.add(storeOp(q.getResult(), storeOp));
			return add(op.build());
		}

		/**
		 * compares two numbers and stores the result at range [-1,1] on the
		 * stack
		 * 
		 * Explanation: Because java bytecode number comparison produces numbers
		 * instead of boolean values, we have to use conditional jumps to assign
		 * boolean values to the desired variable. The default behavoir of this
		 * construct is:
		 * <ol>
		 * <li>compare numbers</li>
		 * <li>jump to false-branch if comparison-result is 0</li>
		 * <li>enter true-branch and load true</li>
		 * <li>jump over false-branch</li>
		 * <li>enter false branch and load false</li>
		 * <li>save result into variable.</li>
		 * </ol>
		 * 
		 * Because this behavior is static, we can use fixed jump offsets.
		 * 
		 * @param q
		 *            the quadruple of the comparation
		 * @param type
		 *            the data type of the numbers to compare. Long or Double
		 * @param loadOp
		 *            the load operation to perform for loading the numbers
		 * @param compareOp
		 *            the comparation operation to perform
		 * @param jumpOp
		 *            the jump operation to perform after comparation.
		 * @return this builder instance
		 */
		private Builder compareNumber(final Quadruple q, final InfoTag type,
				final Mnemonic loadOp, final Mnemonic compareOp,
				final Mnemonic jumpOp) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			// 1: load first variable/constant
			op.add(loadOp(q.getArgument1(), type, loadOp));
			// 2: load second variable/constant
			op.add(loadOp(q.getArgument2(), type, loadOp));
			// 3: execute comparison (pushes -1/0/1 to the stack)
			op.add(compareOp);
			// 4: execute a conditional jump with target line 7
			final Instruction loadFalse = new Instruction(Mnemonic.ICONST_0);
			final JumpInstruction jumpFalse = new JumpInstruction(jumpOp,
					loadFalse);
			op.add(jumpFalse);
			jumpInstructions.add(jumpFalse);
			// 5: load 1 (true) onto the stack
			op.add(Mnemonic.ICONST_1);
			// 6: execute an unconditional jump with target line 8
			final Instruction storeRes = storeOp(q.getResult(), Mnemonic.ISTORE);
			final JumpInstruction jumpStore = new JumpInstruction(
					Mnemonic.GOTO, storeRes);
			op.add(jumpStore);
			jumpInstructions.add(jumpStore);
			// 7: load 0 (false) onto the stack
			op.add(loadFalse);
			// 8: store result 1/0 into variable
			op.add(storeRes);
			return add(op.build());
		}

		/**
		 * Generic binary boolean operation. TAC supports AND and OR.
		 * 
		 * @param q
		 *            quadruple of operation
		 * @param mnemonic
		 *            mnemonic of binary boolean bytecode operation
		 * @return this builder instance
		 */
		private Builder booleanOp(final Quadruple q, final Mnemonic mnemonic) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(loadOp(q.getArgument1(), null, Mnemonic.ILOAD));
			if (!"!".equals(q.getArgument2())) {
				op.add(loadOp(q.getArgument2(), null, Mnemonic.ILOAD));
			}
			op.add(mnemonic);
			op.add(storeOp(q.getResult(), Mnemonic.ISTORE));
			return add(op.build());
		}

		/**
		 * Operation to print a constant value or the content of a variable of
		 * the types string, long or double. Loads the System.out object and
		 * calls the virtual method println
		 * 
		 * @param arg1
		 *            constant or variable name to be printed
		 * @param type
		 *            type of arg1
		 * @param varLoadOp
		 *            operation to use for loading arg1 as a variable
		 * @return this builder instance
		 */
		private Builder print(final String arg1, final InfoTag type,
				final String printMethodSignature, final Mnemonic varLoadOp) {
			final short printIndex = addPrintMethodToClassfile(printMethodSignature);
			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(Mnemonic.GETSTATIC,
					ByteUtils.shortToByteArray(systemOutIndex));
			op.add(loadOp(arg1, type, varLoadOp));
			op.add(Mnemonic.INVOKEVIRTUAL,
					ByteUtils.shortToByteArray(printIndex));
			return add(op.build());
		}

		/**
		 * TODO javadoc
		 * 
		 * @param dimensions
		 * @param type
		 * @return
		 */
		private short addMultiArraySignature(final byte dimensions,
				final ArrayType type) {
			final String classSignature = new String(new char[dimensions])
					.replace("\0", "[") + type.getClassName();
			return classfile.addClassConstantToConstantPool(classSignature);
		}

		/**
		 * TODO javadoc
		 * 
		 * @param type
		 *            datatype of array contents
		 * @return this builders instance
		 */
		private Builder arrayCreate(final ArrayType type) {
			assert !"!".equals(arrayName) && !arrayDimensions.isEmpty();
			final Operation.Builder op = Operation.Builder.newBuilder();
			final byte dimensions = (byte) arrayDimensions.size();

			// add all dimensions to stack for array creation
			while (!arrayDimensions.isEmpty()) {
				op.add(loadOp(arrayDimensions.pop(), InfoTag.LONG,
						Mnemonic.LLOAD));
				op.add(Mnemonic.L2I);
			}

			if (dimensions > 1) {
				// if more than 1 dimension, create a multi dimensional array
				// every multi dimensional array is an array of references
				final short classIndex = addMultiArraySignature(dimensions,
						type);
				final byte[] classIndexArray = shortToByteArray(classIndex);
				assert classIndexArray.length == 2;
				op.add(Mnemonic.MULTIANEWARRAY, classIndexArray[0],
						classIndexArray[1], dimensions);
			} else if (type.isPrimitive()) {
				// if single dimensional and primitive, create with type tag
				op.add(Mnemonic.NEWARRAY, type.getValue());
			} else {
				// if single dimensional and complex (object), create with
				// class reference
				final short classIndex = classfile
						.addClassConstantToConstantPool(type.getClassName());
				op.add(Mnemonic.ANEWARRAY, shortToByteArray(classIndex));
			}

			op.add(storeOp(arrayName, Mnemonic.ASTORE));
			return add(op.build());
		}

		/**
		 * Returns the value of an array at a specified index. Because the
		 * available type is by definition LONG, and the internal array index
		 * type of java is INT, every index specification must be transformed
		 * into INT, with L2I.
		 * 
		 * @param q
		 *            quadruple of operation
		 * @param loadOp
		 *            operation to perform for loading the value from the array
		 * @param storeOp
		 *            operation to store the retrieved array value
		 * @return
		 */
		private Builder arrayGet(final Quadruple q, final Mnemonic loadOp,
				final Mnemonic storeOp) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			final byte arrayIndex = classfile.getIndexOfVariableInMethod(
					methodName, q.getArgument1());
			op.add(Mnemonic.getMnemonic("ALOAD", arrayIndex), arrayIndex);
			op.add(loadOp(q.getArgument2(), InfoTag.LONG, Mnemonic.LLOAD));
			op.add(Mnemonic.L2I);
			op.add(loadOp);
			op.add(storeOp(q.getResult(), storeOp));
			return add(op.build());
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 * @param constType
		 * @param varLoadOp
		 * @param storeOp
		 * @return
		 */
		private Builder arraySet(final Quadruple q, final InfoTag constType,
				final Mnemonic varLoadOp, final Mnemonic storeOp) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			final byte arrayIndex = classfile.getIndexOfVariableInMethod(
					methodName, q.getArgument1());
			op.add(Mnemonic.getMnemonic("ALOAD", arrayIndex), arrayIndex);
			op.add(loadOp(q.getArgument2(), InfoTag.LONG, Mnemonic.LLOAD));
			op.add(Mnemonic.L2I);
			op.add(loadOp(q.getResult(), constType, varLoadOp));
			op.add(storeOp);
			return add(op.build());
		}

		// CONSTANT POOL HELPERS -----------------------------------------------

		/**
		 * <h1>addSystemExitMethodToClassfile</h1>
		 * <p>
		 * This method checks whether the return flag is already set and if not,
		 * it'll add the needed system exit data to the classfile's constant
		 * pool.
		 * </p>
		 * 
		 * @author Marco
		 * @since 13.05.2013
		 * 
		 * @return short index into the constant pool of the system exit's
		 *         methodref entry.
		 */
		private short addSystemExitMethodToClassfile() {
			if (!returnFlag) {
				returnFlag = true;
				// TODO externalize static strings
				systemExitIndex = classfile.addMethodrefConstantToConstantPool(
						"exit", "(I)V", "java/lang/System");
			}
			return systemExitIndex;
		}

		/**
		 * <h1>addPrintMethodToClassfile</h1>
		 * <p>
		 * This method checks whether the print index is already set and if not,
		 * it'll add the needed print data to the classfile's constant pool.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.05.2013
		 */
		private short addPrintMethodToClassfile(final String paramType) {
			// add system.out fieldref info to constant pool, if necessary
			if (systemOutIndex == 0) {
				// TODO externalize static strings
				systemOutIndex = classfile.addFieldrefConstantToConstantPool(
						"out", "Ljava/io/PrintStream;", "java/lang/System");
			}

			// add print methodref info to constant pool, if necessary
			// TODO externalize static strings
			return classfile.addMethodrefConstantToConstantPool("print", "("
					+ paramType + ")V", "java/io/PrintStream");
		}

		/**
		 * Adds a return operation to the program.
		 */
		private void addReturnOp() {
			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(Mnemonic.RETURN);
			add(op.build());
		}

		private void addNop() {
			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(Mnemonic.NOP);
			add(op.build());
		}

		// OPERATIONS ----------------------------------------------------------

		public Builder nop() {
			addNop();
			return this;
		}

		/*
		 * === M1 === FINISHED
		 */

		public Builder declareLong(final Quadruple q) {
			if ("!".equals(q.getResult())) {
				return arrayCreate(ArrayType.LONG);
			}

			return this;
		}

		public Builder declareDouble(final Quadruple q) {
			if ("!".equals(q.getResult())) {
				return arrayCreate(ArrayType.DOUBLE);
			}

			return this;
		}

		public Builder declareString(final Quadruple q) {
			if ("!".equals(q.getResult())) {
				return arrayCreate(ArrayType.STRING);
			}

			return this;
		}

		public Builder declareBoolean(final Quadruple q) {
			if ("!".equals(q.getResult())) {
				return arrayCreate(ArrayType.BOOLEAN);
			}

			return this;
		}

		/**
		 * TAC Operation: LONG_TO_DOUBLE
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>LONG_TO_DOUBLE</td>
		 * <td>source name</td>
		 * <td></td>
		 * <td>destination name</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder longToDouble(final Quadruple q) {
			return assign(q, InfoTag.LONG, Mnemonic.LLOAD, Mnemonic.L2D,
					Mnemonic.DSTORE);
		}

		/**
		 * TAC Operation: DOUBLE_TO_LONG
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>DOUBLE_TO_LONG</td>
		 * <td>source name</td>
		 * <td></td>
		 * <td>destination name</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder doubleToLong(final Quadruple q) {
			return assign(q, InfoTag.DOUBLE, Mnemonic.DLOAD, Mnemonic.D2L,
					Mnemonic.LSTORE);
		}

		/**
		 * TAC Operation: ASSIGN_LONG
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ASSIGN_LONG</td>
		 * <td>source</td>
		 * <td></td>
		 * <td>destination name</td>
		 * <td>source may be either an identifier or a Long constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder assignLong(final Quadruple q) {
			return assign(q, InfoTag.LONG, Mnemonic.LLOAD, null,
					Mnemonic.LSTORE);
		}

		/**
		 * TAC Operation: ASSIGN_DOUBLE
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ASSIGN_DOUBLE</td>
		 * <td>source</td>
		 * <td></td>
		 * <td>destination name</td>
		 * <td>source may be either an identifier or a Double constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder assignDouble(final Quadruple q) {
			return assign(q, InfoTag.DOUBLE, Mnemonic.DLOAD, null,
					Mnemonic.DSTORE);
		}

		/**
		 * TAC Operation: ASSIGN_STRING
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ASSIGN_STRING</td>
		 * <td>source</td>
		 * <td></td>
		 * <td>destination name</td>
		 * <td>source may be either an identifier or a String constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder assignString(final Quadruple q) {
			return assign(q, InfoTag.STRING, Mnemonic.ALOAD, null,
					Mnemonic.ASTORE);
		}

		/**
		 * TAC Operation: ASSIGN_BOOLEAN
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ASSIGN_BOOLEAN</td>
		 * <td>source</td>
		 * <td></td>
		 * <td>destination name</td>
		 * <td>source may be either an identifier or a Boolean constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder assignBoolean(final Quadruple q) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(loadOp(q.getArgument1(), null, Mnemonic.ILOAD));
			op.add(storeOp(q.getResult(), Mnemonic.ISTORE));
			return add(op.build());
		}

		/**
		 * TAC Operation: ADD_LONG
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ADD_LONG</td>
		 * <td>left hand</td>
		 * <td>right hand</td>
		 * <td>destination</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder addLong(final Quadruple q) {
			return calculateNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LADD, Mnemonic.LSTORE);
		}

		/**
		 * TAC Operation: ADD_DOUBLE
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ADD_DOUBLE</td>
		 * <td>left hand</td>
		 * <td>right hand</td>
		 * <td>destination</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder addDouble(final Quadruple q) {
			return calculateNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DADD, Mnemonic.DSTORE);
		}

		/**
		 * TAC Operation: SUB_LONG
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>SUB_LONG</td>
		 * <td>left hand</td>
		 * <td>right hand</td>
		 * <td>destination</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder subLong(final Quadruple q) {
			return calculateNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LSUB, Mnemonic.LSTORE);
		}

		/**
		 * TAC Operation: SUB_DOUBLE
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>SUB_DOUBLE</td>
		 * <td>left hand</td>
		 * <td>right hand</td>
		 * <td>destination</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder subDouble(final Quadruple q) {
			return calculateNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DSUB, Mnemonic.DSTORE);
		}

		/**
		 * TAC Operation: MUL_LONG
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>MUL_LONG</td>
		 * <td>left hand</td>
		 * <td>right hand</td>
		 * <td>destination</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder mulLong(final Quadruple q) {
			return calculateNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LMUL, Mnemonic.LSTORE);
		}

		/**
		 * TAC Operation: MUL_DOUBLE
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>MUL_DOUBLE</td>
		 * <td>left hand</td>
		 * <td>right hand</td>
		 * <td>destination</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder mulDouble(final Quadruple q) {
			return calculateNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DMUL, Mnemonic.DSTORE);
		}

		/**
		 * TAC Operation: DIV_LONG
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>DIV_LONG</td>
		 * <td>left hand</td>
		 * <td>right hand</td>
		 * <td>destination</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder divLong(final Quadruple q) {
			return calculateNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LDIV, Mnemonic.LSTORE);
		}

		/**
		 * TAC Operation: DIV_DOUBLE
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>DIV_DOUBLE</td>
		 * <td>left hand</td>
		 * <td>right hand</td>
		 * <td>destination</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder divDouble(final Quadruple q) {
			return calculateNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DDIV, Mnemonic.DSTORE);
		}

		/**
		 * TAC Operation: RETURN
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>RETURN</td>
		 * <td>return value</td>
		 * <td></td>
		 * <td></td>
		 * <td>return value may be an identifier or a constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder returnLong(final Quadruple q) {
			final short systemExitIndex = addSystemExitMethodToClassfile();

			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(loadOp(q.getArgument1(), InfoTag.LONG, Mnemonic.LLOAD));
			op.add(Mnemonic.L2I);
			op.add(Mnemonic.INVOKESTATIC,
					ByteUtils.shortToByteArray(systemExitIndex));
			op.add(Mnemonic.RETURN);
			return add(op.build());
		}

		/*
		 * === M2 === WORK IN PROGRESS
		 */

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>OR_BOOLEAN</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>destination := lhs or rhs</td>
		 * </tr>
		 * <tr>
		 * <td>AND_BOOLEAN</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>destination := lhs and rhs</td>
		 * </tr>
		 * </tbody>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder notBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.INEG);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>OR_BOOLEAN</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>destination := lhs or rhs</td>
		 * </tr>
		 * </tbody>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder orBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.IOR);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>AND_BOOLEAN</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>destination := lhs and rhs</td>
		 * </tr>
		 * </tbody>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder andBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.IAND);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_LONG_E</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs equals rhs ?</td>
		 * </tr>
		 * <tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareLongE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LCMP, Mnemonic.IFNE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_LONG_G</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs greater than rhs ?</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareLongG(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LCMP, Mnemonic.IFLE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_LONG_L</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs less than rhs ?</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareLongL(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LCMP, Mnemonic.IFGE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <tr>
		 * <td>COMPARE_LONG_GE</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs greater than or equals rhs ?</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareLongGE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LCMP, Mnemonic.IFLT);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_LONG_LE</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs less than or equals rhs ?</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareLongLE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, Mnemonic.LLOAD,
					Mnemonic.LCMP, Mnemonic.IFGT);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_DOUBLE_E</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs equals rhs ?</td>
		 * </tr>
		 * <tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareDoubleE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DCMPL, Mnemonic.IFNE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_DOUBLE_G</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs greater than rhs ?</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareDoubleG(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DCMPL, Mnemonic.IFLE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_DOUBLE_L</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs less than rhs ?</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareDoubleL(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DCMPG, Mnemonic.IFGE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_DOUBLE_GE</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs greater than or equals rhs ?</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareDoubleGE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DCMPL, Mnemonic.IFLT);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>COMPARE_DOUBLE_LE</td>
		 * <td>lhs</td>
		 * <td>rhs</td>
		 * <td>destination</td>
		 * <td>lhs less than or equals rhs ?</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder compareDoubleLE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, Mnemonic.DLOAD,
					Mnemonic.DCMPG, Mnemonic.IFGT);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>LABEL</td>
		 * <td>unique identifier</td>
		 * <td></td>
		 * <td></td>
		 * <td>is a NOP</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder label(final Quadruple q) {
			labelFlag = true;
			labelNames.push(q.getArgument1());
			return this;
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>BRANCH</td>
		 * <td>target</td>
		 * <td></td>
		 * <td></td>
		 * <td>unconditional branch</td>
		 * </tr>
		 * <tr>
		 * <td>BRANCH</td>
		 * <td>true target</td>
		 * <td>false target</td>
		 * <td>condition</td>
		 * <td>conditional branch, condition is a boolean variable</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder branch(final Quadruple q) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			if ("!".equals(q.getResult())) {
				// unconditional branch
				final JumpInstruction jumpOp = new JumpInstruction(
						Mnemonic.GOTO, q.getArgument1());
				op.add(jumpOp);
				jumpInstructions.add(jumpOp);
			} else {
				// conditional branch
				final JumpInstruction trueJump = new JumpInstruction(
						Mnemonic.IFNE, q.getArgument1());
				final JumpInstruction falseJump = new JumpInstruction(
						Mnemonic.GOTO, q.getArgument2());
				op.add(loadOp(q.getResult(), null, Mnemonic.ILOAD));
				op.add(trueJump);
				op.add(falseJump);
				jumpInstructions.add(trueJump);
				jumpInstructions.add(falseJump);
			}
			return add(op.build());
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>PRINT_BOOLEAN</td>
		 * <td>value</td>
		 * <td></td>
		 * <td></td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder printBoolean(final Quadruple q) {
			final short printIndex = addPrintMethodToClassfile("Z");
			final Operation.Builder op = Operation.Builder.newBuilder();
			op.add(Mnemonic.GETSTATIC,
					ByteUtils.shortToByteArray(systemOutIndex));
			op.add(loadOp(q.getArgument1(), null, Mnemonic.ILOAD));
			op.add(Mnemonic.INVOKEVIRTUAL,
					ByteUtils.shortToByteArray(printIndex));
			return add(op.build());
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>PRINT_LONG</td>
		 * <td>value</td>
		 * <td></td>
		 * <td></td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder printLong(final Quadruple q) {
			return print(q.getArgument1(), InfoTag.LONG, "J", Mnemonic.LLOAD);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>PRINT_DOUBLE</td>
		 * <td>value</td>
		 * <td></td>
		 * <td></td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder printDouble(final Quadruple q) {
			return print(q.getArgument1(), InfoTag.DOUBLE, "D", Mnemonic.DLOAD);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>PRINT_STRING</td>
		 * <td>value</td>
		 * <td></td>
		 * <td></td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder printString(final Quadruple q) {
			return print(q.getArgument1(), InfoTag.STRING,
					"Ljava/lang/String;", Mnemonic.ALOAD);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>DECLARE_ARRAY</td>
		 * <td>size</td>
		 * <td></td>
		 * <td>name</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder declareArray(final Quadruple q) {
			if (!"!".equals(q.getResult())) {
				arrayName = q.getResult();
			}
			arrayDimensions.push(q.getArgument1());
			return this;
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_GET_LONG</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>destination name</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arrayGetLong(final Quadruple q) {
			return arrayGet(q, Mnemonic.LALOAD, Mnemonic.LSTORE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_GET_DOUBLE</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>destination name</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arrayGetDouble(final Quadruple q) {
			return arrayGet(q, Mnemonic.DALOAD, Mnemonic.DSTORE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_GET_BOOLEAN</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>destination name</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arrayGetBoolean(final Quadruple q) {
			return arrayGet(q, Mnemonic.BALOAD, Mnemonic.ISTORE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_GET_STRING</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>destination name</td>
		 * <td></td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arrayGetString(final Quadruple q) {
			return arrayGet(q, Mnemonic.AALOAD, Mnemonic.ASTORE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_GET_REFERENCE</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>destination name</td>
		 * <td>destination will contain a reference to the array at the index</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arrayGetReference(final Quadruple q) {
			return arrayGet(q, Mnemonic.AALOAD, Mnemonic.ASTORE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_SET_LONG</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>source</td>
		 * <td>source may be either an identifier or a Long constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arraySetLong(final Quadruple q) {
			return arraySet(q, InfoTag.LONG, Mnemonic.LDC2_W, Mnemonic.LASTORE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_SET_DOUBLE</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>source</td>
		 * <td>source may be either an identifier or a Double constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arraySetDouble(final Quadruple q) {
			return arraySet(q, InfoTag.DOUBLE, Mnemonic.LDC2_W,
					Mnemonic.DASTORE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_SET_BOOLEAN</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>source</td>
		 * <td>source may be either an identifier or a Boolean constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arraySetBoolean(final Quadruple q) {
			final Operation.Builder op = Operation.Builder.newBuilder();
			final byte arrayIndex = classfile.getIndexOfVariableInMethod(
					methodName, q.getArgument1());
			op.add(Mnemonic.getMnemonic("ALOAD", arrayIndex), arrayIndex);
			op.add(loadOp(q.getArgument2(), InfoTag.LONG, Mnemonic.LLOAD));
			op.add(Mnemonic.L2I);
			op.add(loadOp(q.getResult(), null, Mnemonic.ILOAD));
			op.add(Mnemonic.BASTORE);
			return add(op.build());
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_SET_STRING</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>source</td>
		 * <td>source may be either an identifier or a String constant</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arraySetString(final Quadruple q) {
			return arraySet(q, InfoTag.STRING, Mnemonic.LDC, Mnemonic.AASTORE);
		}

		/**
		 * <table>
		 * <thead>
		 * <tr>
		 * <th>Operator</th>
		 * <th>Argument 1</th>
		 * <th>Argument 2</th>
		 * <th>Result</th>
		 * <th>Remarks</th>
		 * </tr>
		 * </thead> <tbody>
		 * <tr>
		 * <td>ARRAY_SET_ARRAY</td>
		 * <td>array name or reference</td>
		 * <td>element index</td>
		 * <td>source</td>
		 * <td>source may be an identifier for either an array or an array
		 * reference</td>
		 * </tr>
		 * </tbody>
		 * </table>
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public Builder arraySetArray(final Quadruple q) {
			return arraySet(q, null, Mnemonic.ALOAD, Mnemonic.AASTORE);
		}

		/*
		 * === M3 === WORK IN PROGRESS
		 */

	}

}
