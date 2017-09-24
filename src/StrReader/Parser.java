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
			String sql = "SELECT timestamp "
					      + "FROM workstatus ORDER BY timestamp DESC";
			rt = st.executeQuery(sql);
			if(rt.next()){
				currentTime = rt.getString(1);
				currentTime = currentTime.substring(0, currentTime.length() - 2);
			}
			sql = "CREATE TABLE IF NOT EXISTS sss ("
			    + "ID INT PRIMARY KEY, "
			    + "Time DATETIME, "
			    + "S_vibrant INT(1) NOT NULL, "
			    + "S_curr INT(1) NOT NULL, "
			    + "S_ultrasound INT(1) NOT NULL, "
			    + "S_human INT(1) NOT NULL)";
			st.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS hhh("
			    + "ID INT PRIMARY KEY, "
			    + "Time DATETIME, "
			    + "H_vibrant INT(1) NOT NULL, "
			    + "H_curr INT(1) NOT NULL, "
			    + "H_ultrasound INT(1) NOT NULL, "
			    + "H_human INT(1) NOT NULL)";
			st.executeUpdate(sql);
			while(true){
				sql = "SELECT timestamp, vibrant, curr, ultrasound, human "
				    + "FROM workstatus "
				    + "WHERE timestamp > '" + currentTime + "' ORDER BY timestamp DESC";
				rt = st.executeQuery(sql);
				while(rt.next()){
					currentTime = rt.getString(1);
					currentTime = currentTime.substring(0, currentTime.length() - 2);
					int aa = statusJudge(rt.getInt(2), rt.getInt(3), rt.getInt(4), rt.getInt(5));
					sql = "insert into sss (Time, Status) values ('" + currentTime + "', '" + aa + "')";
					st1.executeUpdate(sql);
					int[] a = healthJudge(rt.getInt(2), rt.getInt(3), rt.getInt(4), rt.getInt(5));
					sql = "insert into hhh (Time, H_vibrant, H_curr, H_ultrasound, H_human) values ('" + currentTime + "', '" + a[0] + "', '" + a[1] + "', '" + a[2] + "', '" + a[3] + "')";
					st1.executeUpdate(sql);
				}
				System.out.println(currentTime);
				Thread.sleep(100);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public int[] healthJudge(Integer A, Integer B, Integer C, Integer D){
		int AA = 0;
		int BB = 0;
		int CC = 0;
		int DD = 0;
		int[] v = new int[4];
		if(A == 1 && B == 1 && C == 1 && D == 1){
			AA = 1;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			AA = 1;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			AA = 1;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			AA = 1;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 1 && B == 0 && C == 1 && D == 1){
			AA = 1;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 1 && B == 0 && C == 1 && D == 1){
			AA = 1;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 1 && B == 0 && C == 1 && D == 1){
			AA = 1;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			AA = 0;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			AA = 0;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			AA = 0;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			AA = 1;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			AA = 0;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			AA = 0;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			AA = 0;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			AA = 1;
			BB = 1;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			AA = 0;
			BB = 0;
			CC = 1;
			DD = 1;
		}
		v[0] = AA;
		v[1] = BB;
		v[2] = CC;
		v[3] = DD;
		return v;
	}

	public int statusJudge(Integer A, Integer B, Integer C, Integer D){
		int status = 0;
		if(A == 1 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 1 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 1;
		}
		else if(A == 1 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 1 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			status = 1;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 1 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 1 && B == 1 && C == 1 && D == 1){
			status = 2;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		else if(A == 0 && B == 0 && C == 1 && D == 1){
			status = 0;
		}
		return status;
	}
}