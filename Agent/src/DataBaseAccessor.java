import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Grzegorz Przytu³a - Class that implements JDBC methods for
 *         connectivity with local data base
 */
public class DataBaseAccessor implements DataBaseAccessible
{
	/** Packet adsress for JDBC driver */
	private static String driver;
	/** URL data base address */
	private static String url;
	/** User login for data base */
	private static String login;
	/** User password for data base */
	private static String password;

	/**
	 * STATIC BLOCK - update default configuration data/download from
	 * dbconfig.properties file - start JDBC driver
	 */
	static
	{
		try
		{
			Configuration();
		}
		catch (IOException ex)
		{
			System.err.println("Configuration failed.\n" + ex.getMessage());
		}

		try
		{
			Class.forName(driver);
		}
		catch (ClassNotFoundException ex)
		{
			System.err.println("Registration the JDBC/ODBC driver failed.\n" + ex.getMessage());
		}
	}

	private static void Configuration() throws IOException
	{
		File file = new File("dbconfig.properties");
		if (!file.exists())
		{
			file.createNewFile();
			String defaultData = "#Driver for JDBC\n" + "driver=oracle.jdbc.driver.OracleDriver\n"
					+ "#Data Base adress (default on your localhost on 80 port)\n"
					+ "url=jdbc:oracle:thin:@//localhost:1522/orcl\n" + "#login on your database\n"
					+ "login=grzegorz\n" + "#your password on database\n" + "password=##TAJNE##";
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(defaultData);
			bufferWritter.close();
		}
		try (FileReader reader = new FileReader("dbconfig.properties"))
		{
			Properties prop = new Properties();
			prop.load(reader);
			driver = prop.getProperty("driver");
			url = prop.getProperty("url");
			login = prop.getProperty("login");
			password = prop.getProperty("password");

		}

	}

	/** Returns connection handler through driver */
	private Connection createConnection() throws SQLException
	{
		return DriverManager.getConnection(url, login, password);
	}

	/** Closing connection with local data base */
	private void endConnection(Connection connection)
	{
		if (connection == null)
		{
			return;
		}
		try
		{
			connection.close();
		}
		catch (SQLException ex)
		{
			System.err.println("Closing connection with local data base failed.\n" + ex.getSQLState());
		}
	}

	/**
	 * Creates new table , squence and trigger for given values
	 * 
	 * @return
	 */
	public String createNewLedger(String ledgerName, Map<String, ValueType> columnTypes)
	{
		String query = " CREATE TABLE " + ledgerName + " (ID INTEGER not NULL, " + columnTypes.keySet().stream()
				.map(k -> k + " " + columnTypes.get(k)).collect(Collectors.joining(",")) + ")";
		String queryToTrigger = " ALTER TABLE " + ledgerName + " ADD (" + "CONSTRAINT " + ledgerName + "_pk"
				+ " PRIMARY KEY (ID))";
		String queryToSequence = "CREATE SEQUENCE " + ledgerName + "_seq START WITH 1";
		String queryTriggeeAdd = "CREATE OR REPLACE TRIGGER " + ledgerName + "_bir " + "BEFORE INSERT ON "
				+ ledgerName + " FOR EACH ROW" +

				" BEGIN" + "  SELECT " + ledgerName + "_seq.NEXTVAL" + "  INTO   :new.id" + "  FROM   dual;"
				+ "END;";

		Connection c = null;
		try
		{
			c = createConnection();
			Statement statement = c.createStatement();
			statement.execute(query);
			statement.execute(queryToTrigger);
			statement.execute(queryToSequence);
			statement.execute(queryTriggeeAdd);
		}
		catch (SQLException ex)
		{
			System.err.println("Creating new table failed.\n" + ex.getSQLState());
			return "401 OPERATION FAILED";
		}
		finally
		{
			endConnection(c);
		}
		return "200 OK TABLE CREATED";
	}

	public String insertLog(String tabName, String eventTime, String machineId, List<String> logRec)
	{
		String query = "INSERT INTO " + tabName + "(EVENT_TIME,REPORTING_MACHINE_ID"
				+ logRec.stream().map(i -> "," + i.substring(0, i.indexOf("="))).collect(Collectors.joining())
				+ ") " + "VALUES (?,?" + logRec.stream().map(i -> ",?").collect(Collectors.joining()) + ") ";

		Connection c = null;
		try
		{
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			int counter = 1;
			statement.setString(counter++, eventTime);
			statement.setString(counter++, machineId);

			for (String value : logRec.stream().map(i -> i.substring(i.indexOf("=") + 1, i.length()))
					.collect(Collectors.toList()))
				statement.setString(counter++, value);

			statement.executeUpdate();
		}
		catch (SQLException ex)
		{
			System.err.println("Inserting record failed.\n" + ex.getSQLState());
			return "401 OPERATION FAILED";
		}
		finally
		{
			endConnection(c);
		}
		return "200 OK INSERTING SUCCESSFUL";
	}

	public boolean tableExists(String tabName)
	{
		Connection c = null;
		try
		{
			c = createConnection();
			DatabaseMetaData metaData = c.getMetaData();
			ResultSet tabData = metaData.getTables(null, null, tabName, null);
			if (tabData.next())
			{
				return true;
			}

		}
		catch (SQLException ex)
		{
			System.err.println("Checking that table exists failed.\n" + ex.getSQLState());
		}
		finally
		{
			endConnection(c);
		}
		return false;
	}

	public List<String> getTables()
	{
		List<String> result = new ArrayList<>();
		Connection c = null;
		try
		{
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT table_name FROM user_tables");
			while (resultSet.next())
			{
				result.add(resultSet.getString("table_name"));
			}
		}
		catch (SQLException ex)
		{
			System.err.println("Selecting tables failed.\n" + ex.getSQLState());
		}
		finally
		{
			endConnection(c);
		}

		return result;
	}

	public Map<String, String> getTableColumnsWithType(String tabName)
	{
		Map<String, String> result = new HashMap<>();
		Connection c = null;
		try
		{
			c = createConnection();

			Statement statement = c.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM \"" + tabName + "\"");
			ResultSetMetaData meta = resultSet.getMetaData();
			for (int i = 1; i <= meta.getColumnCount(); i++)
			{
				result.put(meta.getColumnLabel(i), meta.getColumnTypeName(i));
			}
		}
		catch (SQLException ex)
		{
			System.err.println("Selecting column failed.\n" + ex.getSQLState());
		}
		finally
		{
			endConnection(c);
		}

		return result;
	}

	public List<String> getAllRowsFromLedger(String tabName)
	{
		String query = "SELECT * FROM " + tabName;
		return executeListLogModelQueryOnDatabase(query);
	}

	private List<String> executeListLogModelQueryOnDatabase(String query)
	{
		List<String> result = new ArrayList<>();
		Connection c = null;
		try
		{
			c = createConnection();
			Statement statement = c.createStatement();
			ResultSet recordlist = statement.executeQuery(query);
			ResultSetMetaData rsmd = recordlist.getMetaData();
			StringBuilder sb = new StringBuilder();
			String separator = "";
			for (int i = 1; i <= rsmd.getColumnCount(); i++)
			{
				sb.append(separator + rsmd.getColumnLabel(i));
				separator = ",";
			}
			result.add(sb.toString());
			while (recordlist.next())
			{
				sb = new StringBuilder();
				separator = "";
				for (int i = 1; i <= rsmd.getColumnCount(); i++)
				{
					if (recordlist.getObject(rsmd.getColumnLabel(i)) != null)
						sb.append(separator + recordlist.getObject(rsmd.getColumnLabel(i)).toString());
					else
						sb.append(separator + " ");
					separator = ",";
				}
				result.add(sb.toString());
			}
		}
		catch (SQLException ex)
		{
			System.err.println("Selecting column with data failed.\n" + ex.getSQLState());
		}
		finally
		{
			endConnection(c);
		}

		return result;
	}

	public String deleteLog(String tabName, String id)
	{
		String query = "DELETE FROM " + tabName + " WHERE ID = ?";
		Connection c = null;
		try
		{
			c = createConnection();
			PreparedStatement statement = c.prepareStatement(query);
			statement.setInt(1, Integer.valueOf(id));
			statement.executeUpdate();
		}
		catch (SQLException ex)
		{
			System.err.println("Deleting record failed." + ex.getSQLState());
			return "401 OPERATION FAILED";
		}
		finally
		{
			endConnection(c);
		}
		return "200 OK DELETING SUCCESSFUL";
	}
	
	public boolean isDataBaseExisting()
	{
		Connection c = null;
		try
		{
			c = createConnection();
		}
		catch (SQLException ex)
		{
			System.err.println("Connection with database failed. Check database.properties file and reconfigure it\n");
			return false;
		}
		finally
		{
			endConnection(c);
		}
		
		return true;
	}
}