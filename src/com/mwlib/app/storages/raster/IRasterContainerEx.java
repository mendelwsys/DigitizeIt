package com.mwlib.app.storages.raster;

import ru.ts.toykernel.storages.raster.IRasterContainer;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 05.05.14
 * Time: 23:14
 * расширение для контейнера растров, должно содержать разрешение
 */
public interface IRasterContainerEx extends IRasterContainer
{
    double getResolution();
    void setResolution(double  resolution);
}
