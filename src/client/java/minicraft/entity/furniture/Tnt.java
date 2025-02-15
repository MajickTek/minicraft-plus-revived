package minicraft.entity.furniture;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteLinker.LinkedSprite;
import minicraft.gfx.SpriteLinker.SpriteType;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.screen.AchievementsDisplay;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Tnt extends Furniture implements ActionListener {
	private static int FUSE_TIME = 90;
	private static int BLAST_RADIUS = 32;
	private static int BLAST_DAMAGE = 75;

	private int ftik = 0;
	private boolean fuseLit = false;
	private Timer explodeTimer;
	private Level levelSave;

	private final String[] explosionBlacklist = new String[]{ "hard rock", "obsidian wall", "stairs up", "stairs down" };

	/**
	 * Creates a new tnt furniture.
	 */
	public Tnt() {
		super("Tnt", new LinkedSprite(SpriteType.Entity, "tnt"), new LinkedSprite(SpriteType.Item, "tnt"), 3, 2);
		fuseLit = false;
		ftik = 0;

		explodeTimer = new Timer(300, this);
	}

	@Override
	public void tick() {
		super.tick();

		if (fuseLit) {
			ftik++;

			if (ftik >= FUSE_TIME) {
				// Blow up
				List<Entity> entitiesInRange = level.getEntitiesInRect(new Rectangle(x, y, BLAST_RADIUS * 2, BLAST_RADIUS * 2, Rectangle.CENTER_DIMS));

				for (Entity e: entitiesInRange) {
					 float dist = (float) Math.hypot(e.x - x, e.y - y);
					 int dmg = (int) (BLAST_DAMAGE * (1 - (dist / BLAST_RADIUS))) + 1;
					 if (e instanceof Mob && dmg > 0)
						((Mob)e).onExploded(this, dmg);

					 // Ignite other bombs in range.
					 if (e instanceof Tnt) {
						 Tnt tnt = (Tnt) e;
						 if (!tnt.fuseLit) {
							 tnt.fuseLit = true;
							 Sound.play("fuse");
							 tnt.ftik = FUSE_TIME * 2 / 3;
						 }
					 }
				}

				int xt = x >> 4;
				int yt = (y - 2) >> 4;

				// Get the tiles that have been exploded.
				Tile[] affectedTiles = level.getAreaTiles(xt, yt, 1);

				// Call the onExplode() event.
				for (int i = 0; i < affectedTiles.length; i++) {
					// This assumes that range is 1.
					affectedTiles[i].onExplode(level, xt + i % 3 - 1, yt + i / 3 - 1);
				}

				AchievementsDisplay.setAchievement("minicraft.achievement.demolition", true);
				Sound.play("explode");

				level.setAreaTiles(xt, yt, 1, Tiles.get("explode"), 0, explosionBlacklist);

				levelSave = level;
				explodeTimer.start();
				super.remove();
			}
		}
	}

	@Override
	public void render(Screen screen) {
		if (fuseLit) {
			int colFctr = 100 * ((ftik%15)/5) + 200;
			col = Color.get(-1, colFctr, colFctr + 100, 555);
		}
		super.render(screen);
	}

	/**
	 * Does the explosion.
	 */
	public void actionPerformed(ActionEvent e) {
		explodeTimer.stop();
		int xt = x >> 4;
		int yt = (y - 2) >> 4;

		if (levelSave.depth != 1) {
			levelSave.setAreaTiles(xt, yt, 1, Tiles.get("hole"), 0, explosionBlacklist);
		} else {
			levelSave.setAreaTiles(xt, yt, 1, Tiles.get("Infinite Fall"), 0, explosionBlacklist);
		}

		levelSave = null;
	}

	@Override
	public boolean interact(Player player, Item heldItem, Direction attackDir) {
		if (!fuseLit) {
			fuseLit = true;
			Sound.play("fuse");
			return true;
		}

		return false;
	}
}
