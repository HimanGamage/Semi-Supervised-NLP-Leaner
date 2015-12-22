package org.db;


import java.sql.*;

class DBCon{
	
    public static Connection con=null;
	
        public static Connection getInstance() throws Exception{
            
            if(con==null){
                System.out.println("Connection created");
               Class.forName("com.mysql.jdbc.Driver");
		//con=DriverManager.getConnection("jdbc:mysql://localhost:3306/fit_sixes","root","echo1234");
             con=DriverManager.getConnection("jdbc:mysql://localhost:3306/ont","root","echo1234");
            }
		
                
		return con;
	}
}