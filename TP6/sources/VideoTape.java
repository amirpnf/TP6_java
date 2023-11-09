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
