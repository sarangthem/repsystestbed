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
 * Copyright (c) 2005, IBM THINKPAD
 * Created on Jun 9, 2005
 */

package cu.rst.gwt.server.jcommando;

/**
 * @author Brett Wooldridge
 *
 */
public class ParseException extends RuntimeException
{
   private static final long serialVersionUID = -5246458546638206705L;

   /**
    * Throw a parse exception with the supplied message.
    *
    * @param message the exception message
    */
   public ParseException(String message)
   {
      super(message);
   }

   /**
    * Throw a parse exception the the supplied message and throwable.
    *
    * @param message the exception message
    * @param t the nested throwable
    */
   public ParseException(String message, Throwable t)
   {
      super(message, t);
   }
}
