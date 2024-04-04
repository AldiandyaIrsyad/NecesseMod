package necesse.engine.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class GameRegistry<T extends IDDataContainer> {
   public static final Pattern stringIDPattern = Pattern.compile("[a-zA-Z0-9_\\-]+");
   protected final String objectCallName;
   protected final int maxSize;
   protected final boolean stringIDUnique;
   protected Pattern thisStringIDPattern;
   private final List<T> list;
   private final HashMap<String, Integer> stringIDToIDMap;
   private boolean registrationOpen;

   public static boolean validStringID(String var0) {
      return stringIDPattern.matcher(var0).matches();
   }

   protected GameRegistry(String var1, int var2) {
      this(var1, var2, true);
   }

   protected GameRegistry(String var1, int var2, boolean var3) {
      this.thisStringIDPattern = stringIDPattern;
      this.objectCallName = var1;
      this.maxSize = var2;
      this.stringIDUnique = var3;
      this.list = new ArrayList();
      this.stringIDToIDMap = new HashMap();
      this.registrationOpen = true;
   }

   public abstract void registerCore();

   protected final int register(String var1, T var2) {
      return this.registerObj(var1, var2).getIDData().getID();
   }

   protected final <R extends T> R registerObj(String var1, R var2) {
      if (!this.registrationOpen) {
         throw new RegistryClosedException(this.objectCallName + " registration is closed");
      } else if (this.list.size() > this.maxSize) {
         throw new IllegalStateException("Could not register " + this.objectCallName + ", max count reached");
      } else if (this.thisStringIDPattern != null && !this.thisStringIDPattern.matcher(var1).matches()) {
         throw new IllegalArgumentException("Tried to register " + this.objectCallName + " with invalid stringID: \"" + var1 + "\"");
      } else if (this.stringIDUnique && this.getElementID(var1) != -1) {
         throw new IllegalStateException("Tried to register duplicate " + this.objectCallName + " with stringID \"" + var1 + "\"");
      } else {
         int var3 = this.list.size();
         this.list.add(var2);
         this.stringIDToIDMap.put(var1, var3);
         var2.getIDData().setData(var3, var1);
         this.onRegister(var2, var3, var1, false);
         return var2;
      }
   }

   protected final int replace(String var1, T var2) {
      return this.replaceObj(var1, var2).getIDData().getID();
   }

   protected final <R extends T> R replaceObj(String var1, R var2) {
      if (!this.registrationOpen) {
         throw new RegistryClosedException(this.objectCallName + " registration is closed");
      } else {
         int var3 = this.getElementID(var1);
         if (var3 == -1) {
            return this.registerObj(var1, var2);
         } else {
            this.list.set(var3, var2);
            var2.getIDData().setData(var3, var1);
            this.onRegister(var2, var3, var1, true);
            return var2;
         }
      }
   }

   protected abstract void onRegister(T var1, int var2, String var3, boolean var4);

   public final void closeRegistry() {
      this.registrationOpen = false;
      this.onRegistryClose();
   }

   public final boolean isOpen() {
      return this.registrationOpen;
   }

   public final boolean isClosed() {
      return !this.isOpen();
   }

   protected abstract void onRegistryClose();

   protected Iterable<T> getElements() {
      return this.list;
   }

   protected Stream<T> streamElements() {
      return this.list.stream();
   }

   protected T getElement(int var1) {
      try {
         return this.getElementRaw(var1);
      } catch (NoSuchElementException var3) {
         System.err.println("Could not find " + this.objectCallName + " id " + var1 + " in memory (out og bounds)");
         return null;
      }
   }

   protected T getElementRaw(int var1) {
      if (var1 >= 0 && var1 < this.list.size()) {
         return (IDDataContainer)this.list.get(var1);
      } else {
         throw new NoSuchElementException();
      }
   }

   protected T getElement(String var1) {
      int var2 = this.getElementID(var1);
      return var2 == -1 ? null : this.getElement(var2);
   }

   protected int getElementID(String var1) {
      try {
         return this.getElementIDRaw(var1);
      } catch (NoSuchElementException var3) {
         return -1;
      }
   }

   protected int getElementIDRaw(String var1) throws NoSuchElementException {
      Integer var2 = (Integer)this.stringIDToIDMap.get(var1);
      if (var2 == null) {
         throw new NoSuchElementException();
      } else {
         return var2;
      }
   }

   protected String getElementStringID(int var1) {
      return this.getElement(var1).getIDData().getStringID();
   }

   protected int size() {
      return this.list.size();
   }
}
