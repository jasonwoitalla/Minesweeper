package com.mine.graphics;

import javax.swing.*;

import com.mine.driver.MouseInput;

import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;

/**
 * Class Canvas - a class to allow for simple graphical drawing on a canvas.
 * 
 * @author Bruce Quig
 * @author Michael Kolling (mik)
 * @author Dave Musicant - made some local changes
 * @author Amy Csizmar Dalal - more minor local changes
 * @author Jeff Ondich - added setFontSize and getInkColor
 *
 * @version 1.6.4 date: 01.13.2005
 */

public class Canvas {
	private JFrame frame;
	protected CanvasPane canvas;
	private Graphics2D graphic;
	private Color backgroundColor;
	private Color inkColor;
	private Image canvasImage;

	/**
	 * Create a Canvas with default height, width and background color (300, 300,
	 * white).
	 * 
	 * @param title
	 *            title to appear in Canvas Frame
	 */
	public Canvas(String title) {
		this(title, 300, 300, Color.white);
	}

	/**
	 * Create a Canvas with default title, height, width and background color
	 * ("Canvas", 300, 300, white).
	 */
	public Canvas() {
		this("Canvas", 300, 300, Color.white);
	}

	/**
	 * Create a Canvas with default background Color (white).
	 * 
	 * @param title
	 *            title to appear in Canvas Frame
	 * @param width
	 *            the desired width for the canvas
	 * @param height
	 *            the desired height for the canvas
	 */
	public Canvas(String title, int width, int height) {
		this(title, width, height, Color.white);
	}
	
	public Canvas(String title, int width, int height, MouseInput input) {
		this(title, width, height, Color.white);
		canvas.addMouseListener(input);
	}

	/**
	 * Create a Canvas.
	 * 
	 * @param title
	 *            title to appear in Canvas Frame
	 * @param width
	 *            the desired width for the canvas
	 * @param height
	 *            the desired height for the canvas
	 * @param bgColor
	 *            the desired background color of the canvas
	 */
	private Canvas(String title, int width, int height, Color bgColor) {
		frame = new JFrame();
		canvas = new CanvasPane();
		frame.setContentPane(canvas);
		//canvas.setLayout(new BoxLayout(canvas, BoxLayout.Y_AXIS));
		frame.setTitle(title);
		frame.setResizable(false);
		canvas.setPreferredSize(new Dimension(width, height));
		backgroundColor = bgColor;
		inkColor = Color.black;
		frame.pack();

		canvas.setOpaque(false);
		// end of hack

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				System.exit(0);
			}
		});

		if (graphic == null) {
			// first time: instantiate the offscreen image and fill it with
			// the background color
			Dimension size = canvas.getSize();
			canvasImage = canvas.createImage(size.width, size.height);
			graphic = (Graphics2D) canvasImage.getGraphics();
			graphic.setColor(backgroundColor);
			graphic.fillRect(0, 0, size.width, size.height);
			graphic.setColor(inkColor);
		}
		setVisible(true);
	}

	/**
	 * Sets the "pen" (outline) color for the Canvas.
	 * 
	 * @param newColor
	 *            The color to which to set the pen/drawing tool.
	 */
	public void setInkColor(Color newColor) {
		inkColor = newColor;
		graphic.setColor(inkColor);
	}

	/**
	 * Returns the current pen color.
	 */
	public Color getInkColor() {
		return inkColor;
	}

	/**
	 * Set the canvas visibility and brings canvas to the front of screen when made
	 * visible. This method can also be used to bring an already visible canvas to
	 * the front of other windows.
	 * 
	 * @param visible
	 *            boolean value representing the desired visibility of the canvas
	 *            (true or false)
	 */
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	/**
	 * Provide information on visibility of the Canvas.
	 * 
	 * @return true if canvas is visible, false otherwise
	 */
	public boolean isVisible() {
		return frame.isVisible();
	}

	public void closeCanvas() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	int selected = 0;
	public JPanel addRadioGroup(int num, String[] text) {
		JRadioButton[] radios = new JRadioButton[num];
		ButtonGroup group = new ButtonGroup();
		JPanel radioPannel = new JPanel();
		radioPannel.setLayout(new BoxLayout(radioPannel, BoxLayout.Y_AXIS));
		radioPannel.setAlignmentX(Box.CENTER_ALIGNMENT);
		radioPannel.add(Box.createVerticalGlue());

		for(int i = 0; i < num; i++) {
			radios[i] = new JRadioButton(text[i]);
			radios[i].setActionCommand(""+i);
			radios[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand().equals("0")) 
						selected = 1;
					else if(e.getActionCommand().equals("1"))
						selected = 2;
					else if(e.getActionCommand().equals("2"))
						selected = 3;
				}
				
			});
			group.add(radios[i]);
			radioPannel.add(radios[i]);
		}

		radioPannel.add(Box.createVerticalGlue());
		canvas.add(radioPannel);
		frame.pack();

		return radioPannel;
	}

	public void removePanel(JPanel panel)
	{
		canvas.remove(panel);
	}
	
	public int getSelected(){
		return selected;
	}

	public void addButton(String text, int width, int height, Font font) {
		JButton button = new JButton(text);
		button.setFont(font);
		button.setPreferredSize(new Dimension(width, height));
		canvas.add(button);
		frame.pack();
	}
	
	public void addTextField(int x, int y, int width, int height) {
		JTextField field = new JTextField();
		field.setLocation(x, y);
		field.setFont(new Font("Arial", Font.PLAIN, 20));
		field.setPreferredSize(new Dimension(width, height));
		canvas.add(field);
		frame.pack();
	}
	
	/**
	 * Erase the whole canvas.
	 */
	public void erase() {
		Color original = graphic.getColor();
		graphic.setColor(backgroundColor);
		Dimension size = canvas.getSize();
		graphic.fill(new Rectangle(0, 0, size.width, size.height));
		graphic.setColor(original);
		canvas.repaint();
	}

	/**
	 * Erases a given shape's outline on the screen.
	 * 
	 * @param shape
	 *            the shape object to be erased
	 */
	private void eraseOutline(Shape shape) {
		Color original = graphic.getColor();
		graphic.setColor(backgroundColor);
		graphic.draw(shape); // erase by drawing background color
		graphic.setColor(original);
		canvas.repaint();
	}

	/**
	 * Draws an image onto the canvas.
	 * 
	 * @param image
	 *            the Image object to be displayed
	 * @param x
	 *            x co-ordinate for Image placement
	 * @param y
	 *            y co-ordinate for Image placement
	 * @return returns boolean value representing whether the image was completely
	 *         loaded
	 */
	public boolean drawImage(Image image, int x, int y) {
		boolean result = graphic.drawImage(image, x, y, null);
		canvas.repaint();
		return result;
	}
	
	public boolean drawImage(Image image, int x, int y, int width, int height) {
		boolean result = graphic.drawImage(image, x, y, width, height, null);
		canvas.repaint();
		return result;
	}

	/**
	 * Draws a String on the Canvas.
	 * 
	 * @param text
	 *            the String to be displayed
	 * @param x
	 *            x co-ordinate for text placement
	 * @param y
	 *            y co-ordinate for text placement
	 */
	public void drawString(String text, int x, int y) {
		graphic.drawString(text, x, y);
		canvas.repaint();
	}

	/**
	 * Erases a String on the Canvas.
	 * 
	 * @param text
	 *            the String to be displayed
	 * @param x
	 *            x co-ordinate for text placement
	 * @param y
	 *            y co-ordinate for text placement
	 */
	public void eraseString(String text, int x, int y) {
		Color original = graphic.getColor();
		graphic.setColor(backgroundColor);
		graphic.drawString(text, x, y);
		graphic.setColor(original);
		canvas.repaint();
	}

	/**
	 * Draws a line on the Canvas.
	 * 
	 * @param x1
	 *            x co-ordinate of start of line
	 * @param y1
	 *            y co-ordinate of start of line
	 * @param x2
	 *            x co-ordinate of end of line
	 * @param y2
	 *            y co-ordinate of end of line
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		graphic.drawLine(x1, y1, x2, y2);
		canvas.repaint();
	}

	/**
	 * Draws a rectangle on the Canvas.
	 * 
	 * @param x1
	 *            x co-ordinate of top left corner
	 * @param y1
	 *            y co-ordinate of top left corner
	 * @param x2
	 *            width
	 * @param y2
	 *            height
	 */
	public void drawRectangle(int x1, int y1, int x2, int y2) {
		graphic.draw(new Rectangle(x1, y1, x2, y2));
		canvas.repaint();
	}

	/**
	 * Draws a filled rectangle on the Canvas.
	 * 
	 * @param x1
	 *            x co-ordinate of top left corner
	 * @param y1
	 *            y co-ordinate of top left corner
	 * @param x2
	 *            width
	 * @param y2
	 *            height
	 */
	public void fillRectangle(int x1, int y1, int x2, int y2) {
		graphic.fill(new Rectangle(x1, y1, x2, y2));
		canvas.repaint();
	}

	/**
	 * Draws a polygon on the Canvas.
	 * 
	 * @param x
	 *            array of x co-ordinates of polygon points
	 * @param y
	 *            array of y co-ordinates of polygon points
	 * @param size
	 *            the number of points (vertices) in the polygon
	 */
	public void drawPolygon(int[] x, int[] y, int size) {
		graphic.draw(new Polygon(x, y, size));
		canvas.repaint();
	}

	/**
	 * Draws a filled polygon on the Canvas.
	 * 
	 * @param x
	 *            array of x co-ordinates of polygon points
	 * @param y
	 *            array of y co-ordinates of polygon points
	 * @param size
	 *            the number of points (vertices) in the polygon
	 */
	public void fillPolygon(int[] x, int[] y, int size) {
		graphic.fill(new Polygon(x, y, size));
		canvas.repaint();
	}

	/**
	 * Erases a rectangle on the Canvas.
	 * 
	 * @param x1
	 *            x co-ordinate of top left corner
	 * @param y1
	 *            y co-ordinate of top left corner
	 * @param x2
	 *            width
	 * @param y2
	 *            height
	 */
	public void eraseRectangle(int x1, int y1, int x2, int y2) {
		eraseOutline(new Rectangle(x1, y1, x2, y2));
	}

	/**
	 * Draws an oval on the Canvas.
	 * 
	 * @param x1
	 *            x co-ordinate of top left corner
	 * @param y1
	 *            y co-ordinate of top left corner
	 * @param x2
	 *            width
	 * @param y2
	 *            height
	 */
	public void drawOval(int x1, int y1, int x2, int y2) {
		graphic.draw(new Ellipse2D.Double(x1, y1, x2, y2));
		canvas.repaint();
	}

	/**
	 * Draws a filled oval on the Canvas.
	 * 
	 * @param x1
	 *            x co-ordinate of top left corner
	 * @param y1
	 *            y co-ordinate of top left corner
	 * @param x2
	 *            width
	 * @param y2
	 *            height
	 */
	public void fillOval(int x1, int y1, int x2, int y2) {
		graphic.fill(new Ellipse2D.Double(x1, y1, x2, y2));
		canvas.repaint();
	}

	/**
	 * Erases an oval on the Canvas.
	 * 
	 * @param x1
	 *            x co-ordinate of top left corner
	 * @param y1
	 *            y co-ordinate of top left corner
	 * @param x2
	 *            width
	 * @param y2
	 *            height
	 */
	public void eraseOval(int x1, int y1, int x2, int y2) {
		eraseOutline(new Ellipse2D.Double(x1, y1, x2, y2));
	}

	/**
	 * Sets the background color of the Canvas.
	 * 
	 * @param newColor
	 *            the new color for the background of the Canvas
	 */
	public void setBackgroundColor(Color newColor) {
		backgroundColor = newColor;
		graphic.setBackground(newColor);
	}

	/**
	 * Fills in the Canvas (background) with the specified color.
	 * 
	 * @param newColor
	 *            the new color for the background of the Canvas
	 */
	public void fillBackground(Color newColor) {
		Dimension size = canvas.getSize();
		backgroundColor = newColor;
		graphic.setColor(backgroundColor);
		graphic.fillRect(0, 0, size.width, size.height);
		graphic.setColor(inkColor);
	}

	/**
	 * Returns the current color of the background
	 * 
	 * @return the color of the background of the Canvas
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * changes the current Font used on the Canvas
	 * 
	 * @param newFont
	 *            new font to be used for String output
	 */
	public void setFont(Font newFont) {
		graphic.setFont(newFont);
	}

	/**
	 * Returns the current font of the canvas.
	 * 
	 * @return the font currently in use
	 **/
	public Font getFont() {
		return graphic.getFont();
	}

	/**
	 * Sets the point size of the current font to the specified value. The style and
	 * font family remain the same.
	 *
	 * @param newSize
	 *            the new point size
	 */
	public void setFontSize(int newSize) {
		Font f = graphic.getFont().deriveFont((float) newSize);
		setFont(f);
	}

	public void drawTextField() {
		
	}
	
	/**
	 * Sets the size of the canvas.
	 * 
	 * @param width
	 *            new width
	 * @param height
	 *            new height
	 */
	public void setSize(int width, int height) {
		canvas.setPreferredSize(new Dimension(width, height));
		Image oldImage = canvasImage;
		canvasImage = canvas.createImage(width, height);
		graphic = (Graphics2D) canvasImage.getGraphics();
		graphic.setColor(backgroundColor);
		graphic.fillRect(0, 0, width, height);
		graphic.setColor(inkColor);
		graphic.drawImage(oldImage, 0, 0, null);
		frame.pack();
	}

	/**
	 * Returns the size of the canvas.
	 * 
	 * @return The current dimension of the canvas
	 */
	public Dimension getSize() {
		return canvas.getSize();
	}

	/**
	 * Waits for a specified number of milliseconds before finishing. This provides
	 * an easy way to specify a small delay which can be used when producing
	 * animations.
	 * 
	 * @param milliseconds
	 *            the number
	 */
	public void wait(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (Exception e) {
			// ignoring exception at the moment
		}
	}
	
	public void loadFont(String font) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font)));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/************************************************************************
	 * Nested class CanvasPane - the actual canvas component contained in the Canvas
	 * frame. This is essentially a JPanel with added capability to refresh the
	 * image drawn on it. MODIFIED by acd: changed visibility to protected (from
	 * private) to allow subclassing (basically, so we can add mouse listeners to
	 * the canvas), and added the call to super.paint() (an additional hack to allow
	 * us to add components like buttons and menus to the canvas).
	 */
	protected class CanvasPane extends JPanel {
		public void paint(Graphics g) {
			g.drawImage(canvasImage, 0, 0, null);
			super.paint(g);
		}
	}
}
