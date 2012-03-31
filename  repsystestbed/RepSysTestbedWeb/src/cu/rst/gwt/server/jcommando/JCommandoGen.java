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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * This class generates a command parser based on an XML input file.
 *
 * @author Brett Wooldridge
 */
public final class JCommandoGen extends GeneratedParser
{
   private String destDir;
   private String inputFile;
   private String className = "";
   private String packageName = "";

   /**
    * The "main" that generates the command parser.
    *
    * @param args command line arguments
    */
   public static void main(String[] args)
   {
      JCommandoGen generator = new JCommandoGen();

      if (args.length == 0)
      {
         System.out.println("JCommando Parser Generator - http://jcommando.sourceforge.net");
         System.out.println();
         System.out.println("Options:");
         generator.printUsage();
         System.exit(0);
      }

      try
      {
         generator.parse(args);
      }
      catch (ParseException pe)
      {
         System.err.println(pe.getMessage());
      }
   }


   // ================================================================================
   //                          P U B L I C   M E T H O D S
   // ================================================================================

   /**
    * Generate a parser from an XML command file.
    *
    */
   public void doGenerate()
   {
      String fullOutputName = (new File(destDir)).getAbsolutePath() + "/" + packageToDir() + "/" + className + ".java";

      File outputFile  = new File(fullOutputName);
      File commandFile = new File(inputFile);

      File dir = new File(destDir + "/" + packageToDir());
      dir.mkdirs();

      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      SAXParser saxParser;
      try
      {
         saxParser = saxParserFactory.newSAXParser();
      }
      catch (Exception e)
      {
         saxParser = null;
         System.err.println("Unable to instantiate a SAXParser.");
         System.exit(-1);
      }

      SAXHandler saxHandler = new SAXHandler(this, commandFile, outputFile, className, packageName);
      try
      {
         saxParser.parse(commandFile, saxHandler);
      }
      catch (SAXException e1)
      {
         throw new RuntimeException(e1.getMessage());
      }
      catch (IOException e1)
      {
         throw new RuntimeException(e1.getMessage());
      }
   }


   /**
    * Convert the package name to a path.
    *
    * @return the converted package name
    */
   private String packageToDir()
   {
      if (packageName == null)
      {
         return "";
      }

      return packageName.replace('.', '/');
   }


   /**
    * Set the destination directory.
    *
    * @param dest the name of the destination directory
    */
   public void setDestDir(String dest)
   {
      File dir = new File(dest);
      if (!dir.exists())
      {
         throw new RuntimeException("Destination directory does not exist.");
      }
      else if (!dir.isDirectory())
      {
         throw new RuntimeException("Destination is not a directory.");
      }
      this.destDir = dest;
   }


   /**
    * Set the source command file.
    *
    * @param file the name of the source file
    */
   public void setInputFile(String file)
   {
      File commandFile = new File(file);
      if (!commandFile.exists())
      {
         throw new RuntimeException("Input file '" + file + "' does not exist.");
      }
      else if (!commandFile.isFile())
      {
         throw new RuntimeException("Specified input source '" + file + "' is not a file.");
      }
      this.inputFile = file;
   }


   /**
    * Set the classname.
    *
    * @param clazz the name of the parser class to generate
    */
   public void setClassName(String clazz)
   {
      className = clazz;
   }


   /**
    * Set the package name.
    *
    * @param pkg the name of package the generated class should reside in
    */
   public void setPackageName(String pkg)
   {
      packageName = pkg;
   }


   /**
    * @return Returns the className.
    */
   public String getClassName()
   {
      return className;
   }


   /**
    * @return Returns the destDir.
    */
   public String getDestDir()
   {
      return destDir;
   }


   /**
    * @return Returns the inputFile.
    */
   public String getInputFile()
   {
      return inputFile;
   }


   /**
    * @return Returns the packageName.
    */
   public String getPackageName()
   {
      return packageName;
   }


   /**
    * Call the super-class.
    */
   public void setHelp()
   {
      System.out.println("JCommando Parser Generator - http://jcommando.sourceforge.net");
      System.out.println();
      System.out.println("Options:");
      printUsage();
      System.exit(0);
   }


   /**
    * Empty constructor.
    */
   public JCommandoGen()
   {
      super();
   }


   // =================================================================
   // Code generation methods.  They're not pretty, but code generation
   // never is.
   // =================================================================

   String generateClassPreamble()
   {
      System.out.println("Generating preamble...");
      StringWriter sw = new StringWriter();
      PrintWriter  pw = new PrintWriter(sw);
      pw.println("/*");
      pw.println(" * THIS IS A GENERATED FILE.  DO NOT EDIT.");
      pw.println(" *");
      pw.println(" * JCommando (http://jcommando.sourceforge.net)");
      pw.println(" */");
      pw.println();
      pw.println("package " + packageName + ";");
      pw.println();
      if (!"cu.repsystestbed.gwt.server.jcommando".equals(packageName))      // don't generate imports for our own package
      {
         pw.println("import cu.repsystestbed.gwt.server.jcommando.Command;");
         pw.println("import cu.repsystestbed.gwt.server.jcommando.JCommandParser;");
         pw.println("import cu.repsystestbed.gwt.server.jcommando.Option;");
         pw.println("import cu.repsystestbed.gwt.server.jcommando.Grouping;");
         pw.println("import cu.repsystestbed.gwt.server.jcommando.And;");
         pw.println("import cu.repsystestbed.gwt.server.jcommando.Or;");
         pw.println("import cu.repsystestbed.gwt.server.jcommando.Xor;");
         pw.println("import cu.repsystestbed.gwt.server.jcommando.Not;");
         pw.println();
      }
      pw.println("/**");
      pw.println(" * JCommando generated parser class.");
      pw.println(" */");
      pw.println("public abstract class " + className + " extends JCommandParser");
      pw.println("{");
      return sw.toString();
   }

   String generateClassConstructor()
   {
      System.out.println("Generating constructor...");
      StringWriter sw = new StringWriter();
      PrintWriter  pw = new PrintWriter(sw);
      pw.println("   /**");
      pw.println("     * JCommando generated constructor.");
      pw.println("     */");
      pw.println("   public " + className + "()");
      pw.println("   {");

      Iterator iter = optionsById.values().iterator();
      while (iter.hasNext())
      {
         Option option = (Option) iter.next();
         pw.println("      Option " + option.getId() + " = new Option();");
         pw.print(option.generateSetters());
         pw.println("      addOption(" + option.getId() + ");");
         pw.println();
      }

      iter = commands.values().iterator();
      while (iter.hasNext())
      {
         Command cmd = (Command) iter.next();
         pw.println("      Command " + cmd.getId() + " = new Command();");
         pw.print(cmd.generateSetters());
         pw.println("      addCommand(" + cmd.getId() + ");");
         pw.println();
      }

      pw.println("   }");
      return sw.toString();
   }


   String generateAbstractCallbacks()
   {
      System.out.println("Generating callbacks...");
      StringWriter sw = new StringWriter();
      PrintWriter  pw = new PrintWriter(sw);

      Iterator optionIter = optionsById.values().iterator();
      while (optionIter.hasNext())
      {
         Option option = (Option) optionIter.next();
         String identifier = toJavaCase(option.getId());
         pw.println();
         pw.println("   /**");
         pw.println("     * Called by parser to set the '" + option.getId() + "' property.");
         pw.println("     *");
         if (option.getOptionType() != null)
         {
            pw.println("     * @param " + option.getId() + " the value to set.");
         }
         pw.println("     */");
         pw.print("   public abstract void set" + identifier + "(");
         if (option.getOptionType() != null)
         {
            pw.print(option.getOptionType() + " " + option.getId());
         }
         pw.println(");");
      }

      Iterator commandIter = commands.values().iterator();
      while (commandIter.hasNext())
      {
         Command cmd = (Command) commandIter.next();
         String identifier = toJavaCase(cmd.getId());
         pw.println();
         pw.println("   /**");
         pw.println("     * Called by parser to perform the '" + cmd.getId() + "' command.");
         pw.println("     *");
         pw.println("     */");
         pw.println("   public abstract void do" + identifier + "();");
      }

      return sw.toString();
   }


   String generateGroupings()
   {
      System.out.println("Generating option groupings...");
      StringWriter sw = new StringWriter();
      PrintWriter  pw = new PrintWriter(sw);

      Iterator iter = commands.values().iterator();
      while (iter.hasNext())
      {
         Command cmd = (Command) iter.next();
         if (!cmd.isEmptyGroupings())
         {
            pw.print(cmd.generateGrouping());
         }
      }

      return sw.toString();
   }


   String generateClassEpilog()
   {
      System.out.println("Generating epilog.");
      StringWriter sw = new StringWriter();
      PrintWriter  pw = new PrintWriter(sw);

      pw.println("}");

      return sw.toString();
   }


   private String toJavaCase(String identifier)
   {
      return identifier.substring(0, 1).toUpperCase() + identifier.substring(1);
   }
}
