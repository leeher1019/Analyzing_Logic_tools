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
					      + "FROM unit1_status "
					      + "WHERE ID = (SELECT MAX(ID) FROM unit1_status ORDER BY ID)";
			rt = st.executeQuery(sql);
			if(rt.next()){
				currentTime = rt.getString(1);
				currentTime = currentTime.substring(0, currentTime.length() - 2);
			}
			while(true){
				sql = "SELECT Time, S_加速度, S_电流, S_上料 "
				    + "FROM unit1_status "
				    + "WHERE TIME > '" + currentTime + "' ORDER BY TIME";
				rt = st.executeQuery(sql);
				while(rt.next()){
					currentTime = rt.getString(1);
					currentTime = currentTime.substring(0, currentTime.length() - 2);
					Integer[] a = parse(rt.getInt(2), rt.getInt(3), rt.getInt(4));
					sql = "insert into unit1_health (Time, H_加速度, H_电流, H_上料) values ('" + currentTime + "', '" + a[0] + "', '" + a[1] + "', '" + a[2] + "')";
					st1.executeUpdate(sql);
				}
				System.out.println(currentTime);
				Thread.sleep(10000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public Integer[] parse(Integer A, Integer B, Integer C){
		ArrayList<Integer> AAA = new ArrayList<Integer>();
		ArrayList<Integer> BBB = new ArrayList<Integer>();
		ArrayList<Integer> CCC = new ArrayList<Integer>();
		Integer[] v = new Integer[3];
		if(A ==1&&B ==1){
			AAA.add(1);
			BBB.add(0);
		}
		if(B ==1&&C ==1){
			BBB.add(1);
			CCC.add(1);
		}
		if(A ==0&&B ==1&&C ==1){
			AAA.add(0);
			BBB.add(1);
			CCC.add(1);
		}
		for (int i = 0; i < AAA.size(); i++){
			if(AAA.get(i) == 0){
				v[0] = 0;
				break;
			}
			else
				v[0] = 1;
		}
		for (int i = 0; i < BBB.size(); i++){
			if(BBB.get(i) == 0){
				v[1] = 0;
				break;
			}
			else
				v[1] = 1;
		}
		for (int i = 0; i < CCC.size(); i++){
			if(CCC.get(i) == 0){
				v[2] = 0;
				break;
			}
			else
				v[2] = 1;
		}
		return v;
	}
}
