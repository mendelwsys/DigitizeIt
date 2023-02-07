package ru.ts.gisutils.proj.transform;

import java.io.*;

/**
 * Transformers factory for two type of transformers
 */
public class TrasformerFactory
{
	//Transformer types
	public static String GEOTRANFORMER="GEOTRANFORMER";
	public static String USERRANFORMER="USERRANFORMER";

	public static IMapTransformer createTransformer(DataInputStream dis)
			throws Exception
	{
		String transformer=dis.readUTF();
		IMapTransformer mapTransformer = getTransformerByType(transformer);
		return mapTransformer.loadTransformer(dis);
	}

	public static IMapTransformer createTransformerByTextFile(InputStream is, String charsetName) throws Exception
	{
		InputStreamReader irs;
		if (charsetName == null)
			irs = new InputStreamReader(is);
		else
			irs = new InputStreamReader(is, charsetName);
		BufferedReader isr = new BufferedReader(irs);
		String  transformerType=isr.readLine();
		IMapTransformer rv=getTransformerByType(transformerType);
		return rv.loadTranformerByTextFile(isr,charsetName);
	}

	public static IMapTransformer getTransformerByType(String transformerType)
	{
		IMapTransformer mapTransformer = null;
		if (transformerType.equals(GEOTRANFORMER))
			mapTransformer = new MapTransformer();
		else
			mapTransformer = new DefMapTransformer();
		return mapTransformer;
	}
}
