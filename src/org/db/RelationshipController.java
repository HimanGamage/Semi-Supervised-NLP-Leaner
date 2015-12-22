package org.db;

import java.sql.Connection;
import java.util.Date;

public class RelationshipController {
	public static boolean addRelationshipData(String cat1,String cat2,String relationship,String seeds) throws Exception{
        Connection con = DBCon.getInstance();
        Date d=new Date();
        String date=d.toString();                                                                                                                                               
        String query = " insert into promoted_relations(Category_1,Category_2,Relationship,Date,Seeds) values('"+cat1+"','"+cat2+"','"+relationship+"','"+date+"','"+seeds+"')";
        int rst = DBHandler.setData(con, query);
      
       if(rst>0){
           return true;
       }else{
           return false;
       } 
	}
}
