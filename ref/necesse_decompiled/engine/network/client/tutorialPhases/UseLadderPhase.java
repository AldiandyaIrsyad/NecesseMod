package necesse.engine.network.client.tutorialPhases;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.AreaFinder;
import necesse.engine.control.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class UseLadderPhase extends TutorialPhase {
   private Point ladderCoord;
   private long findLadderCooldown;
   private LocalMessage craftLadder;
   private LocalMessage useLadder;
   private HudDrawElement drawElement;

   public UseLadderPhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void start() {
      super.start();
      this.findLadderCooldown = 0L;
      this.ladderCoord = null;
      this.craftLadder = new LocalMessage("tutorials", "craftladderfloat");
      this.useLadder = new LocalMessage("tutorials", "useladderfloat");
      this.drawElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, final PlayerMob var3) {
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return Integer.MAX_VALUE;
               }

               public void draw(TickManager var1) {
                  Point var3x = UseLadderPhase.this.ladderCoord;
                  DrawOptions var2x;
                  FairTypeDrawOptions var4;
                  if (var3x != null) {
                     var4 = UseLadderPhase.this.getTextDrawOptions(UseLadderPhase.this.getTutorialText(UseLadderPhase.this.useLadder.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE2)));
                     var2x = UseLadderPhase.this.getLevelTextDrawOptions(var4, var3x.x * 32 + 16, var3x.y * 32 + 8, var2, var3, true);
                  } else {
                     var4 = UseLadderPhase.this.getTextDrawOptions(UseLadderPhase.this.getTutorialText(UseLadderPhase.this.craftLadder.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE1)));
                     var2x = UseLadderPhase.this.getLevelTextDrawOptions(var4, var3.getX(), var3.getY() - 50, var2, var3, false);
                  }

                  var2x.draw();
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
      this.findLadder();
      if (this.ladderCoord == null) {
         this.setObjective(var1, "craftladder");
      } else {
         this.setObjective(var1, "useladder");
      }

   }

   public void tick() {
      if (this.client.getLevel().getIslandDimension() < 0) {
         this.over();
      } else {
         this.findLadder();
      }
   }

   private void findLadder() {
      if (this.findLadderCooldown <= this.client.worldEntity.getLocalTime()) {
         if (this.client.getLevel() != null) {
            PlayerMob var1 = this.client.getPlayer();
            if (var1 != null) {
               this.ladderCoord = null;
               final AtomicBoolean var2 = new AtomicBoolean();
               (new AreaFinder(var1, 40) {
                  public boolean checkPoint(int var1, int var2x) {
                     if (UseLadderPhase.this.client.getLevel().getTileID(var1, var2x) == TileRegistry.emptyID) {
                        var2.set(true);
                        return true;
                     } else if (UseLadderPhase.this.isLadder(var1, var2x)) {
                        UseLadderPhase.this.ladderCoord = new Point(var1, var2x);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }).runFinder();
               this.findLadderCooldown = this.client.worldEntity.getLocalTime() + (long)(var2.get() ? 1000 : 10000);
            }
         }
      }
   }

   private boolean isLadder(int var1, int var2) {
      return this.client.getLevel().getObject(var1, var2).getStringID().equals("ladderdown");
   }
}
