cd bin
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\add.prog
java add
echo "---------"
echo "Running Program add. Result:"
echo $LastExitCode
echo "========="
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\simple_add.prog
java simple_add
echo "---------"
echo "Running Program add. Result:"
echo $LastExitCode
echo "========="
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\simple_mul.prog
java simple_mul
echo "---------"
echo "Running Program add. Result:"
echo $LastExitCode
echo "========="
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\paratheses.prog
java paratheses
echo "---------"
echo "Running Program add. Result:"
echo $LastExitCode
echo "========="
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\error_undef_return.prog
java error_undef_return
echo "---------"
echo "Running Program add. Result:"
echo $LastExitCode
echo "========="
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\error_double_decl.prog
echo "========="
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\error_invalid_ids.prog
echo "========="
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\error_multiple_minus_e_notation.prog
echo "========="
java -jar javabite-compiler-0.1.jar ..\common\examples\m1\error_multiple_pluses_in_exp.prog
echo "========="
cd ..
