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

/**
 * This class encapsulates the state of an 'option' specified in a JCommando
 * XML file.
 *
 * @author Brett Wooldridge
 */
public class Option
{
   private String shortMnemonic;
   private String longMnemonic;
   private String optionType;
   private String description;
   private String id;
   private Object value;

   private double min;
   private double max;

   // ================================================================================
   //                         P U B L I C   M E T H O D S
   // ================================================================================

   /**
    * Default constructor.
    *
    */
   public Option()
   {
      min = Double.MIN_VALUE;
      max = Double.MAX_VALUE;
   }


   /**
    * Get the textual description of this option.
    *
    * @return Returns the description.
    */
   public String getDescription()
   {
      return description;
   }


   /**
    * Get the long form of this option.
    *
    * @return Returns the longMnemonic.
    */
   public String getLongMnemonic()
   {
      return longMnemonic;
   }


   /**
    * Get the Java type of this option.  The returned string is one of: long, double, String
    *
    * @return Returns the optionType.
    */
   public String getOptionType()
   {
      return optionType;
   }


   /**
    * Get the short form of this option.
    *
    * @return Returns the shortMnemonic.
    */
   public String getShortMnemonic()
   {
      return shortMnemonic;
   }


   /**
    * Set the textual description of this option.
    *
    * @param desc The description to set.
    */
   public void setDescription(String desc)
   {
      this.description = desc;
   }


   /**
    * Set the long form of this option.
    *
    * @param lMnemonic The longMnemonic to set.
    */
   public void setLongMnemonic(String lMnemonic)
   {
      this.longMnemonic = lMnemonic;
   }


   /**
    * Set the Java type of this option.  This string must be one of: long, double, String.
    *
    * @param type The optionType to set.
    */
   public void setOptionType(String type)
   {
      if ("long".equals(type))
      {
         min = Long.MIN_VALUE;
         max = Long.MAX_VALUE;
      }
      else if ("double".equals(type))
      {
         min = Double.MIN_VALUE;
         max = Double.MAX_VALUE;
      }

      if ("long".equals(type) || "double".equals(type) || "String".equals(type) || type == null)
      {
         this.optionType = type;
      }
      else
      {
         throw new ParseException("Type '" + type + "' is not a valid type.");
      }
   }


   /**
    * Set the short form of this option.
    *
    * @param sMnemonic The shortMnemonic to set.
    */
   public void setShortMnemonic(String sMnemonic)
   {
      this.shortMnemonic = sMnemonic;
   }


   /**
    * Get the unique identifier for this option.
    *
    * @return Returns the id.
    */
   public String getId()
   {
      return id;
   }


   /**
    * Set the unique identifier for this option.  This identifier must be a valid
    * Java identifier.
    *
    * @param optionId The id to set.
    */
   public void setId(String optionId)
   {
      this.id = optionId;
   }


   /**
    * Get the maximum valid value of this option.  If not set, or not applicable, this
    * returns Long.MAX_VALUE if the 'type' is long, or Double.MAX_VALUE if the 'type' is
    * double.
    *
    * @return Returns the max.
    */
   public double getMax()
   {
      return max;
   }


   /**
    * Set the maximum valid value of this option.  Only applicable for options where the type
    * is long or double.
    *
    * @param d The max to set.
    */
   public void setMax(double d)
   {
      this.max = d;
   }


   /**
    * Get the minumum valid value of this option.  If not set, or not applicable, this
    * returns Long.MAX_VALUE if the 'type' is long, or Double.MAX_VALUE if the 'type' is
    * double.
    *
    * @return Returns the min.
    */
   public double getMin()
   {
      return min;
   }


   /**
    * Set the minumum valid value of this option.  Only applicable for options where the type
    * is long or double.
    *
    * @param d The min to set.
    */
   public void setMin(double d)
   {
      this.min = d;
   }


   // ================================================================================
   //                         P A C K A G E   M E T H O D S
   // ================================================================================

   Object getValue()
   {
      return value;
   }


   void setValue(Object val)
   {
      this.value = val;
   }


   String generateSetters()
   {
      StringBuffer sb = new StringBuffer();
      if (id != null)
      {
         sb.append("      " + id + ".setId(\"");
         sb.append(id + "\");\n");
      }
      if (shortMnemonic != null)
      {
         sb.append("      " + id + ".setShortMnemonic(\"");
         sb.append(shortMnemonic + "\");\n");
      }
      if (longMnemonic != null)
      {
         sb.append("      " + id + ".setLongMnemonic(\"");
         sb.append(longMnemonic + "\");\n");
      }
      if (description != null)
      {
         sb.append("      " + id + ".setDescription(\"");
         sb.append(description + "\");\n");
      }
      if (optionType != null)
      {
         sb.append("      " + id + ".setOptionType(\"");
         sb.append(optionType + "\");\n");
      }
      if (optionType != null && !"String".equals(optionType))
      {
         sb.append("      " + id + ".setMin(" + (min == Double.MIN_VALUE ? "Double.MIN_VALUE" : min + "d") + ");\n");
         sb.append("      " + id + ".setMax(" + (max == Double.MAX_VALUE ? "Double.MAX_VALUE" : max + "d") + ");\n");
      }

      return sb.toString();
   }
}
