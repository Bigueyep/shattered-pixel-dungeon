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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

import java.util.ArrayList;

//stores a portion of the damage the wearer takes, which can be released at once as a laser
public class RingOfVengeance extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_VENGEANCE;
		buffClass = Vengeance.class;

		//only set when a release is actually started, see execute()
		usesTargeting = false;
	}

	public static final String AC_RELEASE = "RELEASE";

	//stored as a float so that small hits still add up
	private float stored = 0;

	//30% of damage taken at +0, +10% per level, up to 100%
	public static float storeFactor( int bonus ){
		return GameMath.gate(0f, 0.2f + 0.1f*bonus, 1f);
	}

	//the ring can hold up to twice the wearer's max HP
	public static int capacity( Char wearer ){
		return 2 * wearer.HT;
	}

	public int stored(){
		return (int)stored;
	}

	//called whenever the hero takes damage, each equipped ring stores its own share
	public static void onDamageTaken( Hero hero, int damage ){
		//like other rings, it has no effect while magic is suppressed
		if (damage <= 0 || hero.buff(MagicImmune.class) != null) return;
		for (Vengeance v : hero.buffs(Vengeance.class)){
			v.store(damage);
		}
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero )) {
			actions.add( AC_RELEASE );
		}
		return actions;
	}

	@Override
	public String actionName( String action, Hero hero ) {
		if (action.equals( AC_RELEASE )){
			return Messages.get(this, "ac_release", stored());
		}
		return super.actionName(action, hero);
	}

	@Override
	public String defaultAction() {
		if (Dungeon.hero != null && isEquipped( Dungeon.hero )){
			return AC_RELEASE;
		}
		return super.defaultAction();
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_RELEASE )) {
			//quickslots arm their targeting after execute() based on this flag,
			// so it must only be true when a cell is actually being selected
			usesTargeting = false;
			if (!isEquipped( hero )) {
				GLog.w( Messages.get(this, "need_equip") );
			} else if (hero.buff(MagicImmune.class) != null) {
				GLog.w( Messages.get(this, "no_magic") );
			} else if (stored() <= 0) {
				GLog.w( Messages.get(this, "empty") );
			} else {
				usesTargeting = true;
				curUser = hero;
				curItem = this;
				GameScene.selectCell( releaser );
			}
		} else {
			usesTargeting = false;
		}
	}

	public void release( Hero hero, int target ){
		Ballistica beam = new Ballistica( hero.pos, target, Ballistica.STOP_SOLID );
		int cell = beam.collisionPos;

		int damage = Math.min( stored(), capacity( hero ) );
		stored = 0;
		updateQuickslot();

		hero.sprite.zap( cell );
		hero.sprite.parent.add( new Beam.DeathRay( hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld( cell ) ) );
		Sample.INSTANCE.play( Assets.Sounds.RAY );

		//every character along the beam takes the full stored damage
		ArrayList<Char> hit = new ArrayList<>();
		for (int c : beam.subPath( 1, beam.dist )) {
			Char ch = Actor.findChar( c );
			if (ch != null && ch != hero) {
				hit.add( ch );
			}
			CellEmitter.center( c ).burst( PurpleParticle.BURST, 1 );
		}
		for (Char ch : hit){
			ch.damage( damage, this );
			if (ch.sprite != null) {
				ch.sprite.centerEmitter().burst( PurpleParticle.BURST, 3 );
				ch.sprite.flash();
			}
		}

		Invisibility.dispel();
		hero.spendAndNext( Actor.TICK );
	}

	@Override
	public void reset() {
		super.reset();
		//rings found in remains from a previous run start empty
		stored = 0;
	}

	private static final CellSelector.Listener releaser = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target == null || !(curItem instanceof RingOfVengeance)) {
				return;
			}
			if (target == curUser.pos) {
				GLog.i( Messages.get(Wand.class, "self_target") );
				return;
			}
			((RingOfVengeance) curItem).release( curUser, target );
		}

		@Override
		public String prompt() {
			return Messages.get( RingOfVengeance.class, "prompt" );
		}
	};

	@Override
	public String status() {
		if (Dungeon.hero != null && isEquipped( Dungeon.hero ) && stored() > 0){
			return Integer.toString( stored() );
		}
		return super.status();
	}

	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats", Math.round(100 * storeFactor(soloBuffedBonus())));
			if (Dungeon.hero != null){
				info += "\n\n" + Messages.get(this, "stored", stored(), capacity(Dungeon.hero));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats", Math.round(100 * storeFactor(1)));
		}
	}

	public String upgradeStat1( int level ){
		if (cursed && cursedKnown) level = Math.min(-1, level-3);
		return Math.round(100 * storeFactor(level+1)) + "%";
	}

	private static final String STORED = "stored";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STORED, stored );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		stored = bundle.getFloat( STORED );
	}

	@Override
	protected RingBuff buff( ) {
		return new Vengeance();
	}

	public class Vengeance extends RingBuff {

		public void store( int damage ){
			int cap = capacity( target );
			stored = Math.min( cap, stored + damage * storeFactor( soloBuffedBonus() ) );
			updateQuickslot();
		}
	}
}
