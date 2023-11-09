import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Catalog {
	private final LinkedHashMap<String, Article> articles = new LinkedHashMap<>();
	
	public void add(Article article) {
		Objects.requireNonNull(article);
		if(articles.putIfAbsent(article.name(), article) != null) {
			throw new NullPointerException();
		}
	}
	
	public Article lookup(String name) {
		return articles.getOrDefault(name, null);
	}
	
	public void save(Path path) throws IOException{
			try(var writer = Files.newBufferedWriter(path)) {
				for(var article : articles.values()) {
					writer.write(article.toText());
					writer.newLine();
				}
			}
	}
	
	public void load {
		
	}
}