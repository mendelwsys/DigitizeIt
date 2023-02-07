package com.mwlib.app.storages.mem;

import com.mwlib.app.geom.DigitizedWrapperGeom;
import com.mwlib.app.utils.PathUtils;
import com.mwlib.ptrace.IOUtils;
import com.mwlib.ptrace.PathDef;
import ru.ts.factory.IFactory;
import ru.ts.toykernel.attrs.AObjAttrsFactory;
import ru.ts.toykernel.attrs.IAttrs;
import ru.ts.toykernel.attrs.IDefAttr;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.factory.BaseInitAble;
import ru.ts.toykernel.filters.IBaseFilter;
import ru.ts.toykernel.geom.IBaseGisObject;
import ru.ts.toykernel.pcntxt.gui.defmetainfo.MainformMonitor;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.storages.INodeStorage;
import ru.ts.utils.gui.elems.IViewProgress;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple DigitizedStorage
 * com.mwlib.app.storages.mem.DigitizedStorage
 */
public class DigitizedStorage
        extends BaseInitAble
                implements INodeStorage, IPathDefContainer {

    public static final String PO_TRACE_PATH = "PO_TRACE_PATH";
    public static final String PO_TRACE_FILE = "PO_TRACE_FILE";
    public static final String PO_TRACE_FILE_EXT=".pth";

    public IAttrs getObjAttrs(String curveid) throws Exception
	{
		return null;
	}

    protected INodeStorage parent;
	protected PathDef pathDef;
    int size;


    public DigitizedStorage()
    {
    }


    public PathDef getPathDef()
    {
        return pathDef;
    }

    public void setPathDef(PathDef pathDef)
	{
        this.pathDef=pathDef;
        size=0;
        if (pathDef!=null)
        {
            size=1;
            while ((pathDef=pathDef.getNext())!=null)
                size++;
        }
	}



	public int getObjectsCount()
	{
		return size;
	}

	public long getLastModified()
	{
		return -1;
	}

	public IBaseStorage filter(IBaseFilter filter) throws Exception
	{
		if (filter==null)
			return this;
		throw new UnsupportedOperationException("Unsupport operation filter for DigitizedStorage");
	}

    protected PathDefIterator innerIterator;

	public IBaseGisObject getBaseGisByCurveId(String curveId) throws Exception
	{
        IBaseGisObject next;
        if (innerIterator!=null && innerIterator.hasNext())
        {
            next = innerIterator.next();
            if (next.getCurveId().equals(curveId))
                return next;
        }

        innerIterator=new PathDefIterator();
        while (innerIterator.hasNext())
        {
            next = innerIterator.next();
            if (next.getCurveId().equals(curveId))
                return next;
        }

        return null;
	}

	public Iterator<IBaseGisObject> filterObjs(IBaseFilter filter)      throws Exception
	{
//		List<IBaseGisObject> keys = new LinkedList<IBaseGisObject>();
//		Iterator<IBaseGisObject> allkeys = getAllObjects();
//		while (allkeys.hasNext())
//		{
//				IBaseGisObject iGisObject = allkeys.next();
//				if (filter.acceptObject(iGisObject))
//					keys.add(iGisObject);
//		}
//		return keys.iterator();
        return getAllObjects();
	}

    public IBaseStorage getStorageByCurveId(String curveId) throws Exception {
        Iterator<String> it = getCurvesIds();
        while (it.hasNext())
        {
            if (it.next().equals(curveId))
                return this;
        }
        return null;
    }

    public String getNodeId() {
        return getObjName();
    }

    public INodeStorage getParentStorage()
    {
        return parent;
    }

    public void setParentStorage(INodeStorage parent)
    {
        this.parent=parent;
    }

    public Collection<INodeStorage> getChildStorages()
    {
        return new LinkedList<INodeStorage>();
    }

    public IAttrs getDefAttrs()
    {
        throw new UnsupportedOperationException();
    }

    public void setDefAttrs(IAttrs defAttrs)
    {
        throw new UnsupportedOperationException();
    }

    public void setNameConverter(INameConverter nmconverter)
    {
        throw new UnsupportedOperationException();
    }

    public void setObjAttrsFactory(AObjAttrsFactory attrsfactory)
    {
        throw new UnsupportedOperationException();
    }

    public AObjAttrsFactory getObjAttrsFactory()
    {
        throw new UnsupportedOperationException();
    }

    public void rebindByObjAttrsFactory(AObjAttrsFactory attrsfactory) throws Exception
    {
        throw new UnsupportedOperationException();
    }

    public void setStoragesfactory(IFactory<INodeStorage> storagesfactory)
    {
    }

    public void setViewProgress(IViewProgress viewProgress)
    {
    }

    public Object init(Object obj) throws Exception {

        IDefAttr attr=(IDefAttr)obj;
        if (attr.getName().equalsIgnoreCase(PO_TRACE_PATH))
            setPathDef((PathDef)attr.getValue());
        else if (attr.getName().equalsIgnoreCase(PO_TRACE_FILE))
        {
            String path=(String)attr.getValue();

            if (!PathUtils.isAbsolutePath(path, MainformMonitor.workDir) && !PathUtils.isAsDefaultWorkDir(MainformMonitor.workDir) )
                path=MainformMonitor.workDir+File.separator+path;

            if (new File(path).exists())
            {
                DataInputStream is = null;
                try {
                    is = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
                    setPathDef(new IOUtils().loadAllFromStream(is));
                } catch (IOException e) {
                    ;//
                }
                finally {
                    if (is!=null)
                        is.close();
                }
            }

        }

        return null;
    }

    public void saveToStream(DataOutputStream dos) throws Exception
    {
        throw new UnsupportedOperationException();
    }

    public void loadFromStream(DataInputStream dis) throws Exception
    {
        throw new UnsupportedOperationException();
    }

    public Iterator<IBaseGisObject> getAllObjects()
    {
        return new PathDefIterator();
    }

    public Iterator<String> getCurvesIds()
    {
        List<String> rv= new LinkedList<String>();
        for (int ix=0;ix<size;ix++)
            rv.add(String.valueOf(ix));
        return rv.iterator();
    }

    protected class PathDefIterator implements Iterator<IBaseGisObject>
    {
        PathDef next =pathDef;
        int ix;

        public boolean hasNext() {

            return next !=null;
        }

        public IBaseGisObject next() {

            DigitizedWrapperGeom digitizedWrapperGeom = new DigitizedWrapperGeom(String.valueOf(ix),next);
            next = next.getNext();
            ix++;
            return digitizedWrapperGeom;
        }

        public void remove() {
            throw new UnsupportedOperationException("Can't remove from DigitizedStorage");
        }
    }

}
