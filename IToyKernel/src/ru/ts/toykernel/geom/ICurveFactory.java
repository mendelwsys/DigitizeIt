package ru.ts.toykernel.geom;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 02.04.2008
 * Time: 16:17:04
 * Changes: yugl, 30.04.2008,
 * исходная функциональность - работа с кривыми (линиями?).
 * doing refactoring, added commentaries, changed dependencies, reduced functionality
 */
public interface ICurveFactory
{
	IBaseGisObject createEmptyCurve() throws Exception;

	
}