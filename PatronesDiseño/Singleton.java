package patterns;

public class Singleton implements Cloneable {
	private static Singleton soleInstance = new Singleton(); // soleInstance = null;
	
	private Singleton() {
		System.out.println("Creating...");
	}
	
	public static Singleton getInstance() {
		return soleInstance;
	}
	
	/*
	public static Singleton getInstance() {
		if(soleInstance == null){
			soleInstance = new Singleton();
		}
		return soleInstance;
	}
	*/
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}

class TestClass {
	public static void main(String[] args) throws Exception {
		Singleton s1 = Singleton.getInstance();
		Singleton s2 = Singleton.getInstance();
		
		print("s1", s1);
		print("s2", s2);
		
		//Reflection
		Class clazz = Class.forName("patterns.Singleton");
		Constructor<Singleton> con = clazz.getDeclaredConstructor();
		con.setAccesible(true); // it lets you violate encapsulation and changes the access modifiers
		Singleton s3 = con.newInstance();
		
		print("s3", s3);
		
		//Serialization
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/tmp/s2.ser"));
		oos.writeObject(s2);
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/tmp/s2.ser"));
		Singleton s3 = (Singleton) ois.readObject();
		print("s3", s3);
		
		//Clone
		Singleton s3 = (Singleton) s2.clone();
		print("s3", s3);
		
	}
	
	static void print(String name, Singleton object) {
		System.out.println(String.format("Object: %s, Hashcode: %d", name, object.hashCode()));
	}
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	
	