package StrReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import com.csvreader.*;


public class sample {
	public static void main(String[] args) throws Exception{
		sample main = new sample();
		Properties property = new Properties();
		FileInputStream is = new FileInputStream(System.getProperty("user.dir") + "\\src\\conf.properties");
		property.load(is);
		main.driverName = property.getProperty("driverName");
		main.dbURL = property.getProperty("dbURL");
		main.userName = property.getProperty("userName");
		main.userPwd = property.getProperty("userPwd");
		
		
		System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_131\\jre");
		
		System.out.println("1為.txt，2為.csv");
		Scanner scan = new Scanner(System.in);
		int chooseFormat = scan.nextInt();
		
		if (chooseFormat == 1){
			main.readfileFromTxt(System.getProperty("user.dir") + "\\src\\unit1_health-1.txt");  
			main.createParser();
		}
		else if (chooseFormat == 2)
			main.readfileFromCsv(".\\src\\unit1_TruthTable.csv");  	
		
		main.dynamicCompile();
	}
	
	public void readfileFromCsv(String filepath){
		Scanner scan = new Scanner(System.in);
		System.out.println("狀態Table Name");
		String sTableName = scan.nextLine();
		System.out.println("最終狀態Table Name");
		String sTableName_final = scan.nextLine();
		System.out.println("健康Table Name");
		String hTableName = scan.nextLine();
		System.out.println("傳感器數量");
		int sensor = scan.nextInt();
		
		ArrayList<String> sensorName = new ArrayList<String>();
		for (int i = 0; i < sensor; i++){
			scan = new Scanner(System.in);
			System.out.println("第" + (i + 1) +"傳感器為");
			sensorName.add(scan.nextLine());
		}
		
		String csv = filepath;
		ArrayList<String[]> truthTableRow = new ArrayList<String[]>();
		ArrayList<String[]> iflist = new ArrayList<String[]>();
		ArrayList<String[]> thenlist = new ArrayList<String[]>();
		ArrayList<String> status = new ArrayList<String>();/*
		ArrayList<String> iflistRow = new ArrayList<String>();
		ArrayList<String> thenlistRow = new ArrayList<String>();*/
		String[] iflistRow = new String[sensor];
		String[] thenlistRow = new String[sensor];
		
		
		try{
			CsvReader reader = new CsvReader(System.getProperty("user.dir") + "\\src\\unit1_TruthTable.csv", ',', Charset.forName("MS950"));		
			reader.readHeaders();
			while (reader.readRecord()){				
				for(int i = 0; i < sensor; i++){
					iflistRow[i] = reader.get(sensorName.get(i));
					thenlistRow[i] = reader.get(sensorName.get(i));
				}
				iflist.add(iflistRow);
				thenlist.add(thenlistRow);
				status.add(reader.get("Status"));				
				iflistRow = new String[sensor];
				thenlistRow = new String[sensor];
			}
			reader.close();
			//--------------------------------------------
			File file = new File("src\\strReader\\Parser.java");
			file.createNewFile();
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file),Charset.forName("MS950"));
			BufferedWriter out = new BufferedWriter(write);
			//Writer out = new FileWriter(file, false);
			out.write("package StrReader;\n");
			//import
			out.write("import java.text.SimpleDateFormat;\n");
			out.write("import java.util.ArrayList;\n");
			out.write("import java.util.Date;\n");
			out.write("import java.sql.Connection;\n");
			out.write("import java.sql.DriverManager;\n");
			out.write("import java.sql.Statement;\n");
			out.write("import java.sql.ResultSet;\n\n");
			//class start
			out.write("public class Parser{\n");
			out.write("\tConnection dbConn;\n");
			out.write("\tResultSet rt;\n");
			
			out.write("\tpublic void main(String dbURL, String userName, String userPwd){\n");
			out.write("\t\ttry{\n");
			out.write("\t\t\tSimpleDateFormat spf = new SimpleDateFormat(\"yyyy:MM:dd hh:mm:ss\");\n");
			out.write("\t\t\tString currentTime = spf.format(new Date().getTime());\n");
			out.write("\t\t\tdbConn = DriverManager.getConnection(dbURL, userName, userPwd);\n");
			out.write("\t\t\tStatement st = dbConn.createStatement();\n");
			out.write("\t\t\tStatement st1 = dbConn.createStatement();\n");
			out.write("\t\t\tString sql = \"SELECT timestamp \"\n");
			out.write("\t\t\t		      + \"FROM " + sTableName + " ORDER BY timestamp DESC\";\n");
			//out.write("\t\t\t		      + \"WHERE ID = (SELECT MAX(timestamp) FROM " + sTableName + " ORDER BY ID)\";\n");
			out.write("\t\t\trt = st.executeQuery(sql);\n");
			out.write("\t\t\tif(rt.next()){\n");
			out.write("\t\t\t\tcurrentTime = rt.getString(1);\n");
			out.write("\t\t\t\tcurrentTime = currentTime.substring(0, currentTime.length() - 2);\n");
			out.write("\t\t\t}\n");
			
			out.write("\t\t\tsql = \"CREATE TABLE IF NOT EXISTS " + sTableName_final + " (\"\n");
			out.write("\t\t\t    + \"ID INT PRIMARY KEY, \"\n");
			out.write("\t\t\t    + \"Time DATETIME, \"\n");
			out.write("\t\t\t    + \"S_" + sensorName.get(0)  + " INT(1) NOT NULL, \"\n");
			for (int i = 1; i < sensor; i++){
				if (i != sensor - 1)
					out.write("\t\t\t    + \"S_" + sensorName.get(i)  + " INT(1) NOT NULL, \"\n");
				else
					out.write("\t\t\t    + \"S_" + sensorName.get(i)  + " INT(1) NOT NULL)\";\n");
			}
			out.write("\t\t\tst.executeUpdate(sql);\n");
			
			out.write("\t\t\tsql = \"CREATE TABLE IF NOT EXISTS "+ hTableName + "(\"\n");
			out.write("\t\t\t    + \"ID INT PRIMARY KEY, \"\n");
			out.write("\t\t\t    + \"Time DATETIME, \"\n");
			out.write("\t\t\t    + \"H_" + sensorName.get(0)  + " INT(1) NOT NULL, \"\n");
			for (int i = 1; i < sensor; i++){
				if (i != sensor - 1)
					out.write("\t\t\t    + \"H_" + sensorName.get(i)  + " INT(1) NOT NULL, \"\n");
				else
					out.write("\t\t\t    + \"H_" + sensorName.get(i)  + " INT(1) NOT NULL)\";\n");
			}
			out.write("\t\t\tst.executeUpdate(sql);\n");
			
			
			
			out.write("\t\t\twhile(true){\n");
			
			out.write("\t\t\t\tsql = \"SELECT timestamp");
			
			for (int i = 0; i < sensorName.size(); i++)
				out.write(", " + sensorName.get(i));
			out.write(" \"\n");
			//--------------------------------------------------------------------------------------
			
			out.write("\t\t\t\t    + \"FROM " + sTableName + " \"\n");
			out.write("\t\t\t\t    + \"WHERE timestamp > '\" + currentTime + \"' ORDER BY timestamp DESC\";\n");
			out.write("\t\t\t\trt = st.executeQuery(sql);\n");
			out.write("\t\t\t\twhile(rt.next()){\n");
			out.write("\t\t\t\t\tcurrentTime = rt.getString(1);\n");
			out.write("\t\t\t\t\tcurrentTime = currentTime.substring(0, currentTime.length() - 2);\n");
			
			//---------------------------------------------------
			out.write("\t\t\t\t\tint aa = statusJudge(rt.getInt(2)");//, rt.getInt(3));\n");
			for (int i = 0; i < sensorName.size() - 1; i++)
				out.write(", rt.getInt(" + (i + 3) + ")");
			out.write(");\n");
			//--------------------------------------------------------------------------------------
			//---------------------------------------------------
			out.write("\t\t\t\t\tsql = \"insert into " + sTableName_final + " (Time, Status) values ('\" + currentTime + \"', '\" + aa + \"')\";\n");			
			out.write("\t\t\t\t\tst1.executeUpdate(sql);\n");
			
			//--------------------------------------------------
			out.write("\t\t\t\t\tint[] a = healthJudge(rt.getInt(2)");//, rt.getInt(3));\n");
			for (int i = 0; i < sensorName.size() - 1; i++)
				out.write(", rt.getInt(" + (i + 3) + ")");
			out.write(");\n");
			//--------------------------------------------------------------------------------------
			//------------------------------------------------------
			out.write("\t\t\t\t\tsql = \"insert into " + hTableName + " (Time");			
			for (int i = 0; i < sensorName.size(); i++)
				out.write(", H_" + sensorName.get(i));
			out.write(") values ('\" + currentTime + \"'");
			for (int i = 0; i < sensorName.size(); i++)
				out.write(", '\" + a[" + i + "] + \"'");
			out.write(")\";\n");
			///-------------------------------------------------------------------------------------
			
			out.write("\t\t\t\t\tst1.executeUpdate(sql);\n");
			out.write("\t\t\t\t}\n");
			out.write("\t\t\t\tSystem.out.println(currentTime);\n");
			out.write("\t\t\t\tThread.sleep(100);\n");
			out.write("\t\t\t}\n");	
			out.write("\t\t}catch(Exception e){\n");
			out.write("\t\t\te.printStackTrace();\n");
			out.write("\t\t}\n");
			out.write("\t}\n\n");
			
			int first = 65;
			ArrayList<String> output = new ArrayList<String>();
			
			//------------------------------------
			out.write("\tpublic int[] healthJudge(Integer A");
			char a = (char)first;						
			output.add(String.valueOf(a));				
			for(int i = 0; i < sensor - 1; i++){
				first++;
				a = (char)first;
				output.add(String.valueOf(a));
				out.write(", Integer " + a);
			}
			out.write("){\n");
			
			for (int i = 0; i < sensor; i++){
				out.write("\t\tint " + output.get(i) + output.get(i) + " = 0;\n");
			}
			
			out.write("\t\tint[] v = new int[" + sensor + "];\n");
			
			for (int i = 0; i < iflist.size(); i++){
				for (int j = 0; j < iflist.get(i).length; j++){
					if (i == 0 && j == 0){
						out.write("\t\tif(" + output.get(j) + " == " + iflist.get(i)[j]);
					}
					else if (i != 0 && j == 0)
						out.write("\t\telse if(" + output.get(j) + " == " + iflist.get(i)[j]);
					else
						out.write(" && " + output.get(j) + " == " + iflist.get(i)[j]);
				}
				out.write("){\n");
				for (int j = 0; j < thenlist.get(i).length; j++)
					out.write("\t\t\t" + output.get(j) + output.get(j) + " = " + thenlist.get(i)[j] + ";\n");
				out.write("\t\t}\n");
			}
			
			for (int i = 0; i < sensor; i++)
				out.write("\t\tv[" + i + "] = " + output.get(i) + output.get(i) + ";\n");
			out.write("\t\treturn v;\n");
			out.write("\t}\n\n");
			
			
			//---------------------------------
			out.write("\tpublic int statusJudge(Integer A");
			first = 65;
			a = (char)first;					
			output.add(String.valueOf(a));				
			for(int i = 0; i < sensor - 1; i++){
				first++;
				a = (char)first;
				output.add(String.valueOf(a));
				out.write(", Integer " + a);
			}
			out.write("){\n");
			
			
			out.write("\t\tint status = 0;\n");
			
			for (int i = 0; i < iflist.size(); i++){
				for (int j = 0; j < iflist.get(i).length; j++){
					if (i == 0 && j == 0){
						out.write("\t\tif(" + output.get(j) + " == " + iflist.get(i)[j]);
					}
					else if (i != 0 && j == 0)
						out.write("\t\telse if(" + output.get(j) + " == " + iflist.get(i)[j]);
					else
						out.write(" && " + output.get(j) + " == " + iflist.get(i)[j]);
				}
				out.write("){\n");
				out.write("\t\t\tstatus = " + status.get(i) + ";\n");
				out.write("\t\t}\n");
			}
			out.write("\t\treturn status;\n");
			out.write("\t}\n");
			out.write("}");
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//
	public void readfileFromTxt(String filepath){
		try{
			String encoding = "utf8";
			String text = "";
			File file = new File(filepath);
			if(file.isFile()&&file.exists()){
				InputStreamReader filereader = new InputStreamReader(new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(filereader);
				String linetext = null;
				while(((linetext = bufferedReader.readLine()) != null)){
					text += linetext;
				}
			//System.out.println(text);
			readword(text);
			filereader.close();
			}
			else{
				System.out.println("找不到檔案");
			}
		}
		catch(Exception e){
			System.out.println("讀取出錯");
			e.printStackTrace();
		}
	}
	
	
	ArrayList<String> iflist = new ArrayList<String>();		
	ArrayList<String> thenlist = new ArrayList<String>();	//
	
	//
	public void readword(String word){
		//
		String tempstr = "";
		int i = 0;
		int j = 0;
		ArrayList<String> wordlist = new ArrayList<String>();
		
	
		for (i = 0;i<word.length();i++){
			if(word.charAt(i)==';'){
				wordlist.add(tempstr);
				tempstr = "";
			}
			else{
				tempstr+=word.charAt(i);
			}
		}
		//
		for(i=0;i<wordlist.size();i++){
			tempstr = wordlist.get(i);
			iflist.add(tempstr.substring(tempstr.indexOf("if")+2,tempstr.indexOf("then")));
			thenlist.add(tempstr.substring(tempstr.indexOf("then")+4));
		}
	}

	//--------------------------------------------------------------------------------
	String driverName = ""; //
	String dbURL = ""; 
	String userName = ""; 
	String userPwd = ""; 
	Connection dbConn = null;
	//--------------------------------------------------------------------------------------------------
		
	
	public void createParser(){
		String filename = "Parser";			
		File file = new File("src\\strReader\\" + filename + ".java");	
		Scanner scan = new Scanner(System.in);							
		int first = 65;													
		System.out.println("狀態Table Name");				
		String sTableName = scan.nextLine();
		System.out.println("最終狀態Table Name");				
		String sTableName_final = scan.nextLine();
		System.out.println("健康Table Name");
		String hTableName = scan.nextLine();
		System.out.println("傳感器數量");
		int sensor = scan.nextInt();
		ArrayList<String> sensorName = new ArrayList<String>();			
		HashMap<String, String> hmSensorName = new HashMap<String, String>();
		
		
		for (int i = 0; i < sensor; i++){
			scan = new Scanner(System.in);
			System.out.println("第" + (i + 1) + "傳感器為");			
			sensorName.add(scan.nextLine());
		}
		
		try{
			ArrayList<String> output = new ArrayList<String>();			
			file.createNewFile();
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file),Charset.forName("MS950"));
			BufferedWriter out = new BufferedWriter(write);
			out.write("package StrReader;\n");
			
			//import
			out.write("import java.text.SimpleDateFormat;\n");
			out.write("import java.util.ArrayList;\n");
			out.write("import java.util.Date;\n");
			out.write("import java.sql.Connection;\n");
			out.write("import java.sql.DriverManager;\n");
			out.write("import java.sql.Statement;\n");
			out.write("import java.sql.ResultSet;\n");
			
			out.write("public class Parser{\n");
			out.write("\tConnection dbConn;\n");
			out.write("\tResultSet rt;\n");
			
			//
			out.write("\tpublic void main(String dbURL, String userName, String userPwd){\n");
			out.write("\t\ttry{\n");
			out.write("\t\t\tSimpleDateFormat spf = new SimpleDateFormat(\"yyyy:MM:dd hh:mm:ss\");\n");
			out.write("\t\t\tString currentTime = spf.format(new Date().getTime());\n");
			out.write("\t\t\tdbConn = DriverManager.getConnection(dbURL, userName, userPwd);\n");
			out.write("\t\t\tStatement st = dbConn.createStatement();\n");
			out.write("\t\t\tStatement st1 = dbConn.createStatement();\n");
			out.write("\t\t\tString sql = \"SELECT timestamp \"\n");
			out.write("\t\t\t		      + \"FROM " + sTableName + " ORDER BY timestamp DESC\";\n");
			//out.write("\t\t\t		      + \"WHERE timestamp = (SELECT MAX(timestamp) FROM " + sTableName + " ORDER BY timestamp)\";\n");
			out.write("\t\t\trt = st.executeQuery(sql);\n");
			out.write("\t\t\tif(rt.next()){\n");
			out.write("\t\t\t\tcurrentTime = rt.getString(1);\n");
			out.write("\t\t\t\tcurrentTime = currentTime.substring(0, currentTime.length() - 2);\n");
			out.write("\t\t\t}\n");
			
			out.write("\t\t\tsql = \"CREATE TABLE IF NOT EXISTS " + sTableName_final + " (\"\n");
			out.write("\t\t\t    + \"ID INT PRIMARY KEY, \"\n");
			out.write("\t\t\t    + \"Time DATETIME, \"\n");
			out.write("\t\t\t    + \"S_" + sensorName.get(0)  + " INT(1) NOT NULL, \"\n");
			for (int i = 1; i < sensor; i++){
				if (i != sensor - 1)
					out.write("\t\t\t    + \"S_" + sensorName.get(i)  + " INT(1) NOT NULL, \"\n");
				else
					out.write("\t\t\t    + \"S_" + sensorName.get(i)  + " INT(1) NOT NULL)\";\n");
			}
			out.write("\t\t\tst.executeUpdate(sql);\n");
			
			out.write("\t\t\tsql = \"CREATE TABLE IF NOT EXISTS "+ hTableName + "(\"\n");
			out.write("\t\t\t    + \"ID INT PRIMARY KEY, \"\n");
			out.write("\t\t\t    + \"Time DATETIME, \"\n");
			out.write("\t\t\t    + \"H_" + sensorName.get(0)  + " INT(1) NOT NULL, \"\n");
			for (int i = 1; i < sensor; i++){
				if (i != sensor - 1)
					out.write("\t\t\t    + \"H_" + sensorName.get(i)  + " INT(1) NOT NULL, \"\n");
				else
					out.write("\t\t\t    + \"H_" + sensorName.get(i)  + " INT(1) NOT NULL)\";\n");
			}
			out.write("\t\t\tst.executeUpdate(sql);\n");
			
			out.write("\t\t\twhile(true){\n");
			//--------------------------------------------------------------
			out.write("\t\t\t\tsql = \"SELECT timestamp");
			//-------------------------------------------------------
			for (int i = 0; i < sensorName.size(); i++)
				out.write(", " + sensorName.get(i));
			out.write(" \"\n");
			//--------------------------------------------------------------------------------------
			
			out.write("\t\t\t\t    + \"FROM " + sTableName + " \"\n");
			out.write("\t\t\t\t    + \"WHERE timestamp > '\" + currentTime + \"' ORDER BY timestamp\";\n");
			out.write("\t\t\t\trt = st.executeQuery(sql);\n");
			out.write("\t\t\t\twhile(rt.next()){\n");
			out.write("\t\t\t\t\tcurrentTime = rt.getString(1);\n");
			out.write("\t\t\t\t\tcurrentTime = currentTime.substring(0, currentTime.length() - 2);\n");
			
			//------------------------------------------
			out.write("\t\t\t\t\tint aa = statusJudge(rt.getInt(2)");//, rt.getInt(3));\n");
			for (int i = 0; i < sensorName.size() - 1; i++)
				out.write(", rt.getInt(" + (i + 3) + ")");
			out.write(");\n");
			//--------------------------------------------------------------------------------------
			//------------------------------------------------
			out.write("\t\t\t\t\tsql = \"insert into " + sTableName_final + " (Time, Status) values ('\" + currentTime + \"', '\" + aa + \"')\";\n");			
			out.write("\t\t\t\t\tst1.executeUpdate(sql);\n");
			
			//--------------------------------------------------
			out.write("\t\t\t\t\tint[] a = healthJudge(rt.getInt(2)");//, rt.getInt(3));\n");
			for (int i = 0; i < sensorName.size() - 1; i++)
				out.write(", rt.getInt(" + (i + 3) + ")");
			out.write(");\n");
			//--------------------------------------------------------------------------------------
			//-------------------------------------------------
			out.write("\t\t\t\t\tsql = \"insert into " + hTableName + " (Time");			
			for (int i = 0; i < sensorName.size(); i++)
				out.write(", H_" + sensorName.get(i));
			out.write(") values ('\" + currentTime + \"'");
			for (int i = 0; i < sensorName.size(); i++)
				out.write(", '\" + a[" + i + "] + \"'");
			out.write(")\";\n");
			///-------------------------------------------------------------------------------------
			
			out.write("\t\t\t\t\tst1.executeUpdate(sql);\n");
			out.write("\t\t\t\t}\n");
			out.write("\t\t\t\tSystem.out.println(currentTime);\n");
			out.write("\t\t\t\tThread.sleep(100);\n");
			out.write("\t\t\t}\n");	
			out.write("\t\t}catch(Exception e){\n");
			out.write("\t\t\te.printStackTrace();\n");
			out.write("\t\t}\n");
			out.write("\t}\n\n");			
			
			
			//----------------------------------------------------------------------------------------------------
			out.write("\tpublic int[] healthJudge(Integer A");
			char a = (char)first;						
			output.add(String.valueOf(a));				
			for(int i = 0; i < sensor - 1; i++){
				first++;
				a = (char)first;
				output.add(String.valueOf(a));
				out.write(", Integer " + a);
			}				
			for (int i = 0; i < sensor; i++)
				hmSensorName.put(sensorName.get(i), output.get(i));
			out.write("){\n");
			//------------------------------------------------------------------------------------------------------------------------------------
			//-------------------------------------------------------
			for (int i = 0; i < sensor; i++)			
				out.write("\t\tArrayList<Integer> " + output.get(i) + output.get(i) + output.get(i) + " = new ArrayList<Integer>();\n");
			out.write("\t\tint[] v = new int[" + sensor + "];\n");
			//------------------------------------------------------------------------------
			//---------------------------------------------
			ArrayList<String> value = new ArrayList<String>();
			for (int i = 0; i < iflist.size(); i++){				
				String temp = iflist.get(i);
				String strTemp;
				for (int j = 0; j < temp.length(); j++){
					if (String.valueOf(temp.charAt(j)).equals("_")){						
						//value.add(output.get(Q));
						String sTemp = "";						
						j++;
						while (!String.valueOf(temp.charAt(j)).equals(" ")){							
							sTemp += temp.charAt(j);
							j++;
						}
						value.add(hmSensorName.get(sTemp));
						value.add(String.valueOf(temp.charAt(j)));
					}
					else if (String.valueOf(temp.charAt(j)).equals("="))
						value.add("==");
					else if (String.valueOf(temp.charAt(j)).equals("0") || String.valueOf(temp.charAt(j)).equals("1") ||
							 String.valueOf(temp.charAt(j)).equals("2") || String.valueOf(temp.charAt(j)).equals("3") ||
							 String.valueOf(temp.charAt(j)).equals("(") || String.valueOf(temp.charAt(j)).equals(")"))
						value.add(String.valueOf(temp.charAt(j)));
					if (j < temp.length() - 2){
						strTemp = String.valueOf(temp.charAt(j)) + String.valueOf(temp.charAt(j + 1)) + String.valueOf(temp.charAt(j + 2));
						if (strTemp.equals("and")){
							value.add("&&");
							j += 2;
						}
					}
					if (j < temp.length() - 1){
						strTemp = String.valueOf(temp.charAt(j)) + String.valueOf(temp.charAt(j + 1));
						if (strTemp.equals("or")){
							value.add("||");
							j += 1;
						}
					}
				}
				
				out.write("\t\tif(");
				for (int j = 0; j < value.size(); j++)
					out.write(value.get(j));
				out.write("){\n");
				
				//------------------------------------------------------------------------
				value.clear();
				temp = thenlist.get(i);
				for (int j = 0; j < temp.length(); j++){
					if (String.valueOf(temp.charAt(j)).equals("_")){
						String sTemp = "";
						j++;
						while(!String.valueOf(temp.charAt(j)).equals(" ")){
							sTemp += temp.charAt(j);
							j++;
						}
						value.add(hmSensorName.get(sTemp));
					}						
					else if (String.valueOf(temp.charAt(j)).equals("0") || String.valueOf(temp.charAt(j)).equals("1") || String.valueOf(temp.charAt(j)).equals("2"))
						value.add(String.valueOf(temp.charAt(j)));						
				}
				
				for (int j = 0; j < value.size(); j++){
					if (j%2 == 0)
						out.write("\t\t\t" + value.get(j) + value.get(j) + value.get(j) + ".add(");
					else 
						out.write(value.get(j) + ");\n");
				}
				out.write("\t\t}\n");
				value.clear();
			}
			
			for (int i = 0; i < sensor; i++){
				out.write("\t\tif (" + output.get(i) + output.get(i) + output.get(i) + ".size() != 0)\n");
				out.write("\t\t\tfor (int i = 0; i < " + output.get(i) + output.get(i) + output.get(i) + ".size(); i++){\n");
				out.write("\t\t\t\tif(" + output.get(i) + output.get(i) + output.get(i) + ".get(i) == 0){\n");
				out.write("\t\t\t\t\tv[" + i + "] = 0;\n");
				out.write("\t\t\t\t\tbreak;\n");
				out.write("\t\t\t\t}\n");
				out.write("\t\t\t\telse\n");
				out.write("\t\t\t\t\tv[" + i + "] = 1;\n");
				out.write("\t\t\t}\n");
				out.write("\t\telse\n");
				out.write("\t\t\tv[" + i + "] = 1;\n");
			}
				
			out.write("\t\treturn v;\n");			
			out.write("\t}\n\n");
			//------------------------------------------------------------------------------------------
			iflist.clear();
			thenlist.clear();
			this.readfileFromTxt(System.getProperty("user.dir") + "\\src\\unit1_status.txt");
			
			
			//----------------------------------------------------------------------------------------------------------------
			out.write("\tpublic int statusJudge(Integer A");
			first = 65;
			a = (char)first;						
			output.add(String.valueOf(a));				
			for(int i = 0; i < sensor - 1; i++){
				first++;
				a = (char)first;
				output.add(String.valueOf(a));
				out.write(", Integer " + a);
			}				
			for (int i = 0; i < sensor; i++)
				hmSensorName.put(sensorName.get(i), output.get(i));
			out.write("){\n");
			//------------------------------------------------------------------------------------------------------------------------------------
			//-------------------------------------------------			
			out.write("\t\tint finalStatus = 0;\n");
			//----------------------------------
			value.clear();;
			for (int i = 0; i < iflist.size(); i++){				
				String temp = iflist.get(i);
				String strTemp;
				int Q = 0;
				for (int j = 0; j < temp.length(); j++){
					if (String.valueOf(temp.charAt(j)).equals("_")){						
						//value.add(output.get(Q));
						String sTemp = "";						
						j++;
						while (!String.valueOf(temp.charAt(j)).equals(" ")){							
							sTemp += temp.charAt(j);
							j++;
						}
						value.add(hmSensorName.get(sTemp));
						value.add(String.valueOf(temp.charAt(j)));
					}
					else if (String.valueOf(temp.charAt(j)).equals("="))
						value.add("==");
					else if (String.valueOf(temp.charAt(j)).equals("0") || String.valueOf(temp.charAt(j)).equals("1") ||
							 String.valueOf(temp.charAt(j)).equals("(") || String.valueOf(temp.charAt(j)).equals(")"))
						value.add(String.valueOf(temp.charAt(j)));
					if (j < temp.length() - 2){
						strTemp = String.valueOf(temp.charAt(j)) + String.valueOf(temp.charAt(j + 1)) + String.valueOf(temp.charAt(j + 2));
						if (strTemp.equals("and")){
							value.add("&&");
							j += 2;
						}
					}
					if (j < temp.length() - 1){
						strTemp = String.valueOf(temp.charAt(j)) + String.valueOf(temp.charAt(j + 1));
						if (strTemp.equals("or")){
							value.add("||");
							j += 1;
						}
					}
				}
				if (i == 0)
					out.write("\t\tif(");
				else 
					out.write("\t\telse if(");
				
				for (int j = 0; j < value.size(); j++)
					out.write(value.get(j));
				out.write("){\n");
				
				//---------------------------------------------------------------
				value.clear();
				temp = thenlist.get(i);
				Q = 0;
				for (int j = 0; j < temp.length(); j++)
					if (String.valueOf(temp.charAt(j)).equals("0") || String.valueOf(temp.charAt(j)).equals("1") || String.valueOf(temp.charAt(j)).equals("2"))
						out.write("\t\t\tfinalStatus = " + String.valueOf(temp.charAt(j)) + ";\n");
				out.write("\t\t}\n");
				value.clear();
			}
				
			out.write("\t\treturn finalStatus;\n");			
			out.write("\t}\n");
			//------------------------------------------------------------------------------------------
			out.write("}\n");
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void dynamicCompile() throws Exception{
		
		String currentDir = System.getProperty("user.dir");
		String fileurl = currentDir + "/src/StrReader/Parser.java"; 
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();  
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);  
        Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(fileurl);  
        CompilationTask cTask = compiler.getTask(null, fileManager, null, null, null, fileObjects);  
        if(cTask.call())
        	System.out.println("Compile Success!");
        else 
        	System.out.println("Compile Fail!");
        fileManager.close();
        
        
        URL[] urls = new URL[] { new URL("file:/" + currentDir + "/src/") };
        URLClassLoader cLoader = new URLClassLoader(urls);  
        Class<?> c = cLoader.loadClass("StrReader.Parser");  
        cLoader.close();
        
        
        Object obj = c.newInstance();  
        Method method = c.getMethod("main", String.class, String.class, String.class);	
        method.invoke(obj, dbURL, userName, userPwd);		
	}
}
			

