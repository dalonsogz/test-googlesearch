package com.googleSearch.test_googleSearch;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/******************************************************************************
 * <tt>Logesta TeseoWebPlus title of the class </tt> <br>
 * description (use as many lines as necessary)
 * @Reuters Consulting
 * @Copyright (c) 2002
 * @author        miguel rivero
 * @version       1.0
 ******************************************************************************/
public class JInternalDialog extends JInternalFrame
{
	private int width;
	private int height;
	private PanelBase jPanelBase;
	private boolean modal	= false;	
	
	public JInternalDialog(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
	{
		super(title, resizable, closable, maximizable, iconifiable);
	}

	public JInternalDialog(String title,PanelBase jPanelBase) {
		this(title,jPanelBase,false,false,false,false);
	}

	public JInternalDialog(String title,PanelBase jPanelBase, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
	{
		super(title,resizable, closable, maximizable, iconifiable);
		JDesktopPane jDesktopPane	= null;
		if (jPanelBase!=null)
			jDesktopPane			= jPanelBase.getJDesktopPane();

		width 			= (jDesktopPane!=null)?jDesktopPane.getWidth():800;
		height 			= (jDesktopPane!=null)?jDesktopPane.getHeight():600;
		this.jPanelBase = jPanelBase;

		if (jPanelBase!=null)
			setFrameIcon(new ImageIcon(jPanelBase.getImageIcon("LLogIcon.gif")));
		setBackground(Color.WHITE);
		setVisible(true);
		
	}
	
	public JInternalDialog(String title,PanelBase jPanelBase, JPanel panelToShow, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
	{
		this(title, jPanelBase, resizable, closable, maximizable, iconifiable);
		final Container cont = getContentPane();
		final GridBagLayout gbl = new GridBagLayout();
		final GridBagConstraints gbc = new GridBagConstraints();
		cont.setLayout(gbl);
		SwingUtilities.setGridBagConstraints(gbc,1,1,1,1,1.0,0.1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,0,0,0,0,0,0);
		gbl.setConstraints(panelToShow,gbc);
		cont.add(panelToShow);
	}
	
	public JInternalDialog(String title, Container container, JPanel panelToShow, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
	{
		this(title, null, resizable, closable, maximizable, iconifiable);
		
		Container cont = container.getParent();  
		while ((cont!=null) && !(cont instanceof JDesktopPane)) {  
			System.out.println(cont);
			cont = cont.getParent();  
		} 
		System.out.println(cont);

//		final GridBagConstraints gbc = new GridBagConstraints();
//		this.setLayout(new GridBagLayout());
//		SwingUtilities.setGridBagConstraints(gbc,0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,0,0,0,0,0,0);
//		this.add(panelToShow,gbc);
//		this.setPreferredSize(new Dimension(400,600));
//		
//		cont.setLayout(new GridBagLayout());
//		SwingUtilities.setGridBagConstraints(gbc,0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,0,0,0,0,0,0);
//		cont.add(this);
		
//	    JDesktopPane desktop = (JDesktopPane)cont;
//	    desktop.setLayout(new GridBagLayout());
//	    desktop.add(this);
//	    this.setBounds(25, 25, 800, 600);
//	    this.setVisible(true);
//	    this.show();
	}
	
	JButton	jbOk		= null;
	JButton	jbCancel	= null;

	public JInternalDialog(String title,PanelBase jPanelBase, JPanel panelToShow, boolean acceptButton, boolean cancelButton)
	{
		this(title,jPanelBase);
		final Container cont	= getContentPane();
		final GridBagLayout gbl			= new GridBagLayout();
		final GridBagConstraints gbc	= new GridBagConstraints();
		cont.setLayout(gbl);
		SwingUtilities.setGridBagConstraints(gbc,0,0,1+((acceptButton && cancelButton) ? 1 : 0),1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0);
		gbl.setConstraints(panelToShow,gbc);
		cont.add(panelToShow);
		int	c = 0;
		if (acceptButton)
		{
			jbOk = new JButton("Aceptar");
			SwingUtilities.setGridBagConstraints(gbc,c++,1,1,1,1.0,0.1,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,7,0,10,0,0,0);
			gbl.setConstraints(jbOk,gbc);
			cont.add(jbOk);
		}

		if (cancelButton)
		{
			jbCancel = new JButton("Cancelar");
			SwingUtilities.setGridBagConstraints(gbc,c++,1,1,1,1.0,0.1,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,7,0,10,0,0,0);
			gbl.setConstraints(jbCancel,gbc);
			cont.add(jbCancel);
		}
		

	}
	public JInternalDialog(String title,PanelBase jPanelBase, JPanel panelToShow)
	{
		this(title, jPanelBase, panelToShow, false, false);
	}

	public	void	addOkActionlistener(ActionListener al)
	{
		if (jbOk != null)
			jbOk.addActionListener(al);
	}

	public	void	addCancelActionlistener(ActionListener al)
	{
		if (jbCancel != null)
			jbCancel.addActionListener(al);
	}

	private void center()
	{
		final Dimension frameSize = getSize();
		int x = (width - frameSize.width)>>1;
		int y = (height - frameSize.height)>>1;

		if (x<0)
			x=0;

		if (y<0)
			y=0;

		setLocation(x,y);
	}
	
	public void setModal(boolean modal)
	{
		this.modal	= modal;	
	}
	
	public void show() 
	{
		super.show();
		pack();
		center();
		updateUI();
		if (modal)
			startModal();
		else
			stopModal();
	}

	public void show(float xAlign, float yAlign) 
	{
		super.show();
		pack();

		final Dimension frameSize = getSize();
		int	x = 0;
		int	y = 0;
		if (xAlign == LEFT_ALIGNMENT)
			x = 0;
		else if (xAlign == RIGHT_ALIGNMENT)
			x = (width - frameSize.width);
		else	x = (width - frameSize.width)>>1;

		if (yAlign == TOP_ALIGNMENT)
			y = 0;
		else if (yAlign == BOTTOM_ALIGNMENT)
			y = (width - frameSize.width);
		else	y = (width - frameSize.width)>>1;

		if (x<0)
			x=0;

		if (y<0)
			y=0;

		setLocation(x,y);

		updateUI();
		if (modal)
			startModal();
		else
			stopModal();
	}

	public void setVisible(boolean ok) 
	{
		super.setVisible(ok);
		
		if (modal)
		{
			if (ok)
			{ 
				startModal();
			}
			else
			{
				stopModal();
			}
		}
	}

	private synchronized void startModal() 
	{
		try 
		{
			if (javax.swing.SwingUtilities.isEventDispatchThread()) 
			{
				EventQueue theQueue = getToolkit().getSystemEventQueue();
				while (isVisible()) 
				{
					// hack to fix window not responding bug
					if (!isSelected())	
					{
						try 
						{
							setSelected(true);
						}
						catch(java.beans.PropertyVetoException pve) 
						{
							pve.printStackTrace();
						}						
					}					
					
					AWTEvent event		= theQueue.getNextEvent();
					Object source		= event.getSource();
					
					boolean dispatch	= true;

					if (event instanceof MouseEvent)
					{
						final MouseEvent e = (MouseEvent)event;
						final MouseEvent m = javax.swing.SwingUtilities.convertMouseEvent((Component)e.getSource(),e,this);
						if (!this.contains(m.getPoint()))
							dispatch = false;
					}										

					if (dispatch)
					{
						if (event instanceof ActiveEvent) 
						{
							((ActiveEvent)event).dispatch();
						} 
						else if (source instanceof Component) 
						{
							((Component)source).dispatchEvent(event);
						} 
					}
				}
			}
			else 
			{
				while (isVisible()) 
				{					
					wait();					
				}
			}
			
		} 
		catch (InterruptedException ignored) 
		{
			;
		}
	}

	synchronized void stopModal() 
	{
		notifyAll();
	}
}
