package network;

public interface ConvertFromNetwork<S, T> {
	public S convertFromNetwork(T target);
}