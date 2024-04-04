package necesse.gfx.forms.presets.debug;

import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormBuffList;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class DebugBuffsForm extends Form {
   public FormBuffList buffList;
   public FormTextInput buffFilter;
   private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
   private int duration = 10;

   public DebugBuffsForm(String var1, final DebugForm var2) {
      super((String)var1, 240, 400);
      this.addComponent(new FormLabel("Buffs", new FontOptions(20), 0, this.getWidth() / 2, 10));
      this.buffList = (FormBuffList)this.addComponent(new FormBuffList(0, 36, this.getWidth(), this.getHeight() - 140) {
         public void onClicked(final Buff var1) {
            MouseDebugGameTool var2x = new MouseDebugGameTool(var2, (String)null) {
               public void init() {
                  this.onLeftClick((var2x) -> {
                     int var3 = this.getMouseX();
                     int var4 = this.getMouseY();
                     Iterator var5 = this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(var3 / 32, var4 / 32, 1).iterator();

                     Mob var6;
                     do {
                        if (!var5.hasNext()) {
                           GameUtils.streamClientClients(this.parent.client.getLevel()).filter((var2xx) -> {
                              return var2xx.playerMob.getSelectBox().contains(var3, var4);
                           }).findFirst().ifPresent((var2xx) -> {
                              Object var3;
                              for(var3 = var2xx.playerMob; ((Mob)var3).isRiding() && ((Mob)var3).getMount() != null; var3 = ((Mob)var3).getMount()) {
                              }

                              ((Mob)var3).addBuff(new ActiveBuff(var1, (Mob)var3, (float)DebugBuffsForm.this.duration, (Attacker)null), true);
                           });
                           return true;
                        }

                        var6 = (Mob)var5.next();
                     } while(!var6.getSelectBox().contains(var3, var4));

                     while(var6.isRiding() && var6.getMount() != null) {
                        var6 = var6.getMount();
                     }

                     var6.addBuff(new ActiveBuff(var1, var6, (float)DebugBuffsForm.this.duration, (Attacker)null), true);
                     return true;
                  }, "Give buff");
                  this.onRightClick((var2x) -> {
                     int var3 = this.getMouseX();
                     int var4 = this.getMouseY();
                     Iterator var5 = this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(var3 / 32, var4 / 32, 1).iterator();

                     Mob var6;
                     do {
                        if (!var5.hasNext()) {
                           GameUtils.streamClientClients(this.parent.client.getLevel()).filter((var2xx) -> {
                              return var2xx.playerMob.getSelectBox().contains(var3, var4);
                           }).findFirst().ifPresent((var1x) -> {
                              Object var2x;
                              for(var2x = var1x.playerMob; ((Mob)var2x).isRiding() && ((Mob)var2x).getMount() != null; var2x = ((Mob)var2x).getMount()) {
                              }

                              ((Mob)var2x).buffManager.removeBuff(var1.getID(), true);
                           });
                           return true;
                        }

                        var6 = (Mob)var5.next();
                     } while(!var6.getSelectBox().contains(var3, var4));

                     if (var6.isRiding() && var6.getMount() != null) {
                        var6 = var6.getMount();
                     }

                     var6.buffManager.removeBuff(var1.getID(), true);
                     return true;
                  }, "Remove buff");
                  this.onScroll((var1x) -> {
                     DebugBuffsForm.this.wheelBuffer.add(var1x);
                     DebugBuffsForm.this.wheelBuffer.useScrollY((var1xx) -> {
                        short var2x;
                        if (DebugBuffsForm.this.duration >= 600) {
                           var2x = 600;
                        } else if (DebugBuffsForm.this.duration >= 60) {
                           var2x = 60;
                        } else if (DebugBuffsForm.this.duration >= 10) {
                           var2x = 10;
                        } else {
                           var2x = 1;
                        }

                        if (var1xx) {
                           DebugBuffsForm.this.duration = Math.min(3600, DebugBuffsForm.this.duration + var2x);
                        } else {
                           DebugBuffsForm.this.duration = Math.max(1, DebugBuffsForm.this.duration - var2x);
                        }

                     });
                     return true;
                  }, "Change duration");
               }

               public GameTooltips getTooltips() {
                  ListGameTooltips var1x = new ListGameTooltips(super.getTooltips());
                  var1x.add("Duration: " + GameUtils.formatSeconds((long)DebugBuffsForm.this.duration));
                  return var1x;
               }
            };
            Screen.clearGameTools(var2);
            Screen.setGameTool(var2x, var2);
         }
      });
      this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, 302));
      this.buffFilter = (FormTextInput)this.addComponent(new FormTextInput(0, 320, FormInputSize.SIZE_32_TO_40, this.getWidth(), -1));
      this.buffFilter.placeHolder = new StaticMessage("Search filter");
      this.buffFilter.rightClickToClear = true;
      this.buffFilter.onChange((var1x) -> {
         this.buffList.setFilter(((FormTextInput)var1x.from).getText());
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth()))).onClicked((var1x) -> {
         var2.makeCurrent(var2.mainMenu);
      });
   }
}
