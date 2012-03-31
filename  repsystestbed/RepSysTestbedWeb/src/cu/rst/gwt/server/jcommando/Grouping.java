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
 * Created on May 26, 2005
 */

package cu.rst.gwt.server.jcommando;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Brett Wooldridge
 *
 */
public abstract class Grouping
{
   protected Set  options;
   protected List groupings;
   protected String id;

   /**
    * Default constructor.
    */
   public Grouping()
   {
      options   = new HashSet();
      groupings = new ArrayList();
   }


   /**
    * Add an option to this grouping.
    *
    * @param option the option to add
    */
   public void addOption(Option option)
   {
      options.add(option);
   }

   /**
    * Get the child "Groupings" of this Grouping.
    *
    * @return Returns the groupings.
    */
   public List getGroupings()
   {
      return groupings;
   }


   /**
    * Get the options in this Grouping.
    *
    * @return Returns the options.
    */
   public Set getOptions()
   {
      return options;
   }


   /**
    * Abstract method implemented by subclasses of Grouping that tests
    * whether all the requirements of the grouping have been satisfied
    * by the supplied options.
    *
    * @param optionSet the set of options parsed from the command-line
    * @return true if the grouping requirement is satisfied.
    */
   public abstract boolean satisfied(Set optionSet);


   // ======================================================================
   //                    P A C K A G E   M E T H O D S
   // ======================================================================

   void setId(String newId)
   {
      this.id = newId;
   }


   String getId()
   {
      return id;
   }


   abstract String generateConstructor();
}
