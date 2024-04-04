package necesse.engine.seasons;

import java.util.function.Supplier;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;

public class SeasonalHat {
   public final Supplier<Boolean> isActive;
   public final float mobWearChance;
   public final String itemDropStringID;
   public final float itemDropChance;
   public String textureName;
   public GameTexture texture;

   public SeasonalHat(Supplier<Boolean> var1, float var2, String var3, float var4, String var5) {
      this.isActive = var1;
      this.mobWearChance = var2;
      this.itemDropStringID = var3;
      this.itemDropChance = var4;
      this.textureName = var5;
   }

   protected void loadTextures() {
      this.texture = GameTexture.fromFile("player/armor/" + this.textureName);
   }

   public HumanDrawOptions.HumanDrawOptionsGetter getDrawOptions() {
      return (var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14) -> {
         return this.texture.initDraw().sprite(var3, var4, var5).light(var12).alpha(var13).size(var8, var9).mirror(var10, var11).addShaderTextureFit(var14, 1).pos(var6, var7);
      };
   }

   public LootTable getLootTable(LootTable var1) {
      return this.itemDropStringID != null && this.itemDropChance > 0.0F ? new LootTable(new LootItemInterface[]{var1, new ChanceLootItem(this.itemDropChance, this.itemDropStringID)}) : var1;
   }
}
