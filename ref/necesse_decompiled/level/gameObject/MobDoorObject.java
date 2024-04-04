package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public class MobDoorObject extends SwitchObject {
   private ArrayList<Mob> originalMobs;
   private ArrayList<Mob> remainingMobs;
   private int[] spawnZone;
   private boolean arenaHasStarted = false;

   public MobDoorObject(Rectangle var1, int var2, ArrayList<Mob> var3) {
      super(var1, var2, false);
      this.originalMobs = var3;
      this.remainingMobs = var3;
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         if (!this.isSwitched && !this.arenaHasStarted && var4 == 0 && this.spawnZone != null) {
            this.setRemainingMobs(this.originalMobs);

            for(int var6 = 0; var6 < this.originalMobs.size(); ++var6) {
            }

            this.arenaHasStarted = true;
         } else if (this.isSwitched && var4 != 0) {
            this.isSwitched = false;
         }

         this.checkSwitchConditions();
      }

   }

   public void checkSwitchConditions() {
      if (this.remainingMobs.isEmpty()) {
         this.isSwitched = true;
         this.spawnZone = null;
      } else {
         this.arenaHasStarted = false;
      }

   }

   public void setRemainingMobs(ArrayList<Mob> var1) {
      this.remainingMobs = var1;
   }

   public void setSpawnZone(int var1, int var2, int var3, int var4) {
      this.spawnZone = new int[]{var1, var2, var3, var4};
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return false;
   }
}
