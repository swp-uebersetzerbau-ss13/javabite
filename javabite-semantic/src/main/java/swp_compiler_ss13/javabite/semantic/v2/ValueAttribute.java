package swp_compiler_ss13.javabite.semantic.v2;

public class ValueAttribute{
	Long val_int;
	Double val_float;
	Boolean val_bool;
	
	
	public ValueAttribute(boolean b){
		this.val_bool=b;
	}
	public ValueAttribute(long i) {
		this.val_int=i;
	}
	public ValueAttribute(double d) {
		this.val_float=d;
	}
	boolean isInt(){
		return val_int!=null;
	}
	boolean isFloat(){
		return val_float!=null;
	}
	long getIntVal(){
		return val_int;
	}
	double getDoubleVal(){
		return val_float;
	}
	boolean getBooleanVal(){
		return val_bool;
	}
	ValueAttribute and(ValueAttribute right){
		return new ValueAttribute(this.getBooleanVal() && right.getBooleanVal());
	}
	
	ValueAttribute or(ValueAttribute right){
		return new ValueAttribute(this.getBooleanVal() || right.getBooleanVal());
	}
	
	ValueAttribute add(ValueAttribute right){
		if (this.isFloat() || right.isFloat()){
			double ret;
			ret=this.isFloat()?this.getDoubleVal():this.getIntVal()+(right.isFloat()?right.getDoubleVal():right.getIntVal());
			return new ValueAttribute(ret);
		}
		else{
			long ret;
			ret=this.getIntVal()+right.getIntVal();
			return new ValueAttribute(ret);
		}
	}
	
	ValueAttribute sub(ValueAttribute right){
		if (this.isFloat() || right.isFloat()){
			double ret;
			ret=this.isFloat()?this.getDoubleVal():this.getIntVal()-(right.isFloat()?right.getDoubleVal():right.getIntVal());
			return new ValueAttribute(ret);
		}
		else{
			long ret;
			ret=this.getIntVal()-right.getIntVal();
			return new ValueAttribute(ret);
		}
	}
	ValueAttribute mul(ValueAttribute right){
		if (this.isFloat() || right.isFloat()){
			double ret;
			ret=this.isFloat()?this.getDoubleVal():this.getIntVal()*(right.isFloat()?right.getDoubleVal():right.getIntVal());
			return new ValueAttribute(ret);
		}
		else{
			long ret;
			ret=this.getIntVal()*right.getIntVal();
			return new ValueAttribute(ret);
		}
	}
	
	ValueAttribute div(ValueAttribute right){
		
		if (this.isFloat() || right.isFloat()){
			double ret;
			ret=this.isFloat()?this.getDoubleVal():this.getIntVal()/(right.isFloat()?right.getDoubleVal():right.getIntVal());
			return new ValueAttribute(ret);
		}
		else{
			long ret;
			ret=this.getIntVal()/right.getIntVal();
			return new ValueAttribute(ret);
		}
	}
	boolean isZero(){
		if (this.isFloat()){
			return val_float.doubleValue()==0;
		}
		else {
			return val_int.longValue()==0;
		}
	}
	
	Number getNumber(){
		if(this.isFloat()){
			return val_float;
		}
		else{
			return val_int;
		}
	}
}
