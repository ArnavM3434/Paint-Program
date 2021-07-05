import java.awt.event.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.swing.event.*;
import javax.imageio.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.*;

public class PaintProgram extends JPanel implements MouseMotionListener, ActionListener, MouseListener, AdjustmentListener, ChangeListener, KeyListener
{
	JFrame frame;
	ArrayList<Point> points;
	//Stack <ArrayList<Point>> pointslist;
	Stack<Object> shapes, undoRedoStack;
	boolean drawingLine = false, drawingRectangle = false, drawingOval = false, eraserOn = false;
	boolean firstClick = true;
	Color currentColor, backgroundColor, oldColor;
	JMenuBar bar;
	JMenu colorMenu, fileMenu;
	JMenuItem save, load, clear, exit;
	JMenuItem[] colorOptions;
	Color[] colors;
	int penWidth, currX, currY, currWidth, currHeight;
	JScrollBar penWidthBar;
	JColorChooser colorChooser;
	Shape currShape;

	JButton freeLineButton, rectangleButton,ovalButton, undoButton, redoButton, eraser;
	ImageIcon freeLineImg, rectImg,ovalImg, loadImg, saveImg, undoImg, redoImg, eraserImg;

	JFileChooser fileChooser;
	BufferedImage loadedImage;


	public PaintProgram()
	{
		frame = new JFrame("Paint Program");
		frame.add(this); //because it has paintComponent

		bar = new JMenuBar();
		colorMenu = new JMenu("Color Options");
		colors = new Color[]{Color.RED, Color.ORANGE, Color.BLUE, Color.YELLOW, Color.GREEN};
		colorOptions = new JMenuItem[colors.length];

		for(int x = 0; x < colors.length; x++)
		{
			colorOptions[x] = new JMenuItem();
			colorOptions[x].putClientProperty("colorIndex",x);//now we don't have to do for loop in action performed to see if any of the buttons were clicked
			colorOptions[x].setBackground(colors[x]);
			colorOptions[x].addActionListener(this);
			colorOptions[x].setFocusable(false);
			colorMenu.add(colorOptions[x]);

		}
		colorChooser = new JColorChooser();
		colorChooser.getSelectionModel().addChangeListener(this);
		colorMenu.add(colorChooser);

		colorMenu.setLayout(new GridLayout(5,1));

		currentColor = colors[0];
		points = new ArrayList<Point>();
	//	pointslist = new Stack <ArrayList<Point>>();
		shapes = new Stack<Object>(); //want to put lines, ovals, and rectangles in here
		undoRedoStack = new Stack<Object>();

		penWidthBar = new JScrollBar(JScrollBar.HORIZONTAL,1,0,1,40);
		//orientation, starting location, scroller size, min value, max value
		penWidthBar.addAdjustmentListener(this);
		penWidthBar.setFocusable(false);
		penWidth = penWidthBar.getValue();



		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		fileMenu = new JMenu("File");
		save = new JMenuItem("Save", KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

		load = new JMenuItem("Load", KeyEvent.VK_L);
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));

		clear = new JMenuItem("New");
		exit = new JMenuItem("Exit");

		saveImg = new ImageIcon("saveImg.png");
		saveImg = new ImageIcon(saveImg.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));
		loadImg = new ImageIcon("loadImg.png");
		loadImg = new ImageIcon(loadImg.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));
		save.setIcon(saveImg);
		load.setIcon(loadImg);




		save.addActionListener(this);
		load.addActionListener(this);
		clear.addActionListener(this);
		exit.addActionListener(this);

		fileMenu.add(clear);
		fileMenu.add(load);
		fileMenu.add(save);
		fileMenu.add(exit);

		bar.add(fileMenu);

		String currDir = System.getProperty("user.dir");
		fileChooser = new JFileChooser(currDir);


		freeLineImg = new ImageIcon("freeLineImg.png");
		freeLineImg = new ImageIcon(freeLineImg.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));
		freeLineButton = new JButton();
		freeLineButton.setIcon(freeLineImg);
		freeLineButton.setBackground(Color.LIGHT_GRAY);
		//you have to add a couple more lines for freeLineButton.setOpaque(true), freeLineButton.setBorderPainted(false);
		freeLineButton.addActionListener(this);
		freeLineButton.setFocusPainted(false);
		freeLineButton.setFocusable(false);

		rectImg = new ImageIcon("rectImg.png");
		rectImg = new ImageIcon(rectImg.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));
		rectangleButton = new JButton();
		rectangleButton.setIcon(rectImg);
		//rectangleButton.setBackground(Color.LIGHT_GRAY);
		//you have to add a couple more lines for freeLineButton.setOpaque(true), freeLineButton.setBorderPainted(false);
		rectangleButton.addActionListener(this);
		rectangleButton.setFocusPainted(false);
		rectangleButton.setFocusable(false);


		ovalImg = new ImageIcon("ovalImg.png");
		ovalImg = new ImageIcon(ovalImg.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH));
		ovalButton = new JButton();
		ovalButton.setIcon(ovalImg);
		//ovalButton.setBackground(Color.LIGHT_GRAY);
		//you have to add a couple more lines for freeLineButton.setOpaque(true), freeLineButton.setBorderPainted(false);
		ovalButton.addActionListener(this);
		ovalButton.setFocusPainted(false);
		ovalButton.setFocusable(false);

		eraserImg = new ImageIcon("eraserImg.png");
		eraserImg = new ImageIcon(eraserImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		eraser = new JButton();
		eraser.setIcon(eraserImg);
		eraser.setFocusPainted(false);
		eraser.setFocusable(false);
		eraser.addActionListener(this);

		undoImg = new ImageIcon("undoImg.png");
		undoImg = new ImageIcon(undoImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		undoButton = new JButton();
		undoButton.setIcon(undoImg);
		undoButton.setFocusPainted(false);
		undoButton.setFocusable(false);
		undoButton.addActionListener(this);

		redoImg = new ImageIcon("redoImg.png");
		redoImg = new ImageIcon(redoImg.getImage().getScaledInstance(20,20,Image.SCALE_SMOOTH));
		redoButton = new JButton();
		redoButton.setIcon(redoImg);
		redoButton.setFocusPainted(false);
		redoButton.setFocusable(false);
		redoButton.addActionListener(this);

		bar.add(fileMenu);
		bar.add(colorMenu);

		bar.add(freeLineButton);
		bar.add(rectangleButton);
		bar.add(ovalButton);
		bar.add(eraser);
		bar.add(undoButton);
		bar.add(redoButton);
		bar.add(penWidthBar);

		backgroundColor = Color.WHITE;

		frame.add(bar, BorderLayout.NORTH);

		frame.setSize(1000,700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g; //eventually want to change pen widths
		g2.setColor(backgroundColor);
		g2.fillRect(0,0,frame.getWidth(), frame.getHeight());
		//connect the dots


		if(loadedImage != null)
		{
			g2.drawImage(loadedImage,0,0,null);
		}



		Iterator it = shapes.iterator();
		while(it.hasNext())
		{
			Object next = it.next();

			if(next instanceof Rectangle)
			{
				Rectangle temp = (Rectangle) next;
				g2.setColor(temp.getColor());
				g2.setStroke(new BasicStroke(temp.getPenWidth()));
				g2.draw(temp.getShape());
			}
			if(next instanceof Oval)
			{
				Oval temp = (Oval) next;
				g2.setColor(temp.getColor());
				g2.setStroke(new BasicStroke(temp.getPenWidth()));
				g2.draw(temp.getShape());
			}

			else if (!(next instanceof Rectangle))
			{
				ArrayList<?> temp = (ArrayList<?>)next;
				if(temp.size() > 0)
				{
					g2.setStroke(new BasicStroke(((Point)temp.get(0)).getPenWidth()));
				//	g2.setColor(((Point)temp.get(0)).getColor());
					for(int x = 0; x < temp.size() -1; x++)
					{
						Point p1 = (Point)(temp.get(x));
						Point p2 = (Point)(temp.get(x+1));
						g2.setColor(p1.getColor());
						g2.drawLine(p1.getX(), p1.getY(), p2.getX(),p2.getY());
					}
				}


			}
		}



		if(drawingLine && points.size() > 0)
		{
			g2.setStroke(new BasicStroke(points.get(0).getPenWidth()));
			g2.setColor(points.get(0).getColor());

			for(int x = 0; x < points.size() -1; x++)
			{
				Point p1 = points.get(x);
				Point p2 = points.get(x+1);
				g2.drawLine(p1.getX(), p1.getY(), p2.getX(),p2.getY());
			}
		}



	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		penWidth = penWidthBar.getValue();
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == save)
		{
			save();

		}

		else if (e.getSource() == load)
		{
			load();

		}



		else if (e.getSource() == clear)
		{
			shapes = new Stack<Object>();
			loadedImage = null;
			repaint();

		}

		else if (e.getSource() == exit)
		{
			boolean okToClose = true;
		/*	if(pointslist.size() > 0)
			{
				JOptionPane
			} */
			System.exit(0);
		}


		else if (e.getSource() == freeLineButton)
		{
			drawingLine = true;
			drawingRectangle = false;
			drawingOval = false;
			eraserOn = false;
			freeLineButton.setBackground(Color.LIGHT_GRAY);
			rectangleButton.setBackground(null);
			ovalButton.setBackground(null);
			eraser.setBackground(null);
			//currentColor = oldColor;
		}

		else if (e.getSource() == rectangleButton)
		{
			drawingLine = false;
			drawingRectangle = true;
			drawingOval = false;
			eraserOn = false;
			rectangleButton.setBackground(Color.LIGHT_GRAY);
			freeLineButton.setBackground(null);
			ovalButton.setBackground(null);
			eraser.setBackground(null);
			//currentColor = oldColor;
		}
		else if (e.getSource() == ovalButton)
		{
			drawingLine = false;
			drawingRectangle = false;
			drawingOval = true;
			eraserOn = false;
			ovalButton.setBackground(Color.LIGHT_GRAY);
			rectangleButton.setBackground(null);
			freeLineButton.setBackground(null);
			eraser.setBackground(null);
			//currentColor = oldColor;
		}

		else if (e.getSource() == eraser)
		{
			drawingLine = true;
			drawingRectangle = false;
			drawingOval = false;
			eraserOn = true;
			freeLineButton.setBackground(null);
			rectangleButton.setBackground(null);
			ovalButton.setBackground(null);
			eraser.setBackground(Color.LIGHT_GRAY);
			oldColor = currentColor;
			currentColor = backgroundColor;
		}
		else if (e.getSource() == undoButton)
		{
			undo();
		}
		else if (e.getSource() == redoButton)
		{
			redo();
		}



		else
		{
			if(eraserOn)
			{
				drawingLine = true;
				drawingRectangle = false;
				drawingOval = false;
				eraserOn = false;
				freeLineButton.setBackground(Color.LIGHT_GRAY);
				rectangleButton.setBackground(null);
				ovalButton.setBackground(null);
				eraser.setBackground(null);

			}
			int index = (int)((JMenuItem)e.getSource()).getClientProperty("colorIndex");
			currentColor = colors[index];
		}



	}

	public void mouseDragged(MouseEvent e)
	{
			if(drawingRectangle || drawingOval)
			{
				if(firstClick)
				{
					currX= e.getX();
					currY = e.getY();
					if(drawingRectangle)
					currShape = new Rectangle(currX, currY, currentColor, penWidthBar.getValue(), 0, 0);
					else currShape = new Oval(currX, currY, currentColor,penWidthBar.getValue(), 0,0);
					firstClick = false;
					shapes.push(currShape);
				}
				else
				{
					currWidth = Math.abs(e.getX() - currX);
					currHeight = Math.abs(e.getY() - currY);
					currShape.setWidth(currWidth);
					currShape.setHeight(currHeight);
					if(currX <= e.getX() && currY >= e.getY())
					{
						currShape.setY(e.getY());
					}
					else if(currX >= e.getX() && currY <= e.getY())
					{
						currShape.setX(e.getX());
					}
					else if(currX >= e.getX() && currY >= e.getY())
					{
						currShape.setX(e.getX());
						currShape.setY(e.getY());
					}

				}

			}

			else
			{
				drawingLine = true;
				points.add(new Point(e.getX(), e.getY(), currentColor, penWidth));
			}

			repaint();


	}

	public void mouseReleased(MouseEvent e)
	{
		if(drawingRectangle || drawingOval)
		{
			shapes.push(currShape);
			firstClick = true;


		}
		else
		{
			shapes.push(points);
			points = new ArrayList<Point>();
			drawingLine = false;
		}
		undoRedoStack = new Stack<Object>();
		repaint();

	}

	public void undo()
	{
		if (!shapes.isEmpty())
		{
			undoRedoStack.push(shapes.pop());
			repaint();
		}
	}
	public void redo()
	{
		if (!undoRedoStack.isEmpty())
		{
			shapes.push(undoRedoStack.pop());
			repaint();
		}
	}

	public void stateChanged(ChangeEvent e)
	{
		currentColor = colorChooser.getColor();

	}


	public void mouseClicked(MouseEvent e)
	{
	}
	public void mouseEntered(MouseEvent e)
	{
	}
	public void mouseExited(MouseEvent e)
	{
	}
	public void mousePressed(MouseEvent e)
	{

	}

	public void mouseMoved(MouseEvent e)
	{


	}

	public void save()
	{
					FileFilter filter = new FileNameExtensionFilter("*.png","png");
					fileChooser.setFileFilter(filter);
					if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) //if you click, or "approve," save
					{
						File file = fileChooser.getSelectedFile();
						try
						{
							String st = file.getAbsolutePath();
							if(st.indexOf(".png") >= 0)
							st=st.substring(0, st.length() - 4); //if you pick file that already has png at end of it, want to drop that part off
							ImageIO.write(createImage(), "png", new File(st+".png"));
						}catch(IOException ioe){}
			}
	}

	public void load()
	{
		fileChooser.showOpenDialog(null);
					File imgFile = fileChooser.getSelectedFile();
					if(imgFile != null && imgFile.toString().indexOf(".png") >= 0) //means we can load it in
					{
						//loadedImage is buffered image declared in beginning
						try
						{
							loadedImage = ImageIO.read(imgFile);
						}catch(IOException ioe){}

						shapes = new Stack<Object>();
						repaint();

					}
					else
					{
						if(imgFile != null)
						JOptionPane.showMessageDialog(null,"Wrong file type. Please select a PNG file.");
					}

	}


	public BufferedImage createImage()
	{
		int width=this.getWidth();
		int height=this.getHeight();
		BufferedImage img=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2=img.createGraphics();
		this.paint(g2);
		g2.dispose();
		return img;
	}

	public void keyPressed(KeyEvent e)
	{
		if(e.isControlDown())
		{
			if(e.getKeyCode() == KeyEvent.VK_Z)
			{
				undo();
			}
			if(e.getKeyCode() == KeyEvent.VK_Y)
			{
				redo();
			}
		}

	}

	public void keyReleased(KeyEvent e)
	{
	}
	public void keyTyped(KeyEvent e)
	{
	}





	public static void main(String[] args)
	{
		PaintProgram app = new PaintProgram();
	}


	public class Point
	{
		private int x,  y, penWidth;
		private Color color;
		public Point(int x, int y, Color color, int penWidth)
		{
			this.x = x;
			this.y = y;
			this.color = color;
			this.penWidth = penWidth;

		}
		public int getPenWidth()
		{
			return penWidth;
		}

		public int getX()
		{
			return x;
		}
		public int getY()
		{
			return y;
		}
		public Color getColor()
		{
			return color;
		}

		public void setX(int x)
		{
			this.x = x;
		}

		public void setY(int y)
		{
			this.y = y;
		}

	}

	public class Shape extends Point
	{
		private int width, height;


		public Shape(int x, int y, Color color, int penWidth, int width, int height)
		{
			super(x,y,color,penWidth);
			this.width = width;
			this.height = height;
		}

		public void setWidth(int width)
		{
			this.width = width;
		}

		public void setHeight(int height)
		{
			this.height = height;
		}



		public int getHeight()
		{
			return height;
		}

		public int getWidth()
		{
			return width;
		}





	}

	public class Rectangle extends Shape
	{
		public Rectangle(int x, int y, Color color, int penWidth, int width, int height)
		{
			super(x,y,color,penWidth,width,height);
		}
		public Rectangle2D.Double getShape()
		{
			return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
		}

	}


	public class Oval extends Shape
	{
		public Oval(int x, int y, Color color, int penWidth, int width, int height)
		{
			super(x,y,color,penWidth,width,height);
		}
		public Ellipse2D.Double getShape()
		{
			return new Ellipse2D.Double(getX(), getY(), getWidth(), getHeight());
		}

	}


}