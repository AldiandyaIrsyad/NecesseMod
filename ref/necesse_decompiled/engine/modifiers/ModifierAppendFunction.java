package necesse.engine.modifiers;

@FunctionalInterface
public interface ModifierAppendFunction<T> {
   T apply(T var1, T var2, int var3);
}
