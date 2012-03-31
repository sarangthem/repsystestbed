/* zlib/libpng license
 *
 * This software is provided 'as-is', without any express or implied warranty. In
 * no event will the authors be held liable for any damages arising from the use of
 * this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including
 * commercial applications, and to alter it and redistribute it freely, subject to
 * the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software in
 *      a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *
 *   3. This notice may not be removed or altered from any source distribution.
 *
 * Copyright (c) 2005, Brett Wooldridge
 * Created on May 19, 2005
 */

package cu.rst.gwt.server.jcommando;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Brett Wooldridge
 *
 */
public class SAXHandler extends DefaultHandler
{
   private static final int STATE_PARSE_TOPLEVEL = 0;
   private static final int STATE_PARSE_OPTION = 1;
   private static final int STATE_PARSE_COMMAND = 2;
   private static final int STATE_PARSE_OPTIONREF = 4;
   private static final int STATE_PARSE_COMMANDLESS = 16;
   private static final int STATE_PARSE_GROUPING = 32;
   private static final int STATE_PARSE_DESCRIPTION = 1024;

   private JCommandoGen generator;
   private PrintWriter writer;
   private JCommandParser commandParser;
   private Option currentOption;
   private Command currentCommand;
   private Stack groupStack;
   private Grouping currentGroup;
   private int state;

   SAXHandler(JCommandoGen gen, File infile, File outfile, String className, String packageName)
   {
      super();

      generator = gen;
      generator.init();
      commandParser = (JCommandParser) gen;
      commandParser.setClassName(className);
      commandParser.setPackageName(packageName);
      groupStack = new Stack();

//      try
//      {
//         writer = new PrintWriter( new FileWriter(outfile) );
//      }
//      catch (IOException e)
//      {
//         System.err.println("Cannot open output file.");
//         System.exit(-1);
//      }
   }


   /**
    * Overloaded method.
    *
    * @exception SAXException thrown if there is a parsing error
    *
    * @see org.xml.sax.ContentHandler#startDocument()
    */
   public void startDocument() throws SAXException
   {
      System.out.println("Generating: " + commandParser.getClassName() + ".java");
   }


   /**
    * Overloaded method.
    *
    * @param uri a SAX imposed parameter representing a URI to the XML input file
    * @param localName a namespace parameter
    * @param qName the name of the current XML element
    * @param attributes a collection of element attribute objects
    * @exception SAXException thrown if there is a parse error
    *
    * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
      if ("jcommando".equals(qName))
      {
         state = STATE_PARSE_TOPLEVEL;
      }
      else if ("command".equals(qName))
      {
         handleCommand(attributes);
      }
      else if ("option".equals(qName))
      {
         handleOption(attributes);
      }
      else if ("option-ref".equals(qName))
      {
         handleOptionRef(attributes);
      }
      else if ("commandless".equals(qName))
      {
         handleCommandless(attributes);
      }
      else if ("and".equals(qName) || "or".equals(qName) || "xor".equals(qName) || "not".equals(qName))
      {
         handleGrouping(qName, attributes);
      }
      else if ("line".equals(qName))
      {
         state |= STATE_PARSE_DESCRIPTION;
      }
      else if ("description".equals(qName))
      {
         state |= STATE_PARSE_DESCRIPTION;
      }
   }


   /**
    * Overloaded method.
    *
    * @param uri a SAX imposed parameter representing a URI to the XML input file
    * @param localName a namespace parameter
    * @param qName the name of the current XML element
    * @exception SAXException thrown if a parsing error occurs
    *
    * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
    */
   public void endElement(String uri, String localName, String qName) throws SAXException
   {
      if ("line".equals(qName))
      {
         state ^= STATE_PARSE_DESCRIPTION;
      }
      else if ("command".equals(qName) || "commandless".equals(qName))
      {
         currentCommand = null;
         state = Integer.MIN_VALUE;
      }
      else if ("option".equals(qName))
      {
         currentOption = null;
         state = Integer.MIN_VALUE;
      }
      else if ("and".equals(qName) || "or".equals(qName) || "xor".equals(qName) || "not".equals(qName))
      {
         currentGroup = (groupStack.empty() ? null : (Grouping) groupStack.pop());
      }
   }


   /**
    * Overloaded method.
    *
    * @param ch an array of characters from the input XML file
    * @param start the index of the first element of the 'ch' array that is meaningful for the current tag
    * @param length the length of valid content in the 'ch' array from the starting index
    * @exception SAXException thrown if a parsing error occurs
    *
    * @see org.xml.sax.ContentHandler#characters(char[], int, int)
    */
   public void characters(char[] ch, int start, int length) throws SAXException
   {
      String tmp = (new String(ch, start, length)).trim();
      if (length == 0 || tmp.length() == 0 || !((state & STATE_PARSE_DESCRIPTION) == STATE_PARSE_DESCRIPTION))
      {
         return;
      }

      if ( (state & STATE_PARSE_COMMAND) == STATE_PARSE_COMMAND )
      {
         currentCommand.setDescription(tmp);
      }
      else if ( (state & STATE_PARSE_OPTION) == STATE_PARSE_OPTION )
      {
         currentOption.setDescription(tmp);
      }
   }


   /**
    * This method is called by the SAX parser when the end of document is
    * reached.  It is at this point that we actually write the generated
    * file.
    *
    * @exception SAXException thrown if a parsing error occurs
    *
    * @see org.xml.sax.ContentHandler#endDocument()
    */
   public void endDocument() throws SAXException
   {
      writer.print(generator.generateClassPreamble());
      writer.print(generator.generateClassConstructor());
      writer.print(generator.generateAbstractCallbacks());
      writer.print(generator.generateGroupings());
      writer.print(generator.generateClassEpilog());

      writer.flush();
      writer.close();
   }


   /**
    * Overloaded method.
    *
    * @param e an instance of a SAXParseException indicating a failure in parsing
    * @exception SAXException thrown if a parsing error occurs
    *
    * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
    */
   public void fatalError(SAXParseException e) throws SAXException
   {
      super.fatalError(e);
   }


   // ================================================================================
   //                         P R I V A T E   M E T H O D S
   // ================================================================================


   /**
    * Handle the 'commandless' element.
    *
    */
   private void handleCommandless(Attributes attributes)
   {
      state = STATE_PARSE_COMMANDLESS;

      currentCommand = new Command();
      currentCommand.setName("commandless");
      currentCommand.setId(attributes.getValue("id"));

      if (commandParser.getCommandById("commandless") != null)
      {
         System.err.println("Only one 'commandless' element can be specified.");
         System.exit(-1);
      }

      commandParser.addCommand(currentCommand);
   }


   /**
    * Handle the 'option-ref' element.
    *
    * @param attributes the xml attributes
    */
   private void handleOptionRef(Attributes attributes)
   {
      state = STATE_PARSE_OPTIONREF;

      if (currentGroup == null)
      {
         System.err.println("Option-ref cannot appear outside of a grouping element ('and', 'or', 'xor').");
         System.exit(-1);
      }

      Option option = commandParser.getOptionById(attributes.getValue("id"));
      if (option == null)
      {
         System.err.println("Option-ref refers to non-existant option with id '" + attributes.getValue("id") + "'.");
         System.exit(-1);
      }

      currentGroup.addOption(option);
      currentCommand.addOption(option);
   }


   /**
    * Handle the 'option' element.
    *
    * @param attributes the xml attributes
    */
   private void handleOption(Attributes attributes)
   {
      state = STATE_PARSE_OPTION;
      currentOption = new Option();
      parseOption(attributes);

      if (commandParser.getOptionById(currentOption.getId()) != null)
      {
         System.err.println("Option with id '" + currentOption.getId() + "' already exists.");
         System.exit(-1);
      }

      commandParser.addOption(currentOption);
   }


   /**
    * Handle the 'command' element.
    *
    * @param attributes the xml attributes
    */
   private void handleCommand(Attributes attributes)
   {
      state = STATE_PARSE_COMMAND;

      currentCommand = new Command();
      currentCommand.setName(attributes.getValue("name"));
      currentCommand.setId(attributes.getValue("id"));
      currentCommand.setAllowOptionless("true".equals(attributes.getValue("allow-optionless")));

      if (commandParser.getCommandById(currentCommand.getId()) != null)
      {
         System.err.println("Command with id '" + currentCommand.getId() + "' already exists.");
         System.exit(-1);
      }

      commandParser.addCommand(currentCommand);
   }


   /**
    * Handle the 'and', 'or', 'xor', and 'not' elements.
    *
    */
   private void handleGrouping(String qName, Attributes attributes)
   {
      state = STATE_PARSE_GROUPING;

      if (currentCommand == null)
      {
         System.err.println("'And' element cannot appear outside of a 'command' or 'commandless' element.");
         System.exit(-1);
      }

      Grouping grouping = null;
      if ("and".equals(qName))
      {
         grouping = new And();
      }
      else if ("or".equals(qName))
      {
         grouping = new Or();
      }
      else if ("xor".equals(qName))
      {
         grouping = new Xor();
      }
      else if ("not".equals(qName))
      {
         grouping = new Not();
      }

      if (currentGroup != null)
      {
         currentGroup.getGroupings().add(grouping);
         groupStack.push(currentGroup);
      }

      currentGroup = grouping;
      currentCommand.pushGrouping(grouping);
   }


   /**
    * Parse the attributes for an option and call the appropriate setters.
    *
    * @param attributes the option attributes
    */
   private void parseOption(Attributes attributes)
   {
      currentOption.setShortMnemonic(attributes.getValue("short"));
      currentOption.setLongMnemonic(attributes.getValue("long"));
      currentOption.setOptionType(attributes.getValue("type"));
      currentOption.setId(attributes.getValue("id"));

      if (attributes.getValue("min") != null)
      {
         try
         {
            double min = Double.parseDouble(attributes.getValue("min"));
            currentOption.setMin(min);
         }
         catch (NumberFormatException nfe)
         {
            throw new RuntimeException("Invalid value for 'min' attribute for option '" + currentOption.getId() + "'");
         }
      }

      if (attributes.getValue("max") != null)
      {
         try
         {
            double max = Double.parseDouble(attributes.getValue("max"));
            currentOption.setMax(max);
         }
         catch (NumberFormatException nfe)
         {
            throw new RuntimeException("Invalid value for 'max' attribute for option '" + currentOption.getId() + "'");
         }
      }
   }
}
