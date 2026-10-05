/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;

//a staff that teleports the user to a visible spot inside the room they are standing in
public class WandOfTranslocation extends Wand {

	{
		image = ItemSpriteSheet.WAND_TRANSLOCATION;

		//the bolt flies over characters, it only stops at walls or at the chosen cell
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	//outside of regular rooms (boss arenas, caves, etc.) the range is limited instead
	public static int range( int lvl ){
		return 6 + 2*lvl;
	}

	public static int destination( Char user, int target, int lvl ){
		return destination(user, new Ballistica(user.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID), lvl);
	}

	//returns the closest valid cell to the target along the line of fire, or -1 if there is none
	public static int destination( Char user, Ballistica path, int lvl ){

		ArrayList<Room> rooms = currentRooms(user.pos);

		int best = -1;
		for (int i = 1; i <= path.dist && i < path.path.size(); i++){
			int cell = path.path.get(i);
			if (rooms != null) {
				if (!insideAny(rooms, cell)) break;
			} else if (Dungeon.level.distance(user.pos, cell) > range(lvl)) {
				break;
			}
			if (Dungeon.level.passable[cell]
					&& !Dungeon.level.avoid[cell]
					&& Dungeon.level.heroFOV[cell]
					&& Actor.findChar(cell) == null){
				best = cell;
			}
		}
		return best;
	}

	//the rooms the user counts as standing in: every room whose area (walls included) contains them,
	// so doorways work, plus standard rooms merged into those (joined by a wide opening rather than a door).
	// Returns null where the level has no rooms.
	private static ArrayList<Room> currentRooms( int pos ){
		if (!(Dungeon.level instanceof RegularLevel)) return null;

		ArrayList<Room> rooms = ((RegularLevel) Dungeon.level).rooms();
		ArrayList<Room> result = new ArrayList<>();
		for (Room r : rooms){
			if (containsInclusive(r, pos)) result.add(r);
		}
		if (result.isEmpty()) return null;

		//merges are detected from the map, as the room connection graph is not kept in saves
		for (Room r : new ArrayList<>(result)){
			if (!(r instanceof StandardRoom)) continue;
			for (Room n : rooms){
				if (n != r && n instanceof StandardRoom && !result.contains(n) && mergedWith(r, n)){
					result.add(n);
				}
			}
		}
		return result;
	}

	//two rooms are merged if their shared wall has an opening of 2 or more non-door tiles,
	// at least one of them walkable (decorative chasm strips between unconnected rooms don't count)
	private static boolean mergedWith( Room a, Room b ){
		Rect edge = a.intersect(b);
		int openings = 0;
		int walkable = 0;
		if (edge.width() == 0 && edge.height() > 0){
			for (int y = edge.top+1; y < edge.bottom; y++){
				int cell = edge.left + y*Dungeon.level.width();
				if (isOpening(Dungeon.level.map[cell])) openings++;
				if (isOpening(Dungeon.level.map[cell]) && Dungeon.level.passable[cell]) walkable++;
			}
		} else if (edge.height() == 0 && edge.width() > 0){
			for (int x = edge.left+1; x < edge.right; x++){
				int cell = x + edge.top*Dungeon.level.width();
				if (isOpening(Dungeon.level.map[cell])) openings++;
				if (isOpening(Dungeon.level.map[cell]) && Dungeon.level.passable[cell]) walkable++;
			}
		}
		return openings >= 2 && walkable >= 1;
	}

	private static boolean isOpening( int terrain ){
		return (Terrain.flags[terrain] & Terrain.SOLID) == 0 && !isDoor(terrain);
	}

	private static boolean isDoor( int terrain ){
		return terrain == Terrain.DOOR || terrain == Terrain.OPEN_DOOR || terrain == Terrain.LOCKED_DOOR
				|| terrain == Terrain.CRYSTAL_DOOR || terrain == Terrain.SECRET_DOOR || terrain == Terrain.HERO_LKD_DR;
	}

	private static boolean containsInclusive( Room r, int cell ){
		Point p = Dungeon.level.cellToPoint(cell);
		return p.x >= r.left && p.x <= r.right && p.y >= r.top && p.y <= r.bottom;
	}

	private static boolean insideAny( ArrayList<Room> rooms, int cell ){
		for (Room r : rooms){
			if (containsInclusive(r, cell)) return true;
		}
		return false;
	}

	@Override
	public boolean tryToZap( Hero owner, int target ){
		//no charge is used if there is nowhere to land
		if (!cursed && destination(owner, target, buffedLvl()) == -1){
			GLog.w( Messages.get(this, "no_destination") );
			return false;
		}
		return super.tryToZap(owner, target);
	}

	@Override
	public void onZap(Ballistica bolt) {
		//walk the bolt that was actually fired, not a re-traced line to where it stopped
		int dest = destination(curUser, bolt, buffedLvl());
		if (dest == -1){
			GLog.w( Messages.get(this, "no_destination") );
			return;
		}

		ScrollOfTeleportation.appear(curUser, dest);
		Dungeon.level.occupyCell(curUser);
		Dungeon.observe();
		GameScene.updateFog();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		//the battlemage's footing becomes slippery: 1 turn of haste, +1 turn per 3 levels
		int level = Math.max( 0, staff.buffedLvl() );
		if (Random.Int(3) == 0) {
			Buff.prolong(attacker, Haste.class, 1f + level/3);
		}
	}

	@Override
	public String statsDesc() {
		if (levelKnown){
			return Messages.get(this, "stats_desc", range(buffedLvl()));
		} else {
			return Messages.get(this, "stats_desc", range(0));
		}
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0x9B6BFF );
		particle.am = 0.6f;
		particle.setLifespan(0.8f);
		particle.speed.polar( Random.Float(PointF.PI2), 3f );
		particle.setSize( 0.5f, 2f );
		particle.shuffleXY(1f);
	}
}
