# XWiki-LaTeX-Math-Rendering
To investigate and address the problem of XWiki's Markdown rendering intertwining formatting symbols (_ and *) with LaTeX math expressions enclosed in $ or $$, follow the analysis and solution below.

1. The Problem

XWiki's Markdown support (specifically Markdown Syntax 1.2, which is based on flexmark-java) follows standard CommonMark rules by default. In standard Markdown, the dollar sign ($) is treated as a regular character, not a special delimiter. Consequently, the parser interprets:

$a_b$ not as "Math: a subscript b", but as "Text: $a", then "start italics with _", then "b", then "close italics with $".

This causes the rendering engine to process underscores and asterisks as formatting (italics/bold) before or instead of treating the content as a math block.

2. Relevant XWiki Component

The issue lies within the XWiki Rendering rendering module, specifically the component responsible for configuring the Flexmark parser.

Component Name: xwiki-rendering-syntax-markdown12

Underlying Library: flexmark-java[1][2][3]

Likely Class: org.xwiki.contrib.rendering.markdown.markdown12.internal.MarkdownParser or DefaultMarkdownConfiguration.

3. Proposed Solution

To fix this, you must configure the Flexmark parser to recognize $ and $$ as special delimiters that create "Math" nodes. When the parser recognizes these as Math nodes, it treats the content inside as verbatim (similar to code blocks), preventing the _ and * characters from being parsed as italics or bold.

You should apply the following changes to the component code (or configuration):

Add the Math Extension: Enable the GitLab extension or the dedicated Admonition / Math extension from the flexmark-java library. The GitLab extension specifically includes support for $ and $$ math delimiters.

Configure Options: Ensure the GitLabExtension is added to the parser's extension list.

By enabling this extension, the parser will tokenize $ ... $ as a MathNode before processing inline formatting, effectively insulating the underscores and asterisks inside the formula.


To implement the fix, you need to modify the source code of the XWiki Markdown Syntax 1.2 extension. This involves adding a library dependency to the project's build file (pom.xml) and then registering that library in the Java parser configuration.

Here is the step-by-step guide to applying this code.

Step 1: Locate the Source Code

You need to find the source code for the xwiki-rendering-syntax-markdown12 module. If you have the XWiki source checked out, navigate to:
xwiki-contrib-markdown/syntax-markdown-commonmark12

If you are just patching a local JAR, you will need to decompile it, but it is highly recommended to clone the official repository to make this change.

Step 2: Add the Maven Dependency

The standard flexmark-java library used by XWiki does not support $ math delimiters out of the box. You must add the GitLab extension (which includes math support) to the project dependencies.

Open the pom.xml file in the syntax-markdown-commonmark12 module and add this dependency inside the <dependencies> block:

code
Xml


<dependency>
  <groupId>com.vladsch.flexmark</groupId>
  <artifactId>flexmark-ext-gitlab</artifactId>
  <version>${flexmark.version}</version>
</dependency>

(Note: ${flexmark.version} should already be defined in the parent POM. If not, match the version of the other flexmark dependencies you see in the file, e.g., 0.64.8.)[1][2]

Step 3: Modify the Java Parser Configuration

Locate the Java class responsible for creating the Markdown parser.

File Path: src/main/java/org/xwiki/contrib/rendering/markdown/markdown12/internal/parser/Markdown12Parser.java (or similarly named Markdown12StreamParser.java).

Goal: Find where Parser.builder(options) is called and add the GitLab extension to it.

Apply these changes:

Add Imports:

code
Java

import com.vsch.flexmark.ext.gitlab.GitLabExtension;
import com.vsch.flexmark.util.data.MutableDataSet;
import java.util.Arrays;

Update the Builder Logic:
Look for the method that initializes the options (usually inside the constructor or a createParser method). It will look something like MutableDataSet options = new MutableDataSet();.

Inject the GitLabExtension into the extensions list:



Step 4: Build and Deploy

Build: Run mvn clean install in the module directory to compile the new JAR file.

Deploy: Copy the generated JAR file (e.g., syntax-markdown-commonmark12-X.X.jar) to your XWiki installation at:
WEB-INF/lib/

Add Dependency JAR: You must also copy the flexmark-ext-gitlab-0.x.x.jar (and its transitive dependencies) to WEB-INF/lib/ so XWiki can find the new library at runtime.

Restart XWiki: Restart your servlet container (Tomcat/Jetty) for the changes to take effect.

Why this works

By registering GitLabExtension, the Flexmark parser gains a specific "Math Parser" that runs early in the parsing process. It identifies $ ... $ sequences and locks them as Math Nodes. Because they are locked as Math Nodes, the standard Markdown processor will skip over them when looking for _ (italics) or * (bold), effectively solving the intertwining problem.

