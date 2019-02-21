import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

class Student{
	public int sid;
	public String sname;
}

class Course{
	public int cid;
	public String cname;
	public int credits;
}

public class student{
	
	private static int sid;
	private static Connection conn;
	public static final String oracleServer = "dbs3.cs.umb.edu";
	public static final String oracleServerSid = "dbs3";

	public static Connection getConnection() {

		// first we need to load the driver
		String jdbcDriver = "oracle.jdbc.OracleDriver";
		try {
			Class.forName(jdbcDriver);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Get username and password
		Scanner input = new Scanner(System.in);
		System.out.print("Username:");
		String username = input.next();
		System.out.print("Password:");
		// the following is used to mask the password
		
		Console console = System.console();
		String password = new String(console.readPassword());
		
		
		
		String connString = "jdbc:oracle:thin:@" + oracleServer + ":1521:"
				+ oracleServerSid;

		System.out.println("Connecting to the database...");

		Connection conn;
		// Connect to the database
		try {
			conn = DriverManager.getConnection(connString, username, password);
			System.out.println("Connection Successful");
		} catch (SQLException e) {
			System.out.println("Connection ERROR");
			e.printStackTrace();
			return null;
		}

		return conn;
	}

	private static Student findStudent(int sid){
		try{
			PreparedStatement pstmt = conn.prepareStatement("select * from students where sid=?");
			pstmt.setInt(1, sid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				Student stu = new Student();
				stu.sid = rs.getInt("sid");
				stu.sname = rs.getString("sname");
				return stu;
			}
			rs.close();
			pstmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	private static Course findCourse(int cid){
		try{
			PreparedStatement pstmt = conn.prepareStatement("select * from courses where cid=?");
			pstmt.setInt(1, cid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				Course c = new Course();
				c.cid = cid;
				c.cname = rs.getString("cname");
				c.credits = rs.getInt("credits");
				return c;
			}
			rs.close();
			pstmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	private static List<Course> searchCourse(String keyword){
		List<Course> list = new ArrayList<Course>();
		try{
			
			PreparedStatement pstmt = conn.prepareStatement("select * from courses where cname like ?");
			pstmt.setString(1, "%" + keyword + "%");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				Course c = new Course();
				c.cid = rs.getInt("cid");
				c.cname = rs.getString("cname");
				c.credits = rs.getInt("credits");
				list.add(c);
			}
			rs.close();
			pstmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}	
	
	private static boolean checkEnrolled(int sid,int cid){
		boolean ret = false;
		try{
			PreparedStatement pstmt = conn.prepareStatement("select * from enrolled where sid=? and cid=?");
			pstmt.setInt(1, sid);
			pstmt.setInt(2, cid);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				ret = true;
			}
			rs.close();
			pstmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	private static void addStudent(int sid,String sname){
		String sql = "insert into students (sid,sname) values (?,?)";
		try{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sid);
			pstmt.setString(2,sname);
			pstmt.executeQuery();
			pstmt.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static void unselect(int sid,int cid){
		String sql = "delete from enrolled where sid=? and cid=?";
		try{
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sid);
			pstmt.setInt(2,cid);
			pstmt.executeUpdate();
			pstmt.close();
			
			
			/*pstmt = conn.prepareStatement("update courses set credits=credits-1 where cid=?");
			pstmt.setInt(1, cid);
			pstmt.executeUpdate();
			pstmt.close();*/
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static void select(int sid,int cid){
		String sql = "insert into enrolled (sid,cid) values (?,?)";
		try{
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sid);
			pstmt.setInt(2,cid);
			pstmt.executeQuery();
			pstmt.close();
			
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
		
	
	private static void printMenu(){
       
		System.out.println("================== System Menu ==============");
		System.out.println("L - List: lists all records in the course table");
		System.out.println("E - Enroll: enrolls the active student in a course; user is prompted for course ID; check for conflicts, i.e., ");
		System.out.println("student cannot enroll twice in same course");
		System.out.println("W - Withdraw: deletes an entry in the Enrolled table corresponding to active student; student is  prompted for course ID to be withdrawn from");
		System.out.println("S - Search: search course based on substring of course name which is given by user; list all matching  courses");
		System.out.println("M - My Classes: lists all classes enrolled in by the active student.");
		System.out.println("X - Exit: exit application");
		System.out.println("================== System Menu ==============");
		System.out.println("Please input the command:");
		
	}
	private static void printL(){
		try{
			PreparedStatement pstmt = conn.prepareStatement("select * from courses");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				System.out.println(rs.getInt("cid") + "," + rs.getString("cname") + "," + rs.getInt("credits"));
			}
			rs.close();
			pstmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static void printM(){
		try{
			PreparedStatement pstmt = conn.prepareStatement("select * from enrolled a inner join courses b on a.cid=b.cid where a.sid=?");
			pstmt.setInt(1, sid);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				System.out.println(rs.getInt("cid") + ":" + rs.getString("cname"));
			}
			rs.close();
			pstmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] arg){
	
		conn = getConnection();
		if(conn == null){
			System.out.print("Cannot connect to the database!!");
			return;
		}
		Scanner input = new Scanner(System.in);
		System.out.print("Please input your Student ID: or Enter -1 for New Student");
		try{
			
			sid = input.nextInt();
		}catch(Exception ex){
            
			System.out.println("Student ID must be a number!");
            sid=input.nextInt();
			System.exit(1);
		}
		
		
		if(sid == -1){
			System.out.print("Please input a New Student ID:");
			try{
                Scanner sid= new Scanner(System.in);
                //sid = input.nextInt();
			}catch(Exception ex){
				
				System.out.println("Student ID must be a number!");
               
				//System.exit(-1);
			}
			 sid=input.nextInt();
			Student stu = findStudent(sid);
			if(stu != null){
				System.out.println("Student ID already exist!. Enter Different Student ID");
                sid=input.nextInt();
				//System.exit(-1);
			}
			
			System.out.print("Please input your name:");
			String name = input.next();
			
			addStudent(sid,name);
		}else{
			
			Student stu = findStudent(sid);
			if(stu == null){
				
				System.out.println("Student(sid=" + sid + ") Not exist! ");
                sid=input.nextInt();
                //System.exit(-1);
			}
		}
		
		
		boolean exit = false;
		
		while(!exit){
			
			
			System.out.println("\n");
			printMenu();
			String cmd = input.next();
			if(cmd.equalsIgnoreCase("x")){
				exit = true;
			}else if(cmd.equalsIgnoreCase("l")){
				printL();
			}else if(cmd.equalsIgnoreCase("e")){
				System.out.print("Please input the Course ID:");
				int cid = input.nextInt();
				
				Course c = findCourse(cid);
				if(c == null){
					System.out.println("Course does not exists!");
					continue;
				}
				
				if(checkEnrolled(sid,cid)){
					System.out.println("Course aleady selected!");
					continue;
				}
				
				select(sid,cid);
				System.out.println("Enrolled in Course");
			}else if(cmd.equalsIgnoreCase("w")){
				System.out.print("Please input the Course ID:");
				int cid = input.nextInt();
				
				Course c = findCourse(cid);
				if(c == null){
					System.out.println("Course not exists!");
					continue;
				}
				
				if(!checkEnrolled(sid,cid)){
					
					System.out.println("You have not selected this course yet!");
					continue;
				}
				
				unselect(sid,cid);
				System.out.println("Withdrawed from Course");
			}else if(cmd.equalsIgnoreCase("s")){
				
				System.out.print("Please input the keyword:");
				String keyword = input.next();
				
				List<Course> list = searchCourse(keyword);
				for(Course c : list){
					System.out.println("cid:" + c.cid + ",cname:" + c.cname + ",credits:" + c.credits);
				}
				
			}else if(cmd.equalsIgnoreCase("m")){
				
				printM();
			}
			System.out.println("\n");
			
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

