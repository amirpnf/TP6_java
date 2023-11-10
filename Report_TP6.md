# TP6 Java - Amirhossein Pouyanfar - 262575 Group 2 Mr. Rémy Forax

## Exercise 1

 On souhaite modéliser le catalogue d'un magasin BlockBuster, un magasin qui loue des cassettes vidéos et des laser discs, sachant que l'on veut être capable de lire/écrire un catalogue à partir de fichiers.
Oui, on fait de l'archéologie, avant Netflix, les vrais gens allaient dans un magasin pour louer des films, soit sur des bandes analogiques (des cassettes vidéo) soit sur disques numériques (des laser discs).
Pour simplifier un peu les choses, on va dire que le magasin a deux sortes d'articles, des VideoTape et des LaserDisc et que pour un nom de film, il ne peut y avoir qu'un article au maximum dans le catalogue.

    Une VideoTape est définie par avec un nom (name) et une durée (duration) de type java.time.Duration (un type déjà fourni par le JDK)
    Un LaserDisc est uniquement défini par un nom.
    Un Catalog permet d'ajouter (add) des articles, de chercher (lookup) un article par son nom, de charger (load des articles à partir d'un fichier et de sauvegarder (save) les articles du catalogue dans un fichier.

1. Écrire les types VideoTape et LaserDisc tels que le code suivant fonctionne
```java
var laserDisc = new LaserDisc("Jaws");
var videoTape = new VideoTape("The Cotton Club", Duration.ofMinutes(128));
var videoTape2 = new VideoTape("Mission Impossible", Duration.ofMinutes(110));
```
Attention à ne pas oublier les pré-conditions. 

**Answer** : Here's the defined records `LaserDisc` and `VideoTape` :
VideoTape:

```java
import java.time.Duration;
import java.util.Objects;

public record VideoTape(String name, Duration duration) implements Article {
	public VideoTape {
		Objects.requireNonNull(name);
		Objects.requireNonNull(duration);
	}
}
```

LaserDisc:
```java
import java.util.Objects;

public record LaserDisc(String name) implements Article {
	public LaserDisc {
		Objects.requireNonNull(name);
	}
}
```
We also are going to define an interface, that will act like some kind of supertype for `LaserDisc` and `VideoTape`:
```java
public interface Article {

}
```
2. On souhaite maintenant écrire un type Catalog avec une méthode

    add qui permet d'ajouter une cassette vidéo ou un laser disc. Attention, cette méthode ne doit pas permettre d'ajouter deux articles ayant le même nom.
    lookup qui permet de rechercher un article par son nom


Quel doit être le type du paramètre de add et le type de retour de lookup ?
Que doit renvoyer lookup s'il n'y a ni cassette vidéo ni laser disc ayant le nom demandé dans le catalogue ?
Implanter le type Catalog sachant que l'on souhaite que le code suivant fonctionne :
```java
    var catalog = new Catalog();
    catalog.add(laserDisc);
    catalog.add(videoTape);
    catalog.add(videoTape2);
    // catalog.add(new LaserDisc("Mission Impossible"));  // exception !
    System.out.println(catalog.lookup("Jaws"));
    System.out.println(catalog.lookup("The Cotton Club"));
    System.out.println(catalog.lookup("Indiana Jones"));
```    

**Answer** :
Here's the `add` and `lookup` methods, defined with required parameters and preconditions.
In Catalog class :
```java
import java.util.HashMap;
import java.util.Objects;

public class Catalog {
	private final HashMap<String, Article> articles = new HashMap<>();
	
	public void add(Article article) {
		Objects.requireNonNull(article);
		if(articles.putIfAbsent(article.name(), article) != null) {
			throw new IllegalArgumentException();
		}
	}
	
	public Article lookup(String name) {
		return articles.getOrDefault(name, null);
	}
}
```
The return type of `add` must be `void`, and if the article exists already, the `add` method will raise
an `IllegalArgumentException`.
However, lookup is a method defined to search for an article, it must return `null` if the article doesn't exist
in the Catalog. 

3. On veut pouvoir charger et sauvegarder les articles du catalogue dans un fichier, un article par ligne. Pour cela, on va dans un premier temps écrire deux méthodes, toText et fromText qui permettent respectivement de renvoyer la forme textuelle d'un article et de créer un article à partir de sa représentation textuelle.
Pourquoi fromText est-elle une méthode statique alors que toText est une méthode d'instance ?
Le format textuel est composé du type de l'article (LaserDisc ou VideoTape) suivi du nom de l'article et, dans le cas de la cassette vidéo, de la durée en minutes (il existe une méthode duration.toMinutes() et une méthode Duration.ofMinutes()). Les différentes parties du texte sont séparées par des ":".
Voici un exemple de fichier contenant un laser disc et une cassette vidéo.
```bash
      LaserDisc:Jaws
      VideoTape:The Cotton Club:128
```    

Dans un premier temps, écrire la méthode toText de telle façon que le code suivant est valide
```java
    var laserDiscText = laserDisc.toText();
    var videoTapeText = videoTape.toText();
    System.out.println(laserDiscText);  // LaserDisc:Jaws
    System.out.println(videoTapeText);  // VideoTape:The Cotton Club:128
```    
Puis écrire le code de la méthode fromText sachant qu'il existe une méthode string.split() pour séparer un texte suivant un délimiteur et que l'on peut faire un switch sur des Strings. Le code suivant devra fonctionner :
```java
    var laserDisc2 = Article.fromText(laserDiscText);
    var videoTape3 = Article.fromText(videoTapeText);
    System.out.println(laserDisc.equals(laserDisc2));  // true
    System.out.println(videoTape.equals(videoTape3));  // true
```    
Enfin, discuter du fait que le type des articles doit être scellé ou non ?
Note : faire en sorte que les noms "LaserDisc" et "VideoTape" soit définis sous forme de constantes pour que le code soit plus lisible.

**Answer** :

here's the `totext` and `fromText` methods in Article interface:
Note : `fromText` should be rather a static method, because it's to tranform a text to an Article. In addition to this,
the Article interface should be a `sealed` one because we don't have the required mechanism to manage other cases. (In the 
switch instruction we have only two main cases and a default one).
```java
import java.time.Duration;

public sealed interface Article permits LaserDisc, VideoTape {
	String name();
	
	String toText();
	
	static Article fromText(String text) {
		var parts = text.split(":");
		return switch(parts[0]) {
			case VideoTape.VIDEOTAPE -> new VideoTape(parts[1], Duration.ofMinutes(Long.parseLong(parts[2])));
			case LaserDisc.LASERDISK -> new LaserDisc(parts[1]);
			default -> throw new IllegalArgumentException("Hey buddy");
		};
	}
}
```
So we define `fromText` in the interface.
Here's the modified implementation of `toText` in VideoTape:

```java
import java.time.Duration;
import java.util.Objects;

public record VideoTape(String name, Duration duration) implements Article {
	public static final String VIDEOTAPE = "VideoTape";
	public VideoTape {
		Objects.requireNonNull(name);
		Objects.requireNonNull(duration);
	}
	
	@Override
	public String toText() {
		return "VideoTape:" + name + ":" + duration.toMinutes();
	}
}
```
and in LaserDisc:
```java
import java.util.Objects;

public record LaserDisc(String name) implements Article {
	public static final String LASERDISK = "LaserDisk";
	public LaserDisc {
		Objects.requireNonNull(name);
	}
	
	@Override
	public String toText() {
		return "LaserDisc:" + name;
	}
}
```

4. On souhaite maintenant ajouter une méthode save qui permet de sauvegarder les articles d'un catalogue dans un fichier.
Quelle méthode doit-on utiliser pour créer un écrivain sur un fichier texte à partir d'un Path ?
Comment doit-on faire pour garantir que la ressource système associée est bien libérée ?
Comment doit-on gérer les exceptions d'entrée/sortie ?
Écrire la méthode save afin que le code suivant fonctionne :
```java
    var catalog2 = new Catalog();
    catalog2.add(laserDisc);
    catalog2.add(videoTape);
    catalog2.save(Path.of("catalog.txt"));
```
Comme Catalog est mutable, on va écrire la méthode load comme une méthode d'instance et non pas comme une méthode statique. Expliquer quel est l'intérêt. Écrire la méthode load dans Catalog afin que le code suivant fonctionne :
```java
    var catalog3 = new Catalog();
    catalog3.load(Path.of("catalog.txt"));
    System.out.println(catalog3.lookup("Jaws"));  // LaserDisc:Jaws
    System.out.println(catalog3.lookup("The Cotton Club"));  // VideoTape:The Cotton Club:128
```    
Note : pour load, on ne vous demande pas de vérifier au préalable que le fichier est bien formé (lignes de la bonne taille, formats numériques corrects, ...). Vous pouvez laisser filer les exceptions susceptibles de survenir dans ces cas là.   

**Answer** :
- In order to create a writer on a file using a `Path` given as a parameter of our function,
we can use the `newBufferedWriter` from `Files` class. This method takes a `Path` object and
a `Charset` (if not given, **UTF-8**), and returns a `Writer` object that we can assign to
a variable.   
- In Java, in order to ensure that system resources associated with an object are properly released,
we should implement resource management practices and mechanisms, specifically using a `try-with-resources`
statement.
- To manage the input/output errors when using `save` and `load` methods, we can insert this statement in front of 
our functions signatures :  
function () `throws IOException` {...}  

Here's the `save` method implementation (in the `Catalog` class) :
```java
public void save(Path path) throws IOException{
  try(var writer = Files.newBufferedWriter(path)) {
  	for(var article : articles.values()) {
  	  writer.write(article.toText());
  	  writer.newLine();
  	}
  } 
}
```  
And the `load` method, we'd rather have this one as an instance method, this will allow us to directly modify
the state of the current `Catalog` object, and to load `Article` objects directly in the existing `Catalog`.
```java
public void load(Path path) throws IOException{
  try(var reader = Files.newBufferedReader(path)) {
  	String line;
  	while((line = reader.readLine()) != null) {
  	  var article = Article.fromText(line);
  	  this.add(article);
  	}
  }
  }
```

5. Tout le monde s'est plus ou moins mis d'accord pour que l'UTF-8 soit le format utilisé pour la stockage, malheureusement, il reste encore plein de Windows XP / Windows 7 qui ne sont pas en UTF8 par défaut. On va donc ajouter deux surcharges à load et save qui prennent en paramètre l'encoding. Le code suivant doit fonctionner :
```java
var catalog4 = new Catalog();
catalog4.add(new LaserDisc("A Fistful of €"));
catalog4.add(new VideoTape("For a Few €s More", Duration.ofMinutes(132)));
catalog4.save(Path.of("catalog-windows-1252.txt"), Charset.forName("Windows-1252"));
var catalog5 = new Catalog();
catalog5.load(Path.of("catalog-windows-1252.txt"), Charset.forName("Windows-1252"));
System.out.println(catalog5.lookup("A Fistful of €"));
System.out.println(catalog5.lookup("For a Few €s More"));
```    
Écrire les deux méthodes et partager le code entre les surcharges pour ne pas dupliquer de code.
Note : il existe une classe StandardCharsets qui est une énumération des encodages standard et qui contient l'encodage UTF-8. 

**Answer** : 
We are going to overload the two methods we defined in the previous question, that will allow us to load and save 
`Article` objects into a `Catalog`, which have non-UTF-8 names. 

Here are they :
```java
public void save(Path path) throws IOException{  // This is the overloaded saving method
	save(path, StandardCharsets.UTF_8);
}
	
public void save(Path path, Charset charset) throws IOException{
	try(var writer = Files.newBufferedWriter(path, charset)) {
		for(var article : articles.values()) {
			writer.write(article.toText());
			writer.newLine();
			}
		} 
}

public void load(Path path) throws IOException{ // This is the overloaded loading method
	load(path, StandardCharsets.UTF_8);
}

public void load(Path path, Charset charset) throws IOException{
	try(var reader = Files.newBufferedReader(path, charset)) {
		String line;
		while((line = reader.readLine()) != null) {
			var article = Article.fromText(line);
			this.add(article);
		}
	}
}
```

6. De façon optionnelle, pour les plus balèzes, on veut ajouter le support des fichiers binaires en ajoutant deux méthodes saveInBinary et loadInBinary qui permettent respectivement de sauver un fichier en binaire et de charger un fichier binaire.
Le format binaire utilisé est

    un entier 32 bits indiquant le nombre d'articles,
    pour chaque article, son type, un entier 8 bits (1 pour VideoTape, 2 pour LaserDisc), son nom en modified UTF8 et dans le cas de VideoTape un entier long 64 bits avec le nombre de minutes.


Il existe des classes DataInputStream et DataOutputStream que l'on peut construire respectivement sur InputStream et OutputStream et qui sont capables de lire/écrire un entier 8 bits, un entier 64 bits ou une chaîne au format modified UTF8.
Écrire les méthodes saveInBinary et loadInBinary et vérifier que le code suivant fonctionne.
```java
var catalog6 = new Catalog();
catalog6.add(new VideoTape("Back to the future", Duration.ofMinutes(116)));
catalog6.add(new LaserDisc("Back to the future part II"));
catalog6.add(new LaserDisc("Back to the future part III"));
catalog6.saveInBinary(Path.of("catalog.binary"));
var catalog7 = new Catalog();
catalog7.loadFromBinary(Path.of("catalog.binary"));
System.out.println(catalog7.lookup("Back to the future"));
System.out.println(catalog7.lookup("Back to the future part II"));
System.out.println(catalog7.lookup("Back to the future part III"));
```    

**Anwser** :

Here are two methods for saving a Catalog to and loading it from a binary file :

```java
public void saveToBinary(Path path) throws IOException {
  try(var writer = new DataOutputStream(new FileOutputStream(path.toFile()))) {
  	writer.writeInt(articles.size());
  	for(var item : articles.values()) {
  	  if(item.isVideoTape()) { // verifying that it's a VideoTape
  	  	writer.writeByte(1);
  	  	writer.writeUTF(item.name());
  	  	writer.writeLong(item.duration().toMinutes()); // Then call duration on it. (for more information, read the P.S below)
  	  } else {
  	  	writer.writeByte(2);
  	  	writer.writeUTF(item.name());
  	  }
  	}
  }
}

public void loadFromBinary(Path path) throws IOException {
  try(var reader = new DataInputStream(new FileInputStream(path.toFile()))) {
  	int itemsNumber = reader.readInt();
  	for(int i = 0; i < itemsNumber; ++i) {
  	  int itemType = reader.readByte();
  	  String itemName = reader.readUTF();
  	  switch(itemType) {
  	  	case 1 -> {
  	  	  long duration = reader.readLong();
  	  	  var tape = new VideoTape(itemName, Duration.ofMinutes(duration));
  	  	  this.add(tape);
  	  	} 
  	  	case 2 -> {
  	  	  var disk = new LaserDisc(itemName);
  	  	  this.add(disk);
  	  	}
  	  	default -> throw new IllegalArgumentException("Invalid Article Type!");
  	  }
  	}
  }
}
```
P.S : In the `saveToBinary` method, when we want to write the duration of a VideoTape, the object on 
which we call `.duration` is an `Article` and not a VideoTape, so that would be a problem unless we add this
method to our interface : 
```java
default Duration duration() {
  throw new UnsupportedOperationException("Unsupported operation for this type of article");
}
```
This will ensure that any object from a class implementing the `Article` interface, upon which the `.duration()` method is invoked, will throw an `UnsupportedOperationException` if `.duration()` is not implemented in its class. So for example if a this
method is called on a `LaserDisc`, it'll throw an `UnsupportedOperationException`. 
