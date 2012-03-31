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

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Brett Wooldridge
 *
 */
public class JCommandParser
{
   protected LinkedHashMap optionsById;
   protected LinkedHashMap optionsByShort;
   protected LinkedHashMap optionsByLong;
   protected LinkedHashMap commands;
   protected LinkedHashMap commandsById;

   private String packageName;
   private String className;

   private Set     parsedOptions;
   private List    unparsedArguments;
   private Command parsedCommand;

   private Map  numericParseMessages;
   private Map  classArgArray;

   {
      init();
   }


   void init()
   {
      optionsById    = new LinkedHashMap();
      optionsByShort = new LinkedHashMap();
      optionsByLong  = new LinkedHashMap();

      commands     = new LinkedHashMap();
      commandsById = new LinkedHashMap();

      parsedOptions     = new LinkedHashSet();
      unparsedArguments = new ArrayList();

      numericParseMessages = new HashMap();
      numericParseMessages.put("int",    "Parameter for option --{0} must be a value between {1,number,integer} and {2,number,integer}.");
      numericParseMessages.put("long",   "Parameter for option --{0} must be a value between {1,number,long} and {2,number,long}.");
      numericParseMessages.put("double", "Parameter for option --{0} must be a floating point value between {1,number,double} and {2,number,double}.");

      classArgArray = new HashMap();
      classArgArray.put("String", toClassArray(String.class) );
      classArgArray.put("int",    toClassArray(Integer.TYPE));
      classArgArray.put("long",   toClassArray(Long.TYPE));
      classArgArray.put("double", toClassArray(Double.TYPE));
   }


   /**
    * This is the primary method of the parser, and is generally invoked by
    * the user with the command-line arguments passed into their 'main'.
    *
    * @param args command-line arguments to parse
    */
   public void parse(String[] args)
   {
      for (int i = 0; i < args.length; i++)
      {
         String arg = args[i];
         if (arg.startsWith("--"))     // long option
         {
            String rawArg = arg.substring(2);
            Option option = (Option) optionsByLong.get(rawArg);
            boolean consume = parseOption(option, rawArg, (i + 1 < args.length ? args[i + 1] : null));
            i += (consume ? 1 : 0);
         }
         else if (arg.startsWith("-")) // short option
         {
            String rawArg = arg.substring(1);
            Option option = (Option) optionsByShort.get(rawArg);
            boolean consume = parseOption(option, rawArg, (i + 1 < args.length ? args[i + 1] : null));
            i += (consume ? 1 : 0);
         }
         else  // command or unparsed arguments
         {
            Command command = (Command) commands.get(arg);
            parseCommand(command, arg);
         }
      }

      if (parsedCommand == null)
      {
         parsedCommand = (Command) commands.get("commandless");
      }

      checkOptions();
      executeSetters();
      executeCommands();
   }


   /**
    * Print the automatically generated usage.
    */
   public void printUsage()
   {
      String tabs = "\t\t\t\t\t\t";

      // Output the commands
      System.out.println("Commands:");
      ArrayList sortedCommands = new ArrayList(commands.keySet());
      Collections.sort(sortedCommands);

      int longest  = 0;
      Iterator iter = sortedCommands.iterator();
      while (iter.hasNext())
      {
         String key = (String) iter.next();
         Command command = (Command) commands.get(key);
         String tmp = "  " + command.getName();
         longest  = Math.max(longest, tmp.length());
      }
      longest = (longest + (8 - (longest % 8)));

      iter = sortedCommands.iterator();
      while (iter.hasNext())
      {
         String key = (String) iter.next();
         Command command = (Command) commands.get(key);
         String tmp = "  " + command.getName();
         System.out.print(tmp);
         int tabCount = ((longest - (tmp.length() + 1)) / 8) + 1;
         System.out.print( tabs.substring(0, tabCount) );
         System.out.println(command.getDescription());
      }

      // Output the options
      System.out.println("\nOptions:");
      ArrayList sortedOptions = new ArrayList(optionsByShort.keySet());
      Collections.sort(sortedOptions);

      iter = sortedOptions.iterator();
      while (iter.hasNext())
      {
         String key = (String) iter.next();
         Option option = (Option) optionsByShort.get(key);
         String tmp = "  -" + option.getShortMnemonic() + ", --" + option.getLongMnemonic();
         longest  = Math.max(longest, tmp.length());
      }
      longest = (longest + (8 - (longest % 8)));

      iter = sortedOptions.iterator();
      while (iter.hasNext())
      {
         String key = (String) iter.next();
         Option option = (Option) optionsByShort.get(key);
         String tmp = "  -" + option.getShortMnemonic() + ", --" + option.getLongMnemonic();
         System.out.print(tmp);
         int tabCount = (((longest - (tmp.length() + 1)) / 8) % 8) + 1;
         System.out.print( tabs.substring(0, tabCount) );
         System.out.println(option.getDescription());
      }
   }


   /**
    * Get the option with the specified id.
    *
    * @param id the id of the option
    * @return Returns the option.
    */
   public Option getOptionById(String id)
   {
      return (Option) optionsById.get(id);
   }


   // ======================================================================
   //                   P R O T E C T E D   M E T H O D S
   // ======================================================================

   protected void addOption(Option option)
   {
      if (option.getId() == null || option.getShortMnemonic() == null || option.getLongMnemonic() == null)
      {
         throw new RuntimeException("Option was missing id, short mnemonic, or long mnemonic.");
      }
      optionsById.put(option.getId(), option);
      optionsByShort.put(option.getShortMnemonic(), option);
      optionsByLong.put(option.getLongMnemonic(), option);
   }

   protected void addCommand(Command command)
   {
      if (command.getId() == null || command.getName() == null)
      {
         throw new RuntimeException("Command was missing id or name");
      }
      commands.put(command.getName(), command);
      commandsById.put(command.getId(), command);
   }


   // ======================================================================
   //                    P R I V A T E   M E T H O D S
   // ======================================================================

   /**
    * Handle the parsing of an option.
    *
    * @return true if the trailingArg was consumed, false otherwise
    */
   private boolean parseOption(Option option, String optionString, String trailingArg)
   {
      if (option == null)
      {
         throw new ParseException("Unknown option '" + optionString + "'");
      }

      if (option.getOptionType() != null)
      {
         if (trailingArg == null)
         {
            throw new ParseException("Option '" + optionString + "' requires a trailing parameter.");
         }

         if (trailingArg.startsWith("-"))
         {
            // TODO we have to allow negative number parameters
            throw new ParseException("Parameter to option may not contain a hyphen (-) character.");
         }

         option.setValue(parseOptionArgument(option, trailingArg));
      }

      parsedOptions.add(option);

      return (option.getOptionType() != null);
   }


   /**
    * Handle the parsing of a command.
    *
    * @param cmd the command to handle, or null if an unknown command
    * @param commandString the command as it existed on the command-line
    */
   private void parseCommand(Command cmd, String commandString)
   {
      if (cmd != null)
      {
         if (this.parsedCommand != null)
         {
            throw new ParseException("Invalid command '" + cmd.getName() + "', command " + parsedCommand.getName() + "' was already specified.");
         }
         this.parsedCommand = cmd;
      }
      else
      {
         unparsedArguments.add(commandString);
      }
   }


   /**
    * Validate that the options required for the supplied commands are
    * present.
    *
    */
   private void checkOptions()
   {
      if (parsedCommand == null)
      {
         throw new ParseException("No command specified, and no 'commandless' behavior defined.");
      }

      if (parsedCommand.isAllowOptionless() && parsedOptions.isEmpty())
      {
         return;
      }

      Grouping grouping = parsedCommand.getGrouping();
      if (!grouping.satisfied(parsedOptions))
      {
         throw new ParseException("Incompatible options specified.");
      }
   }


   /**
    * Use reflection to invoke all of the setters.
    *
    */
   private void executeSetters()
   {
      Iterator iter = parsedOptions.iterator();
      while (iter.hasNext())
      {
         Option option = (Option) iter.next();
         try
         {
            String methodName = "set" + toJavaCase(option.getId());
            Class[] paramClazz = (Class[]) classArgArray.get(option.getOptionType());
            Method method = this.getClass().getMethod(methodName, paramClazz);

            Object[] params = null;
            if (option.getOptionType() != null)
            {
               params = new Object[1];
               params[0] = option.getValue();
            }
            method.invoke(this, params);
         }
         catch (Exception e)
         {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
         }
      }
   }


   /**
    * Execute all of the 'do-ers' for the commands specified.
    *
    */
   private void executeCommands()
   {
      try
      {
         String methodName = "do" + toJavaCase(parsedCommand.getId());
         Class[] paramClazz = null;
         Method method = this.getClass().getMethod(methodName, paramClazz);

         Object[] params = null;
         method.invoke(this, params);
      }
      catch (Exception e)
      {
         e.printStackTrace(System.err);
         throw new RuntimeException(e.getMessage());
      }
   }


   private Object parseOptionArgument(Option option, String value)
   {
      Object ret = null;

      String type = option.getOptionType();
      if ("String".equals(type))
      {
         ret = value;
      }
      else
      {
         try
         {
            if ("int".equals(type))
            {
               ret = new Integer(value);
            }
            else if ("long".equals(type))
            {
               ret = new Long(value);
            }
            else if ("double".equals(type))
            {
               ret = new Double(value);
            }
         }
         catch (NumberFormatException nfe)
         {
            Object[] messageArgs = { option.getLongMnemonic(), new Double(option.getMin()), new Double(option.getMax()) };
            String message = MessageFormat.format((String) numericParseMessages.get(type), messageArgs);
            throw new ParseException(message, nfe);
         }
      }
      return ret;
   }


   /**
    * Convert an identifier string to that it can be combined with another string
    * to generate a Java standard identifier.  I.e. if 'linkedList' is passed as
    * a parameter, it is converted to 'LinkedList', so that when combined with a
    * method prefix, like 'set', the result is 'setLinkedList'.
    *
    * @param identifier the identifier to transform
    * @return the transformed identifier
    */
   private String toJavaCase(String identifier)
   {
      return identifier.substring(0, 1).toUpperCase() + identifier.substring(1);
   }


   /**
    * Convenience method to create a an array of classes, containing exactly one
    * class -- the one passed as a parameter.
    *
    * @param clazz the class to encapsulate in an array
    * @return an array of classes, of size 1, containing the class object passed in
    */
   private Class[] toClassArray(Class clazz)
   {
      if (clazz == null)
      {
         return null;
      }

      Class[] array = { clazz };
      return array;
   }


   // ======================================================================
   //                    P A C K A G E   M E T H O D S
   // ======================================================================

   /**
    * @return Returns the className.
    */
   String getClassName()
   {
      return className;
   }


   /**
    * @param className The className to set.
    */
   void setClassName(String clazzName)
   {
      this.className = clazzName;
   }


   /**
    * @return Returns the packageName.
    */
   String getPackageName()
   {
      return packageName;
   }


   /**
    * @param packageName The packageName to set.
    */
   void setPackageName(String pkgName)
   {
      this.packageName = pkgName;
   }


   /**
    * @return Returns the commands.
    */
   LinkedHashMap getCommands()
   {
      return commands;
   }

   Command getCommandById(String id)
   {
      return (Command) commandsById.get(id);
   }
}
