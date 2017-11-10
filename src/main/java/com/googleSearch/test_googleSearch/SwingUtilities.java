package com.googleSearch.test_googleSearch;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class SwingUtilities
{
	public static JButton fitButtonRL(Dimension db,String texto)
	{
		final JButton jb = new JButton(texto);

		if ((db!=null) && (jb!=null))
		{
			jb.setMaximumSize(db);
			jb.setMinimumSize(db);
			jb.setMargin(new Insets(0,0,0,0));
			jb.setPreferredSize(db);
		}

		return jb;
	}

	/**
	 * Clones a JComboBox
	 * @param cb The JComboBox to be cloned
	 * @return The clone of the JComboBox
	 */
	public static JComboBox cloneJComboBox(JComboBox cb)
	{
		final int s = cb.getItemCount();
		final ComboBoxModel model = cb.getModel();
		Vector v = new Vector(s >0? s + 10: 10);
		for(int i = 0 ; i < s; i++)
			v.add(i, model.getElementAt(i));

		return new JComboBox(v);
	}

	public static JScrollPane getScrollFrom(Container cont, Component c)
	{
		Component[]	components = cont.getComponents();
	
		for (int i = 0 ; components != null & i < components.length ; i++)
		{
			if (components[i] != null && components[i] instanceof Container)
			{
				JScrollPane pr = getScrollFrom((Container)components[i], c);
				if (pr != null) return pr;
			}
			if (components[i] != null && components[i] instanceof JScrollPane)
			{
				if (c.equals(((JScrollPane)components[i]).getViewport().getView()))
					return (JScrollPane)components[i];
			}
		}
		return null;
	}

	public static JTable setTableModelAnchors(JTable jt,int[] anchors)
	{
		if ((jt!=null)&&(anchors!=null))
		{
			final TableColumnModel tcm	= jt.getColumnModel();

			try
			{
				for (int i=0;i<anchors.length;i++)
				{
					if (anchors[i]>0)
					{
						final TableColumn column = tcm.getColumn(i);
						column.setMinWidth(anchors[i]);
						column.setMaxWidth(anchors[i]);
						column.setPreferredWidth(anchors[i]);
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return jt;
	}

	public static JTable setTableModelVariableAnchors(JTable jt,int[] anchors)
	{
		if ((jt!=null)&&(anchors!=null))
		{
			final TableColumnModel tcm	= jt.getColumnModel();

			try
			{
				for (int i=0;i<anchors.length;i++)
				{
					if (anchors[i]>0)
					{
						final TableColumn column = tcm.getColumn(i);
						column.setPreferredWidth(anchors[i]);
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return jt;
	}

	public static void setGridBagConstraints
	(
		GridBagConstraints gbc,
		int gridx,
		int gridy,
		int gridwidth,
		int gridheight,
		double weightx,
		double weighty,
		int anchor,
		int fill,
		Insets insets,
		int ipadx,
		int ipady
	)
	{
		setGridBagConstraints (gbc,gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill, insets.top,insets.left,insets.bottom,insets.right, ipadx, ipady);
	}

	public static void setGridBagConstraints
	(
		GridBagConstraints gbc,
		int gridx,
		int gridy,
		int gridwidth,
		int gridheight,
		double weightx,
		double weighty,
		int anchor,
		int fill,
		int top,
		int left,
		int bottom,
		int right,
		int ipadx,
		int ipady
	)
	{
		if (gbc!=null)
		{
			gbc.gridx			= gridx;
			gbc.gridy 			= gridy;
			gbc.gridwidth 		= gridwidth;
			gbc.gridheight 		= gridheight;
			gbc.weightx 		= weightx;
			gbc.weighty 		= weighty;
			gbc.anchor 			= anchor;
			gbc.fill 			= fill;

			if (gbc.insets!=null)
			{
				gbc.insets.top 		= top;
				gbc.insets.left 	= left;
				gbc.insets.bottom 	= bottom;
				gbc.insets.right 	= right;
			}

			gbc.ipadx 			= ipadx;
			gbc.ipady 			= ipady;
		}
	}

	public static void freeMemory(Container container)
	{
		if (container!=null)
		{
			final Component[] components = container.getComponents();

			if (components!=null)
			{
				for (int i=0;i<components.length;i++)
				{
					Component c = components[i];

					if (c!=null)
					{
						if (c instanceof JTree)
						{
							((JTree)c).setCellRenderer(null);
						}
						else if (c instanceof JTable)
						{
							removeTableRenderers((JTable)c);
						}
						else if (c instanceof JTabbedPane)
						{
							EventListener[] eventListeners = ((JTabbedPane)c).getListeners(ChangeListener.class);
							if (eventListeners!=null)
							{
								for (int x=0;x<eventListeners.length;x++)
								{
									if (eventListeners[x]!=null)
									{
										Class declaringClass	= eventListeners[x].getClass().getDeclaringClass();
										boolean rm	= true;
										if (declaringClass!=null)
										{
											String declaringClassName	= declaringClass.getName();
											rm	= !declaringClassName.equals("javax.swing.plaf.basic.BasicTabbedPaneUI");
										}
										
										if (rm)
											((JTabbedPane)c).removeChangeListener((ChangeListener)eventListeners[x]);
									}
								}
							}
						}

						if (c instanceof Container)
							freeMemory((Container)c);

						c = null;
					}
				}
			}

			try
			{
				container.removeAll();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private static void removeTableRenderers(JTable jTable)
	{
		if (jTable!=null)
		{
			final TableColumnModel tableColumnModel	= jTable.getColumnModel();
			if (tableColumnModel!=null)
			{
				final Enumeration columns	= tableColumnModel.getColumns();
				if (columns!=null)
				{
					while (columns.hasMoreElements())
					{
						final Object o = columns.nextElement();
						if ((o!=null)&&(o instanceof TableColumn))
						{
							TableColumn tableColumn	= (TableColumn)o;
							tableColumn.setCellRenderer(null);
						}
					}
				}
			}
		}
	}

	public static void disableComponent (JComponent component)
	{
		component.setEnabled(false);
		if(component instanceof JTextField)((JTextField)component).setDisabledTextColor(Color.BLACK);
	}
	
	public static void enableComponent (JComponent component)
	{
		component.setEnabled(true);
	}
	
	public static Point getComponentLocationOnScreen(final Component component)
	{
		if (component == null)
			return null;

		final Point relativeLocation = component.getLocationOnScreen();
		final Rectangle currentScreenBounds = component.getGraphicsConfiguration().getBounds();
		relativeLocation.x -= currentScreenBounds.x;
		relativeLocation.y -= currentScreenBounds.y;

		return relativeLocation;
	}

	/**
	 * Multiscreen compatible capture.
	 * For more info visit http://whileonefork.blogspot.com.es/2011/02/java-multi-monitor-screenshots.html
	 * @param bounds a Rectangle measured from first screen origin.
	 * @return a BufferedImage with content of given bounds.
	 */
	public static BufferedImage captureImage(Rectangle bounds)
	{
		if (bounds.width <= 0 || bounds.height <= 0)
		{
			return null;
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = ge.getScreenDevices();

		Rectangle allScreenBounds = new Rectangle();
		Rectangle screenBounds;
		for (GraphicsDevice screen : screens)
		{
			screenBounds = screen.getDefaultConfiguration().getBounds();
			allScreenBounds.width += screenBounds.width;
			allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
		}
		BufferedImage result = null;
		try
		{
			Robot robot = new Robot();
			BufferedImage screenShot = robot.createScreenCapture(allScreenBounds);
			result = screenShot.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return result;
	}


}
