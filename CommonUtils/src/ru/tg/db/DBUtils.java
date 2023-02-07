package ru.tg.db;

import java.sql.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: vladm
 * Date: 22.06.2011
 * Time: 16:18:40
 * Утилита установки объекта из данных
 */
public class DBUtils
{

	public static Connection _getConnection( String driver, String prefix,
	        String dbPath, Properties props ) throws SQLException,
	        InstantiationException, IllegalAccessException,
	        ClassNotFoundException

	{
		Class.forName( driver ).newInstance();
		if ( props != null )
			return DriverManager.getConnection( prefix + dbPath, props );
		else
			return DriverManager.getConnection( prefix + dbPath );
	}


	public static <T> Collection<T> setObjectsBySQL(Connection conn,String sql,T obj)
			throws Exception
	{

		List<T> ll = new LinkedList<T>();

		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(setObjectBySQL(rs,obj)!=null)
			{
				ll.add(obj);
				obj= (T) obj.getClass().newInstance();
			}
			return ll;
		}
		finally
		{
			if (rs!=null)
				rs.close();
			if (stmt!=null)
				stmt.close();
		}
	}

	public static <T> T setObjectBySQL(ResultSet rs,T obj)
			throws Exception
	{
		if (rs.next())
		{
			Field[] fld= obj.getClass().getFields();
			for (Field field : fld)
				field.set(obj,rs.getString(field.getName()));
			return obj;
		}
		else
			return null;
	}

}
