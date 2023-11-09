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

**Answer** : Here's the defines records `LaserDisc` and `VideoTape` :
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
 