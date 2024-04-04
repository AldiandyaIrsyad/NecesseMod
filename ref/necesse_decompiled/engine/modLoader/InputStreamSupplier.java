package necesse.engine.modLoader;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface InputStreamSupplier {
   InputStream get() throws IOException;
}
