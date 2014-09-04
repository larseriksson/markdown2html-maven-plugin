package se.aftonbladet.plugin.md2html;

import java.io.IOException;
import java.util.Map;

/**
 * Created by lareri on 2014-09-04.
 */
public interface Processor {
	String process(String value, Map<String, Object> context) throws IOException;
}
