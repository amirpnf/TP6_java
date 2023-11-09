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
