package necesse.engine.network.client.tutorialPhases;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.AreaFinder;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.hudManager.HudDrawElement;

public class InteractSettlementFlagPhase extends TutorialPhase {
   private boolean settlementActive;
   private Point chestCoord;
   private long findFlagCooldown;
   private HudDrawElement drawElement;

   public InteractSettlementFlagPhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void start() {
      super.start();
      this.chestCoord = null;
      this.findFlagCooldown = 0L;
      this.drawElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, final PlayerMob var3) {
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return Integer.MAX_VALUE;
               }

               public void draw(TickManager var1) {
                  Point var2x = InteractSettlementFlagPhase.this.chestCoord;
                  DrawOptions var3x = null;
                  FairTypeDrawOptions var4;
                  if (InteractSettlementFlagPhase.this.settlementActive) {
                     var4 = InteractSettlementFlagPhase.this.getTextDrawOptions(InteractSettlementFlagPhase.this.getTutorialText(Localization.translate("tutorials", "useflag")).replaceAll("<key>", new FairControlKeyGlyph(Control.OPEN_SETTLEMENT)));
                     var3x = InteractSettlementFlagPhase.this.getLevelTextDrawOptions(var4, var3.getX(), var3.getY() - 50, var2, var3, false);
                  } else if (var2x != null) {
                     var4 = InteractSettlementFlagPhase.this.getTextDrawOptions(InteractSettlementFlagPhase.this.getTutorialText(Localization.translate("tutorials", "takeflag")));
                     var3x = InteractSettlementFlagPhase.this.getLevelTextDrawOptions(var4, var2x.x * 32 + 16, var2x.y * 32 + 8, var2, var3, true);
                  }

                  if (var3x != null) {
                     var3x.draw();
                  }

               }
            });
         }
      };
      this.client.getLevel().hudManager.addElement(this.drawElement);
   }

   public void end() {
      super.end();
      if (this.drawElement != null) {
         this.drawElement.remove();
      }

      this.drawElement = null;
   }

   public void updateObjective(MainGame var1) {
      this.setObjective(var1, new LocalMessage("tutorials", "settlement", "key", "[input=" + Control.OPEN_SETTLEMENT.id + "]"));
   }

   public void tick() {
      this.settlementActive = this.client.getLevel().settlementLayer.isActive();
      if (!this.settlementActive) {
         this.findFlag();
      }

      Container var1 = this.client.getContainer();
      if (var1 instanceof SettlementContainer) {
         this.over();
      }

   }

   public void findFlag() {
      if (this.findFlagCooldown <= this.client.worldEntity.getLocalTime()) {
         if (this.client.getLevel() != null) {
            PlayerMob var1 = this.client.getPlayer();
            if (var1 != null) {
               this.chestCoord = null;
               final AtomicBoolean var2 = new AtomicBoolean();
               (new AreaFinder(var1, 30) {
                  public boolean checkPoint(int var1, int var2x) {
                     if (InteractSettlementFlagPhase.this.client.getLevel().getTileID(var1, var2x) == TileRegistry.emptyID) {
                        var2.set(true);
                        return true;
                     } else if (InteractSettlementFlagPhase.this.containsSettlementFlag(var1, var2x)) {
                        InteractSettlementFlagPhase.this.chestCoord = new Point(var1, var2x);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }).runFinder();
               this.findFlagCooldown = this.client.worldEntity.getLocalTime() + (long)(var2.get() ? 1000 : 5000);
            }
         }
      }
   }

   public boolean containsSettlementFlag(int var1, int var2) {
      ObjectEntity var3 = this.client.getLevel().entityManager.getObjectEntity(var1, var2);
      if (var3 instanceof OEInventory) {
         Inventory var4 = ((OEInventory)var3).getInventory();
         if (var4 != null) {
            for(int var5 = 0; var5 < var4.getSize(); ++var5) {
               InventoryItem var6 = var4.getItem(var5);
               if (var6 != null && var6.item.getStringID().equals("settlementflag")) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
