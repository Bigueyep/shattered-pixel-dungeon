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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

//the Mirror Shade's throwable consumable, a puff of mirror silvering that blinds enemies
public class TainDust extends Item {

	public static final float BLIND_DURATION = 5f;

	{
		image = ItemSpriteSheet.TAIN_DUST;

		stackable = true;
		defaultAction = AC_THROW;
		usesTargeting = true;

		bones = false;
	}

	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.level.pit[cell] || !Dungeon.level.passable[cell]){
			super.onThrow( cell );
			return;
		}

		Sample.INSTANCE.play( Assets.Sounds.PUFF );
		for (int i : PathFinder.NEIGHBOURS9){
			int c = cell + i;
			if (c < 0 || c >= Dungeon.level.length() || Dungeon.level.solid[c]) continue;

			GameScene.add( Blob.seed( c, 8, SmokeScreen.class ) );
			if (Dungeon.level.heroFOV[c]) {
				CellEmitter.get( c ).burst( Speck.factory( Speck.SMOKE ), 3 );
			}

			Char ch = Actor.findChar( c );
			if (ch != null && ch.alignment == Char.Alignment.ENEMY){
				Buff.prolong( ch, Blindness.class, BLIND_DURATION );
			}
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 15 * quantity;
	}
}
