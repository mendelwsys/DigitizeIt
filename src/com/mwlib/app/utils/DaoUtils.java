package com.mwlib.app.utils;

/**
 * Created by IntelliJ IDEA.
 * User: VMendelevich
 * Date: 19.12.11
 * Time: 15:08
 *
 */

import ru.tg.db.PollConnection;

import java.sql.*;
import java.util.*;

public final class DaoUtils {

//    public static void main(String[] args) {
//        test2(args);
//    }

//    public static void test2(String[] args) {
//
//        Map<String, String> params = DaoUtils.createRefMap(new String[][]{{"userId", "LOGIN"}, {"comment", "PERSON_COMMENT"}});
//        Object fss = new FssUser();
//        setObjectBySettersParams(fss, params, ToCase.notChange);
//
//        System.out.println("fss = " + fss);
//    }

//    public static void test1(String[] args) {
//        FssUser user = new FssUser("12", "23", "34", "56", "78", "344", "1223234", "QWERTY","1");
//
//        Map<String, Object> attrMap = DaoUtils.object2paramMap(user, ToCase.ToUpperCase, DaoUtils.createRefMap(new String[][]{{"USERID", "LOGIN"}, {"COMMENT", "PERSON_COMMENT"}}));
//        for (String s : attrMap.keySet()) {
//            System.out.println("s = " + s);
//        }
//    }

    static public interface ResultProcessor {
        void processResult(ResultSet result) throws SQLException;

        void processConn(Connection con) throws SQLException;
    }

    static public class CommitResultProcessor implements ResultProcessor {
        public void processResult(ResultSet result) throws SQLException {
        }

        public void processConn(Connection con) throws SQLException {
            con.commit();
        }
    }

    static public abstract class DummyResultProcessor implements ResultProcessor {

        public void processConn(Connection con) throws SQLException {
        }
    }

    static public Set<String> querySet(final String queryToRun, final int resultLimit) {
        final Set<String> rv = new HashSet<String>();
        try {
            query(queryToRun, new DaoUtils.DummyResultProcessor() {
                public void processResult(ResultSet result) throws SQLException {
                    while (result.next()) {
                        if (resultLimit >= 0 && rv.size() > resultLimit) {
//                           if (debug.messageEnabled())
//                               debug.message("limit of query is exceeds: limit:" + resultLimit+" queryToRun:"+queryToRun);
                            break;
                        }
                        rv.add(result.getString(1));
                    }
                }
            });
        } catch (Exception ex1) {
//            if (debug.messageEnabled()) {
//                debug.message("JdbcSimpleRoleDao.search:" + ex1);
//            }
            throw new RuntimeException(ex1);
        }
        if (rv.isEmpty())
            return Collections.EMPTY_SET;
        return rv;
    }


    static public void callProc(Map<String, Object> attrMap, String pkg_name, String proc_name, String proc_prefix)
    {
        //Get metadata for procedure request
        String metadatareq = "SELECT ARGUMENT_NAME,POSITION,DEFAULTED " +
                "  FROM SYS.ALL_ARGUMENTS " +
                "  WHERE PACKAGE_NAME = '" + pkg_name +
                "'  AND " +
                "  OBJECT_NAME = '" + proc_name + "'";


        Map<Integer, String[]> pos2argname_def = new TreeMap<Integer, String[]>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        //RFE: later deal better with various types
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(metadatareq);
            while (rs.next())
                pos2argname_def.put(rs.getInt(2), new String[]{rs.getString(1), rs.getString(3)});
        } catch (Exception ex1) {
//            if (debug!=null && debug.messageEnabled())  {
//                debug.message("JdbcSimpleUserDao.createUser:" + ex1);
//            }
            throw new RuntimeException(ex1);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
            closeConnection(con);
        }

        String procparams = "";
        int posinparams = 1;
        Map<Integer, Object> ix2val = new HashMap<Integer, Object>();
        for (int ix : pos2argname_def.keySet()) {
            String[] argname_def = pos2argname_def.get(ix);
            String parname = argname_def[0];
            Object val = attrMap.get(parname);
            if (val == null && parname.startsWith(proc_prefix))
                val = attrMap.get(parname.substring(proc_prefix.length()));
            //get the value of the attribute
            if (val != null) {
                if (procparams.length() > 0)
                    procparams += ",";
                procparams += "?";
                ix2val.put(posinparams, val);
                posinparams++;
            } else if (argname_def[1].equalsIgnoreCase("Y"))
                break;
            else { //Параметр не определен, возможно это выходной параметр
                if (procparams.length() > 0)
                    procparams += ",";
                procparams += "?";
                posinparams++;
            }
        }
        procparams = "{call " + pkg_name + "." + proc_name + "(" + procparams + ")}";
        CallableStatement pstmt = null;
        //RFE: later deal better with various types
        try {
            con = getConnection();
            pstmt = con.prepareCall(procparams);

            ParameterMetaData md = pstmt.getParameterMetaData();
            int cnt = md.getParameterCount();
            for (int ix = 1; ix <= cnt; ix++) {
                if (ix2val.containsKey(ix)) {
                    Object o = ix2val.get(ix);
                    pstmt.setString(ix, o.toString());
                } else {
//                    if (debug!=null && debug.messageEnabled())
//                        debug.message("Can't find duty parameters for call procedure : " + proc_name + " paramix:" + ix + " set null");
                    pstmt.setNull(ix, Types.VARCHAR);
                }
            }
            pstmt.execute();
            con.commit();
        } catch (Exception ex1) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.createUser:" + ex1);
//            }
            throw new RuntimeException(ex1);
        } finally {
            closeStatement(pstmt);
            closeConnection(con);
        }
    }


    static public void callProc2(Map<String, Object> attrInMap,Map<String, Object> attrOutMap, String pkg_name, String proc_name, String proc_prefix)
    {
        //Get metadata for procedure request
        String metadatareq = "SELECT ARGUMENT_NAME,POSITION,DEFAULTED " +
                "  FROM SYS.ALL_ARGUMENTS " +
                "  WHERE PACKAGE_NAME = '" + pkg_name +
                "'  AND " +
                "  OBJECT_NAME = '" + proc_name + "'";


        Map<Integer, String[]> pos2argname_def = new TreeMap<Integer, String[]>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        //RFE: later deal better with various types
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(metadatareq);
            while (rs.next())
                pos2argname_def.put(rs.getInt(2), new String[]{rs.getString(1), rs.getString(3)});
        } catch (Exception ex1) {
//            if (debug!=null && debug.messageEnabled())  {
//                debug.message("JdbcSimpleUserDao.createUser:" + ex1);
//            }
            throw new RuntimeException(ex1);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
            closeConnection(con);
        }

        String procparams = "";
        int posinparams = 1;
        Map<Integer, Object> ix2val = new HashMap<Integer, Object>();
        Map<String, Integer> parnameout2ix = new HashMap<String, Integer>();

        for (int ix : pos2argname_def.keySet()) {
            String[] argname_def = pos2argname_def.get(ix);
            String parname = argname_def[0];

            Object val = attrInMap.get(parname);
            if (val == null && parname.startsWith(proc_prefix))
                val = attrInMap.get(parname.substring(proc_prefix.length()));

            if (attrOutMap.containsKey(parname))
                parnameout2ix.put(parname,posinparams);
            else if (attrOutMap.containsKey(parname.substring(proc_prefix.length())))
                parnameout2ix.put(parname.substring(proc_prefix.length()),posinparams);

            //get the value of the attribute
            if (val != null) {
                if (procparams.length() > 0)
                    procparams += ",";
                procparams += "?";
                ix2val.put(posinparams, val);
                posinparams++;
            } else if (argname_def[1].equalsIgnoreCase("Y"))
                break;
            else { //Параметр не определен, возможно это выходной параметр
                if (procparams.length() > 0)
                    procparams += ",";
                procparams += "?";
                posinparams++;
            }
        }
        procparams = "{call " + pkg_name + "." + proc_name + "(" + procparams + ")}";
        CallableStatement pstmt = null;
        //RFE: later deal better with various types
        try {
            con = getConnection();
            pstmt = con.prepareCall(procparams);

            ParameterMetaData md = pstmt.getParameterMetaData();
            int cnt = md.getParameterCount();
            for (int ix = 1; ix <= cnt; ix++) {
                if (ix2val.containsKey(ix)) {
                    Object o = ix2val.get(ix);
                    pstmt.setString(ix, o.toString());
                } else {
//                    if (debug!=null && debug.messageEnabled())
//                        debug.message("Can't find duty parameters for call procedure : " + proc_name + " paramix:" + ix + " set null");
                    pstmt.setNull(ix, Types.VARCHAR);
                }
            }

            for (String parname : parnameout2ix.keySet())
                      pstmt.registerOutParameter(parnameout2ix.get(parname), Types.VARCHAR);

            pstmt.execute();

            for (String parname : parnameout2ix.keySet()) {
                Object val = pstmt.getObject(parnameout2ix.get(parname));
                attrOutMap.put(parname, val);
            }


            con.commit();



        } catch (Exception ex1) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.createUser:" + ex1);
//            }
            throw new RuntimeException(ex1);
        } finally {
            closeStatement(pstmt);
            closeConnection(con);
        }
    }


    static public void query(String queryToRun, ResultProcessor processor) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(queryToRun);
            result = stmt.executeQuery();
            processor.processResult(result);
            processor.processConn(con);
        } catch (SQLException e) {
//            if (debug!=null && debug.messageEnabled())
//                debug.message("JdbcSimpleRoleDao.search:" + e);
            throw e;
        } finally {
            closeResultSet(result);
            closeStatement(stmt);
            closeConnection(con);
        }
    }


    static public Connection getConnection() throws SQLException {
        try {
            return PollConnection.getConnection();
        //} catch (ClassNotFoundException e) {
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }


    static public void closeConnection(Connection dbConnection) {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
        } catch (SQLException se) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.closeConnection: SQL Exception"
//                        + " while closing DB connection: \n" + se);
//            }
        }
    }

    //should I catch all Exceptions instead of just SQL ????? I think so
    static public void closeResultSet(ResultSet result) {
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException se) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.closeResultSet: SQL Exception"
//                        + " while closing Result Set: \n" + se);
//            }
        }
    }

    //should I catch all Exceptions instead of just SQL ????? I think so
    static public void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.closeStatement: SQL Exception"
//                        + " while closing Statement : \n" + se);
//            }
        }
    }

}
