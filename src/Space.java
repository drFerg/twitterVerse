import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


public class Space {
	ConcurrentHashMap<String, FlyingObject> stars;
	ConcurrentHashMap<String,FlyingObject> planets;
	ConcurrentHashMap<String, FlyingObject> moons;
	
	public Space (ConcurrentHashMap<String, FlyingObject> stars, ConcurrentHashMap<String, FlyingObject> planets,ConcurrentHashMap<String, FlyingObject> moons){
		super();
		this.stars = stars;
		this.planets = planets;
		this.moons = moons;
	}
	
	public void addSun(String name, int xPos, int yPos) {
		if (name != null) {
			FlyingObject sun = new FlyingObject(name, "sun.png", xPos,
					yPos, 92, 92, true);
			sun.setOrbitTo(null);
			this.addToSolarSystem(stars, sun);
			System.out.println("Added a sun");
			
		}
	}
	
	public void addPlanet(String name, int xPos, int yPos, int velocity){
		FlyingObject planet = new FlyingObject(name, "moon.png",
				xPos, yPos, 30, 30, false);
		planet.setOrbitTo(findParentToOrbit(planet, true));
		planet.setRadius(Math.sqrt(Math.pow(planet.getOrbitTo().getX() - planet.getX(), 2)
				                 + Math.pow(planet.getOrbitTo().getY() - planet.getY(), 2)));
		planet.setDegree(Math.toDegrees(Math.asin(Math.abs((planet.getY() - planet.getOrbitTo().getY()))/ planet.getRadius())));
		
		if (planet.getX() < planet.getOrbitTo().getX()) {
			if (planet.getY() < planet.getOrbitTo().getY()) {
				planet.setDegree(planet.getDegree() - 180);
			} else {
				planet.setDegree(180 - planet.getDegree());
			}
		} else {
			if (planet.getY() < planet.getOrbitTo().getY()) {
				planet.setDegree(planet.getDegree() * (-1)); // (90-myPlanet.degree);
			}
		}
		planet.setVelocity(((double) velocity)/ (planet.getRadius()));
		planet.getOrbitTo().addOrbiter(planet);
		addToSolarSystem(planets,planet);
		System.out.println("Added a planet");
	}
	
	
	public void addToSolarSystem(ConcurrentHashMap<String, FlyingObject> stars2, FlyingObject obj) {
		stars2.put(obj.getUid(), obj);
	}
	
	private FlyingObject findParentToOrbit(FlyingObject child, boolean isPlanet) {
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

}
