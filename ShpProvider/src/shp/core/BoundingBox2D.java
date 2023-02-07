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

package shp.core;

public class BoundingBox2D
{      
        
        /* ------------------------------------------------------------------------ */
        /* CLASS MEMBERS                                                            */
        /* ------------------------------------------------------------------------ */
    
        /**
         * The X min coordinate.
         * */
        private double xMin;
        
        /**
         * The Y min coordinate.
         * */
        private double yMin;
        
        /**
         * The X max coordinate.
         * */
        private double xMax;
        
        /**
         * The Y max coordinate.
         * */
        private double yMax;
        
        
        
        /* ------------------------------------------------------------------------ */
        /* PUBLIC FUNCTIONS                                                         */
        /* ------------------------------------------------------------------------ */
    
        /**
         * Initializes the bounding box.
         * */
        public BoundingBox2D()
        {
            xMin = yMin = Double.MAX_VALUE;
            xMax = yMax = Double.MIN_VALUE;
        }
        
        /**
         * Updates the bounding box with the given point. Precisely, if the point
         * formed by x, y is outside the bounding box, the box will be increased to
         * contain the given point.
         * 
         * @param x    The x coordinate.
         * @param y    The y coordinate.
         * */
        public void update(double x, double y)
        {
            if(x < xMin)
            {
                xMin = x;
            }
            
            if(x > xMax)
            {
                xMax = x;
            }
            
            if(y < yMin)
            {
                yMin = y;
            }
            
            if(y > yMax)
            {
                yMax = y;
            }
        }
        
        /**
         * Gets the X min coordinate.
         * 
         * @return The x min coordinate.
         * */
        public double getXMin()
        {
            return xMin;
        }
        
        /**
         * Gets the Y min coordinate.
         * */
        public double getYMin()
        {
            return yMin;
        }
        
        /**
         * Gets the X max coordinate.
         * 
         * @return The X max coordinate.
         * */
        public double getXMax()
        {
            return xMax;
        }
        
        /**
         * Gets the Y max coordinate.
         * 
         * @return 
         * */
        public double getYMax()
        {
            return yMax;
        }
        
        
        /* ------------------------------------------------------------------------ */
        /* PROTECTED FUNCTIONS                                                      */
        /* ------------------------------------------------------------------------ */
        
        
        /* ------------------------------------------------------------------------ */
        /* PRIVATE FUNCTIONS                                                        */
        /* ------------------------------------------------------------------------ */
}
