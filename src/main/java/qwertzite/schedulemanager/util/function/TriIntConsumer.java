package qwertzite.schedulemanager.util.function;

@FunctionalInterface
public interface TriIntConsumer {
	public static final TriIntConsumer NONE = (i, j, k) -> {};
	
	public void apply(int i, int j, int k);
}
