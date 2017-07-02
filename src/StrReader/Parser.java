package StrReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
public class Parser{
	Connection dbConn;
	ResultSet rt;
	public void main(String dbURL, String userName, String userPwd){
		try{
		SimpleDateFormat spf = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
		String currentTime = spf.format(new Date().getTime());
		dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
		Statement st = dbConn.createStatement();
		Statement st1 = dbConn.createStatement();
		String sql = "SELECT Time "
				      + "FROM source "
				      + "WHERE ID = (SELECT MAX(ID) FROM source ORDER BY ID)";
		rt = st.executeQuery(sql);
		if(rt.next())
			currentTime = rt.getString(1);
		currentTime = currentTime.substring(0, currentTime.length() - 2);
		while(true){
			sql = "SELECT Time, S_加速度, S_电流 "
			    + "FROM source "
			    + "WHERE TIME >= '" + currentTime + "'";
			rt = st.executeQuery(sql);
			while(rt.next()){
				currentTime = rt.getString(1);
				currentTime = currentTime.substring(0, currentTime.length() - 2);
				Integer[] a = parse(rt.getInt(2), rt.getInt(3));
				sql = "insert into result (Time, H_加速度, H_电流) values ('" + currentTime + "', '" + a[0] + "', '" + a[1] + "')";
				st1.executeUpdate(sql);
			}
			System.out.println(currentTime);
			Thread.sleep(10000);
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	}
	public Integer[] parse(Integer A, Integer B){
		int AA = 0;
		int BB = 0;
		Integer[] v = new Integer[2];
		if(A ==0||B ==1){
			AA = 0;
			BB = 0;
		}
		v[0] = AA;
		v[1] = BB;
		return v;
	}
}
