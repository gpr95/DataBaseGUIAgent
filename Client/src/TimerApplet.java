import java.awt.Color;
import java.awt.Graphics;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JApplet;

public class TimerApplet extends JApplet implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Thread mainThread;
	public void init()
	{
		if(mainThread == null)
		{
	      mainThread = new Thread(this);
	      mainThread.start();
		}
	}
	@SuppressWarnings("static-access")
	@Override
	public void run()
	{
		while(true)
		{
			try{mainThread.sleep(1000);} catch (InterruptedException e){}
			repaint();
		}
		
	}

	public void paint(Graphics g) 
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getSize().width, getSize().height);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		g.setColor(Color.BLACK);
		g.drawString(dateFormat.format(cal.getTime()),16,13);
	}
}