package converter;

public interface NetworkConverter<S, T> {
	public S convert(T target);
}
