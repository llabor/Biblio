package patterns;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;

public class Singleton implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
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
		Singleton obj1 = Singleton.getInstance();
		Singleton obj2 = Singleton.getInstance();
		Singleton otherObject= Singleton.getInstance();
		
		print("obj1", obj1);
		print("obj2", obj2);
		print("\"other\"", otherObject);
		
		//Reflection
		Class clazz = Class.forName("patterns.Singleton");
		Constructor<Singleton> con = clazz.getDeclaredConstructor();
		con.setAccessible(true); // it lets you violate encapsulation and changes the access modifiers
		Singleton obj3 = con.newInstance();
		print("obj3", obj3);
		
		//Serialization (To be finished, it doesn't create/write file obj2.ser)
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/tmp/obj2.ser"));
		oos.writeObject(obj2);
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/tmp/obj2.ser"));
		Singleton obj31 = (Singleton) ois.readObject();
		print("obj31", obj31);
		
		//Clone
		Singleton obj4 = (Singleton) obj1.clone();
		print("obj4", obj4);
		
		oos.close();
		ois.close();
	}
	
	static void print(String name, Singleton object) {
		System.out.println(String.format("Object: %s, Hashcode: %d", name, object.hashCode()));
	}
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	
	
