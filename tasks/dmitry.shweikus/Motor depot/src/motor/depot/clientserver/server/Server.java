/**
 * 
 */
package motor.depot.clientserver.server;

import java.io.IOException;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import motor.depot.clientserver.ClientServerCommand;
import motor.depot.clientserver.ConnectionSettings;
import motor.depot.model.Dispatcher;
import motor.depot.model.MotorDepot;
import motor.depot.storages.csv.Storage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author dima
 * main class for server
 */
public class Server
{
	/**
	 * @author dima
	 * 
	 */
	private ServerSocket servers;
	public BlockingQueue<ClientThread> threads = new LinkedBlockingQueue<ClientThread>();
	private MotorDepot motorDepot = null;
	public void addThreadToQueue(ClientThread thread)
	{
		// offer is sync. we dont need "synchronized"
		threads.offer(thread);
	}

	public synchronized void removeThreadFromQueue(ClientThread thread)
	{
		threads.remove(thread);
	}

	static final Logger LOGGER = Logger.getLogger(Server.class);
	private static Server instance = null;

	public static Server getInstance()
	{
		if (instance == null)
			instance = new Server();
		return instance;
	}

	private Server() {
		instance = this;
		motorDepot = new MotorDepot(new Storage());
	}
	
	public synchronized boolean disconnectUser(ClientThread thread,String message)
	{
		if (thread.getUser() != null)
			if(thread.getUser() instanceof Dispatcher)
				return false;
		try
		{
			ClientServerCommand.sendText(thread.getOut(), message);
			ClientServerCommand.CLOSE_CLIENT.getImpl().sendToClient(thread.getOut());
		} catch (IOException e)
		{
			LOGGER.error("Error disconnecting user " + thread.toString(),e);
		}
		return true;
	}
	public void closeAll(String message, int seconds)
	{
		for (ClientThread thread : threads)
		{
			try
			{
				ClientServerCommand.sendText(thread.getOut(), message);
			} catch (IOException e)
			{
				LOGGER.error("IOException by sending closing text to " + thread.toString(),e);
			}
		}
		try
		{
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e1)
		{
		} 
		for (ClientThread thread : threads)
		{
			try
			{
				ClientServerCommand.CLOSE_CLIENT.getImpl().sendToClient(thread.getOut());
			} catch (IOException e)
			{
				LOGGER.error("IOException by sending closing command to " + thread.toString(),e);
			}
		}
		threads.clear();
		try
		{
			servers.close();
		} catch (IOException e)
		{
			LOGGER.error("Error closing main socket in closing command", e);
		}
		motorDepot.save(new Storage());
		LOGGER.debug("Server closed"); //$NON-NLS-1$
		System.exit(0);
	}

	public void execute()
	{
		LOGGER.debug("Server started"); //$NON-NLS-1$
		ConnectionSettings.load();
		try
		{
			servers = new ServerSocket(ConnectionSettings.port);
			try
			{
				int newId = 0;
				while (!servers.isClosed())
				{
					Socket socket = servers.accept();
					if (socket != null)
					{
						newId++;
						ClientThread runnable = new ClientThread(socket, newId,motorDepot);
						Thread thread = new Thread(runnable);
						addThreadToQueue(runnable);
						thread.start();
					}
				}
			} catch (IOException e)
			{
				//IOexception occurs, when we closing opened serversocket.
				//we don't need handle it.
			}
			if (!servers.isClosed())
				servers.close();
		} catch (IOException e)
		{
			LOGGER.error("IOException by creating serversocket", e); //$NON-NLS-1$
		}
	}

	public static void main(String[] args)
	{
		PropertyConfigurator.configure("log4j-server.properties"); //$NON-NLS-1$
		Server.getInstance().execute();
	}
}
