package com.example.fms;

import java.util.*;
import java.util.Date;

import java.sql.*;

public class testdb
{
    public String ans="";
    private Connection con;
    private Statement st;
    private ResultSet rs;

    //Constructor
    public testdb()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/flightmanagment","root","7703");
            st=con.createStatement();
        }catch(ClassNotFoundException | SQLException ex)
        {
            //JOptionPane.showMessageDialog(null,"Error: "+ex);
            System.out.println("DB Error");
        }
    }
    
    
    public int isExists(String qtemp)
    {
        String query = String.format("select exists(%s)",qtemp);
        try
        {
            rs = st.executeQuery(query);
            if(rs.getString(1).equals("1"))
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        catch(SQLException ex)
        {
            //JOptionPane.showMessageDialog(null,"Error:"+ex);
            System.out.println("DB Error");
        }
        return 0;
    }
    
    
    public List<List<String>> getAvailableFlights(FlightInfoUser ob)
    {
        Date date = ob.getDate();
        String to = ob.getTo();
        String from = ob.getFrom();
        int passengers = ob.getPassengers();
        //select * from allflightnum where destTO = "Pune" AND destFROM = "Dubai" AND DATE(flightDATE) >= "2023-01-07" AND numAvailableSeats >= 5 ORDER BY ecoPrice ASC;
        String query = String.format("select * from allflightnum where destTO = \"%s\" AND destFROM = \"%s\" AND DATE(flightDATE) >= \"%s\" AND numAvailableSeats >= %d ORDER BY ecoPrice ASC",to,from,date,passengers);
        List<List<String>> ans = new ArrayList<List<String>>();
        
        int recordavail = isExists(query);
        if(recordavail == 1)
        {
            try
            {
                int x = 0;
                rs = st.executeQuery(query);
                while(rs.next())
                {
                    ans.get(x).add(rs.getString(1)); // flightnum
                    ans.get(x).add(rs.getString(2)); // to
                    ans.get(x).add(rs.getString(3)); // from
                    ans.get(x).add(rs.getString(4)); // date
                    ans.get(x).add(rs.getString(5)); // seats available
                    ans.get(x).add(rs.getString(6)); // economy price
                    double bp = Double.parseDouble(rs.getString(6));
                    bp = bp * 1.5;
                    // String.valueOf(d); 
                    ans.get(x).add(String.valueOf(bp)); // Buissness price
                    x++;
                    //ans += rs.getString(1)  +" "+ rs.getString(2) +" "+ rs.getString(3) +"\n";
                }
                return ans;
            }
            catch(SQLException ex)
            {
                System.out.println("DB Error");
                //return ex.toString();
            }
        }
        else
        {
            System.out.println("No records Available");
            return ans;
        }
        return ans;
    }
    
    
    // Check if user login credentials are correct
    public String checkLogin(LoginUser ob)
    {
        String usr = ob.getUsername();
        String pass = ob.getPassword();
        
        String query = String.format("select * from userlogin where username = \"%s\" and passwd = \"%s\"",usr,pass);
        
        int recordavail = isExists(query);
        if(recordavail == 1)
        {
            return "Successful";
        }
        else
        {
            System.out.println("No records Available");
            return "Invalid username or incorrect password";
        }
    }
    
    // Register data of new user
    public String registerUser(NewUser ob)
    {
        String name = ob.getName();
        String usr = ob.getUsername();
        String pass = ob.getPassword();
        
        String query1 = String.format("Insert into userlogin values(\"%s\", \"%s\", \"%s\")",usr,pass,name);
        String query2 = String.format("Create table %s (FlightsBooked varchar(10), flightDate datetime)");
        
        String qchk = String.format("select * from userlogin where username = \"%s\"",usr);
        int recordavail = isExists(qchk);
        if(recordavail == 1)
        {
            return "Username already exists";
        }
        else
        {
            try
            {
                st.executeUpdate(query1);
                st.executeUpdate(query2);
                return "User successfully added";
            }
            catch(SQLException ex)
            {
                System.out.println("DB Error");
                return "DB Error";
            }
        }
        //if(username exists then return username exists else register user)
    }
    
    
    
    public List<String> availableSeats(String flightNO) // flightNO ob
    {
        // flno = ob.flightnum;
        //select seatName from EK507 where seatTaken = 0;
        String query = String.format("select seatName from %s where seatTaken = 0",flightNO);
        List<String> ans = new ArrayList<String>();
        
        try
        {
            rs = st.executeQuery(query);
            while(rs.next())
            {
                ans.add(rs.getString(1));
            }
            return ans;
        }
        catch(SQLException ex)
        {
            System.out.println("DB Error");
            return ans;
        }
    }
    
    
    public List<String> userBookedFlights(String username) // flightNO ob
    {
        // usr = ob.Username
        // select now();
        
        String query1 = "select current_date";
        String currentDATE="";
        try
        {
            rs = st.executeQuery(query1);
            currentDATE = rs.getString(1);
        }
        catch(SQLException ex)
        {
            System.out.println("DB Error");
        }
        
        String query2 = String.format("Select FlightsBooked from %s where DATE(flightDate) > \"%s\"",username,currentDATE);
        
        List<String> ans = new ArrayList<String>();
        try
        {
            rs = st.executeQuery(query2);
            while(rs.next())
            {
                ans.add(rs.getString(1));
            }
            return ans;
        }
        catch(SQLException ex)
        {
            System.out.println("DB Error");
            return ans;
        }
    }
    
    public List<String> getSeatPrice(String flightNo)
    {
        List<String> ans = new ArrayList<String>();
        // select ecoPrice from allflightnum where flightnum = <flightNo>;
        // economy = output;
        // buissness = output*1.5;
        return ans; // return [eco,biss]
    }
    
    
    // Update seats which were booked in DB
    public void addSeatBooked(String seat, String flightNO, String username)
    {
        // update ek507 set bookedBy = <username>, seatTaken = 1 where seatName = ;
        String query = String.format("Update %s SET bookedBy = \"%s\", seatTaken = 1 where seatName = \"%s\"",flightNO ,username ,seat);
        try 
        {
            st.executeUpdate(query);
        } 
        catch(SQLException e) 
        {
            System.out.println("DB Error");
        }
    }
    
    // calls above function for each seat in array list
    public void arraySeatsBooked(String flightNO, List<String> seats, String username)
    {
        Iterator<String> itr = seats.iterator();
        while(itr.hasNext())
        {
            addSeatBooked(itr.next(), flightNO, username);
        }
    }
    
    
    // Update seats which were cancled in DB
    public void removeSeatBooked(String flightNO)
    {
        
    }
    
    // Update Available Seats
    public void updateAvailableSeats(String flightNO)
    {
        // select count(seatName) from EK507 where seatTaken = 0; 
        int numSeats = 0;
        try
        {
            String query1 = String.format("select count(seatName) from %s where seatTaken = 0",flightNO);
            rs = st.executeQuery(query1);
            numSeats = Integer.parseInt(rs.getString(1));
        }
        catch(SQLException e)
        {
            System.out.println("DB Error");
        }

        String query2 = String.format("UPDATE allflightnum SET numAvailableSeats = %d WHERE flightnum = \"%s\"",numSeats,flightNO);
        try
        {
            st.executeUpdate(query2);
            System.out.println("Successful");
        }
        catch(SQLException ex)
        {
            System.out.println("DB Error");
        }
    }
    
    
    public void userBookedSeats()
    {
        // SET SQL_SAFE_UPDATES = 0;
        // update ek507 set bookedBy = Null, seatTaken = 0 where bookedBy = "rahuls";
        // SET SQL_SAFE_UPDATES = 1;
    }
    
    
    public void userCancleFlight()
    {
        // call remove seats and userbooked seats
    }
    
    
}
