package necesse.entity.objectEntity;

import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;

public class SpiderEggObjectEntity extends ObjectEntity {
   private final String[] mobList = new String[]{"webspinner", "bloatedspider", "spiderkin"};
   public boolean isBroken = false;

   public SpiderEggObjectEntity(Level var1, int var2, int var3) {
      super(var1, "spideregg", var2, var3);
   }

   public void breakEgg() {
      if (!this.isBroken) {
         this.spawnContainedMob(this.getLevel());
         this.isBroken = true;
      }

   }

   private void spawnContainedMob(Level var1) {
      for(int var2 = 0; var2 < GameRandom.globalRandom.getIntBetween(3, 5); ++var2) {
         String var3 = (String)GameRandom.globalRandom.getOneOf((Object[])this.mobList);
         var1.entityManager.addMob(MobRegistry.getMob(var3, var1), (float)(this.getX() * 32 + GameRandom.globalRandom.getIntBetween(8, 24)), (float)(this.getY() * 32 + GameRandom.globalRandom.getIntBetween(8, 24)));
      }

   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addBoolean("isBroken", this.isBroken);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.isBroken = var1.getBoolean("isBroken", false);
   }
}
