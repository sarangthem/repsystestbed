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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * @author Brett Wooldridge
 *
 */
public class Command
{
   private Stack groupings;
   private Set options;
   private Grouping grouping;
   private String name;
   private String description;
   private String id;
   private boolean allowOptionless;

   {
      groupings = new Stack();
      options   = new HashSet();
   }

   /**
    * Constructs an object representing a command and it's required and optional "Options".
    *
    */
   public Command()
   {
   }


   /**
    * @return Returns the id.
    */
   public String getId()
   {
      return id;
   }


   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return name;
   }


   /**
    * @return Returns the description.
    */
   public String getDescription()
   {
      return description;
   }


   /**
    * @param cmdId The cmdId to set.
    */
   public void setId(String cmdId)
   {
      this.id = cmdId;
   }


   /**
    * @param cmdName The name to set.
    */
   public void setName(String cmdName)
   {
      this.name = cmdName;
   }


   /**
    * @param desc The description to set.
    */
   public void setDescription(String desc)
   {
      this.description = desc;
   }


   /**
    * @return Returns the grouping.
    */
   public Grouping getGrouping()
   {
      return grouping;
   }


   /**
    * Set the grouping object for this command.
    *
    * @param group The grouping to set.
    */
   public void setGrouping(Grouping group)
   {
      this.grouping = group;
   }


   /**
    * Add an option to the set of options supported by this command.
    *
    * @param option the option
    */
   public void addOption(Option option)
   {
      options.add(option);
   }


   /**
    * Determines whether this command will be executed even if there are
    * no options specified.
    *
    * @return Returns true if this command allows optionless execution
    */
   public boolean isAllowOptionless()
   {
      return allowOptionless;
   }


   /**
    * Sets whether this command will be executed even if there are
    * no options specified..
    *
    * @param optionless true if optionless execution is allowed,
    *        false otherwises
    */
   public void setAllowOptionless(boolean optionless)
   {
      this.allowOptionless = optionless;
   }


   // ======================================================================
   //                    P R I V A T E   M E T H O D S
   // ======================================================================

   private String toJavaCase(String identifier)
   {
      return identifier.substring(0, 1).toUpperCase() + identifier.substring(1);
   }


   // ======================================================================
   //                    P A C K A G E   M E T H O D S
   // ======================================================================

   void pushGrouping(Grouping grp)
   {
      groupings.push(grp);
   }


   boolean isEmptyGroupings()
   {
      return groupings.empty();
   }


   String generateSetters()
   {
      StringBuffer sb = new StringBuffer();
      if (name != null)
      {
         sb.append("      " + id + ".setName(\"");
         sb.append(name + "\");\n");
      }
      if (id != null)
      {
         sb.append("      " + id + ".setId(\"");
         sb.append(id + "\");\n");
      }
      if (description != null)
      {
         sb.append("      " + id + ".setDescription(\"");
         sb.append(description + "\");\n");
      }
      if (allowOptionless)
      {
         sb.append("      " + id + ".setAllowOptionless(true);\n");
      }

      Iterator iter = options.iterator();
      while (iter.hasNext())
      {
         Option option = (Option) iter.next();
         sb.append("      " + id + ".addOption(");
         sb.append(option.getId() + ");\n");
      }

      if (!groupings.empty())
      {
         sb.append("      " + id + ".setGrouping( create" + toJavaCase(id) + "Grouping() );\n");
      }
      return sb.toString();
   }


   /**
    * This is a bit tricky, and it's bad to be too clever, so here's an explanation.
    *
    * This class contains a Stack, on which we push instances of Grouping objects (And, Or, Xor)
    * in the order in which they are encountered in the XML.  Additionally, each Grouping object
    * knows who its immediate children are.  So, if we look at the following XML snippet:
    *
    *    <command id="Foo">
    *       <xor>
    *          <and>
    *             <options .../>
    *          </and>
    *          <or>
    *             <options .../>
    *          </or>
    *       </xor>
    *    </command>
    *
    * The resulting stack looks like:
    *    Top of stack (i.e. last on) --> or  (children: none)
    *                                    and (children: none)
    *                                    xor (children: and, or)
    *
    * In order to generate code, we start at the top of the stack, popping each element,
    * assigning it a Java identifier, and emitting its code.  In the example above, we pop
    * 'or' from the stack, assign it the name 'or1' (if there was another 'or' encountered
    * later that 'or' would be called 'or2'), and generate it's code.
    *
    * By doing it this way, by the time we generate the code for 'and' (named 'and1'), we
    * have already generated the code to create 'or1', and therefore can generate code to
    * add 'or1' to the children of 'and1'.  And so on, guaranteeing that we have generated
    * objects before other objects need to reference them.
    *
    * @return
    */
   String generateGrouping()
   {
      StringWriter sw = new StringWriter();
      PrintWriter  pw = new PrintWriter(sw);

      pw.println("\n   /**");
      pw.println("    * Generate the grouping for the '" + id + "' command.");
      pw.println("    */");
      pw.println("   private Grouping create" + toJavaCase(id) + "Grouping()");
      pw.println("   {");

      int andCount = 0;
      int orCount  = 0;
      int xorCount = 0;
      int notCount = 0;
      while (!groupings.empty())
      {
         Grouping grp = (Grouping) groupings.pop();
         String grpName = "";
         if (grp instanceof And)
         {
            grpName = "and" + (++andCount);
         }
         else if (grp instanceof Or)
         {
            grpName = "or" + (++orCount);
         }
         else if (grp instanceof Xor)
         {
            grpName = "xor" + (++xorCount);
         }
         else if (grp instanceof Not)
         {
            grpName = "not" + (++notCount);
         }
         grp.setId(grpName);
         pw.print( grp.generateConstructor() );

         Iterator iter = grp.getGroupings().iterator();
         while (iter.hasNext())
         {
            Grouping subGroup = (Grouping) iter.next();
            pw.println("      " + grpName + ".getGroupings().add(" + subGroup.getId() + ");");
         }

         iter = grp.getOptions().iterator();
         while (iter.hasNext())
         {
            Option option = (Option) iter.next();
            pw.println("      " + grpName + ".addOption(getOptionById(\"" + option.getId() + "\"));");
         }

         if (groupings.empty())
         {
            pw.println("      return " + grpName + ";");
         }
      }
      pw.println("   }");

      return sw.toString();
   }
}
