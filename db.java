import java.sql.*;
import java.io.*;
import java.io.File;
import java.util.Date;

class db {

    public static void main(String args[]) throws IOException {

        File f = new File("temp.jpg");
        Metadata meta = new Metadata();
        try {
            exif.ExifData d = exif.parse(f.getAbsolutePath());
            meta.filename = f.getName();
            meta.datetime = d.date;
            meta.latitude = d.lat;
            meta.longitude = d.lon;
            // meta.sha256 = HashUtil.sha256(f);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse EXIF for file: " + f.getName());
        }

        String url;
        Connection conn;
        PreparedStatement pStatement;
        int result;
        ResultSet rs;
        String queryString;

        try { Class.forName("org.postgresql.Driver"); }
        catch (ClassNotFoundException e) { System.out.println("Failed to find the JDBC driver"); }

        try {
            url = "jdbc:postgresql://localhost:5432/postgres";
            conn = DriverManager.getConnection(url, "postgres", "rubiks");

            queryString = "set search_path to cs370";
            pStatement = conn.prepareStatement(queryString);
            result = pStatement.executeUpdate();

            queryString = "drop table if exists images";
            pStatement = conn.prepareStatement(queryString);
            result = pStatement.executeUpdate();

            queryString = "create table images (latitude char(40), longitude char(40), hash char(40), date char(40), time char(40))";
            pStatement = conn.prepareStatement(queryString);
            result = pStatement.executeUpdate();

            queryString = "insert into images (latitude, longitude, date) values (?, ?, ?)";
            pStatement = conn.prepareStatement(queryString);
            pStatement.setString(1, meta.latitude.toString());
            pStatement.setString(2, meta.longitude.toString());
            pStatement.setString(3, meta.datetime);
            result = pStatement.executeUpdate();
        }
        catch (SQLException se) { System.err.println("SQL Exception." + "<Message>: " + se.getMessage()); }
    }
}
