///////////////////////////////////////////////////////////////////////////////
//  BZApplet.java
//  Created by Mark Williamsen on 12/8/06
//  UWM Physics  PH-551 Fall 2006
//  Brillouin Zone Java applet
//  Java 1.1 event model used
//  ver 0.2  12/12/06  added variable Brillouin zone boundaries
//  ver 0.3  12/13/06  added light gray axes and diagonals
//  ver 0.4  12/04/10  fixed name conflict for myChoiceHandler
//  ver 0.5   4/10/12  Added JSlider controls
///////////////////////////////////////////////////////////////////////////////

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class BZApplet extends Applet
{
	// instance variables for applet
	Choice checkColor = new Choice();
	JSlider checkSlide = new JSlider(0, 16, 4);
	JSlider checkZone = new JSlider(0, 8, 3);
	myChoiceHandler checkHandler = new myChoiceHandler(this);
	mySlideHandler slideHandler = new mySlideHandler(this);
	Panel myPanel = new Panel();
	Label myLabel = new Label("m");
	Label myZone = new Label("z");
	BZCanvas myCanvas = new BZCanvas(this);

	// set lattice parameter
	final int lattice = 200;

	// initialize applet object
	public void init()
	{
		// add user controls
		myPanel.add(checkColor); 
		myPanel.add(checkSlide);
		myPanel.add(myLabel);
		myPanel.add(checkZone);
		myPanel.add(myZone);
		add(myPanel);
		add(myCanvas);
        
        // set up tick marks
        checkSlide.setMajorTickSpacing(8);
        checkSlide.setMinorTickSpacing(1);
        checkSlide.setPaintTicks(true);
        checkSlide.setPaintLabels(true);
        checkZone.setMajorTickSpacing(4);
        checkZone.setMinorTickSpacing(1);
        checkZone.setPaintTicks(true);
        checkZone.setPaintLabels(true);
        
		// set initial conditions
		setBackground(Color.white);
		myCanvas.setBackground(Color.white);
		myPanel.setBackground(Color.lightGray);
		checkColor.addItem("Color");
		checkColor.addItem("Gray");
		checkColor.addItem("None");  
		myLabel.setBackground(Color.lightGray);
		myZone.setBackground(Color.lightGray);
		checkColor.setBackground(Color.lightGray);

		// register event handlers
		checkColor.addItemListener(checkHandler);
		checkSlide.addChangeListener(slideHandler);
		checkZone.addChangeListener(slideHandler);
				
	}	// init

	public String getAppletInfo()
	{
		return "Brillouin Zone Applet, ver. 0.5, M. Williamsen, April 12, 2012";
	}
}	// class BZApplet

// handler for button clicks
class myChoiceHandler implements ItemListener
{
	// need reference to applet object
	BZApplet parent = null;

	// constructor with one parameter
	myChoiceHandler(BZApplet b)
	{parent = b;}

	// override button clicked handler
	public void itemStateChanged(ItemEvent e)
	{
		// request redraw of applet window
		parent.myCanvas.repaint();
	}
}	// class myChoiceHandler

// handler for slide bar changes
class mySlideHandler implements ChangeListener
{
	// need reference to applet object
	BZApplet parent = null;

	// constructor with one parameter
	mySlideHandler(BZApplet b)
	{parent = b;}

	// override scrollbar adjusted handler
	public void stateChanged(ChangeEvent e)
	{
		// request redraw of applet window
		parent.myCanvas.repaint();
	}
}	// class mySlideHandler

class BZCanvas extends Canvas
{
	// special colors defined, some are transparent
	Color grayTrans  = new Color( 80, 100, 120,  80);
	Color redTrans   = new Color(255, 255,   0, 128);
	Color greenTrans = new Color(  0, 255, 255, 128);
	Color blueTrans  = new Color(255,   0, 255, 128);
	Color lightGray  = new Color(225, 225, 225);
		
	// constructor with one parameter
	BZApplet parent = null;
	BZCanvas(BZApplet b)
	{
		parent = b;
	}

	// tell layout manager how to draw canvas
	public Dimension getPreferredSize()
	{return new Dimension(3*parent.lattice+1, 3*parent.lattice+1);}

	// override paint method
	public void paint()
	{paint(getGraphics());}

	public void paint(Graphics g)
	{
		// calculate diameter of Fermi circle
		int lattice = parent.lattice;
		float fermi = (float) Math.sqrt(parent.checkSlide.getValue() / 2.0f / Math.PI);
		int radius = Math.round(lattice * fermi);
		int diameter = 2 * radius;

		// place origin of first circle
		g.translate(3*lattice/2, 3*lattice/2);

		// draw axes and diagonals
		g.setColor(lightGray);
		lineit(g, lattice, -2, 0, 2, 0);
		lineit(g, lattice,  0,-2, 0, 2);
		lineit(g, lattice, -2,-2, 2, 2);
		lineit(g, lattice, -2, 2, 2,-2);

		// fill Fermi circles
		if (radius != 0)	// skip if no circle
		if (parent.checkColor.getSelectedItem() == "Gray")
		{
			// handle gray scale case
			g.setColor(grayTrans);
			for (int x = -1; x < 2; x++)
			for (int y = -1; y < 2; y++)
			{
				g.fillOval(x*lattice-radius, y*lattice-radius, diameter, diameter);
			}
		}
		else if (parent.checkColor.getSelectedItem() == "Color")
		{
			// handle full color case
			for (int x = -1; x < 2; x++)
			for (int y = -1; y < 2; y++)
			{
				// cycle through transparent colors
				int cIndex = (x+y) % 3;
				switch(cIndex)
				{
					case -2: case 1: g.setColor(greenTrans); break;
					case -1: case 2: g.setColor(blueTrans); break;
					case 0: g.setColor(redTrans); break;
					default: g.setColor(grayTrans); break;
				}
				g.fillOval(x*lattice-radius, y*lattice-radius, diameter, diameter);
			}
		}
		
		// draw Fermi circles
		g.setColor(Color.black);
		if (radius != 0)	// skip if no circle
		for (int x = -1; x < 2; x++)
		for (int y = -1; y < 2; y++)
		{
			g.drawOval(x*lattice-radius, y*lattice-radius, diameter, diameter);
		}

		// draw lattice points
		for (int x = -1; x < 2; x++)
		for (int y = -1; y < 2; y++)
		{
			g.fillOval(x*lattice-2, y*lattice-2, 4, 4);
		}

		// draw bisectors defining Brillouin Zones
		g.setColor(Color.black);

		// check zone setting to see how many zones to draw
		switch(parent.checkZone.getValue())
		{
			case 8:
			// draw grid for 8th Brillouin Zone
			lineit(g, lattice/2, -5, 1,-1,-5);
			lineit(g, lattice/2, -1, 5,-5,-1);
			lineit(g, lattice/2,  5, 1, 1,-5);
			lineit(g, lattice/2,  1, 5, 5,-1);

			lineit(g, lattice/2, -5,-1, 1,-5);
			lineit(g, lattice/2,  1, 5,-5, 1);
			lineit(g, lattice/2,  5,-1,-1,-5);
			lineit(g, lattice/2, -1, 5, 5, 1);

			case 7:
			// draw grid for 7th Brillouin Zone
			lineit(g, lattice, -2,-1, 1,-2);
			lineit(g, lattice, -1,-2, 2,-1);
			lineit(g, lattice,  1,-2, 2, 1);
			lineit(g, lattice,  2,-1, 1, 2);

			lineit(g, lattice,  2, 1,-1, 2);
			lineit(g, lattice,  1, 2,-2, 1);
			lineit(g, lattice, -1, 2,-2,-1);
			lineit(g, lattice, -2, 1,-1,-2);

			case 6:
			// draw grid for 6th Brillouin Zone
			lineit(g, 3*lattice/2, -3,-1, 3,-1);
			lineit(g, 3*lattice/2, -3, 1, 3, 1);
			lineit(g, 3*lattice/2, -1,-3,-1, 3);
			lineit(g, 3*lattice/2,  1,-3, 1, 3);

			case 5:
			// draw grid for 5th Brillouin Zone
			lineit(g, lattice, -3,-1, 1, 3);
			lineit(g, lattice, -3, 1, 1,-3);
			lineit(g, lattice, -1,-3, 3, 1);
			lineit(g, lattice, -1, 3, 3,-1);

			case 4:
			// draw grid for 4th Brillouin Zone
			lineit(g, lattice/2, -3, 1,-1,-3);
			lineit(g, lattice/2, -3,-1, 1,-3);
			lineit(g, lattice/2, -1,-3, 3,-1);
			lineit(g, lattice/2,  1,-3, 3, 1);

			lineit(g, lattice/2,  3,-1, 1, 3);
			lineit(g, lattice/2,  3, 1,-1, 3);
			lineit(g, lattice/2,  1, 3,-3, 1);
			lineit(g, lattice/2, -1, 3,-3,-1);

			case 3:
			// draw grid for 3rd Brillouin Zone
			lineit(g, lattice, -3,-1, 3,-1);
			lineit(g, lattice, -3, 1, 3, 1);
			lineit(g, lattice, -1,-3,-1, 3);
			lineit(g, lattice,  1,-3, 1, 3);

			case 2:
			// draw grid for 2nd Brillouin Zone
			lineit(g, lattice/2, -3,-1, 1, 3);
			lineit(g, lattice/2, -3, 1, 1,-3);
			lineit(g, lattice/2, -1,-3, 3, 1);
			lineit(g, lattice/2, -1, 3, 3,-1);

			case 1:
			// draw grid for 1st Brillouin Zone
			lineit(g, lattice/2, -3,-1, 3,-1);
			lineit(g, lattice/2, -3, 1, 3, 1);
			lineit(g, lattice/2, -1,-3,-1, 3);
			lineit(g, lattice/2,  1,-3, 1, 3);

			case 0:
			// don't draw any zone boundaries
			default:
		}
		
	}	// paint
	
	// internal method to aid bookkeeping
	void lineit(Graphics g, int lat, int a1, int b1, int a2, int b2)
	{
		g.drawLine(lat*a1, lat*b1, lat*a2, lat*b2);
	}
	
}	// class BZCanvas

