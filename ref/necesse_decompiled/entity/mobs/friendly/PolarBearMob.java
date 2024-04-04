package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PolarBearMob extends FriendlyMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("icefish", 2, 5), new LootItem("polarclaw")});
   private HashSet<Mob> targets = new HashSet();
   public final BooleanMobAbility setHostileAbility;

   public PolarBearMob() {
      super(1000);
      this.setArmor(40);
      this.updateStats();
      this.setFriction(3.0F);
      this.setKnockbackModifier(0.2F);
      this.prioritizeVerticalDir = false;
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-20, -16, 40, 32);
      this.selectBox = new Rectangle(-20, -50, 40, 55);
      this.setHostileAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            PolarBearMob.this.isHostile = var1;
            PolarBearMob.this.updateStats();
         }
      });
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.isHostile = var1.getNextBoolean();
      this.updateStats();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.isHostile);
   }

   public void updateStats() {
      this.setSpeed(this.isHostile ? 40.0F : 10.0F);
      this.setCombatRegen(this.isHostile ? 0.0F : 10.0F);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionChaserWandererAI<PolarBearMob>((Supplier)null, 480, new GameDamage(50.0F), 100, 40000) {
         public GameAreaStream<Mob> streamPossibleTargets(PolarBearMob var1, Point var2, TargetFinderDistance<PolarBearMob> var3) {
            return var3.streamMobsAndPlayersInRange(var2, var1).filter((var1x) -> {
               return PolarBearMob.this.targets.contains(var1x);
            });
         }

         // $FF: synthetic method
         // $FF: bridge method
         public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
            return this.streamPossibleTargets((PolarBearMob)var1, var2, var3);
         }
      });
   }

   public void serverTick() {
      super.serverTick();
      this.targets.removeIf((var1) -> {
         return var1.removed() || !var1.isSamePlace(this) || var1.getDistance(this) > 384.0F;
      });
      this.setHostile(!this.targets.isEmpty());
   }

   public MobWasHitEvent isServerHit(GameDamage var1, float var2, float var3, float var4, Attacker var5) {
      MobWasHitEvent var6 = super.isServerHit(var1, var2, var3, var4, var5);
      if (var6 != null && !var6.wasPrevented) {
         Mob var7 = var5.getAttackOwner();
         if (var7 != null) {
            this.targets.add(var7);
         }
      }

      return var6;
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("polar", 3);
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.polarBear, var3, 16, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 64;
      int var12 = var8.getDrawY(var6) - 128 + 36;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.polarBear.initDraw().sprite(var13.x, var13.y, 128).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.polarBear_shadow;
      int var6 = var4.getDrawX(var1) - 22 - 10;
      int var7 = var4.getDrawY(var2) - 44 - 8;
      var7 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(0, this.dir, 64).light(var3).pos(var6, var7);
   }

   public int getRockSpeed() {
      return 10;
   }

   public void setHostile(boolean var1) {
      if (this.getLevel() != null && this.getLevel().getServer() != null) {
         if (this.isHostile != var1) {
            this.setHostileAbility.runAndSend(var1);
         }
      }
   }
}
