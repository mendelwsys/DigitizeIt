package ru.ts.toykernel.geom.def.stream;

import ru.ts.toykernel.geom.def.EditableCurve;
import ru.ts.toykernel.geom.def.ROCurve;
import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.toykernel.storages.mem.IStreamStorageable;
import ru.ts.stream.ISerializer;

/**
 * Editbale curve that can be stored in MemStorageLr
 */
public class StreamEditableCurve extends EditableCurve implements IStreamStorageable
{
    private EditableImpl serializer;

	public StreamEditableCurve(String geotype, String curveId, IAttrsPool attrsPool, INameConverter storNm2CodeNm,String parentId)
	{
		super(geotype, curveId, attrsPool, storNm2CodeNm,parentId);
	}

	public StreamEditableCurve(INameConverter storNm2CodeNm)
	{
		super(storNm2CodeNm);
	}

	public ISerializer getSerializer(IAttrsPool pool)
    {
        if (serializer==null)
            serializer = new EditableImpl(this,pool);
        return serializer;
    }


	protected static class EditableImpl extends StreamROCurve.ROImpl
	{
		public EditableImpl(ROCurve curve,IAttrsPool pool)
		{
			super(curve,pool);
		}
	}
}
