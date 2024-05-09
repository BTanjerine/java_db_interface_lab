package jdbclab04;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class lab04_SQL {

    DBConnect cnctn;
    ui_util ui;

    lab04_SQL(){
        cnctn = new DBConnect();        
        ui = new ui_util();
        this.lab04_program();
    }

    public void lab04_program(){
        int main_opt = 0;

        while(main_opt != 9){
            main_opt = ui.main_menu();

            try{
                switch(main_opt){
                    case 1:
                        String start = ui.get_start_location();
                        String dest = ui.get_destination();
                        String date = ui.get_date();
                        //City C City D 2024-04-28

                        System.out.println("\nTrip Data: ");
                        getTripInfo(start, dest, date);
                    break;

                    case 2:
                        int id = ui.get_trip_number();

                        System.out.println("\nStops for trip number " + id + ": ");
                        getTripStops(id);
                    break;

                    case 3:
                        String name = ui.get_driver_name(); 
                        String dr_date = ui.get_date();

                        System.out.println("\nDriver Schedule for driver " + name + " on " + dr_date + ": ");
                        getDriverSchedule(name, dr_date);
                    break;

                    //
                    case 4:
                        editSchedules();

                    break;

                    //add actual info
                    case 5:
                        int act_id = ui.get_trip_number();
                        String act_date = ui.get_date();
                        String act_sched_start = ui.get_scheduled_start_time();
                        int stopNumber = ui.get_stop_number();
                        String act_sched_arrival= ui.get_scheduled_arrival_time();
                        String act_start = ui.get_actual_start_time();
                        String act_arrival = ui.get_actual_arrival_time();
                        int act_numIn = ui.get_num_passangers_in(); 
                        int act_numOut = ui.get_num_passangers_out();

                        addActualTrip(act_id, act_date, act_sched_start, stopNumber, act_sched_arrival, act_start, act_arrival, act_numIn, act_numOut);                        
                    break;

                    //add driver
                    case 6:
                        String dr_name = ui.get_driver_name();
                        String dr_number = ui.get_driver_phone_number();

                        addDriver(dr_name, dr_number);
                    break;

                    //delete bus
                    case 7:
                        int bus_id_del= ui.get_bus_ID();

                        deleteBus(bus_id_del);
                    break;

                    //add bus
                    case 8:
                        int bus_id_add = ui.get_bus_ID();
                        String bus_model = ui.get_bus_model();
                        int bus_year = ui.get_bus_year();

                        addBus(bus_id_add, bus_model, bus_year);
                    break;
                }
            }
            catch(SQLException e){
                System.out.println("Get Error u kno");
                System.out.println(e.getMessage());
            }
        }
    }

    private void getTripInfo(String startloc, String destination, String date) throws SQLException{

        String s = "SELECT Tr.TripNumber, Tr.StartLocationName, Tr.DestinationName, TrO.ScheduledStartTime, TrO.ScheduledArrivalTime, TrO.DriverName, TrO.BusID FROM Trip Tr INNER JOIN TripOffering TrO ON Tr.TripNumber == TrO.TripNumber WHERE Tr.StartLocationName == ? AND  Tr.DestinationName == ? AND TrO.Date == ?;";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setString(1, startloc);
        stmnt.setString(2, destination);
        stmnt.setString(3, date);

        //get the results
        ResultSet res = stmnt.executeQuery();

        while(res.next()){
            System.out.println("TripId: " + res.getInt(1));
            System.out.println("StartLocation: " + res.getString(2));
            System.out.println("Destination: " + res.getString(3));
            System.out.println("Scheduled StartTime: " + res.getString(4));
            System.out.println("Scheduled ArrivalTime: " + res.getString(5));
            System.out.println("Driver Name: " + res.getString(6));
            System.out.println("Date: " + res.getInt(7));
        }
    }

    private void getTripStops(int tripID) throws SQLException{

        String s = "SELECT trStop.StopNumber, trStop.SequenceNumber, trStop.DrivingTime FROM TripStopInfo trStop WHERE trStop.TripNumber == ?;";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setInt(1, tripID);

        //get the results
        ResultSet res = stmnt.executeQuery();

        while(res.next()){
            System.out.println("Stop Number: " + res.getInt(1));
            System.out.println("Sequence Number: " + res.getInt(2));
            System.out.println("Date: " + res.getString(3));
        }
    }

    private void getDriverSchedule(String driverName, String date) throws SQLException{

        String s = "SELECT TrO.TripNumber, TrO.ScheduledStartTime, TrO.ScheduledArrivalTime, TrO.BusID FROM TripOffering TrO WHERE TrO.DriverName == ? AND TrO.Date == ?;";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setString(1, driverName);
        stmnt.setString(2, date);

        //get the results
        ResultSet res = stmnt.executeQuery();

        while(res.next()){
            System.out.println("Trip Number: " + res.getString(1));
            System.out.println("Scheduled Start Time: " + res.getString(2));
            System.out.println("Scheduled Arrival Time: " + res.getString(3));
            System.out.println("BusID: " + res.getInt(4));
        }
    }

    private void editSchedules() throws SQLException{
        int schd_opt = 0;
        while(schd_opt != 5){
        
            schd_opt = ui.schedule_menu();

            switch(schd_opt){
                //delete trip
                case 1:
                    int id_del = ui.get_trip_number();    
                    String date_del = ui.get_date();
                    String start_time_del = ui.get_scheduled_arrival_time();

                    deleteTripOffering(id_del, date_del, start_time_del);
                break;

                //add trip
                case 2:
                    while(true){

                        int id = ui.get_trip_number();                        
                        String date = ui.get_date();
                        String start_time = ui.get_scheduled_start_time();
                        String arrival_time = ui.get_scheduled_arrival_time();
                        String driverName = ui.get_driver_name();
                        int busID = ui.get_bus_ID();
                        
                        System.out.println("New trip:");
                        System.out.println("Trip Number: " + id);
                        System.out.println("Date: " + date);
                        System.out.println("Scheduled Start Time: " + start_time);
                        System.out.println("Scheduled Arrival Time: " + arrival_time);
                        System.out.println("Driver Name: " + driverName);
                        System.out.println("Bus ID: " + busID);    
                         
                        addTripOffering(id, date, start_time, arrival_time, driverName, busID);
                        if(!ui.ask_trip_offering()) break;
                    }
                break;

                //change driver
                case 3:
                    int id_drive_upd = ui.get_trip_number();
                    String date_drive_upd = ui.get_date();
                    String start_time_drive_upd = ui.get_scheduled_start_time();
                    System.out.println("Enter updated driver name");
                    String new_driverName = ui.get_driver_name();

                    updateDriver(id_drive_upd, date_drive_upd, start_time_drive_upd, new_driverName);
                break;

                //change bus 
                case 4:
                    int id_bus_upd = ui.get_trip_number();
                    String date_bus_upd = ui.get_date();
                    String start_time_bus_upd = ui.get_scheduled_start_time();
                    System.out.println("Enter updated busID");
                    int new_busID= ui.get_bus_ID();

                    updateBus(id_bus_upd, date_bus_upd, start_time_bus_upd, new_busID);
                break;
            }
        }
    }

    private void addTripOffering(int id, String date, String start, String arrival, String driverN, int busID) throws SQLException{

        String s = "INSERT INTO TripOffering (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID) VALUES (?, ?, ?, ?, ?, ?);";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setInt(1, id);
        stmnt.setString(2, date);
        stmnt.setString(3, start);
        stmnt.setString(4, arrival);
        stmnt.setString(5, driverN);
        stmnt.setInt(6, busID);

        //send update to database
        stmnt.executeUpdate();
        System.out.println("added new trip");
    }

    private void deleteTripOffering(int id, String date, String start) throws SQLException{

        String s = "DELETE FROM TripOffering WHERE TripNumber == ? AND Date == ? AND ScheduledStartTime == ?;";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setInt(1, id);
        stmnt.setString(2, date);
        stmnt.setString(3, start);

        //send update to database
        stmnt.executeUpdate();
        System.out.println("removed trip");
    }

    private void updateDriver(int id, String date, String start, String new_driverName) throws SQLException{

        String s = "UPDATE TripOffering SET DriverName = ? WHERE TripNumber == ? AND Date == ? AND ScheduledStartTime == ?;";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setString(1, new_driverName);
        stmnt.setInt(2, id);
        stmnt.setString(3, date);
        stmnt.setString(4, start);

        //send update to database
        stmnt.executeUpdate();
        System.out.println("updating trip driver's name to " + new_driverName);
    }

    private void updateBus(int id, String date, String start, int new_busID) throws SQLException{

        String s = "UPDATE TripOffering SET BusID = ? WHERE TripNumber == ? AND Date == ? AND ScheduledStartTime == ?;";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setInt(1, new_busID);
        stmnt.setInt(2, id);
        stmnt.setString(3, date);
        stmnt.setString(4, start);

        //send update to database
        stmnt.executeUpdate();
        System.out.println("updating trip bus to bus number: "+ new_busID);
    }

    private void addActualTrip(int id, String date, String sched_start, int stopNumber, String sched_arrival, String act_start, String act_arrival, int act_numIn, int act_numOut) throws SQLException
    {
        String s = "INSERT INTO ActualTripStopInfo (TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOFPassengerOut) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setInt(1, id);
        stmnt.setString(2, date);
        stmnt.setString(3, sched_start);
        stmnt.setInt(4, stopNumber);
        stmnt.setString(5, sched_arrival);
        stmnt.setString(6, act_start);
        stmnt.setString(7, act_arrival);
        stmnt.setInt(8, act_numIn);
        stmnt.setInt(9, act_numOut);

        //send update to database
        stmnt.executeUpdate();
        System.out.println("recording actual trip of id: " + id);
    }

    private void addDriver(String name, String teleNum) throws SQLException{

        String s = "INSERT INTO Driver (DriverName, DriverTelephoneNumber) VALUES (?, ?);";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setString(1, name);
        stmnt.setString(2, teleNum);

        //send update to database
        stmnt.executeUpdate();
        System.out.println("Adding new driver: " + name + " - " + teleNum);
    }

    private void deleteBus(int id) throws SQLException{

        String s = "DELETE FROM BUS WHERE BusID == ?;";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setInt(1, id);

        //send update to database
        stmnt.executeUpdate();
        System.out.println("Removed Bus with id: " + id);
    }

    private void addBus(int id, String model, int year) throws SQLException{

        String s = "INSERT INTO Bus (BusID, Model, Year) VALUES (?, ?, ?);";

        PreparedStatement stmnt = cnctn.connection.prepareStatement(s);

        //make sure query doesnt
        stmnt.setQueryTimeout(30);

        //set parameter for statement
        stmnt.setInt(1, id);
        stmnt.setString(2, model);
        stmnt.setInt(3, year);

        //send update to database
        stmnt.executeUpdate();
        System.out.println("Added bus with id: " + id);
    }
}
