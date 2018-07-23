import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.awt.Canvas;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Cursor;
import java.awt.Point;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
//import sun.java2d.opengl.*;


public class SpaceField extends Canvas /*
										 * implements ImageObserver,
										 * MouseListener
										 */{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ConcurrentHashMap<String, BufferedImage> sprites;
	private ConcurrentHashMap<String,FlyingObject> stars;
	private ConcurrentHashMap<String,FlyingObject> planets;
	private ConcurrentHashMap<String,FlyingObject> satellites;
	private BufferStrategy strategy;
	 public static final int WIDTH =
	 Toolkit.getDefaultToolkit().getScreenSize().width;
	 public static final int HEIGHT =
	 Toolkit.getDefaultToolkit().getScreenSize().height;
//	private int WIDTH = 1280;
//	private int HEIGHT = 7;
	private boolean paused = false;
	public byte cursorIsDoing = 0;
	public Cursor appCursor;
	JFrame mainForm;
 
	private int MouseX = 0;
	private int MouseY = 0;
	private int Mode = 0;
	private boolean debugModeOn = false;
	private VolatileImage BG;
	private Space space;
	private TweetStreamer tweeter;
	private boolean tweetRun = false;
	
	public SpaceField(boolean sMode) {
		sprites = new ConcurrentHashMap<String, BufferedImage>();
		stars = new ConcurrentHashMap<String, FlyingObject>();
		planets = new ConcurrentHashMap<String, FlyingObject>(1000,(float) 0.75);
		satellites = new ConcurrentHashMap<String, FlyingObject>();
		space = new Space(stars, planets, satellites);
		mainForm = new JFrame("The Twitter-verse");
		JPanel panel = (JPanel) mainForm.getContentPane();
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				getSprite("arrow.gif"), new Point(0, 0), "mainCursor"));
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (Mode == 0) {
						pauseIt(true);
						if (JOptionPane.showConfirmDialog(getParent(),
								"Do you want to quit?", "Exit Planetarium",
								JOptionPane.OK_CANCEL_OPTION) == 0) {
							System.exit(0);
						} else {
							pauseIt(false);
						}
					} else {
						pauseIt(false);
						Mode = 0;
						setCursor(Toolkit.getDefaultToolkit()
								.createCustomCursor(getSprite("arrow.gif"),
										new Point(0, 0), "mainCursor"));
					}
				}
				if (event.getKeyCode() == KeyEvent.VK_PAUSE) {
					pauseIt(!paused);
				}
				if (event.getKeyCode() == KeyEvent.VK_F1) {
					showHelp();
				}
				if (event.getKeyCode() == KeyEvent.VK_F2) {
					pauseIt(true);
					Mode = 1;
					setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
							getSprite("sunCursor.gif"), new Point(0, 0),
							"sunCursor"));
				}
				if (event.getKeyCode() == KeyEvent.VK_F3) {
					pauseIt(true);
					if (stars.size() == 0) {
						JOptionPane
								.showMessageDialog(
										getParent(),
										"You cannot add planets.\nPlease, add a star first.",
										"Error!", JOptionPane.ERROR_MESSAGE);
						pauseIt(false);
						Mode = 0;
						setCursor(Toolkit.getDefaultToolkit()
								.createCustomCursor(getSprite("arrow.gif"),
										new Point(0, 0), "mainCursor"));
					} else {
						pauseIt(true);
						Mode = 2;
						setCursor(Toolkit.getDefaultToolkit()
								.createCustomCursor(
										getSprite("planetCursor.gif"),
										new Point(0, 0), "sunCursor"));
					}
				}
				if (event.getKeyCode() == KeyEvent.VK_F4) {
					System.out.println("--------Getting ready to BOOP! Twitter thread");
					tweeter.changeQuery("pikachu");
					System.out.println("BOOPED!");
					
				}
				if (event.getKeyCode() == KeyEvent.VK_D) {
					debugModeOn = !debugModeOn;
				}
			}

			public void keyReleased(KeyEvent event) {
			}

			public void keyTyped(KeyEvent event) {
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				MouseX = e.getX();
				MouseY = e.getY();

				if (e.getButton() == 1) {
					if (Mode == 1) {
						addSun();
					}
					if (Mode == 2) {
						addPlanet();
					}
					if (Mode == 3) {
						addPlanet();
					}
				} else {
					pauseIt(false);
					Mode = 0;
					setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
							getSprite("arrow.gif"), new Point(0, 0),
							"mainCursor"));
				}
			}
		});
		setBounds(0, 0, WIDTH, HEIGHT);
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);
		panel.add(this);
		mainForm.setBounds(0, 0, WIDTH, HEIGHT);
		mainForm.setUndecorated(true);
		mainForm.setResizable(true);
		mainForm.setVisible(true);
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		this.requestFocus();
		tweeter = new TweetStreamer(this);
		
		updateWorld();
	}

	public void showHelp() {
		pauseIt(true);
		JOptionPane
				.showMessageDialog(
						this,
						""
								+ "Welcome to The Twitter-verse!\n\n"
								+ "INSTRUCTIONS\n\n"
								+ "F1 - opens this window\n"
								+ "F2 - adds a star object\n"
								+ "F3 - adds a planetary object\n"
								+ "F4 - adds a satellite object\n"
								+ "F8 - removes an object\n"
								+ "F10 - shows / hides names and paths of all available objects\n\n"
								+ "ESC - quit"
								,
						"Welcome!", JOptionPane.PLAIN_MESSAGE);
		pauseIt(false);
	}


	private void addSun() {
		String name = JOptionPane.showInputDialog(this,
				"Enter hashtag:", "Adding a Star to the Twitter-verse",
				JOptionPane.QUESTION_MESSAGE);
		space.addSun(name, MouseX, MouseY);
		
		pauseIt(false);
		Mode = 0;
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				getSprite("arrow.gif"), new Point(0, 0), "mainCursor"));
		if (!tweetRun) {
			tweetRun = true;
			tweeter.run(name);
			System.out.println("first run");
		}
		else {System.out.println("adding to stars");tweeter.addToQuery(name);}
	}

	
	public void addPlanet(){
		String velocity = JOptionPane.showInputDialog(this,
				"Select velocity (1-500)", "Adding a Planet to the Galaxy",
				JOptionPane.QUESTION_MESSAGE);
		int v = Integer.parseInt(velocity);
		space.addPlanet("A",MouseX, MouseY, v);
	}
	
	public synchronized void addTweetPlanet(String string) {
		int v = 100;
		Enumeration<String> uuids =stars.keys();
		FlyingObject parentStar = stars.get(uuids.nextElement());
		
		while (!string.toLowerCase().contains((parentStar.getName().toLowerCase()))){
			parentStar = stars.get(uuids.nextElement());
		}
		int x = (int) (parentStar.getX() - 15 + Math.random() *  parentStar.getX() + 15); 
		int y = (int) (parentStar.getY() - 15 + Math.random() *  parentStar.getY() + 15);
		space.addPlanet("A",x,y, v);
		notifyAll();
		
		
		
	}
	
	
	
	public synchronized void addToSolarSystem(Hashtable<String,FlyingObject> solarGroup, FlyingObject obj) {
		solarGroup.put(obj.getUid(), obj);
	}

	public FlyingObject connectTo(FlyingObject child, boolean isPlanet) {
		Iterator<String> it;
		FlyingObject master = null;
		ConcurrentHashMap<String, FlyingObject> masterSet;
		if (isPlanet) {
			masterSet = stars;
		} else {
			masterSet = planets;
		}
		double dist = 10000;
		double parentDist = 0;
		it = masterSet.keySet().iterator();
		Object key;
		while (it.hasNext()) {
			key = it.next();
			FlyingObject parent =  masterSet.get(key);
			parentDist = Math.sqrt(Math.pow((parent).getX() - child.getX(), 2)
				                 + Math.pow((parent).getY() - child.getY(), 2));
			if (parentDist < dist) {
				master = (FlyingObject) masterSet.get(key);
				dist = parentDist;
			}
		}
		return master;
	}

	public synchronized void DrawingEngine() {
		Graphics2D canvas = (Graphics2D) strategy.getDrawGraphics();
		BG = drawVolatileImage(canvas, BG, 0, 0, getSprite("sky.png"));

		canvas.setColor(Color.white);
		//canvas.drawString((paused == true) ? "paused" : "running", 0, 12);

		Iterator<FlyingObject> it;
		it = stars.values().iterator();
		while (it.hasNext()) {
			try {
				FlyingObject mySun = (FlyingObject) it.next();
				mySun.paintObject(canvas, getSprite(mySun.getUsedImage()), this,
						false, true, false, debugModeOn);
			} catch (Exception e) {
			}
		}
		it = planets.values().iterator();
		long time = System.currentTimeMillis();
		while (it.hasNext()) {
			try {
				FlyingObject myPlanet = (FlyingObject) it.next();
				if (time - myPlanet.getBirthTime() > 60000){
					System.out.println("Old planet");
					planets.remove(myPlanet.getUid());
					
				}
				else{
					// System.out.println(myPlanet.orbitTo.name);
					myPlanet.updateOrbit(1);
					// System.out.println(myPlanet.name+" orbits at "+myPlanet.degree+" degrees");
					myPlanet.paintObject(canvas, getSprite(myPlanet.getUsedImage()),
						this, false, false, false, debugModeOn);
				}
			} catch (Exception e) {
			}
		}
		it = satellites.values().iterator();
		while (it.hasNext()) {
			try {
				FlyingObject mySatellite = (FlyingObject) it.next();
				if (!paused) {
					mySatellite.updateOrbit(1);
					// System.out.println(myPlanet.name+" orbits at "+myPlanet.degree+" degrees");
				}
				mySatellite.paintObject(canvas,
						getSprite(mySatellite.getUsedImage()), this, false,
						false, false, debugModeOn);
			} catch (Exception e) {
			}
		}
		strategy.show();
		notifyAll();
	}


	public void updateWorld() {
		while (isVisible()) {
			DrawingEngine();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void pauseIt(boolean stop) {
		paused = stop;
	}

	public VolatileImage drawVolatileImage(Graphics2D g, VolatileImage img,
			int x, int y, BufferedImage orig) {
		final int MAX_TRIES = 100;
		for (int i = 0; i < MAX_TRIES; i++) {
			if (img != null) {
				// Draw the volatile image
				g.drawImage(img, x, y, null);
				// Check if it is still valid
				if (!img.contentsLost()) {
					return img;
				}
			} else {
				// Create the volatile image
				img = g.getDeviceConfiguration().createCompatibleVolatileImage(
						orig.getWidth(null), orig.getHeight(null));
			}
			// Determine how to fix the volatile image
			switch (img.validate(g.getDeviceConfiguration())) {
			case VolatileImage.IMAGE_OK:
				// This should not happen
				break;
			case VolatileImage.IMAGE_INCOMPATIBLE:
				// Create a new volatile image object;
				// this could happen if the component was moved to another
				// device
				img.flush();
				img = g.getDeviceConfiguration().createCompatibleVolatileImage(
						orig.getWidth(null), orig.getHeight(null));
			case VolatileImage.IMAGE_RESTORED:
				// Copy the original image to accelerated image memory
				Graphics2D gc = (Graphics2D) img.createGraphics();
				gc.drawImage(orig, 0, 0, null);
				gc.dispose();
				break;
			}
		}
		// The image failed to be drawn after MAX_TRIES;
		// draw with the non-accelerated image
		g.drawImage(orig, x, y, null);
		return img;
	}

	public BufferedImage loadImage(String path) {
		URL url = null;
		try {
			url = getClass().getClassLoader().getResource("res/"+path);
			System.out.println(url);
			return ImageIO.read(url);
		} catch (Exception e) {

			System.out.println("The Image " + path
					+ " was not found at the url:" + url);
			System.out.println("The error was: " + e.getClass().getName() + " "
					+ e.getMessage());
			System.exit(0);

		}
		return null;
	}

	public BufferedImage getSprite(String uid) {
		BufferedImage img = (BufferedImage) sprites.get(uid);
		if (img == null) {
			img = loadImage(uid);
			sprites.put(uid, img);
		}
		return img;
	}

	public static void main(String[] args) {
		boolean safeMode = false;
		if (args.length > 0) {
			if (args[0].equals("-safemode")) {
				safeMode = true;
			}
		}
		SpaceField milkyway = new SpaceField(safeMode);
	}

	

}
