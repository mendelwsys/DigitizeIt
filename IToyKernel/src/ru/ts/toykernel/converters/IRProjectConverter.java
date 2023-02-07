package ru.ts.toykernel.converters;

import ru.ts.toykernel.converters.IProjConverter;
import ru.ts.toykernel.converters.ILinearConverter;


/**
 * Ковертер проекта с растровыми слоями
 */
public interface IRProjectConverter
		extends IProjConverter
{
	ILinearConverter getSrc2SyncConverter();
	ILinearConverter getSync2DstConverter();
}
