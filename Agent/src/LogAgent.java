import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 2016 GRZEGORZ PRZYTU£A ALL RIGHTS RESERVED 
 * - Server with data base connectivity and my own protocol through TCP/IP
 */
public class LogAgent
{
	private DataBaseAccessor dba;

	public static void main(String[] args)
	{
		LogAgent la = new LogAgent();	
		la.startServer();
	}

	public LogAgent()
	{
		dba = new DataBaseAccessor();
	}

	public void startServer()
	{
		if(!dba.isDataBaseExisting())
		{
			sysOut("Press enter...");
			System.console().readLine();
			return;
		}
		
		ServerSocket server = null;
		sysOut("---> Server start <---");
		try
		{
			server = new ServerSocket(ProtocolConsts.PORT_NR, ProtocolConsts.MAX_CLIENTS);
			while (true)
			{
				Socket connection = server.accept();
				sysOut("\n---> New Connection");
				new Thread(new ServerThread(connection,dba)).start();
			}
		}
		catch (IOException ex)
		{
			sysOut(ex.getMessage() + "\n---> Server gone down <---");
		}
		finally
		{
			try
			{
				if(server != null)
				server.close();
			}
			catch (IOException e)
			{
				sysOut( e.getMessage() + "\n---> Problems with closing.");
			}
		}
		sysOut("Press enter...");
		System.console().readLine();
		sysOut("\n---> Server stop <---");
	}

	private void sysOut(String msg)
	{
		System.out.println(msg);
	}
}
