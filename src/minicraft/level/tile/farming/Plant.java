package minicraft.level.tile.farming;

import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Items;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class Plant extends FarmTile {
    protected static final int maxAge = 100;
    private String name;

    protected Plant(String name) {
        super(name, (Sprite)null);
        this.name = name;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        int age = level.getData(x, y);
        int icon = age / (maxAge / 5);

        Tiles.get("Farmland").render(screen, level, x, y);

        screen.render(x * 16 + 0, y * 16 + 0, 13 + 0 * 32 + icon, 0, 1);
        screen.render(x * 16 + 8, y * 16 + 0, 13 + 0 * 32 + icon, 0, 1);
        screen.render(x * 16 + 0, y * 16 + 8, 13 + 0 * 32 + icon, 1, 1);
        screen.render(x * 16 + 8, y * 16 + 8, 13 + 0 * 32 + icon, 1, 1);
    }

    @Override
    public void steppedOn(Level level, int xt, int yt, Entity entity) {
        super.steppedOn(level, xt, yt, entity);
        harvest(level, xt, yt, entity);
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
        harvest(level, x, y, source);
        return true;
    }

    @Override
    public void tick(Level level, int xt, int yt) {
        if (random.nextInt(2) == 0) return;

        int age = level.getData(xt, yt);
        if (!IfWater(level, xt, yt)) {
            if (age < maxAge) level.setData(xt, yt, age + 1);
        } else if (IfWater(level, xt, yt)) {
            if (age < maxAge) level.setData(xt, yt, age + 2);
        }
    }

    protected boolean IfWater(Level level, int xs, int ys) {
        Tile[] areaTiles = level.getAreaTiles(xs, ys, 1);
        for(Tile t: areaTiles)
            if(t == Tiles.get("Water"))
                return true;

        return false;
    }
    protected void harvest(Level level, int x, int y, Entity entity) {
        int age = level.getData(x, y);

        level.dropItem(x * 16 + 8, y * 16 + 8, 1, Items.get(name + " Seeds"));

        int count = 0;
        if (age >= maxAge) {
            count = random.nextInt(3) + 2;
        } else if (age >= maxAge - maxAge / 5) {
            count = random.nextInt(2) + 1;
        }

        level.dropItem(x*16+8, y*16+8, count, Items.get(name));

        if (age >= maxAge && entity instanceof Player) {
            ((Player)entity).addScore(random.nextInt(5) + 1);
        }

        level.setTile(x, y, Tiles.get("Dirt"));
    }
}
