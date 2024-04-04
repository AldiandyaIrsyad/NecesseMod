package necesse.entity.mobs.hostile.bosses;

import java.util.List;
import necesse.engine.GameDifficulty;
import necesse.engine.tickManager.TickManager;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;

public class NightSwarmStartMob extends FlyingBossMob {
   private int eventUniqueID;

   public NightSwarmStartMob() {
      super((Integer)NightSwarmLevelEvent.BAT_MAX_HEALTH.get(GameDifficulty.CLASSIC) * NightSwarmLevelEvent.START_BAT_COUNT);
      this.shouldSave = false;
   }

   public void init() {
      super.init();
      if (!this.isClient()) {
         NightSwarmLevelEvent var1 = new NightSwarmLevelEvent(this, this.x, this.y);
         this.getLevel().entityManager.addLevelEvent(var1);
         this.eventUniqueID = var1.getUniqueID();
      }

   }

   public LootTable getLootTable() {
      return NightSwarmLevelEvent.lootTable;
   }

   public LootTable getPrivateLootTable() {
      return NightSwarmLevelEvent.privateLootTable;
   }

   public void serverTick() {
      super.serverTick();
      LevelEvent var1 = this.getLevel().entityManager.getLevelEvent(this.eventUniqueID, false);
      if (!(var1 instanceof NightSwarmLevelEvent)) {
         this.remove();
      }

   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
   }

   public boolean isHealthBarVisible() {
      return false;
   }

   public boolean canTakeDamage() {
      return false;
   }

   public boolean countDamageDealt() {
      return false;
   }
}
