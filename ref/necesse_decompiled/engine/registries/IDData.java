package necesse.engine.registries;

public class IDData {
   private boolean isLocked = false;
   private int ID = -1;
   private String stringID = null;

   public IDData() {
   }

   public void setData(int var1, String var2) {
      if (this.isLocked) {
         throw new IllegalStateException("Cannot set ID data twice");
      } else {
         this.ID = var1;
         this.stringID = var2;
         this.isLocked = true;
      }
   }

   public int getID() {
      if (!this.isLocked) {
         throw new IllegalStateException("Data ID has not been set");
      } else {
         return this.ID;
      }
   }

   public String getStringID() {
      if (!this.isLocked) {
         throw new IllegalStateException("Data stringID has not been set");
      } else {
         return this.stringID;
      }
   }

   public boolean isSet() {
      return this.isLocked;
   }
}
