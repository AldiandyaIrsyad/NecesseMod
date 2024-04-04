package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.GameDamage;

public class GNDItemGameDamage extends GNDItem {
   public GameDamage damage;

   public GNDItemGameDamage(GameDamage var1) {
      this.damage = var1;
   }

   public GNDItemGameDamage(PacketReader var1) {
      this.readPacket(var1);
   }

   public GNDItemGameDamage(LoadData var1) {
      this.damage = GameDamage.fromLoadData(var1.getFirstLoadDataByName("value"));
   }

   public String toString() {
      return this.damage == null ? "damage{null}" : "damage{" + this.damage.type + ", " + this.damage.damage + ", " + this.damage.armorPen + ", " + this.damage.baseCritChance + ", " + this.damage.playerDamageMultiplier + "}";
   }

   public boolean isDefault() {
      return this.damage == null;
   }

   public boolean equals(GNDItem var1) {
      if (!(var1 instanceof GNDItemGameDamage)) {
         return false;
      } else {
         GNDItemGameDamage var2 = (GNDItemGameDamage)var1;
         return this == var1 || this.damage.equals(var2.damage);
      }
   }

   public GNDItemGameDamage copy() {
      return new GNDItemGameDamage(this.damage);
   }

   public void addSaveData(SaveData var1) {
      SaveData var2 = new SaveData("value");
      this.damage.addSaveData(var2);
      var1.addSaveData(var2);
   }

   public void writePacket(PacketWriter var1) {
      this.damage.writePacket(var1);
   }

   public void readPacket(PacketReader var1) {
      this.damage = GameDamage.fromReader(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem copy() {
      return this.copy();
   }
}
