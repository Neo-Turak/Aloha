package cn.nurasoft.aloha;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

class SQLHelper {

     private  String IP;
     private  String DBName;
     private  String USER;
     private  String PWD;

     public SQLHelper(String ip,String dbname,String usr,String pwd) {
        IP=ip;
        DBName=dbname;
        USER=usr;
        PWD=pwd;
    }


    /** 创建数据库对象 */
    private  Connection getSQLConnection() {
        Connection con = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //加上 useunicode=true;characterEncoding=UTF-8 防止中文乱码
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + IP + ":1433/" + DBName + ";useunicode=true;characterEncoding=UTF-8", USER, PWD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /** 查询数据库 */
    public  String Query() {
        String result = "";
        try {
            Connection conn = getSQLConnection();
            String sql = "select 1*2 as result";
            Statement stmt = conn.createStatement();//
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                 result = rs.getString("result");
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            result += "查询数据异常!" + e.getMessage();
        }
        return result;
    }

     void Add(float celicus, String d){
        try{
            Connection cn=getSQLConnection();
            String sql="insert into life values(" +celicus +",'"+d+"');";
            Statement st=cn.createStatement();
            st.executeUpdate(sql);
            st.close();
            cn.close();
        }catch (Exception e){
            Log.e("Add exception:",e.getMessage());
        }
         }
     int count(){
         int c=0;
         try {
             Connection cn=getSQLConnection();
             String sql="select count(id) from life";
             Statement st=cn.createStatement();
             ResultSet rs=st.executeQuery(sql);
             while (rs.next()){
                 c=rs.getInt(1);
             }
             st.close();
             cn.close();
             rs.close();
         }catch (Exception e){
             Log.e("Count:",e.getMessage());
         }
         return c;
    }

   public  ArrayList<LifeUtil> all(){
        ArrayList<LifeUtil> list=new ArrayList<LifeUtil>();
        LifeUtil life;
        try{
            Connection cn=getSQLConnection();
            String sql="select * from life order by id asc";
            Statement st=cn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while (rs.next()){
                life=new LifeUtil(rs.getString("Celcius"),rs.getString("date"));
                list.add(life);
            }
            rs.close();
            st.close();
            cn.close();
        }catch (Exception e){
            Log.e("All:",e.getMessage());
        }
        return list;
    }
}