package converter;

public interface ConvertFromNetwork<S, T> {
	public S convertFromNetwork(T target);
}
