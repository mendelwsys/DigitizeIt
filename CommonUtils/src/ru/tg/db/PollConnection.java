package ru.tg.db;

import java.sql.*;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Simple test pool connection
 */

public class PollConnection
{
	static private long CONN_TIME_OUT = 10 * 60 * 1000;

	static private class PairClass
	{
		public boolean m_busy; //Флаг занятости
		public Connection m_conn;//Соединение
		public long getlasttime;//Время которое прошло со сремени последнего обращение

		public PairClass(Connection conn)
		{
			m_busy = true;
			m_conn = conn;
			getlasttime = System.currentTimeMillis();
		}
	}

	static private List m_con_list = new LinkedList(); //Список связей
	static private boolean wasLoad = false;

	private static Connection getInstance() throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException
	{
		Connection conn = null;
		if (!wasLoad)
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			wasLoad = true;
		}
		conn = DriverManager.getConnection("jdbc:mysql://localhost/gisattrsdb", "root", "root");
		return conn;
	}

	public static void closeResources(Statement stmt, ResultSet rs)
	{
		try
		{
			if (rs != null)
				rs.close();
			rs = null;
			if (stmt != null)
				stmt.close();
			stmt = null;
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}

	public synchronized static Connection getConnection() throws IllegalAccessException, SQLException, InstantiationException, ClassNotFoundException
	{
		for (int i = 0; i < m_con_list.size(); i++)
		{
			PairClass pcl = (PairClass) m_con_list.get(i);
			if (!pcl.m_busy)
			{
				if ((System.currentTimeMillis() - pcl.getlasttime) < CONN_TIME_OUT)
				{
					pcl.m_busy = true;
					pcl.getlasttime = System.currentTimeMillis();
					return pcl.m_conn;
				}
				else
					m_con_list.remove(i);
			}
		}

		PairClass pcl = new PairClass(getInstance());
		m_con_list.add(pcl);
		return pcl.m_conn;
	}

	public synchronized static void ReleaseConnection(Connection conn)
	{
		if (conn == null)
			return;
		try
		{
			conn.setAutoCommit(true);
		}
		catch (SQLException e)
		{
			;
		}

		Iterator iter = m_con_list.iterator();
		while (iter.hasNext())
		{
			PairClass pcl = ((PairClass) iter.next());
			if (pcl.m_conn == conn)
			{
				pcl.m_busy = false;
//				pcl.getlasttime = System.currentTimeMillis();
				break;
			}
		}
	}
}
