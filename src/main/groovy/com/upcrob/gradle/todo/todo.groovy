package com.upcrob.gradle.todo

import java.util.regex.Pattern
import java.util.regex.Matcher
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.io.BufferedWriter

import org.gradle.api.Project
import org.gradle.api.Plugin

class ToDoExtension {
	String outputFile = "ToDo-Listing.md"
}

class ToDo implements Plugin<Project> {
	private static final Pattern todoSingle = Pattern.compile("//[\\t\\s]*TODO(.*)")
	void apply(Project project) {
		project.extensions.create("ToDo", ToDoExtension)

		project.task('todo') << {
			//String outFile = null
			String outFile = project.ToDo.outputFile
			if (outFile == null)
				outFile = "ToDo-Listing.md"

			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)))
			out.println("# Source TODO Report\n")
			new File('src').eachFileRecurse { file->
				String name = file.getName()
				if (name.endsWith(".java")
						|| name.endsWith(".groovy")) {
					// This is a Java or Groovy source file
					// Open up the file for reading
					BufferedReader reader = new BufferedReader(new FileReader(file))
				
					// Read each line in the file, looking for TODO comments
					int lineNumber = 2
					boolean titleWritten = false
					String prev = ""
					String current = ""
					String next = ""
					
					// Queue up line variables
					String line = reader.readLine()
					if (line != null)
						prev = line
					line = reader.readLine()
					if (line != null)
						current = line
					line = reader.readLine()
					if (line != null)
						next = line

					// Check for match on first line
					if (isMatch(prev)) {
						out.println("## " + name + "\n")
						out.println(getMatchMarkdown(1, prev, current, null))
						titleWritten = true
					}

					// Loop through internal lines
					while (next != null) {
						// Check for match
						if (isMatch(current)) {
							// Write file name if it hasn't been printed yet
							if (!titleWritten) {
								out.println("## " + name + "\n")
								titleWritten = true
							}

							// Write match
							out.println(getMatchMarkdown(lineNumber, prev, current, next))
						}

						// Read next line
						prev = current
						current = next
						next = reader.readLine()
						lineNumber++
					}

					// Check for a match on the last line
					if (isMatch(current)) {
						if (!titleWritten) {
							out.println("## " + name + "\n")
						}
						out.println(getMatchMarkdown(lineNumber, prev, current, null))
					}

					// Close reader
					reader.close()
					out.println()
				}
			}
			out.close()
		}
	}

	String getMatchMarkdown(int line, String prev, String current, String next) {
		StringBuilder sb = new StringBuilder()
		sb.append("Line " + line + ":\n\n")
		if (prev != null && prev.trim() != "")
			sb.append("\t" + prev + "\n");
		sb.append("\t" + current + "\n")
		if (next != null && next.trim() != "")
			sb.append("\t" + next + "\n")
		return sb.toString()
	}

	boolean isMatch(String line) {
		if (line == null)
			return false
		Matcher m = todoSingle.matcher(line)
		if (m.find()) {
			return true
		} else {
			return false
		}
	}
}
