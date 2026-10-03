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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mirrorshade;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MirrorCracks;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

//surrounds the hero with reflections, each attack has a chance to strike a reflection instead
public class Kaleidoscope extends ArmorAbility {

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		int facets = 3 + hero.pointsInTalent(Talent.KALEIDO_FACETS);
		float duration = 5 + 2*hero.pointsInTalent(Talent.KALEIDO_PERSISTENCE);

		Buff.prolong(hero, Facets.class, duration).facets = facets;

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();

		hero.sprite.burst(0xFFB79CFF, 2*facets);
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		hero.sprite.operate(hero.pos);
		hero.spendAndNext(1f);
	}

	@Override
	public int icon() {
		return HeroIcon.KALEIDOSCOPE;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.KALEIDO_FACETS, Talent.KALEIDO_PERSISTENCE, Talent.KALEIDO_SHRAPNEL, Talent.HEROIC_ENERGY};
	}

	public static class Facets extends FlavourBuff {

		{
			type = buffType.POSITIVE;
		}

		public int facets = 3;
		private Char pending = null;

		//called when the hero's evasion is tested, returns true if a reflection is struck instead
		public boolean deflect( Char attacker ){
			if (attacker == null || facets <= 0) return false;
			//each attack only has a 1/(facets+1) chance to find the real hero
			if (Random.Int(facets + 1) != 0){
				pending = attacker;
				return true;
			}
			return false;
		}

		public boolean isPending(){
			return pending != null;
		}

		public void clearPending(){
			pending = null;
		}

		public String shatter( Hero hero ){
			Char attacker = pending;
			pending = null;
			facets--;

			int shrapnel = hero.pointsInTalent(Talent.KALEIDO_SHRAPNEL);
			if (shrapnel > 0 && attacker != null && attacker.isAlive()
					&& Dungeon.level.adjacent(hero.pos, attacker.pos)){
				//3/5/7/9 damage to adjacent attackers
				MirrorCracks.reflect(hero, attacker, 1 + 2*shrapnel, 0);
			}
			Sample.INSTANCE.play(Assets.Sounds.SHATTER, 0.5f, 1.4f);

			if (facets <= 0){
				detach();
			}
			return Messages.get(Kaleidoscope.class, "shattered");
		}

		@Override
		public int icon() {
			return BuffIndicator.INVISIBLE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.7f, 0.5f, 1f);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(facets);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", facets, dispTurns(visualcooldown()));
		}

		private static final String FACETS = "facets";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(FACETS, facets);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			facets = bundle.getInt(FACETS);
		}
	}
}
