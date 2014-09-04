# Maven Plugin for converting Mardown to HTML

Author: Lars Eriksson, lars.eriksson@aftonbladet.se

This is a maven plugin that transforms a given markdown file to a html file. There is a possibility to add a handlebar
template to render a nicer layout than just plain html.

The example below shows an example how the plugin can be configured to render a html page from the README.md file.

> **inputFile** defines the input file.  
> **targetFileOrDirectory** defines the target for the rendered file. If a directory is specified, the same filename is used
> as in inputFile but with the extension switched from md to html  
> **overwriteExisting** determines if an existing file should be replaced with the generated one  
> **templateFile** is optional and specifies a handlebar template. As default the content is put into content variable 
> 'content unless overridden with a non-default value for _templateVariableName_. If no template is specified the raw
> html will be written without any templates.
> **templateVariableName** re-defines the context variable name for the raw html content

Observe that markdown is not yet standardized that means that the output may differ between different renders.


            <plugin>
                <groupId>se.aftonbladet.plugins.maven</groupId>
                <artifactId>md2html-maven-plugin</artifactId>                
                <version>0.1-SNAPSHOT</version>
                <executions>
                    <execution>
                        <id>package-and-convert</id>
                        <goals>
                            <goal>convert</goal>
                        </goals>
                        <phase>generate-resources</phase>
                        <configuration>
                            <inputFile>${project.basedir}/../README.md</inputFile>
                            <targetFileOrDirectory>${pom.basedir}/target/classes/resources/index.html</targetFileOrDirectory>
                            <overwriteExisting>true</overwriteExisting>
                            <templateFile>${pom.basedir}/src/main/template/template.html</templateFile>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
