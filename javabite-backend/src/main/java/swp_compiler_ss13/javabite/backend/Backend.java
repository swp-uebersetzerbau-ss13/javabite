package swp_compiler_ss13.javabite.backend;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Backend {

	Map<String, InputStream> generateTargetCode(List<Quadruple> tac);

}