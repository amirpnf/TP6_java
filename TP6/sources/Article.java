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
