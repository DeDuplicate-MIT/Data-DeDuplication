package Final;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
public class Connect 
{
	public static Connection getConnect() throws Exception
	{
		Properties pro=new Properties();
		FileInputStream fin=new FileInputStream("dbCred.properties");
		pro.load(fin);
		String driver=pro.getProperty("driver");
		String url=pro.getProperty("url");
		String user=pro.getProperty("user");
		String pass=pro.getProperty("pass");
		Class.forName(driver);
		Connection con=DriverManager.getConnection(url,user,pass);
		return con;
	}
}
