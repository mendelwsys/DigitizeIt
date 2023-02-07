/*
 * Created on 11 Oct 2007
 *
 */
package ru.ts.gisutils.tc;

import ru.ts.gisutils.geometry.IGetXY;

/**
 * @author yugl
 *
 * Интерфейс представляет геометрический объект, который после топологической 
 * чистки будут использован для построения узла в сетевом графе.
 */
public interface ITcObjNode extends ITcObjBase, IGetXY {

}