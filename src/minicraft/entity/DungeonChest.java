package minicraft.entity;

import java.util.Random;
import minicraft.Game;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.item.ResourceItem;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.item.resource.Resource;
import minicraft.screen.ModeMenu;

public class DungeonChest extends Chest {
	private static int openCol = Color.get(-1, 2, 115, 225);
	private static int lockCol = Color.get(-1, 222, 333, 555);
	
	public Random random = new Random();
	public boolean isLocked;
	
	public DungeonChest() {
		super("Dungeon Chest", lockCol);
		populateInv();
		
		isLocked = true;
		//sprite = 1;
		/*col = Color.get();
		col0 = Color.get(-1, 111, 222, 444);
		col1 = Color.get(-1, 222, 333, 555);
		col2 = Color.get(-1, 111, 222, 444);
		col3 = Color.get(-1, 0, 111, 333);
		*/
		//col = lockCol;
	}
	
	public boolean use(Player player, int attackDir) {
		if (isLocked) {
			boolean activeKey = player.activeItem != null && player.activeItem.getName().equals("Key");
			boolean invKey = player.inventory.hasResources(Resource.key, 1);
			if(activeKey || invKey) { // if the player has a key...
				if (!ModeMenu.creative) { // remove the key unless on creative mode.
					if (activeKey) { // remove activeItem
						ResourceItem key = (ResourceItem)player.activeItem;
						key.count--;
					} else { // remove from inv
						player.inventory.removeResource(Resource.key, 1);
					}
				}
				
				isLocked = false;
				col = openCol; // set to the unlocked color
				
				level.add(new SmashParticle(x * 16 + 8, y * 16 + 8));
				level.add(new TextParticle("-1 key", x, y, Color.get(-1, 500, 500, 500)));
				level.chestcount--;
				if(level.chestcount == 0) { // if this was the last chest...
					for(int i = 0; i < 5; i++) { // add 5 golden apples to the level
						level.add(new ItemEntity(new ResourceItem(Resource.goldapple), x, y));
					}
					
					Game.notifications.add("You hear a noise from the surface!"); // notify the player of the developments
					// add a level 2 airwizard to the middle surface level.
					AirWizard wizard = new AirWizard(true);
					wizard.x = Game.levels[3].w / 2;
					wizard.y = Game.levels[3].h / 2;
					Game.levels[3].add(wizard);
				}
				
				return super.use(player, attackDir); // the player unlocked the chest.
			}
			
			return false; // the chest is locked, and the player has no key.
		}
		else return super.use(player, attackDir); // the chest was already unlocked.
	}
	
	/** Populate the inventory of the DungeonChest, psudo-randomly. */
	private void populateInv() {
		inventory.clearInv(); // clear the inventory.
		Inventory inv = inventory; // Yes, I'm that lazy.
		inv.tryAdd(5, Resource.steak, 6);
		inv.tryAdd(5, Resource.cookedpork, 6);
		inv.tryAdd(4, Resource.wood, 20);
		inv.tryAdd(4, Resource.wool, 12);
		inv.tryAdd(2, Resource.coal, 4);
		inv.tryAdd(5, Resource.gem, 7);
		inv.tryAdd(5, Resource.gem, 8);
		inv.tryAdd(8, Resource.gemarmor, 1);
		inv.tryAdd(6, Resource.garmor, 1);
		inv.tryAdd(5, Resource.iarmor, 2);
		inv.tryAdd(3, Resource.potion, 10);
		inv.tryAdd(4, Resource.speedpotion, 2);
		inv.tryAdd(6, Resource.speedpotion, 5);
		inv.tryAdd(3, Resource.lightpotion, 2);
		inv.tryAdd(4, Resource.lightpotion, 3);
		inv.tryAdd(7, Resource.regenpotion, 1);
		inv.tryAdd(7, Resource.energypotion, 1);
		inv.tryAdd(14, Resource.timepotion, 1);
		inv.tryAdd(14, Resource.shieldpotion, 1);
		inv.tryAdd(7, Resource.lavapotion, 1);
		inv.tryAdd(5, Resource.hastepotion, 3);
		
		inv.tryAdd(6, ToolType.bow, 3);
		inv.tryAdd(7, ToolType.bow, 4);
		inv.tryAdd(4, ToolType.sword, 3);
		inv.tryAdd(7, ToolType.sword, 4);
		inv.tryAdd(4, ToolType.claymore, 1);
		inv.tryAdd(6, ToolType.claymore, 2);
		
		if(inventory.invSize() < 1) { // add this if none of the above was added.
			inventory.add(new ResourceItem(Resource.steak, 6));
			inventory.add(new ResourceItem(Resource.timepotion, 1));
			inventory.add(new ToolItem(ToolType.axe, 4));
		}
	}
	
	/** what happens if the player tries to push a Dungeon Chest. */
	protected void touchedBy(Entity entity) {
		if(!isLocked) // can only be pushed if unlocked.
			super.touchedBy(entity);
	}
	
	/** what happens if the player tries to grab a Dungeon Chest. */
	public void take(Player player) {
		if(!isLocked) // can only be taken if unlocked.
			super.take(player);
	}
}
