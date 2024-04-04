package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public abstract class RandomBreakObject extends GameObject {
   private final GameRandom drawRandom;
   public String texturePath;
   public GameTexture objectTexture;
   public GameTexture debrisTexture;
   public boolean spawnsDebris;
   public boolean countAsCratesBroken;
   public boolean lightUpAsTreasure;

   public RandomBreakObject(Rectangle var1, String var2, Color var3, boolean var4) {
      super(var1);
      this.countAsCratesBroken = true;
      this.lightUpAsTreasure = true;
      this.texturePath = var2;
      this.mapColor = var3;
      this.spawnsDebris = var4;
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.attackThrough = true;
      this.drawRandom = new GameRandom();
      this.canPlaceOnProtectedLevels = true;
   }

   public RandomBreakObject(Rectangle var1, String var2, Color var3) {
      this(var1, var2, var3, true);
   }

   public void loadTextures() {
      super.loadTextures();
      if (this.objectTexture == null) {
         this.objectTexture = GameTexture.fromFile("objects/" + this.texturePath);
      }

      if (this.spawnsDebris && this.debrisTexture == null) {
         this.debrisTexture = GameTexture.fromFile("objects/" + this.texturePath + "debris");
      }

   }

   public LootTable getLootTable(final Level var1, final int var2, final int var3) {
      return new LootTable(new LootItemInterface[]{new LootItemInterface() {
         public void addPossibleLoot(LootList var1x, Object... var2x) {
            RandomBreakObject.this.getBreakLootTable(var1, var2, var3).addPossibleLoot(var1x, var2x);
         }

         public void addItems(List<InventoryItem> var1x, GameRandom var2x, float var3x, Object... var4) {
            GameRandom var5 = new GameRandom(RandomBreakObject.this.getTileSeed(var2, var3, (int)var1.getSeed() + var1.getIslandDimension()));
            RandomBreakObject.this.getBreakLootTable(var1, var2, var3).addItems(var1x, var5, var3x, var4);
         }
      }});
   }

   public abstract LootTable getBreakLootTable(Level var1, int var2, int var3);

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4, Attacker var5) {
      super.attackThrough(var1, var2, var3, var4, var5);
      if (this.countAsCratesBroken) {
         Mob var6 = var5 != null ? var5.getAttackOwner() : null;
         if (var6 != null && var6.isPlayer) {
            ((PlayerMob)var6).getServerClient().newStats.crates_broken.increment(1);
         }
      }

   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      this.playDamageSound(var1, var2, var3, true);
   }

   public void spawnDestroyedParticles(Level var1, int var2, int var3) {
      super.spawnDestroyedParticles(var1, var2, var3);
      GameRandom var4 = new GameRandom((long)GameRandom.globalRandom.nextInt(32767));
      GameTextureSection[] var5 = this.getDebrisSprites(new GameRandom(), var2, var3);
      if (var5 != null) {
         for(int var6 = 0; var6 < 4; ++var6) {
            var1.entityManager.addParticle((Particle)(new FleshParticle(var1, var5[var4.nextInt(var5.length)], (float)(var2 * 32 + 16), (float)(var3 * 32 + 16), 10.0F, 0.0F, 0.0F)), Particle.GType.IMPORTANT_COSMETIC);
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      GameTextureSection var12;
      boolean var13;
      synchronized(this.drawRandom) {
         var12 = this.getSprite(this.drawRandom, var4, var5);
         var13 = this.drawRandom.seeded(this.getTileSeed(var4, var5, 6199)).nextBoolean();
      }

      boolean var14 = var8 != null && this.lightUpAsTreasure && (Boolean)var8.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER);
      final SharedTextureDrawOptions var15 = new SharedTextureDrawOptions(var12.getTexture());
      var11 -= var12.getHeight() - 32;
      var15.add(var12).mirror(var13, false).spelunkerLight(var9, var14, (long)this.getID(), var3).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var15.draw();
         }
      });
   }

   protected int getSprite(GameRandom var1, int var2, int var3, int var4) {
      return var1.seeded(this.getTileSeed(var2, var3)).nextInt(var4);
   }

   public GameTextureSection getSprite(GameRandom var1, int var2, int var3) {
      int var4 = this.objectTexture.getWidth() / 32;
      int var5 = this.getSprite(var1, var2, var3, var4);
      return (new GameTextureSection(this.objectTexture)).sprite(var5, 0, 32, this.objectTexture.getHeight());
   }

   public GameTextureSection[] getDebrisSprites(GameRandom var1, int var2, int var3) {
      GameTextureSection var4 = this.getSprite(var1, var2, var3);
      if (!this.spawnsDebris) {
         return null;
      } else {
         int var5 = this.debrisTexture.getHeight() / 32;
         GameTextureSection[] var6 = new GameTextureSection[var5];

         for(int var7 = 0; var7 < var5; ++var7) {
            int var8 = var7 * 32;
            var6[var7] = new GameTextureSection(this.debrisTexture, var4.getStartX(), var4.getEndX(), var8, var8 + 32);
         }

         return var6;
      }
   }

   public long getTreasureHash() {
      return (long)this.getID();
   }
}
