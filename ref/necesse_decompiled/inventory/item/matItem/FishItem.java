package necesse.inventory.item.matItem;

import necesse.entity.particle.CirclingFishParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class FishItem extends MatItem implements FishItemInterface {
   public GameTexture circlingFishTexture;

   public FishItem(int var1, String... var2) {
      super(var1, var2);
   }

   public FishItem(int var1, Item.Rarity var2, String... var3) {
      super(var1, var2, var3);
   }

   public FishItem(int var1, Item.Rarity var2, String var3) {
      super(var1, var2, var3);
   }

   public FishItem(int var1, Item.Rarity var2, String var3, String... var4) {
      super(var1, var2, var3, var4);
   }

   public void loadTextures() {
      super.loadTextures();
      this.circlingFishTexture = GameTexture.fromFile("particles/circlingfish");
   }

   public Particle getParticle(Level var1, int var2, int var3, int var4) {
      return new CirclingFishParticle(var1, (float)var2, (float)var3, (long)var4, this.circlingFishTexture, 60);
   }
}
