package necesse.entity.mobs.buffs.staticBuffs.armorBuffs;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;

public abstract class ArmorBuff extends Buff {
   public ArmorBuff() {
      this.canCancel = false;
      this.isVisible = false;
      this.isPassive = true;
      this.shouldSave = false;
   }

   public int getUpgradeLevel(ActiveBuff var1) {
      return var1.getGndData().getInt("upgradeLevel");
   }

   public float getUpgradeTier(ActiveBuff var1) {
      return (float)this.getUpgradeLevel(var1) / 100.0F;
   }

   public void tickEffect(ActiveBuff var1, Mob var2) {
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      LinkedList var4 = new LinkedList();
      this.addStatTooltips(var4, var1, (ActiveBuff)var2.get(ActiveBuff.class, "compareValues"));
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         ItemStatTip var6 = (ItemStatTip)var5.next();
         var3.add((Object)var6.toTooltip((Color)GameColor.GREEN.color.get(), (Color)GameColor.RED.color.get(), (Color)GameColor.YELLOW.color.get(), false));
      }

      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
   }
}
