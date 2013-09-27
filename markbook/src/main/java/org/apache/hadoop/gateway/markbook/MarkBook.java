/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.gateway.markbook;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkBook {

  public static void main( String[] args ) throws IOException, ParseException {
//    Pattern pattern = Pattern.compile( "<<(.+?)>>" );
//    Matcher matcher = pattern.matcher( "before <<first>> <<second>> after" );
//    matcher.find();
//    System.out.println( matcher.group( 1 ) );
//    System.out.println( matcher.replaceFirst( "__" + matcher.group( 1 ) + "__" ) );
//    matcher = pattern.matcher( matcher.replaceFirst( "__" + matcher.group( 1 ) + "__" ) );
//    matcher.find();
//    System.out.println( matcher.group( 1 ) );
//    System.out.println( matcher.replaceFirst( "__" + matcher.group( 1 ) + "__" ) );

//    Pattern pattern = Pattern.compile( "<\\!---\\s*.*\\s*--->" );
//    String text = "before <!---\nincluded\n---> after";
//    Matcher matcher = pattern.matcher( text );
//    matcher.find();
//    System.out.println( replace( matcher, text, "X" ) );

//    System.out.println( replaceHeadings( "#text#" ) );
//    System.out.println( replaceHeadings( "# text #" ) );
//    System.out.println( replaceHeadings( "## text ##" ) );
//    System.out.println( removeComments( "line\r\n<!--- \r\n comment \r\n comment \r\n---> \r\nline" ) );

//    System.out.println( replaceReferences( "#[ text ]" ) );
//    System.out.println( replaceReferences( "* #[ some text ]after" ) );
//    System.out.println( replaceReferences( "\n#[ text ]" ) );

    CommandLine command = parseCommandLine( args );
    String markdown = loadMarkdown( command );
//    System.out.println( markdown );
    storeHtml( command, markdown );
  }

  private static void storeHtml( CommandLine command, String markdown ) throws IOException {
    PegDownProcessor processor = new PegDownProcessor(
        Extensions.AUTOLINKS | Extensions.FENCED_CODE_BLOCKS | Extensions.QUOTES +
        Extensions.SMARTS | Extensions.TABLES | Extensions.WIKILINKS );
    log( "Converting markdown (" + markdown.length() + " bytes) to HTML" );
    String html = processor.markdownToHtml( markdown.toString() );
    File outputFile = new File( command.getOptionValue( "o" ) );
    log( "Storing HTML to " + outputFile.getAbsolutePath() );
    FileUtils.write( outputFile, html );
  }

  private static String loadMarkdown( CommandLine command ) throws IOException {
    StringBuilder markdown = new StringBuilder();
    for( String inputFileName : command.getOptionValues( "i" ) ) {
      File inputFile = new File( inputFileName );
      log( "Loading markdown from " + inputFile.getAbsolutePath() );
      markdown.append( loadMarkdown( inputFile ) );
      markdown.append( SystemUtils.LINE_SEPARATOR );
    }
    return markdown.toString();
  }

  private static String loadMarkdown( File file ) throws IOException {
    String text = FileUtils.readFileToString( file );
    text = removeComments( text );
    text = replaceHeadings( text );
    text = replaceReferences( text );
    text = replaceIncludes( file, text );
    return text;
  }

  private static String replaceIncludes( File file, String text ) throws IOException {
    Pattern pattern = Pattern.compile( "<<\\s*(.+?)\\s*>>" );
    Matcher matcher = pattern.matcher( text );
    while( matcher.find() ) {
      String includeFileName = matcher.group( 1 );
      File includeFile = new File( file.getParent(), includeFileName );
      if( includeFile.exists() && includeFile.canRead() ) {
        String includeString = loadMarkdown( includeFile );
        text = replace( matcher, text, includeString );
      } else {
        throw new FileNotFoundException( includeFile.getAbsolutePath() );
        //text = replace( matcher, text, includeFileName );
      }
      matcher = pattern.matcher( text );
    }
    return text;
  }

  private static String replaceHeadings( String text ) throws IOException {
    Pattern pattern = Pattern.compile( "^(#+)(.+?)#*$", Pattern.MULTILINE );
    Matcher matcher = pattern.matcher( text );
    while( matcher.find() ) {
      String tag = matcher.group( 1 );
      String name = matcher.group( 2 ).trim();
      String id = id( name );
      if( !name.startsWith( "<a id=" ) ) {
        text = replace( matcher, text, String.format( "%s <a id=\"%s\"></a>%s %s", tag, id, name, tag ) );
        matcher = pattern.matcher( text );
      }
    }
    return text;
  }

  private static String replaceReferences( String text ) throws IOException {
    Pattern pattern = Pattern.compile( "(\\s)#\\[(.+?)\\]" );
    Matcher matcher = pattern.matcher( text );
    while( matcher.find() ) {
      String space = matcher.group( 1 );
      String name = matcher.group( 2 ).trim();
      String id = id( name );
      text = replace( matcher, text, String.format( "%s[%s](#%s)", space, name, id ) );
      matcher = pattern.matcher( text );
    }
    return text;
  }

  private static String removeComments( String text ) {
    Pattern pattern = Pattern.compile( "<!---.*--->", Pattern.DOTALL );
    Matcher matcher = pattern.matcher( text );
    while( matcher.find() ) {
      text = replace( matcher, text, "" );
      matcher = pattern.matcher( text );
    }
    return text;
  }

  private static String replace( Matcher matcher, String original, String replace ) {
    return original.substring( 0, matcher.start() ) + replace + original.substring( matcher.end(), matcher.regionEnd() );
  }

  private static String id( String name ) {
    String id = name.replaceAll( "\\s", "+" );
    return id;
  }

  private static Options createOptions() {
    Options options = new Options();

    Option help = OptionBuilder
        .withArgName( "help" )
        .withLongOpt( "help" )
        .withDescription( "Prints this help." )
        .create( "h" );

    Option input = OptionBuilder
        .withArgName( "input" )
        .withLongOpt( "input" )
        .hasArg()
        .withDescription( "Repeat for each Markdown input file name." )
        .create( "i" );

    Option output = OptionBuilder
        .withArgName( "output" )
        .withLongOpt( "output" )
        .hasArgs( 1 )
        //.isRequired()
        .withDescription( "A single output HTML file name." )
        .create( "o" );

    options.addOption( input );
    options.addOption( output );
    options.addOption( help );

    return options;
  }

  private static CommandLine parseCommandLine( String[] args ) throws ParseException {
    Options options = createOptions();
    CommandLineParser parser = new BasicParser();
    CommandLine command = parser.parse( options, args );
    if( command.hasOption( "help" ) ) {
      printHelp( options );
    }
    if( !command.hasOption( "input" ) || command.getOptionValues( "input" ).length == 0 ) {
      printHelp( options );
    }
    if( !command.hasOption( "output" ) || command.getOptionValues( "output" ).length != 1 ) {
      printHelp( options );
    }
    return command;
  }

  private static void printHelp( Options options ) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "java -jar pegdown.jar", options );
    System.exit( -1 );
  }

  private static void log( String message ) {
    System.out.println( message );
  }

}
