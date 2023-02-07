/* 
 *  Copyright 2012 Oliver Dozsa
 *      
 *  This program is free software: you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package shp.exception;

public class ShpException extends Exception
{    

    /* ------------------------------------------------------------------------ */
    /* CLASS MEMBERS                                                            */ 
    /* ------------------------------------------------------------------------ */
    
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -32668998608596146L;
    
    
    
    /* ------------------------------------------------------------------------ */
    /* PUBLIC FUNCTIONS                                                         */ 
    /* ------------------------------------------------------------------------ */
    
    /**
     * A wrapper over the base class constructor.
     * 
     * @param message    The message.
     * */
    public ShpException(String message)
    {
        super(message);
    }
    
    
    /* ------------------------------------------------------------------------ */
    /* PRIVATE FUNCTIONS                                                        */ 
    /* ------------------------------------------------------------------------ */
}
