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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MirrorCracks;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

//the Mirror Shade's starting weapon, a quick shard of black glass
public class ObsidianShard extends MeleeWeapon {

	{
		image = ItemSpriteSheet.OBSIDIAN_SHARD;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.2f;

		tier = 1;
		DLY = 0.8f; //1.25x speed

		bones = false;
	}

	@Override
	public int max(int lvl) {
		return  3*(tier+1) +    //6 base, down from 10
				lvl*(tier+1);   //scaling unchanged
	}

	public static float crackChance( int lvl ){
		//25% chance, +5% per level, to a max of 50%
		return Math.min(0.5f, 0.25f + 0.05f*lvl);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Float() < crackChance(buffedLvl())){
			MirrorCracks.gain(attacker, 1);
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String statsInfo() {
		int chance = Math.round(100*crackChance(isIdentified() ? buffedLvl() : 0));
		return Messages.get(this, "stats_desc", chance);
	}
}
