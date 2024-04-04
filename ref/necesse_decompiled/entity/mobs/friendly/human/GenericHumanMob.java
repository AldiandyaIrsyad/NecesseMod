package necesse.entity.mobs.friendly.human;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameHair;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ShirtArmorItem;
import necesse.inventory.item.armorItem.ShoesArmorItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.light.GameLight;

public class GenericHumanMob extends HumanShop {
   public HumanLook look = new HumanLook();

   public GenericHumanMob() {
      super(500, 200, "generic");
      this.attackCooldown = 500;
      this.attackAnimTime = 500;
      this.setSwimSpeed(1.0F);
      this.equipmentInventory.setItem(6, new InventoryItem("coppersword"));
   }

   public void setSettlerSeed(int var1) {
      this.look = new HumanLook(new GameRandom((long)(var1 + 5)));
      super.setSettlerSeed(var1);
   }

   public HumanMob.HumanGender getHumanGender() {
      return this.convertHairGender(GameHair.getHairGender(this.look.getHair()));
   }

   public LootTable getLootTable() {
      return super.getLootTable();
   }

   public void spawnDeathParticles(float var1, float var2) {
      GameSkin var3 = this.look.getGameSkin(true);

      for(int var4 = 0; var4 < 4; ++var4) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), var3.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7) {
      GameLight var8 = var1.getLightLevel(var2 / 32, var3 / 32);
      int var9 = var5.getDrawX(var2) - 22 - 10;
      int var10 = var5.getDrawY(var3) - 44 - 7;
      InventoryItem var11 = ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.look.getShirtColor());
      InventoryItem var12 = ShoesArmorItem.addColorData(new InventoryItem("shoes"), this.look.getShoesColor());
      Point var13 = this.getAnimSprite(var2, var3, this.dir);
      HumanDrawOptions var14 = (new HumanDrawOptions(var1, this.look, true)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, var11)).boots(this.getDisplayArmor(2, var12)).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var13).dir(this.dir).light(var8);
      var7.accept(var14);
      DrawOptions var15 = var14.pos(var9, var10);
      DrawOptions var16 = this.getMarkerDrawOptions(var2, var3, var8, var5, 0, -45, var6);
      return () -> {
         var15.draw();
         var16.draw();
      };
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.objectUser == null || this.objectUser.object.drawsUsers()) {
         if (this.isVisible()) {
            GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
            int var11 = var8.getDrawX(var5) - 22 - 10;
            int var12 = var8.getDrawY(var6) - 44 - 7;
            InventoryItem var13 = ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.look.getShirtColor());
            InventoryItem var14 = ShoesArmorItem.addColorData(new InventoryItem("shoes"), this.look.getShoesColor());
            Point var15 = this.getAnimSprite(var5, var6, this.dir);
            var12 += this.getBobbing(var5, var6);
            var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
            HumanDrawOptions var16 = (new HumanDrawOptions(var4, this.look, true)).helmet(this.getDisplayArmor(0, (String)null)).chestplate(this.getDisplayArmor(1, var13)).boots(this.getDisplayArmor(2, var14)).invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).sprite(var15).dir(this.dir).light(var10);
            if (this.inLiquid(var5, var6)) {
               var12 -= 10;
               var16.armSprite(2);
               var16.mask(MobRegistry.Textures.boat_mask[var15.y % 4], 0, -7);
            }

            var16 = this.setCustomItemAttackOptions(var16);
            final DrawOptions var17 = var16.pos(var11, var12);
            final TextureDrawOptionsEnd var18 = this.inLiquid(var5, var6) ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, this.dir % 4, 64).light(var10).pos(var11, var12 + 7) : null;
            final DrawOptions var19 = this.getMarkerDrawOptions(var5, var6, var10, var8, 0, -45, var9);
            var1.add(new MobDrawable() {
               public void draw(TickManager var1) {
                  if (var18 != null) {
                     var18.draw();
                  }

                  var17.draw();
                  var19.draw();
               }
            });
            this.addShadowDrawables(var2, var5, var6, var10, var8);
         }
      }
   }

   public QuestMarkerOptions getMarkerOptions(PlayerMob var1) {
      return this.isTravelingHuman() ? new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor) : super.getMarkerOptions(var1);
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      GameTexture var4 = this.look.getGameSkin(true).getBodyTexture();
      GameTexture var5 = this.look.getHairTexture();
      GameTexture var6 = this.look.getBackHairTexture();
      if (var6 != null) {
         var6.initDraw().sprite(0, 2, 64).size(32, 32).draw(var2 - 8 - 8, var3 - 8 - 4);
      }

      var4.initDraw().sprite(0, 9, 32).size(16, 16).draw(var2 - 8, var3 - 8);
      if (var5 != null) {
         var5.initDraw().sprite(0, 2, 64).size(32, 32).draw(var2 - 8 - 8, var3 - 8 - 4);
      }

   }

   public LevelIdentifier getRecruitedToLevel(ServerClient var1) {
      return this.isTravelingHuman() && SettlementLevelData.getSettlementData(this.getLevel()) != null ? this.getLevel().getIdentifier() : null;
   }

   public List<InventoryItem> getRecruitItems(ServerClient var1) {
      if (this.isSettler()) {
         return null;
      } else {
         GameRandom var2 = new GameRandom((long)(this.getSettlerSeed() * 127));
         if (this.isTravelingHuman()) {
            return Collections.singletonList(new InventoryItem("coin", var2.getIntBetween(100, 250)));
         } else {
            LootTable var3 = new LootTable(new LootItemInterface[]{new LootItem("coin", Integer.MAX_VALUE), new CountOfTicketLootItems(1, new Object[]{100, new LootItem("wheat", Integer.MAX_VALUE), 100, new LootItem("wool", Integer.MAX_VALUE), 100, new LootItem("leather", Integer.MAX_VALUE)})});
            int var4 = var2.getIntBetween(100, 250);
            ArrayList var5 = GameLootUtils.getItemsValuedAt(var2, var4, 0.20000000298023224, var3);
            var5.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
            return var5;
         }
      }
   }
}
