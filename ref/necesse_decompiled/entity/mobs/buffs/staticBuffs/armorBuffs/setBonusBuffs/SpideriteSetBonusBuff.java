package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.SpideriteWaveProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.Level;

public class SpideriteSetBonusBuff extends SetBonusBuff implements BuffAbility {
   public FloatUpgradeValue spitDamage = (new FloatUpgradeValue(0.0F, 0.2F)).setBaseValue(48.0F).setUpgradedValue(1.0F, 48.0F);

   public SpideriteSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setMaxModifier(BuffModifiers.SLOW, 0.0F);
   }

   public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      PacketReader var4 = new PacketReader(var3);
      float var5 = 60.0F;
      Level var6 = var1.getLevel();
      float var7 = 160.0F * (Float)var1.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY);
      GameDamage var8 = new GameDamage(this.spitDamage.getValue(this.getUpgradeTier(var2)));
      if (var1.isServer()) {
         SpideriteWaveProjectile var9 = new SpideriteWaveProjectile(var6, var1.x, var1.y, (float)var4.getNextInt(), (float)var4.getNextInt(), var7, 1000, var8, var1);
         var6.entityManager.projectiles.addHidden(var9);
         var9.moveDist(20.0);
         var6.getServer().network.sendToClientsWithEntity(new PacketSpawnProjectile(var9), var9);
      }

      var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDERITE_SET_COOLDOWN, var1, var5, (Attacker)null), false);
   }

   public Packet getAbilityContent(PlayerMob var1, ActiveBuff var2, GameCamera var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextInt(var3.getMouseLevelPosX());
      var5.putNextInt(var3.getMouseLevelPosY());
      return var4;
   }

   public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3) {
      return !var2.owner.buffManager.hasBuff(BuffRegistry.Debuffs.SPIDERITE_SET_COOLDOWN.getID());
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add((String)Localization.translate("itemtooltip", "spideriteset"), 400);
      return var3;
   }
}
