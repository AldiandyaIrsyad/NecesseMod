package necesse.entity.mobs.hostile;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class HostileMob extends AttackAnimMob {
   public static LootItemInterface randomMapDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.05F, "mapfragment")});
   public static LootItemInterface randomPrivatePortalDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItemList(0.02F, new LootItemInterface[]{new ConditionLootItem("mysteriousportal", (var0, var1) -> {
      Mob var2 = (Mob)LootTable.expectExtra(Mob.class, var1, 0);
      ServerClient var3 = (ServerClient)LootTable.expectExtra(ServerClient.class, var1, 1);
      return var2 != null && var2.getLevel().isCave && var3 != null && var3.playerMob.getInv().getAmount(ItemRegistry.getItem("mysteriousportal"), false, true, true, "have") == 0 && var3.characterStats().mob_kills.getKills("evilsprotector") == 0;
   })})});

   public HostileMob(int var1) {
      super(var1);
      this.isHostile = true;
      this.setTeam(-2);
      this.canDespawn = true;
   }

   public boolean shouldSave() {
      return this.shouldSave && !this.canDespawn();
   }

   public boolean isValidSpawnLocation(Server var1, ServerClient var2, int var3, int var4) {
      return (new MobSpawnLocation(this, var3, var4)).checkLightThreshold(var2).checkMobSpawnLocation().checkMaxHostilesAround(4, 8, var2).validAndApply();
   }

   public float getOutgoingDamageModifier() {
      float var1 = super.getOutgoingDamageModifier();
      if (this.getLevel() != null) {
         var1 *= (Float)this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_DAMAGE);
      }

      return var1;
   }

   public float getSpeedModifier() {
      float var1 = super.getSpeedModifier();
      if (this.getLevel() != null) {
         var1 *= (Float)this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_SPEED);
      }

      return var1;
   }

   public float getMaxHealthModifier() {
      float var1 = super.getMaxHealthModifier();
      if (this.getLevel() != null) {
         var1 *= (Float)this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_MAX_HEALTH);
      }

      return var1;
   }
}
