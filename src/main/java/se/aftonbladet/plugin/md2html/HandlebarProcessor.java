package se.aftonbladet.plugin.md2html;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HandlebarProcessor implements Processor {
	private final Handlebars handlebar;

	private Template template;
	private String templateVariableName;

	public HandlebarProcessor() {
		this.handlebar = new Handlebars();

	}

	public void setTemplateVariableName(String templateVariableName) {
		this.templateVariableName = templateVariableName;
	}

	public void setTemplate(File file) throws IOException {
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			String content = CharStreams.toString(new BufferedReader(reader));
			template = handlebar.compileInline(content);
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
	}

	@Override
	public String process(String value, Map<String, Object> context) throws IOException {
		if(template == null) {
			throw new IllegalStateException("template not initialized");
		}

		HashMap<String,Object> extendedContext = new HashMap<String, Object>(context);
		extendedContext.put(templateVariableName, value);

		return template.apply(extendedContext);
	}
}
