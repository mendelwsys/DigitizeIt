package su.web;

/**
 * Created by IntelliJ IDEA.
 * User: VMendelevich
 * Date: 29.03.2012
 * Time: 20:01:06
 * TODO Возможно дописать манагер для сессий что бы быть независимым от настроек браузера
 */
public class Sessstor
{
	private static Sessstor stor=new Sessstor();
	public static synchronized Sessstor getSessionStore()
	{
		return stor;
	}

	public Object getAttribute(String attrname)
	{
	  return null;
	}

	public void setAttribute(String attrname,Object obj)
	{

	}
}
