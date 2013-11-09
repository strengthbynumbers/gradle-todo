# gradle-todo

This is a simple Gradle plugin that finds // TODO comment tags in Java and Groovy source
files within a project and generates a report showing where these tags appear.  The
output report is generated in Markdown format.

## Usage

The todo task can be used by including the following line in your build script:

	apply plugin: 'ToDo'

If you will be using the raw gradle-todo.jar file, it can be added to your classpath in
the following manner:

	buildscript {
		dependencies {
			classpath files('PATH/TO/JAR/gradle-todo.jar')
		}
	}

By default, report is generated in a file called, 'ToDo-Listing.md'.  The filename can
be changed by modifying the 'outputFile' property of the plugin in your build.gradle:

	ToDo.outputFile = 'my-output-file.md'
