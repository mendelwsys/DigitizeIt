package ru.ts.toykernel.storages.mem;

import ru.ts.toykernel.attrs.IAttrsPool;
import ru.ts.stream.ISerializer;


/**
 * StreamStorageble Object
 */
public interface IStreamStorageable
{
    public ISerializer getSerializer(IAttrsPool pool);
}
