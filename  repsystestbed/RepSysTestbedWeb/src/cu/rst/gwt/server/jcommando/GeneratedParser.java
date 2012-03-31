/*
 * THIS IS A GENERATED FILE.  DO NOT EDIT.
 *
 * JCommando (http://jcommando.sourceforge.net)
 */

package cu.rst.gwt.server.jcommando;

/**
 * JCommando generated parser class.
 */
public abstract class GeneratedParser extends JCommandParser
{
   /**
     * JCommando generated constructor.
     */
   public GeneratedParser()
   {
      Option inputFile = new Option();
      inputFile.setId("inputFile");
      inputFile.setShortMnemonic("f");
      inputFile.setLongMnemonic("inputfile");
      inputFile.setDescription("Specify the name of the JCommando XML input file.");
      inputFile.setOptionType("String");
      addOption(inputFile);

      Option className = new Option();
      className.setId("className");
      className.setShortMnemonic("c");
      className.setLongMnemonic("classname");
      className.setDescription("Specify the name of the parser class to generate.");
      className.setOptionType("String");
      addOption(className);

      Option packageName = new Option();
      packageName.setId("packageName");
      packageName.setShortMnemonic("p");
      packageName.setLongMnemonic("packagename");
      packageName.setDescription("Specify the Java package name for the generated parser.");
      packageName.setOptionType("String");
      addOption(packageName);

      Option destDir = new Option();
      destDir.setId("destDir");
      destDir.setShortMnemonic("d");
      destDir.setLongMnemonic("destdir");
      destDir.setDescription("Specify the output directory.");
      destDir.setOptionType("String");
      addOption(destDir);

      Option help = new Option();
      help.setId("help");
      help.setShortMnemonic("?");
      help.setLongMnemonic("help");
      help.setDescription("Display this help text.");
      addOption(help);

      Command generate = new Command();
      generate.setName("commandless");
      generate.setId("generate");
      generate.addOption(help);
      generate.addOption(destDir);
      generate.addOption(className);
      generate.addOption(inputFile);
      generate.addOption(packageName);
      generate.setGrouping( createGenerateGrouping() );
      addCommand(generate);

   }

   /**
     * Called by parser to set the 'inputFile' property.
     *
     * @param inputFile the value to set.
     */
   public abstract void setInputFile(String inputFile);

   /**
     * Called by parser to set the 'className' property.
     *
     * @param className the value to set.
     */
   public abstract void setClassName(String className);

   /**
     * Called by parser to set the 'packageName' property.
     *
     * @param packageName the value to set.
     */
   public abstract void setPackageName(String packageName);

   /**
     * Called by parser to set the 'destDir' property.
     *
     * @param destDir the value to set.
     */
   public abstract void setDestDir(String destDir);

   /**
     * Called by parser to set the 'help' property.
     *
     */
   public abstract void setHelp();

   /**
     * Called by parser to perform the 'generate' command.
     *
     */
   public abstract void doGenerate();

   /**
    * Generate the grouping for the 'generate' command.
    */
   private Grouping createGenerateGrouping()
   {
      And and1 = new And();
      and1.addOption(getOptionById("destDir"));
      and1.addOption(getOptionById("className"));
      and1.addOption(getOptionById("inputFile"));
      and1.addOption(getOptionById("packageName"));
      Xor xor1 = new Xor();
      xor1.getGroupings().add(and1);
      xor1.addOption(getOptionById("help"));
      return xor1;
   }
}
