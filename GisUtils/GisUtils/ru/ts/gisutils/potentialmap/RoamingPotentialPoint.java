/**
 * Created on 05.06.2008 18:38:31 2008 by Syg
 * for project in 'ru.ts.gisutils.potentialmap' of 'test' 
 */
package ru.ts.gisutils.potentialmap;

import ru.ts.gisutils.algs.common.IMCoordinate;
import ru.ts.gisutils.algs.common.MPointZM;

import java.util.Random;

/**
 * @author Syg
 */
public class RoamingPotentialPoint extends MPointZM
{

	public static Random _rnd = new Random();
	
	/* movement vector */
	private double direction; 
	
	/* movement sector */
	private double sector;
	
	private int step;
	
	private double grow_step;
	
	public final int getStep()
    {
    	return step;
    }

	public final void setStep( int step )
    {
    	this.step = step;
    }

	/**
	 * @param x
	 * @param y
	 * @param initval
	 */
	public RoamingPotentialPoint( double x, double y, double initval, double sector, int step )
	{
		super( x, y, 0,initval );
		// any direction
		setDirectionDegrees( _rnd.nextInt( 360 ) );
		// sector from 10 to 15 degree
		this.sector = sector;
		// movement step length
		this.step = step;
		// grow value
		double val =  0.01 + _rnd.nextDouble() * 0.01;
		grow_step = _rnd.nextBoolean() ?  val: -val;
	}

	public RoamingPotentialPoint( IMCoordinate point )
	{
		this( point.getX(), point.getY(), point.getM(), Math.toRadians( 10.0 + _rnd.nextInt(5) ), 3 );
	}

	public RoamingPotentialPoint( IMCoordinate point, double sector, int step )
	{
		this( point.getX(), point.getY(), point.getM(), sector, step );
	}

	public void randomize( double value)
	{
		super.setM(value);
		setDirectionDegrees( _rnd.nextInt( 360 ) );
		setGrow_step( 0.01 + _rnd.nextDouble() * 0.01 );
		setSectorDegrees(_rnd.nextDouble() * 15.0 );
	}
	
	/* get direction in radians */
	public final double getDirection()
    {
    	return direction;
    }

	/* sets direction in degrees */
	public final void setDirectionDegrees( double direction )
    {
    	this.direction = Math.toRadians( direction % 360.0 );
    }

	public final double getSector()
    {
    	return sector;
    }

	/**
	 * set sector in degrees
	 * @param sector new sector value
	 */
	public final void setSectorDegrees( double sector )
    {
    	this.sector = Math.toRadians( sector );
    }

    public final void growUp()
    {
		setM(getM()+grow_step);
    }

	/**
     * @return the grow_step
     */
    public final double getGrow_step()
    {
    	return grow_step;
    }

	/**
     * @param grow_step the grow_step to set
     */
    public final void setGrow_step( double grow_step )
    {
    	this.grow_step = grow_step;
    }

    public final void inverseGrow(  )
    {
    	grow_step = -grow_step;
    }
    
}
