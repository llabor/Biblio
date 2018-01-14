# Biblio

## Acceso desde una clase Java a otra
Cómo llamar a métodos o acceder a atributos de una clase java desde otra clase; varias opciones:


### La clase A instancia a la clase B y llama a un método de la misma

Si desde una clase A queremos llamar a métodos de una clase B, lo más inmediato es que la clase A haga el new de la clase B y llame a su método. Por ejemplo, si la clase B es

<pre class="sh_java">
public class B {
   public void unMetodoDeB() {
      System.out.println("soy B");
   }
}
</pre>

La clase A sólo tendría que hacer algo como esto

<pre class="sh_java">
public class A {
   public void unMetodoDeA() {
       B b = new B();
       b.unMetodoDeB();
   }
}
</pre>

Por supuesto, vale cualquier variante, hacer el new de B en el constructor y guardárselo para usarlo luego o lo que queramos

<pre class="sh_java">
public class A {
   private B b = new B();
   public void unMetodoDeA() {
      b.unMetodoDeB();
   }
}
</pre>

== Pasar la clase B a la clase A desde el exterior. Inversión de control ==

El método anterior no siempre puede ser válido. A veces en el main o en otro sitio hacemos los new de las clases A y B y no nos interesa que A haga otro new de B. Para estos casos, la solución es pasarle a A, bien en el constructor, bien en un método hecho con tal fin, la clase B para que pueda usarla. El código de A podría ser cualquiera de los dos siguientes

<pre class="sh_java">
public class A {
   private B b;
   public A(B b){
      this.b = b;
   }
   public void unMetodoDeA() {
      b.unMetodoDeB();
   }
}
</pre>

<pre class="sh_java">
public class A {
   private B b;
   public void setB(B b) {
      this.b = b;
   }
   public void unMetodoDeA() {
      b.unMetodoDeB();
   }
}
</pre>

Y en el sitio donde hagamos los new, haríamos una de las siguientes cosas

<pre class="sh_java">
B b = new B();
A a = new A(b);
a.unMetodoDeA();
</pre>

<pre class="sh_java">
B b = new B();
A a = new A();
a.setB(b);
a.unMetodoDeA();
</pre>

Esto se conoce como "inversión de control" o "IoC". La clase A, en vez de hacer el new, espera que alguien lo haga por ella y se lo pase.


== Uso de interfaces ==

Con el procedimiento anterior, pasar la clase B a la clase A, suelen usarse interfaces, aunque no es necesario. La idea es que B implemente una InterfazB y que A espere recibir esa interfaz, tal que así

<pre class="sh_java">
public interface InterfazB {
   public void unMetodoDeB();
}
</pre>

<pre class="sh_java">
public class B implements InterfazB {
   @Override
   public void unMetodoDeB() {
      System.out.println("Soy B");
   }
}
</pre>

<pre class="sh_java">
public class A {
   private InterfazB b;
   public A (InterfazB b) {
      this.b = b;
   }
   public void unMetodoDeA() {
      b.unMetodoDeB();
   }
}
</pre>

Las ventajas del uso de interface en vez de pasar directamente la clase B son principalmente dos. Imagina que la clase A es el algoritmo de un juego maravilloso que acabas de hacer y que la clase B es la interfaz gráfica de usuario. Cuando A llama a b.unMetodoDeB() en realidad le está diciendo a la interfaz de usuario que dibuje un movimiento que nuestro algoritmo de juego acaba de decidir. Imagina ahora que tienes otro proyecto en el que el juego es el mismo, pero esta vez el juego es en web y la interfaz de usuario B no te sirve para nada, tienes que hacer otra interfaz web y además, como tu algoritmo A va a estar en el servidor, la interfaz web se ejecuta en los navegadores del cliente.

La primera ventaja del uso de interfaces es la siguiente: Si no usamos interfaz y A usa directamente a B, en nuestro nuevo proyecto tendríamos que llevarnos tanto la clase A como la clase B, aunque esta última no nos sirva de nada. Nuestro servidor tendrá que cargar inútilmente con toda la interfaz gráfica de usuario, o bien tendremos que modificar el código de la clase A para que no haga uso de esta clase B. Si usamos una interfaz, entonces nuestro servidor podría tener solo la clase A y la InterfazB, mucho más ligera que la clase B completa. Además, nuestra clase A podría ser un poco lista y hacer algo como

<pre class="sh_java">
public class A {
   private InterfazB b = null;
   public void setB (InterfazB b) {
      this.b = b;
   }
   public void unMetodoDeA() {
      if ( null != b) {
         b.unMetododDeB();
      }
   }
}
</pre>

es decir, A es lo suficientemente lista como para llamar a b sólo si se la han pasado previamente. Así no tenemos que tocar nada en A para llevárnosla a nuestro nuevo juego en web.

La segunda ventaja es la siguiente: En nuestro juego en web, posiblemente cuando A decida hacer una jugada tendrá que llamar a alguiente para que la pinte, pero en este caso quizás sea enviar ese movimiento por un socket o una conexión en vez de llamar directamente a la interfaz gráfica de usuario B. Al haber hecho la interface, en nuestro nuevo juego podemos hacer una clase Conexion encargada de enviar los movimientos decididos por A a la página web que está visualizando el cliente. Sólo tendremos que hacer que nuestra nueva clase Conexion implemente InterfazB y podremos usarla exactamente igual que usamos antes la clase B, sin necesidad de tocar para nada A.

<pre class="sh_java">
public class Conexion implements InterfazB {
   @Override
   public void unMetodoDeB() {
      System.out.println("Te he engañado, no soy B, soy la nueva clase Conexion");
   }
}
</pre>

<pre class="sh_java">
Conexion c = new Conexion();
A a = new A(c);
a.unMetodoDeA();
</pre>
Resumiendo, las dos ventajas del uso de interfaces son:

* Nuestra clase es más reutilizable, podemos llevarla a otros proyectos sin  necesidad de llevarnos muchas más clases.
* Nuestra clase puede cambiar su comportamiento, llamando a futuras clases que hagamos simplemente implementando la interfaz.

== Complicando el asunto ==

El ejemplo que hemos hecho de A y B es bastante simple en el sentido de que hay un trozo de código que hace new de A y de B y luego pasa B a A. Pero en un programa más complejo, esto puede liarse. Imagina que tenemos una clase AA que es la que hace el new de A y otra clase BB que es la que hace el new de B y seguimos necesitando que A pueda llamar a métodos de B. El procedimiento que debemos seguir es el mismo, pero tenemos que poner métodos setB() y getB() por doquier, de forma que donde se haga el new de AA y de BB se pueda obtener B y pasárselo a A. El código puede ser así

Por un lado, AA debe tener un setB() que le pasa B a A.

<pre class="sh_java">
public class AA {
   private A a = new A();
   public void setB (InterfazB b) {
      a.setB(b);
   }
}
</pre>

Por otro lado, BB debe tener un método getB() que nos  permita obtener B

<pre class="sh_java">
public class BB {
   private InterfazB b = new B();
   public InterfazB getB() {
      return b;
   }
}
</pre>

Y nuestro main() haría algo como esto

<pre class="sh_java">
AA aa = new AA();
BB bb = new BB();
aa.setB(bb.getB());
</pre>


== Localizador de servicios ==

El tema puede complicarse mucho, puede haber muchas más clases que debamos pasar de un lado a otro, no solo B. Y puede haber muchos niveles de clases, como AAAA que hace new de AAA que a su vez hace new de AA y a su vez de A, por lo que el trasiego de getB() y setB() puede crecer mucho.

Cuando una clase B se va a usar en muchos sitios y queremos evitar todo el "follón" de andar poniendo getters y setters en las clases, hay otra alternativa que es usar lo que se conoce como un localizador de servicios. En realidad, hay muchas variantes, como localizador de servicios, singleton, factorías, etc, etc, pero la idea básica de todos estos mecanismos es la misma y vamos a explicar cómo.

La idea es tener una clase, llamemosla Servicio, que sea la que tenga la instancia de B y un método para obtenerla. Como queremos poder llamar a esta clase en cualquier sitio, tanto la instancia de B como el método deben ser estáticos. Algo así

<pre class="sh_java">
public class Servicio {
   private static InterfazB b = new B();
   public static InterfazB getB() {
      return b;
   }
}
</pre>

Listo,al ser static, podemos llamarla desde cualquier lado de nuestro código haciendo esto

<pre class="sh_java">
public class A {
   public void unMetodoDeA() {
      Servicio.getB().unMetodoDeB();
   }
}
</pre>

Este mecanismo es muy útil cuando la clase B es algo que se va a usar en muchos sitios de nuestro programa, como una clase de acceso a base de datos, a sockets, etc.

La "pega" tal cual lo hemos hecho es que si queremos reutilizar A en otro código, tenemos que llevarnos la clase Servicio y para llevarnos esta, tenemos que llevarnos también la clase B, aunque estemos usando la Interfaz, ¿por qué? porque la clase Servicio está haciendo directamente un new de B y por tanto, sin B, no funciona.

Para evitar esto, debemos evitar que Servicio haga el new directamente, así que le ponemos un método set, tal que asi

<pre class="sh_java">
public class Servicio {
   private static InterfazB b;
   public static setB(InterfazB b) {
      this.b = b;
   }
   public static InterfazB getB() {
      return b;
   }
}
</pre>

Y en nuestro main tendremos que hacer esto

<pre class="sh_java">
Servicio.setB (new B());
A a = new A();
a.unMetodoDeA();
</pre>

Ya hemos conseguido lo que queríamos, podemos llevarnos A, Servicio e InterfazB a cualquier otro proyecto sin necesidad de llevarnos la clase B que ha dejado de sernos útil. También, en el nuevo proyecto del juego web que comentábamos antes, podríamos pasar a Servicio la clase Conexion en lugar de pasarle la clase B.

<pre class="sh_java">
Servicio.setB (new Conexion());
A a = new A();
a.unMetodoDeA();
</pre>

Comentábamos antes que había varias variantes de esta opción: localizador de servicios, factorías, singleton, etc. La diferencia básica entre todas ellas es básicamente dónde se hace el new de la clase B y si siempre se devuelve la misma instancia de B o se devuelven instancias distintas cada vez que se llama a Servicio.getB().

La versión de Servicio que acabamos de ver es un "localizador de servicios". Los servicios (la clase B) se "registran" en algún momento (Servicio.setB(new B());) y a partir de ahí se pueden usar en cualquier sitio (Servicio.getB().unMetodoDeB();)

== ¿Inversión de control o localizador de servicios? ==

¿Cual es mejor? Por supuesto, va en cuestión de gustos. Lo habitual es que cuando una clase se va a usar en muchos sitios y se quiera que sea siempre la misma instancia, por ejemplo, conexiones a base de datos, sockets de comunicaciones, etc, se use un servicio. Por otro lado, si la clase B fuese una clase un tanto especial que solo se va a usar en un sitio o dos, o queremos que haya muchas instancias de B circulando por ahí, como por ejemplo un panel de una interfaz de usuario con una tabla de ventas, una caja de texto que pide valores numéricos, etc, entonces se usaría inversión de control, es decir, pasarle a las clase A de nuestro código a través de setter sobre qué clases B en concreto tendrían que actuar.

Pero como siempre, es mejor usar la cabeza que seguir recetas al pie de la letra. El uso de uno u otro mecanismo depende de cada caso concreto y de los posibles cambios y posibilidades de reutilización que preveamos en nuestro código. 

Si no prevemos que la clase A se vaya a reutilizar nunca, podemos obviar el uso de InterfazB y hacer que vea directamente a B, el código queda más sencillo y no lo complicamos para algo que nunca vamos a usar.

Un localizador de servicios añade una complejidad adicional, es una clase más (Servicio) que necesita ser inicializada en nuestro main() ( Servicio.setB(new B)); ) y que nunca sabemos dónde se va a usar (clase A, otra clase C, otra clase D, ...), por lo que es bastante frecuente llevarse esas clase A,C,D a otros proyectos ... y olvidarse de registrar los servicios que necesitan. Así que debemos evaluar si esa complejidad adicional compensa los setters y getters que eliminamos al no usar la inversión de control.
