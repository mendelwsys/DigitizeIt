package ru.ts.toykernel.proj.stream.def;

import ru.ts.toykernel.pcntxt.IProjContext;
import ru.ts.toykernel.converters.ConvB64Initializer;
import ru.ts.stream.ISerializer;

/**
 * Сериализуемый в старый формат проект
 */
public interface IStreamAbleProj extends IProjContext, ISerializer
{
	ConvB64Initializer getConvInitializer();
}
