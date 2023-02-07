package com.mwlib.app.storages.mem;

import com.mwlib.ptrace.PathDef;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 04.01.14
 * Time: 21:34
 * To change this template use File | Settings | File Templates.
 */
public interface IPathDefContainer
{
    PathDef getPathDef();
    void setPathDef(PathDef pathDef);
}
