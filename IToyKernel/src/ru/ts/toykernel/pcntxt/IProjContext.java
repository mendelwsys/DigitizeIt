package ru.ts.toykernel.pcntxt;

import ru.ts.gisutils.proj.transform.IMapTransformer;
import ru.ts.utils.data.Pair;
import ru.ts.toykernel.drawcomp.ILayer;
import ru.ts.toykernel.storages.IBaseStorage;
import ru.ts.toykernel.consts.INameConverter;
import ru.ts.factory.IInitAble;

import java.util.List;
import java.util.Map;

/**
 * Контекст проекта, проект представляет собой логическое объедение слоев хранилища (или хранилищ), а так же
 * описательной информации проекта, как то набор трансформаторов координат, название и пр.
 * Project context
 */
public interface IProjContext extends IInitAble
{
	/**
	 * @return layer list of project
	 */
	List<ILayer> getLayerList();

    /**
	 * Трансляторы в и из координат проекта, отображение тип преобразователя -> собственно преобразователь
     * @return reflection <Transformer Units>-><Transformer>
     */
    Map<Pair<String,Boolean>, IMapTransformer> getMapTransformers();

    /**
     * get trasformer from geo coordinates to coordinates of project
     * @return geo transformer
     */
    IMetaInfoBean getProjMetaInfo();

	/**
	 * get storage of project
	 * @return retrun storage of project
	 */
	IBaseStorage getStorage();

	/**
	 * @return name converter
	 */
	INameConverter getNameConverter();

	/**
	 * @return project location (in general case it's string reprecentation of URL)
	 */
	String getProjectlocation();

	/**
	 * set project location
	 * @param projectlocation project location
	 */
	void setProjectlocation(String projectlocation);
}
