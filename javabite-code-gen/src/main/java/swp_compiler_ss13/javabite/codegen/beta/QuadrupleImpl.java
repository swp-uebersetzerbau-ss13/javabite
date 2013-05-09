package swp_compiler_ss13.javabite.codegen.beta;


import swp_compiler_ss13.common.backend.Quadruple;

/**
* Implementation of the quadruple interface. Instances of this class are
* immutable.
*
* @author "Frank Zechert"
* @version 1
*/
public class QuadrupleImpl implements Quadruple {

/**
* The operator of the quadruple
*/
private final Operator operator;

/**
* The first argument of the quadruple
*/
private final String arg1;

/**
* The second argument of the quadruple
*/
private final String arg2;

/**
* The result address of the quadruple
*/
private final String res;

/**
* Create a new immutable quadruple object
*
* @param operator
* The operator
* @param arg1
* Address of argument 1
* @param arg2
* Address of argument 2
* @param res
* Address of the result
*/
public QuadrupleImpl(Operator operator, String arg1, String arg2, String res) {
this.operator = operator;
this.arg1 = arg1;
this.arg2 = arg2;
this.res = res;
}

@Override
public Operator getOperator() {
return this.operator;
}

@Override
public String getArgument1() {
return this.arg1;
}

@Override
public String getArgument2() {
return this.arg2;
}

@Override
public String getResult() {
return this.res;
}

}