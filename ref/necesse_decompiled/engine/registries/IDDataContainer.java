package necesse.engine.registries;

public interface IDDataContainer {
   IDData getIDData();

   default String getStringID() {
      return this.getIDData().getStringID();
   }

   default int getID() {
      return this.getIDData().getID();
   }
}
