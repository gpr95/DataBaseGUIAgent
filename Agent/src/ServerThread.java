import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerThread extends Thread
{
	private Socket connection;
	private DataBaseAccessor dba;
	private ObjectInputStream oInputStream;
	private ObjectOutputStream oOutputStream;

	ServerThread(Socket connection, DataBaseAccessor dba)
	{
		this.connection = connection;
		this.dba = dba;
	}

	public void run()
	{
		sysOut("---> Thread start <---");
		String receivedMessage;
		String respondMessage = null;

		try
		{
			openStreams();
			receivedMessage = getMessageFromClient();
			if (receivedMessage != null)
				respondMessage = askDataBase(receivedMessage);
			sendMessage(respondMessage);
		}
		catch (EOFException ex)
		{
			sysOut(ex.getMessage() + "\n---> Connection with client failed.");
		}
		catch (IOException ex)
		{
			sysOut(ex.getMessage() + "\n---> Thread gone down <---");
		}
		finally
		{
			try
			{
				oOutputStream.close();
				oInputStream.close();
				connection.close();
			}
			catch (IOException e)
			{
				sysOut( e.getMessage() + "\n---> Problems with closing.");
			}
		}
		sysOut("---> Thread stop <---\n");
	}

	private void openStreams() throws IOException
	{
		oInputStream = new ObjectInputStream(connection.getInputStream());
		oOutputStream = new ObjectOutputStream(connection.getOutputStream());
		oOutputStream.flush();
	}

	private String askDataBase(String message)
	{
		List<String> splitted = Arrays.asList(message.split("[<>]+"));
		String command = splitted.get(1);
		splitted = splitted.subList(2, splitted.size());
		sysOut(command + " from " + connection.getInetAddress().getHostName());

		switch (command)
		{
			case ProtocolConsts.CREATE_TABLE_HEADER:
				return createTable(splitted);
			case ProtocolConsts.GET_TABLES_HEADER:
				List<String> tables = dba.getTables();
				if (!tables.isEmpty())
					return "<200 OK SENDING TABLES>"
							+ dba.getTables().stream().collect(Collectors.joining(","));
				else
					return "404 TABLES NOT FOUND";
			case ProtocolConsts.GET_CLUMNS_WITH_META_HEADER:
				return getColumnsWithMeta(splitted.get(0));
			case ProtocolConsts.GET_CLUMNS_AND_VALUES_HEADER:
				return getColumnsWithData(splitted.get(0));
			case ProtocolConsts.ADD_RECORD_HEADER:
				return addRecord(splitted);
			case ProtocolConsts.DELETE_HEADER:
				return deleteRecord(splitted);
			default:
				return "404 COMMAND NOT FOUND";
		}
	}

	private String deleteRecord(List<String> splitted)
	{
		String tabName = splitted.get(0);
		tabName = tabName.substring(tabName.indexOf("=") + 1, tabName.length());
		if (!dba.tableExists(tabName))
			return "404 TABLE NOT FOUND";
		splitted = splitted.subList(1, splitted.size());

		return dba.deleteLog(tabName, splitted.get(0));
	}

	private String getColumnsWithData(String tabName)
	{
		if (!dba.tableExists(tabName))
			return "<404 TABLE NOT FOUND>";

		List<String> rows = dba.getAllRowsFromLedger(tabName);
		StringBuilder sb = new StringBuilder("<200 OK SENDING COLUMNS WITH DATA>");
		rows.forEach(r -> sb.append("<" + r + ">"));
		return sb.toString();
	}

	private String addRecord(List<String> splitted)
	{
		String tabName = splitted.get(0);
		tabName = tabName.substring(tabName.indexOf("=") + 1, tabName.length());
		if (!dba.tableExists(tabName))
			return "404 TABLE NOT FOUND";
		splitted = splitted.subList(1, splitted.size());
		return dba.insertLog(tabName,
				splitted.get(0).substring(splitted.get(0).indexOf("=") + 1, splitted.get(0).length()),
				splitted.get(1).substring(splitted.get(1).indexOf("=") + 1, splitted.get(1).length()),
				splitted.subList(2, splitted.size()));
	}

	private String getColumnsWithMeta(String tabName)
	{
		if (!dba.tableExists(tabName))
			return "<404 TABLE NOT FOUND>";
		Map<String, String> columns = dba.getTableColumnsWithType(tabName);
		if (columns.isEmpty())
			return "401 OPERATION FAILED";
		StringBuilder sb = new StringBuilder("<200 OK SENDING COLUMNS FOR TABLE " + tabName + ">");
		for (String column : columns.keySet())
		{
			if (!column.equals("EVENT_TIME") && !column.equals("REPORTING_MACHINE_ID") && !column.equals("ID"))
				sb.append("<" + column + "=" + columns.get(column) + ">");
		}
		return sb.toString();
	}

	private String createTable(List<String> splitted)
	{
		String tabName = splitted.get(0);
		if (dba.tableExists(tabName))
			return "409 TABLE ALREADY EXISTS";
		splitted = splitted.subList(1, splitted.size());
		tabName = tabName.substring(tabName.indexOf("=") + 1, tabName.length());
		Map<String, ValueType> columnTypes = new HashMap<String, ValueType>();
		for (String type : splitted)
			columnTypes.put(type.substring(0, type.indexOf("=")),
					ValueType.fromStringValue(type.substring(type.indexOf("=") + 1, type.length())));

		return dba.createNewLedger(tabName, columnTypes);
	}

	private void sendMessage(String respondMsg) throws IOException
	{
		oOutputStream.writeObject(respondMsg);
		oOutputStream.flush();
	}

	private String getMessageFromClient() throws IOException
	{
		String message = null;
		try
		{
			message = oInputStream.readObject().toString();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return message;
	}

	private void sysOut(String msg)
	{
		System.out.println(msg);
	}
}
