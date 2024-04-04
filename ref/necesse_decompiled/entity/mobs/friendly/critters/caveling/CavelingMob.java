package necesse.entity.mobs.friendly.critters.caveling;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CritterRunAINode;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SingleRockSmall;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.light.GameLight;

public class CavelingMob extends CritterMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemInterface() {
      public void addPossibleLoot(LootList var1, Object... var2) {
         CavelingMob var3 = (CavelingMob)LootTable.expectExtra(CavelingMob.class, var2, 0);
         if (var3 != null && var3.item != null) {
            var1.addCustom(var3.item);
         }

      }

      public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
         CavelingMob var5 = (CavelingMob)LootTable.expectExtra(CavelingMob.class, var4, 0);
         if (var5 != null && var5.item != null) {
            if (var5.preventLootMultiplier) {
               var3 = 1.0F;
            }

            int var6 = LootTable.getLootAmount(var2, var5.item.getAmount(), var3);
            if (var6 > 0) {
               int var7 = Math.min(10, var6);
               int var8 = var6 / var7;
               int var9 = var6 - var8 * var7;

               for(int var10 = 0; var10 < var7; ++var10) {
                  int var11 = var8 + (var10 < var9 ? 1 : 0);
                  var1.add(var5.item.copy(var11));
               }
            }
         }

      }
   }});
   public HumanTexture texture;
   public Color popParticleColor;
   public String singleRockSmallStringID;
   public boolean isRock = true;
   public InventoryItem item;
   public boolean preventLootMultiplier;

   public CavelingMob(int var1, int var2) {
      super(var1);
      this.setSpeed((float)var2);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-16, -40, 32, 50);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.item != null) {
         SaveData var2 = new SaveData("item");
         this.item.addSaveData(var2);
         var1.addSaveData(var2);
      }

      var1.addBoolean("preventLootMultiplier", this.preventLootMultiplier);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.item = InventoryItem.fromLoadData(var1.getFirstLoadDataByName("item"));
      this.preventLootMultiplier = var1.getBoolean("preventLootMultiplier", this.preventLootMultiplier, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      InventoryItem.addPacketContent(this.item, var1);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.item = InventoryItem.fromContentPacket(var1);
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CritterRunAINode());
   }

   public void clientTick() {
      super.clientTick();
      this.tickIsRock();
   }

   public void serverTick() {
      super.serverTick();
      this.tickIsRock();
   }

   public void tickIsRock() {
      boolean var1 = !this.isAccelerating() && this.dx == 0.0F && this.dy == 0.0F;
      if (this.isRock != var1) {
         this.isRock = var1;
         if (this.isClient() && this.popParticleColor != null) {
            for(int var2 = 0; var2 < 20; ++var2) {
               int var3 = GameRandom.globalRandom.getIntBetween(2, 10);
               this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 8.0F, this.y - 4.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, Particle.GType.IMPORTANT_COSMETIC).movesFriction(GameRandom.globalRandom.floatGaussian() * 20.0F, GameRandom.globalRandom.floatGaussian() * 16.0F, 2.0F).color(this.popParticleColor).heightMoves((float)var3, (float)(var3 + 20)).lifeTime(1000);
            }
         }
      }

   }

   public void spawnDeathParticles(float var1, float var2) {
      HumanTexture var3 = this.texture != null ? this.texture : MobRegistry.Textures.stoneCaveling;

      for(int var4 = 0; var4 < 6; ++var4) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), var3.body, var4, 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 48;
      HumanTexture var13 = this.texture != null ? this.texture : MobRegistry.Textures.stoneCaveling;
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      boolean var14 = var9 != null && (Boolean)var9.buffManager.getModifier(BuffModifiers.SPELUNKER);
      boolean var15 = false;
      final TextureDrawOptionsEnd var19;
      if (this.dx == 0.0F && this.dy == 0.0F && this.singleRockSmallStringID != null) {
         GameObject var16 = ObjectRegistry.getObject(this.singleRockSmallStringID);
         if (var16 instanceof SingleRockSmall) {
            GameTexture var17 = ((SingleRockSmall)var16).texture;
            int var18 = (new GameRandom((long)this.getUniqueID())).nextInt(var17.getWidth() / 32);
            var19 = var17.initDraw().sprite(var18, 0, 32, var17.getHeight()).spelunkerLight(var10, var14, (long)this.getID(), this).pos(var11 + 16, var12 + 16 - var17.getHeight() + 32);
            var15 = true;
            var1.add(new MobDrawable() {
               public void draw(TickManager var1) {
                  var19.draw();
               }
            });
         }
      }

      if (!var15) {
         Point var23 = this.getAnimSprite(var5, var6, this.dir);
         var12 += this.getBobbing(var5, var6);
         final TextureDrawOptionsEnd var24 = var13.rightArms.initDraw().sprite(var23.x, var23.y, 64).spelunkerLight(var10, var14, (long)this.getID(), this).pos(var11, var12);
         final TextureDrawOptionsEnd var25 = var13.body.initDraw().sprite(var23.x, var23.y, 64).spelunkerLight(var10, var14, (long)this.getID(), this).pos(var11, var12);
         if (this.item == null) {
            var19 = null;
         } else {
            Color var20 = this.item.item.getDrawColor(this.item, var9);
            int var21 = var23.x != 1 && var23.x != 3 ? 0 : 2;
            GameLight var22 = var14 ? var10.minLevelCopy(100.0F) : var10;
            var19 = this.item.item.getItemSprite(this.item, var9).initDraw().colorLight(var20, var22).mirror(var23.y < 2, false).size(32).posMiddle(var11 + 32, var12 + 16 + var21);
         }

         final TextureDrawOptionsEnd var26 = var13.leftArms.initDraw().sprite(var23.x, var23.y, 64).spelunkerLight(var10, var14, (long)this.getID(), this).pos(var11, var12);
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               var24.draw();
               var25.draw();
               if (var19 != null) {
                  var19.draw();
               }

               var26.draw();
            }
         });
         TextureDrawOptionsEnd var27 = MobRegistry.Textures.caveling_shadow.initDraw().sprite(var23.x, var23.y, 64).light(var10).pos(var11, var12);
         var2.add((var1x) -> {
            var27.draw();
         });
      }

   }

   public int getRockSpeed() {
      return 10;
   }

   public boolean isLavaImmune() {
      return true;
   }

   public boolean isSlimeImmune() {
      return true;
   }

   public int getTileWanderPriority(TilePosition var1) {
      return 0;
   }

   public MobSpawnLocation checkSpawnLocation(MobSpawnLocation var1) {
      return var1.checkNotSolidTile().checkNotOnSurfaceInsideOnFloor().checkNotLevelCollides().checkTile((var1x, var2) -> {
         return this.getLevel().getLightLevel(var1x, var2).getLevel() <= 50.0F;
      });
   }
}
