package StrReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import com.csvreader.*;


public class sample {
	public static void main(String[] args) throws Exception{
		System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_131\\jre");
		sample main = new sample();
		System.out.println("选择文本格式，输入1为.txt，输入2为.csv");
		Scanner scan = new Scanner(System.in);
		int chooseFormat = scan.nextInt();
		
		if (chooseFormat == 1){
			main.readfileFromTxt("D:\\CheFossetta\\PKU\\一下課程\\西門子\\test logic\\unit1_health-1.txt");  //文件目录用双斜杠
			main.createParser();
		}
		else if (chooseFormat == 2)
			main.readfileFromCsv("D:\\CheFossetta\\PKU\\一下課程\\西門子\\test logic\\unit1_TruthTable.csv");  //文件目录用双斜杠	
		
		main.dynamicCompile();
	}
	
	public void readfileFromCsv(String filepath){
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入工作状态Table Name：");
		String sTableName = scan.nextLine();
		System.out.println("请输入最终工作状态Table Name：");
		String sTableName_final = scan.nextLine();
		System.out.println("请输入健康状态Table Name：");
		String hTableName = scan.nextLine();
		System.out.println("请输入传感器数量：");
		int sensor = scan.nextInt();
		
		ArrayList<String> sensorName = new ArrayList<String>();
		for (int i = 0; i < sensor; i++){
			scan = new Scanner(System.in);
			System.out.println("请输入第" + (i + 1) +"传感器名称");
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
			CsvReader reader = new CsvReader(csv, ',', Charset.forName("UTF-8"));		
			reader.readHeaders();
			while (reader.readRecord()){				
				for(int i = 0; i < sensor; i++){
					iflistRow[i] = reader.get("S" + sensorName.get(i));
					thenlistRow[i] = reader.get("H" + sensorName.get(i));
				}
				iflist.add(iflistRow);
				thenlist.add(thenlistRow);
				status.add(reader.get("Status"));
				iflistRow = new String[sensor];
				thenlistRow = new String[sensor];
			}
			reader.close();
			//------------创建Parser.java并开始写入--------------------------------
			File file = new File("src\\strReader\\Parser.java");
			file.createNewFile();
			Writer out = new FileWriter(file, false);
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
			//被dynamicCompile方法调用的方法，里面会调用parse method
			out.write("\tpublic void main(String dbURL, String userName, String userPwd){\n");
			out.write("\t\ttry{\n");
			out.write("\t\t\tSimpleDateFormat spf = new SimpleDateFormat(\"yyyy:MM:dd hh:mm:ss\");\n");
			out.write("\t\t\tString currentTime = spf.format(new Date().getTime());\n");
			out.write("\t\t\tdbConn = DriverManager.getConnection(dbURL, userName, userPwd);\n");
			out.write("\t\t\tStatement st = dbConn.createStatement();\n");
			out.write("\t\t\tStatement st1 = dbConn.createStatement();\n");
			out.write("\t\t\tString sql = \"SELECT Time \"\n");
			out.write("\t\t\t		      + \"FROM " + sTableName + " \"\n");
			out.write("\t\t\t		      + \"WHERE ID = (SELECT MAX(ID) FROM " + sTableName + " ORDER BY ID)\";\n");
			out.write("\t\t\trt = st.executeQuery(sql);\n");
			out.write("\t\t\tif(rt.next()){\n");
			out.write("\t\t\t\tcurrentTime = rt.getString(1);\n");
			out.write("\t\t\t\tcurrentTime = currentTime.substring(0, currentTime.length() - 2);\n");
			out.write("\t\t\t}\n");
			out.write("\t\t\twhile(true){\n");
			//--------------最终工作状态-------------------------------------------------------------
			out.write("\t\t\t\tsql = \"SELECT Time");
			//--------------写入SQL查询的属性名称----------------------------------------------------
			for (int i = 0; i < sensorName.size(); i++)
				out.write(", S_" + sensorName.get(i));
			out.write(" \"\n");
			//--------------------------------------------------------------------------------------
			
			out.write("\t\t\t\t    + \"FROM " + sTableName + " \"\n");
			out.write("\t\t\t\t    + \"WHERE TIME > '\" + currentTime + \"' ORDER BY TIME\";\n");
			out.write("\t\t\t\trt = st.executeQuery(sql);\n");
			out.write("\t\t\t\twhile(rt.next()){\n");
			out.write("\t\t\t\t\tcurrentTime = rt.getString(1);\n");
			out.write("\t\t\t\t\tcurrentTime = currentTime.substring(0, currentTime.length() - 2);\n");
			
			//-------------写入statusJudge判定健康状态后的返回值-------------------------------------------
			out.write("\t\t\t\t\tint aa = statusJudge(rt.getInt(2)");//, rt.getInt(3));\n");
			for (int i = 0; i < sensorName.size() - 1; i++)
				out.write(", rt.getInt(" + (i + 3) + ")");
			out.write(");\n");
			//--------------------------------------------------------------------------------------
			//-------------写入SQL新增资料的属性名与数值---------------------------------------------
			out.write("\t\t\t\t\tsql = \"insert into " + sTableName_final + " (Time, Status) values ('\" + currentTime + \"', '\" + aa + \"')\";\n");			
			out.write("\t\t\t\t\tst1.executeUpdate(sql);\n");
			
			//-------------写入healthJudge判定健康状态后的返回值-------------------------------------------
			out.write("\t\t\t\t\tint[] a = healthJudge(rt.getInt(2)");//, rt.getInt(3));\n");
			for (int i = 0; i < sensorName.size() - 1; i++)
				out.write(", rt.getInt(" + (i + 3) + ")");
			out.write(");\n");
			//--------------------------------------------------------------------------------------
			//-------------写入SQL新增资料的属性名与数值---------------------------------------------
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
			out.write("\t\t\t\tThread.sleep(10000);\n");
			out.write("\t\t\t}\n");	
			out.write("\t\t}catch(Exception e){\n");
			out.write("\t\t\te.printStackTrace();\n");
			out.write("\t\t}\n");
			out.write("\t}\n\n");
			
			int first = 65;
			ArrayList<String> output = new ArrayList<String>();
			
			//---------------写入healthJudge方法----------------------------
			out.write("\tpublic int[] healthJudge(Integer A");
			char a = (char)first;						//将ASCII转成字符
			output.add(String.valueOf(a));				//储存字符作为Argument，ex: parse(Integer A, Integer B, Integer C)，output就存A,B,C三个值
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
			
			
			//---------------写入statusJudge方法----------------------------
			out.write("\tpublic int statusJudge(Integer A");
			first = 65;
			a = (char)first;						//将ASCII转成字符
			output.add(String.valueOf(a));				//储存字符作为Argument，ex: parse(Integer A, Integer B, Integer C)，output就存A,B,C三个值
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
	
	//读文件
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
				System.out.println("找不到文件");
			}
		}
		catch(Exception e){
			System.out.println("读取文件出错");
			e.printStackTrace();
		}
	}
	
	
	ArrayList<String> iflist = new ArrayList<String>();		//储存if判断式内容 ex: if S_加速度 = 1 and S_电流 = 1 then ... 则储存S_加速度 = 1 and S_电流 = 1在iflist里
	ArrayList<String> thenlist = new ArrayList<String>();	//储存then内容 ex: if ... then H_加速度 = 1, H_电流 = 1; 则储存H_加速度 = 1, H_电流 = 1在thenlist里
	
	//将文本内容存进iflist与thenlist
	public void readword(String word){
		//分割字符串
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
		//分割if 和 then
		for(i=0;i<wordlist.size();i++){
			tempstr = wordlist.get(i);
			iflist.add(tempstr.substring(tempstr.indexOf("if")+2,tempstr.indexOf("then")));
			thenlist.add(tempstr.substring(tempstr.indexOf("then")+4));
		}
	}

	//---------连接数据库的属性--------------------------------------------------------------------------
	String driverName = "com.mysql.jdbc.Driver"; // 加载JDBC驱动
	String dbURL = "jdbc:mysql://localhost:3306/siemens_parser?useSSL=true"; // 连接服务器和数据库test
	String userName = "root"; // 默认用户名
	String userPwd = ""; // 密码
	Connection dbConn = null;
	//--------------------------------------------------------------------------------------------------
		
	// 补充：\t为一个tab空格、\n为换行、\"表示写入文本为 -> " 。  
	
	//生成一个类，里面有两个方法。类
	public void createParser(){
		String filename = "Parser";			//类的名称
		File file = new File("src\\strReader\\" + filename + ".java");	//类的档案位置
		Scanner scan = new Scanner(System.in);							//用来读取键盘输入
		int first = 65;													//生成方法的Argument名称，使用ASCII，十进制65为大写A的编码
		System.out.println("请输入工作状态Table Name：");				
		String sTableName = scan.nextLine();
		System.out.println("请输入最终工作状态Table Name：");				
		String sTableName_final = scan.nextLine();
		System.out.println("请输入健康状态Table Name：");
		String hTableName = scan.nextLine();
		System.out.println("输入传感器数量");
		int sensor = scan.nextInt();
		ArrayList<String> sensorName = new ArrayList<String>();			//用来储存数据库table的属性名称
		HashMap<String, String> hmSensorName = new HashMap<String, String>();
		
		//储存数据库table属性名称
		for (int i = 0; i < sensor; i++){
			scan = new Scanner(System.in);
			System.out.println("请输入第" + (i + 1) + "个传感器名称：");			
			sensorName.add(scan.nextLine());
		}
		
		try{
			ArrayList<String> output = new ArrayList<String>();			//储存判定健康状态方法返回值的Argument名称
			file.createNewFile();
			Writer out = null;
			out = new FileWriter(file, false);
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
			
			//被dynamicCompile方法调用的方法，里面会调用parse method
			out.write("\tpublic void main(String dbURL, String userName, String userPwd){\n");
			out.write("\t\ttry{\n");
			out.write("\t\t\tSimpleDateFormat spf = new SimpleDateFormat(\"yyyy:MM:dd hh:mm:ss\");\n");
			out.write("\t\t\tString currentTime = spf.format(new Date().getTime());\n");
			out.write("\t\t\tdbConn = DriverManager.getConnection(dbURL, userName, userPwd);\n");
			out.write("\t\t\tStatement st = dbConn.createStatement();\n");
			out.write("\t\t\tStatement st1 = dbConn.createStatement();\n");
			out.write("\t\t\tString sql = \"SELECT Time \"\n");
			out.write("\t\t\t		      + \"FROM " + sTableName + " \"\n");
			out.write("\t\t\t		      + \"WHERE ID = (SELECT MAX(ID) FROM " + sTableName + " ORDER BY ID)\";\n");
			out.write("\t\t\trt = st.executeQuery(sql);\n");
			out.write("\t\t\tif(rt.next()){\n");
			out.write("\t\t\t\tcurrentTime = rt.getString(1);\n");
			out.write("\t\t\t\tcurrentTime = currentTime.substring(0, currentTime.length() - 2);\n");
			out.write("\t\t\t}\n");
			out.write("\t\t\twhile(true){\n");
			//--------------最终工作状态-------------------------------------------------------------
			out.write("\t\t\t\tsql = \"SELECT Time");
			//--------------写入SQL查询的属性名称----------------------------------------------------
			for (int i = 0; i < sensorName.size(); i++)
				out.write(", S_" + sensorName.get(i));
			out.write(" \"\n");
			//--------------------------------------------------------------------------------------
			
			out.write("\t\t\t\t    + \"FROM " + sTableName + " \"\n");
			out.write("\t\t\t\t    + \"WHERE TIME > '\" + currentTime + \"' ORDER BY TIME\";\n");
			out.write("\t\t\t\trt = st.executeQuery(sql);\n");
			out.write("\t\t\t\twhile(rt.next()){\n");
			out.write("\t\t\t\t\tcurrentTime = rt.getString(1);\n");
			out.write("\t\t\t\t\tcurrentTime = currentTime.substring(0, currentTime.length() - 2);\n");
			
			//-------------写入statusJudge判定健康状态后的返回值-------------------------------------------
			out.write("\t\t\t\t\tint aa = statusJudge(rt.getInt(2)");//, rt.getInt(3));\n");
			for (int i = 0; i < sensorName.size() - 1; i++)
				out.write(", rt.getInt(" + (i + 3) + ")");
			out.write(");\n");
			//--------------------------------------------------------------------------------------
			//-------------写入SQL新增资料的属性名与数值---------------------------------------------
			out.write("\t\t\t\t\tsql = \"insert into " + sTableName_final + " (Time, Status) values ('\" + currentTime + \"', '\" + aa + \"')\";\n");			
			out.write("\t\t\t\t\tst1.executeUpdate(sql);\n");
			
			//-------------写入healthJudge判定健康状态后的返回值-------------------------------------------
			out.write("\t\t\t\t\tint[] a = healthJudge(rt.getInt(2)");//, rt.getInt(3));\n");
			for (int i = 0; i < sensorName.size() - 1; i++)
				out.write(", rt.getInt(" + (i + 3) + ")");
			out.write(");\n");
			//--------------------------------------------------------------------------------------
			//-------------写入SQL新增资料的属性名与数值---------------------------------------------
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
			out.write("\t\t\t\tThread.sleep(10000);\n");
			out.write("\t\t\t}\n");	
			out.write("\t\t}catch(Exception e){\n");
			out.write("\t\t\te.printStackTrace();\n");
			out.write("\t\t}\n");
			out.write("\t}\n\n");			
			
			//写入判定方法至文本
			//------------------------------------写入Argument------------------------------------------------------------------------------------
			out.write("\tpublic int[] healthJudge(Integer A");
			char a = (char)first;						//将ASCII转成字符
			output.add(String.valueOf(a));				//储存字符作为Argument，ex: parse(Integer A, Integer B, Integer C)，output就存A,B,C三个值
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
			//-----------------------写入要被回传的参数--------------------------------------
			for (int i = 0; i < sensor; i++)			
				out.write("\t\tArrayList<Integer> " + output.get(i) + output.get(i) + output.get(i) + " = new ArrayList<Integer>();\n");
			out.write("\t\tint[] v = new int[" + sensor + "];\n");
			//------------------------------------------------------------------------------
			//--------------------将文本判断内容解析为java代码-------------------------------
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
				
				//-----------------------------处理then子句-----------------------------------------------------
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
			this.readfileFromTxt("D:\\CheFossetta\\PKU\\一下課程\\西門子\\test logic\\unit1_status.txt");
			
			//写入判定方法至文本
			//------------------------------------写入Argument------------------------------------------------------------------------------------
			out.write("\tpublic int statusJudge(Integer A");
			first = 65;
			a = (char)first;						//将ASCII转成字符
			output.add(String.valueOf(a));				//储存字符作为Argument，ex: parse(Integer A, Integer B, Integer C)，output就存A,B,C三个值
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
			//-----------------------写入要被回传的参数--------------------------------------			
			out.write("\t\tint finalStatus = 0;\n");
			//------------------------------------------------------------------------------
			//--------------------将文本判断内容解析为java代码-------------------------------
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
				
				//-----------------------------处理then子句-----------------------------------------------------
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
		// 使用JavaCompiler 编译java文件
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
        
        //使用URLClassLoader加载class到内存  
        URL[] urls = new URL[] { new URL("file:/" + currentDir + "/src/") };
        URLClassLoader cLoader = new URLClassLoader(urls);  
        Class<?> c = cLoader.loadClass("StrReader.Parser");  
        cLoader.close();
        
        // 利用class创建实例，反射执行方法  
        Object obj = c.newInstance();  
        Method method = c.getMethod("main", String.class, String.class, String.class);	//调用Parse类里的main方法  
        method.invoke(obj, dbURL, userName, userPwd);		//调用
	}
}
			

