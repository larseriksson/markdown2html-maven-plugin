package se.aftonbladet.plugin.md2html;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.rjeschke.txtmark.Processor;
import com.google.common.base.Objects;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Goal which touches a timestamp file.
 *
 * @goal convert
 * @description Convert content of a markdown file to a html file
 * @phase generate-resources
 */
public class MarkdownConverterPlugin
    extends AbstractMojo
{
	/**
	 *
	 */
	private HandlebarProcessor handlebarProcessor = null;
	private final NullProcessor defaultProcessor = new NullProcessor();

	/**
	 * @parameter
	 */
	private File templateFile;

	/**
	 * @parameter default-value="content"
	 */
	private String templateVariableName;

	public File getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}

	public String getTemplateVariableName() {
		return templateVariableName;
	}

	public void setTemplateVariableName(String templateVariableName) {
		this.templateVariableName = templateVariableName;
	}

	/**
	 * If existing target will be overwritten
	 * @parameter default-value="true"
	 *
	 */
	private boolean overwriteExisting;
	/**
	 * Location of input file
	 * @parameter expression="${project.basedir}/src/main/resources/README.md"
	 * @required
	 */
	private File inputFile;
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File targetFileOrDirectory;

	public boolean isOverwriteExisting() {
		return overwriteExisting;
	}

	public void setOverwriteExisting(boolean overwriteExisting) {
		this.overwriteExisting = overwriteExisting;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public File getTargetFileOrDirectory() {
		return targetFileOrDirectory;
	}

	public void setTargetFileOrDirectory(File targetFileOrDirectory) {
		this.targetFileOrDirectory = targetFileOrDirectory;
	}


	private void setup() throws MojoExecutionException {
		try {
			if(templateFile != null && templateVariableName != null) {
				handlebarProcessor = new HandlebarProcessor();
				handlebarProcessor.setTemplate(templateFile);
				handlebarProcessor.setTemplateVariableName(templateVariableName);
			} else {
				handlebarProcessor = null;
			}
		} catch (IOException e) {
			throw new MojoExecutionException("failure during initialization: "+e.getMessage(), e);
		}
	}

	public void execute()
        throws MojoExecutionException
    {
	    setup();

	    Path input = Paths.get(inputFile.getAbsolutePath());
	    Path target = Paths.get(targetFileOrDirectory.getAbsolutePath());

	    boolean targetIsDirectory = Files.isDirectory(target);

	    Path outputPath;
	    if(targetIsDirectory) {
		    String targetFileName = targetIsDirectory ? input.getFileName().toString().replaceAll("\\.md$", ".html") : target.getFileName().toString();
			outputPath = target.resolve(targetFileName);
	    } else {
		    outputPath = target;
	    }

		String output;
	    try {
		   String htmlOutput = Processor.process(input.toFile(), true);
		   output = Objects.firstNonNull(handlebarProcessor, defaultProcessor).process(htmlOutput, getPluginContext());

	    } catch (IOException e) {
		    throw new MojoExecutionException("error while processing md file", e);
	    }

	    if(Files.exists(outputPath)) {
			if(!overwriteExisting) {
				String reason = String.format("could not write \"%s\", file already exists", outputPath);
				throw new MojoExecutionException(reason);
			}

			if(Files.isDirectory(outputPath)) {
				String reason = String.format("could not delete \"%s\". Target is a directory", outputPath);
				throw new MojoExecutionException(reason);
			}

			try {
				Files.delete(outputPath);
			} catch (IOException e) {
				String reason = String.format("Error while deleting existing resource \"%s\"", outputPath);
				throw new MojoExecutionException(reason, e);
			}
		}


	    try {
		    if(!Files.exists(outputPath.getParent())) {
			    Files.createDirectories(outputPath.getParent());
		    }

		    Files.createFile(outputPath);
		    Files.write(outputPath, output.getBytes(Charset.forName("UTF-8")));
	    } catch (IOException e) {
		    throw new MojoExecutionException("error while writing resource", e);

	    }
    }
}
