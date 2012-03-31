/*
 * THIS IS A GENERATED FILE.  DO NOT EDIT.
 *
 * JCommando (http://jcommando.sourceforge.net)
 */

package cu.rst.gwt.server.parse;

import org.jcommando.Command;
import org.jcommando.JCommandParser;
import org.jcommando.Option;
import org.jcommando.Grouping;
import org.jcommando.And;
import org.jcommando.Or;
import org.jcommando.Xor;
import org.jcommando.Not;

/**
 * JCommando generated parser class.
 */
public abstract class CommandLineParser extends JCommandParser
{
   /**
     * JCommando generated constructor.
     */
   public CommandLineParser()
   {
      Option alg = new Option();
      alg.setId("alg");
      alg.setShortMnemonic("a");
      alg.setLongMnemonic("alg");
      alg.setDescription("Specify the name of the algorithm");
      alg.setOptionType("String");
      addOption(alg);

      Option classpath = new Option();
      classpath.setId("classpath");
      classpath.setShortMnemonic("cp");
      classpath.setLongMnemonic("classpath");
      classpath.setDescription("Specify the class path of the algorithm.");
      classpath.setOptionType("String");
      addOption(classpath);

      Option graph = new Option();
      graph.setId("graph");
      graph.setShortMnemonic("g");
      graph.setLongMnemonic("graph");
      graph.setDescription("Specify the name of the graph.");
      graph.setOptionType("String");
      addOption(graph);

      Option graphtype = new Option();
      graphtype.setId("graphtype");
      graphtype.setShortMnemonic("gt");
      graphtype.setLongMnemonic("graphtype");
      graphtype.setDescription("Specify the type of the graph.");
      graphtype.setOptionType("String");
      addOption(graphtype);

      Option inputfile = new Option();
      inputfile.setId("inputfile");
      inputfile.setShortMnemonic("i");
      inputfile.setLongMnemonic("inputfile");
      inputfile.setDescription("Specify the input file for the graph.");
      inputfile.setOptionType("String");
      addOption(inputfile);

      Option workflow = new Option();
      workflow.setId("workflow");
      workflow.setShortMnemonic("w");
      workflow.setLongMnemonic("workflow");
      workflow.setDescription("Specify the name of the workflow.");
      workflow.setOptionType("String");
      addOption(workflow);

      Option defn = new Option();
      defn.setId("defn");
      defn.setShortMnemonic("d");
      defn.setLongMnemonic("defn");
      defn.setDescription("Specify the definition of the workflow.");
      defn.setOptionType("String");
      addOption(defn);

      Command create = new Command();
      create.setName("create");
      create.setId("create");
      create.addOption(graph);
      create.addOption(classpath);
      create.addOption(alg);
      create.addOption(graphtype);
      create.addOption(workflow);
      create.addOption(defn);
      create.addOption(inputfile);
      create.setGrouping( createCreateGrouping() );
      addCommand(create);

      Command list = new Command();
      list.setName("list");
      list.setId("list");
      list.setAllowOptionless(true);
      list.setGrouping( createListGrouping() );
      addCommand(list);

      Command run = new Command();
      run.setName("run");
      run.setId("run");
      run.setAllowOptionless(true);
      run.addOption(workflow);
      run.setGrouping( createRunGrouping() );
      addCommand(run);

   }

   /**
     * Called by parser to set the 'alg' property.
     *
     * @param alg the value to set.
     */
   public abstract void setAlg(String alg);

   /**
     * Called by parser to set the 'classpath' property.
     *
     * @param classpath the value to set.
     */
   public abstract void setClasspath(String classpath);

   /**
     * Called by parser to set the 'graph' property.
     *
     * @param graph the value to set.
     */
   public abstract void setGraph(String graph);

   /**
     * Called by parser to set the 'graphtype' property.
     *
     * @param graphtype the value to set.
     */
   public abstract void setGraphtype(String graphtype);

   /**
     * Called by parser to set the 'inputfile' property.
     *
     * @param inputfile the value to set.
     */
   public abstract void setInputfile(String inputfile);

   /**
     * Called by parser to set the 'workflow' property.
     *
     * @param workflow the value to set.
     */
   public abstract void setWorkflow(String workflow);

   /**
     * Called by parser to set the 'defn' property.
     *
     * @param defn the value to set.
     */
   public abstract void setDefn(String defn);

   /**
     * Called by parser to perform the 'create' command.
     *
     */
   public abstract void doCreate();

   /**
     * Called by parser to perform the 'list' command.
     *
     */
   public abstract void doList();

   /**
     * Called by parser to perform the 'run' command.
     *
     */
   public abstract void doRun();

   /**
    * Generate the grouping for the 'create' command.
    */
   private Grouping createCreateGrouping()
   {
      And and1 = new And();
      and1.addOption(getOptionById("defn"));
      And and2 = new And();
      and2.getGroupings().add(and1);
      and2.addOption(getOptionById("workflow"));
      And and3 = new And();
      and3.addOption(getOptionById("graphtype"));
      and3.addOption(getOptionById("inputfile"));
      Or or1 = new Or();
      or1.getGroupings().add(and3);
      or1.addOption(getOptionById("graphtype"));
      And and4 = new And();
      and4.getGroupings().add(or1);
      and4.addOption(getOptionById("graph"));
      And and5 = new And();
      and5.addOption(getOptionById("classpath"));
      And and6 = new And();
      and6.getGroupings().add(and5);
      and6.addOption(getOptionById("alg"));
      Or or2 = new Or();
      or2.getGroupings().add(and6);
      or2.getGroupings().add(and4);
      or2.getGroupings().add(and2);
      return or2;
   }

   /**
    * Generate the grouping for the 'list' command.
    */
   private Grouping createListGrouping()
   {
      Or or1 = new Or();
      return or1;
   }

   /**
    * Generate the grouping for the 'run' command.
    */
   private Grouping createRunGrouping()
   {
      Or or1 = new Or();
      or1.addOption(getOptionById("workflow"));
      return or1;
   }
}
