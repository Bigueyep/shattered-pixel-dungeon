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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

//swaps the percentage of remaining HP between the hero and an enemy
public class Inversion extends ArmorAbility {

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public int targetedPos(Char user, int dst) {
		return dst;
	}

	public static int range( Hero hero ){
		return 3 + hero.pointsInTalent(Talent.INVERTED_REACH);
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Char ch = Actor.findChar(target);

		if (ch == null || !Dungeon.level.heroFOV[target]){
			GLog.w(Messages.get(this, "no_target"));
			return;
		} else if (ch.alignment != Char.Alignment.ENEMY){
			GLog.w(Messages.get(this, "ally_target"));
			return;
		} else if (Dungeon.level.distance(hero.pos, ch.pos) > range(hero)){
			GLog.w(Messages.get(this, "too_far"));
			return;
		}

		//bosses are immune, minibosses can be partially inverted with a talent
		float efficiency = 1f;
		if (Char.hasProp(ch, Char.Property.BOSS)){
			GLog.w(Messages.get(this, "boss_target"));
			return;
		} else if (Char.hasProp(ch, Char.Property.MINIBOSS)){
			if (!hero.hasTalent(Talent.INVERTED_SOVEREIGNTY)){
				GLog.w(Messages.get(this, "boss_target"));
				return;
			}
			efficiency = 0.25f * hero.pointsInTalent(Talent.INVERTED_SOVEREIGNTY);
		}

		float heroPct = hero.HP / (float)hero.HT;
		float enemyPct = ch.HP / (float)ch.HT;

		float newHeroPct = heroPct + efficiency*(enemyPct - heroPct);
		float newEnemyPct = enemyPct + efficiency*(heroPct - enemyPct);

		int oldHeroHP = hero.HP;
		hero.HP = Math.max(1, Math.min(hero.HT, Math.round(newHeroPct * hero.HT)));
		ch.HP = Math.max(1, Math.min(ch.HT, Math.round(newEnemyPct * ch.HT)));

		int diff = hero.HP - oldHeroHP;
		if (diff > 0){
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(diff), FloatingText.HEALING);
		} else if (diff < 0){
			hero.sprite.showStatus(CharSprite.NEGATIVE, Integer.toString(-diff));
		}

		if (hero.hasTalent(Talent.INVERTED_SHOCK)){
			Buff.prolong(ch, Weakness.class, 3*hero.pointsInTalent(Talent.INVERTED_SHOCK));
		}

		hero.sprite.burst(0xFFB79CFF, 8);
		if (ch.sprite != null) {
			ch.sprite.burst(0xFFB79CFF, 8);
			ch.sprite.flash();
		}
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		hero.sprite.zap(target);
		hero.spendAndNext(1f);
	}

	@Override
	public int icon() {
		return HeroIcon.INVERSION;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.INVERTED_REACH, Talent.INVERTED_SHOCK, Talent.INVERTED_SOVEREIGNTY, Talent.HEROIC_ENERGY};
	}
}
