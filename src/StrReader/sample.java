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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.reflect.Array;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


public class sample {
	public static void main(String[] args) throws Exception{
		System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_131\\jre");
		sample main = new sample();
		main.readfile("D:\\CheFossetta\\PKU\\一下課程\\西門子\\test.txt");  //文件目录用双斜杠
		main.createParser();
		main.dynamicCompile();
	}
	
	public void readfile(String filepath){
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
	
	ArrayList<String> iflist = new ArrayList<String>();
	ArrayList<String> thenlist = new ArrayList<String>();
	
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
		//SQLAct(iflist,thenlist);
	}

	String driverName = "com.mysql.jdbc.Driver"; // 加载JDBC驱动
	String dbURL = "jdbc:mysql://localhost:3306/siemens_parser?useSSL=true"; // 连接服务器和数据库test
	String userName = "root"; // 默认用户名
	String userPwd = ""; // 密码
	Connection dbConn = null;
	
	//修改数据库
	public void SQLAct(ArrayList a,ArrayList b){
		
		ArrayList<String> IDlist = new ArrayList<String>(); 
		int i = 0;
		int j = 0;
		int sucline = 0;
		try {
			System.out.print("Connection start..."); // 如果连接成功
			Class.forName(driverName); 
			dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println("Connection Successful!"); // 如果连接成功
			Statement st = dbConn.createStatement();
			for(i=0;i<a.size();i++){
				//初始化IDlist
				IDlist.clear();
				ResultSet rs = st.executeQuery((String) a.get(i));	//检查是否有此工作状态
				//System.out.println( a.get(i));
				while(rs.next()){
					IDlist.add(rs.getString(1));
				}
				System.out.println(IDlist);
				//判断是否有符合条件的ID，有则执行更新
				if(IDlist.size()>0){
					for(j=0;j<IDlist.size();j++){
						sucline += st.executeUpdate((b.get(i)+IDlist.get(j)));	//sucline为更新的数量
					}								
				}
			}
			//System.out.println(IDlist);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createParser(){
		String filename = "Parser";
		File file = new File("src\\strReader\\" + filename + ".java");
		
		try{
			file.createNewFile();
			Writer out = null;
			out = new FileWriter(file, false);
			out.write("package StrReader;\n");
			out.write("public class Parser{\n");
			out.write("\tpublic Integer[] parse(Integer sAcceleration, Integer sCurrent){\n");
			out.write("\t\tint hAcceleration = 0;\n");
			out.write("\t\tint hCurrent = 0;\n");
			out.write("\t\tInteger[] v = new Integer[2];\n");
			ArrayList<String> value = new ArrayList<String>();
			for (int i = 0; i < iflist.size(); i++){				
				String temp = iflist.get(i);
				String strTemp;
				for (int j = 0; j < temp.length(); j++){
					if (String.valueOf(temp.charAt(j)).equals("0") || String.valueOf(temp.charAt(j)).equals("1"))
						value.add(String.valueOf(temp.charAt(j)));
					else if (j < temp.length() - 2){
						strTemp = String.valueOf(temp.charAt(j)) + String.valueOf(temp.charAt(j + 1)) + String.valueOf(temp.charAt(j + 2));
						if (strTemp.equals("and"))
							value.add("&&");
					}
					else if (j < temp.length() - 1){
						strTemp = String.valueOf(temp.charAt(j)) + String.valueOf(temp.charAt(j + 1));
						if (strTemp.equals("or"))
							value.add("||");
					}
				}
				if (i == 0)
					out.write("\t\tif(sAcceleration == " + value.get(0) + " " + value.get(1) + " sCurrent == " + value.get(2) + "){\n");
				else
					out.write("\t\telse if(sAcceleration == " + value.get(0) + " " + value.get(1) + " sCurrent == " + value.get(2) + "){\n");
				value.clear();
				temp = thenlist.get(i);
				for (int j = 0; j < temp.length(); j++){
					if (String.valueOf(temp.charAt(j)).equals("0") || String.valueOf(temp.charAt(j)).equals("1"))
						value.add(String.valueOf(temp.charAt(j)));					
				}
				
				out.write("\t\t\thAcceleration = " + value.get(0) + ";\n");
				out.write("\t\t\thCurrent = " + value.get(1) + ";\n");
				out.write("\t\t}\n");
				value.clear();
			}
			out.write("\t\tv[0] = hAcceleration;\n");
			out.write("\t\tv[1] = hCurrent;\n");
			out.write("\t\treturn v;\n");			
			out.write("\t}\n");
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
        Method method = c.getMethod("parse", Integer.class, Integer.class);  
        method.invoke(obj, 1, 1);
        
        
        SimpleDateFormat spf = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
        String currentTime = spf.format(new Date().getTime());
        dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
        Statement st = dbConn.createStatement();
        Statement st1 = dbConn.createStatement();
        String sql = "SELECT Time "
        		   + "FROM source "
        		   + "WHERE ID = (SELECT MAX(ID) FROM source ORDER BY ID)";
        ResultSet rt = st.executeQuery(sql);
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
        		
        		Object health = method.invoke(obj, rt.getInt(2), rt.getInt(3));
        		int length = Array.getLength(health);
        		List<Object> list = new ArrayList<Object>();
        		for (int i=0; i<length; i++) {
        		    list.add(Array.get(health, i));
        		}
        		
        		
        		sql = "insert into result (Time, H_加速度, H_电流) values ('" + currentTime + "', '" + list.get(0) + "', '" + list.get(1) + "')";
        		st1.executeUpdate(sql);
        	}        	
        	System.out.println(currentTime);
        	Thread.sleep(10000);
        }
        
        
        
        
        
	}
	
	
}
			

