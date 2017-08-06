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
				sql = "SELECT Time, S_加速度, S_电流, S_超声波 "
				    + "FROM unit1_status "
				    + "WHERE TIME > '" + currentTime + "' ORDER BY TIME";
				rt = st.executeQuery(sql);
				while(rt.next()){
					currentTime = rt.getString(1);
					currentTime = currentTime.substring(0, currentTime.length() - 2);
					int aa = statusJudge(rt.getInt(2), rt.getInt(3), rt.getInt(4));
					sql = "insert into unit1_status_final (Time, Status) values ('" + currentTime + "', '" + aa + "')";
					st1.executeUpdate(sql);
					int[] a = healthJudge(rt.getInt(2), rt.getInt(3), rt.getInt(4));
					sql = "insert into unit1_health (Time, H_加速度, H_电流, H_超声波) values ('" + currentTime + "', '" + a[0] + "', '" + a[1] + "', '" + a[2] + "')";
					st1.executeUpdate(sql);
				}
				System.out.println(currentTime);
				Thread.sleep(10000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public int[] healthJudge(Integer A, Integer B, Integer C){
		int AA = 0;
		int BB = 0;
		int CC = 0;
		int[] v = new int[3];
		if(A == 0 && B == 0 && C == 0){
			AA = 1;
			BB = 1;
			CC = 1;
		}
		else if(A == 0 && B == 0 && C == 1){
			AA = 0;
			BB = 0;
			CC = 1;
		}
		else if(A == 0 && B == 1 && C == 0){
			AA = 0;
			BB = 0;
			CC = 0;
		}
		else if(A == 0 && B == 1 && C == 1){
			AA = 0;
			BB = 0;
			CC = 0;
		}
		else if(A == 1 && B == 0 && C == 0){
			AA = 0;
			BB = 0;
			CC = 0;
		}
		else if(A == 1 && B == 0 && C == 1){
			AA = 0;
			BB = 0;
			CC = 1;
		}
		else if(A == 1 && B == 1 && C == 0){
			AA = 0;
			BB = 0;
			CC = 0;
		}
		else if(A == 1 && B == 1 && C == 1){
			AA = 0;
			BB = 0;
			CC = 1;
		}
		else if(A == 2 && B == 0 && C == 0){
			AA = 0;
			BB = 0;
			CC = 0;
		}
		else if(A == 2 && B == 0 && C == 1){
			AA = 0;
			BB = 0;
			CC = 1;
		}
		else if(A == 2 && B == 1 && C == 0){
			AA = 0;
			BB = 0;
			CC = 0;
		}
		else if(A == 2 && B == 1 && C == 1){
			AA = 1;
			BB = 1;
			CC = 0;
		}
		v[0] = AA;
		v[1] = BB;
		v[2] = CC;
		return v;
	}

	public int statusJudge(Integer A, Integer B, Integer C){
		int status = 0;
		if(A == 0 && B == 0 && C == 0){
			status = 0;
		}
		else if(A == 0 && B == 0 && C == 1){
			status = 0;
		}
		else if(A == 0 && B == 1 && C == 0){
			status = 0;
		}
		else if(A == 0 && B == 1 && C == 1){
			status = 0;
		}
		else if(A == 1 && B == 0 && C == 0){
			status = 0;
		}
		else if(A == 1 && B == 0 && C == 1){
			status = 0;
		}
		else if(A == 1 && B == 1 && C == 0){
			status = 0;
		}
		else if(A == 1 && B == 1 && C == 1){
			status = 0;
		}
		else if(A == 2 && B == 0 && C == 0){
			status = 0;
		}
		else if(A == 2 && B == 0 && C == 1){
			status = 0;
		}
		else if(A == 2 && B == 1 && C == 0){
			status = 0;
		}
		else if(A == 2 && B == 1 && C == 1){
			status = 1;
		}
		return status;
	}
}