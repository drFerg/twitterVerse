import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
//import java.util.ArrayList;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
//import java.awt.font.fon

public class FlyingObject {
	
	private String uid;
	private long birthTime;
	private String name;
	private int x = 0;
	private int y = 0;
	private int realWidth = 0;
	private int realHeight = 0;
	private double velocity;
	private double radius = 0;
	private double degree = 0;
	private String usedImage;
	private boolean moveToTheRight;
	private BufferStrategy strategy;
	private boolean fixed;
	private boolean debugged = false;
	private HashMap<String,FlyingObject> orbitingObjects = new HashMap<String, FlyingObject>();
	private FlyingObject orbitTo;
	private final Font font = new Font("Serif", Font.PLAIN, 12);
	
	public FlyingObject(String name, String usedImage, int x, int y, int width, int height, boolean fixed) {
		setUid(Long.toString(System.currentTimeMillis()) + "$" + Long.toString(Math.round(Math.random()*1256000)));
		this.setName(name);
		this.setUsedImage(usedImage);
		this.fixed = fixed;
		this.setX(x);
		this.setY(y);
		this.realWidth = width;
		this.realHeight = height;
		this.birthTime = System.currentTimeMillis();
	}
	
	public FlyingObject(String name, String usedImage, boolean fixed, int x, int y, int width, int height, double rad, double deg, double velocity, boolean moveToTheRight) {
		setUid(Long.toString(System.currentTimeMillis()) + "$" + Long.toString(Math.round(Math.random()*1256000)));
		this.setName(name);
		this.setUsedImage(usedImage);
		this.fixed = fixed;
		this.setX(x);
		this.setY(y);
		this.realWidth = width;
		this.realHeight = height;
		this.setRadius(rad);
		this.setDegree(deg);
		this.setVelocity(velocity);
		this.moveToTheRight = moveToTheRight;
		this.birthTime = System.currentTimeMillis();
	}
	
	public void paintObject(Graphics2D canvas, BufferedImage sprite, ImageObserver img, boolean drawOrbit, boolean drawName, boolean drawRadius, boolean debugging)
	{
		if ((drawOrbit || debugging || debugged)&&!fixed){
			canvas.setColor(Color.RED); // Sets colour to be used in future operations
			canvas.drawArc((int) (getOrbitTo().getX() - Math.round(getRadius())), (int) (getOrbitTo().getY() - Math.round(getRadius())), (int) (Math.round(getRadius()*2)), (int) (Math.round(getRadius()*2)), 0, 360);
		}
		if ((drawRadius || debugging || debugged)&&!fixed){
			canvas.setColor(Color.GREEN);
			canvas.drawLine(getOrbitTo().getX(), getOrbitTo().getY(), getX(), getY());
		}
		if (debugging || debugged){
			canvas.setColor(Color.GREEN);
			canvas.drawLine(getX()-100, getY(), getX()+100, getY());
			canvas.drawLine(getX(), getY()-100, getX(), getY()+100);
			canvas.setColor(Color.YELLOW);
			canvas.drawRect(getX()-4, getY()-4, 8, 8);
		} else {
			canvas.drawImage(sprite, getX()-Math.round(sprite.getWidth()/2), getY()-Math.round(sprite.getHeight()/2), img);
		}
		if (drawName || debugging || debugged){
			FontMetrics fsize = canvas.getFontMetrics();
			canvas.setColor(Color.YELLOW);
			canvas.drawString(getName(), getX()-Math.round(fsize.stringWidth(getName())/2), getY()+Math.round(realHeight/2)+fsize.getHeight());
		}
	}
	
	public void updateOrbit(long time)
	{
		if (fixed ==  false){
			for (long i=0; i<time; i++){
				setDegree((getDegree() + getVelocity())% 360);
			}
			setX((int) (getOrbitTo().getX() + Math.round(getRadius() * Math.cos(Math.toRadians(getDegree())))));
			setY((int) (getOrbitTo().getY() + Math.round(getRadius() * Math.sin(Math.toRadians(getDegree())))));
		}
	}
	
	public void addOrbiter(FlyingObject planetary){
		orbitingObjects.put(planetary.getUid(),planetary);
	}
	
	public void remove(Object key){
		orbitingObjects.remove(key);
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public void setOrbitTo(FlyingObject orbitTo) {
		this.orbitTo = orbitTo;
	}

	public FlyingObject getOrbitTo() {
		return orbitTo;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getRadius() {
		return radius;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}

	public double getDegree() {
		return degree;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setUsedImage(String usedImage) {
		this.usedImage = usedImage;
	}

	public String getUsedImage() {
		return usedImage;
	}

	public long getBirthTime() {
		return birthTime;
	}
}
