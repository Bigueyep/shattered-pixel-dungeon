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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfArcana;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfVengeance;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTranslocation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Katana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarScythe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.watabou.utils.Reflection;

//optional "arsenal" mode: the hero starts the run with a +10 weapon and a relic (ring, wand or artifact)
// picked when the hero is created. The items are ordinary inventory items, so the mode needs no state
// in the save and works the same with the checkpoint mode.
public class Loadout {

	public static final int WEAPON_LEVEL = 10;
	//level of the chosen ring or wand, artifacts have their own progression and start as usual
	public static final int RELIC_LEVEL = 3;

	//ordered from the lightest to the heaviest weapons
	public static final Class<?>[] WEAPONS = {
			Dagger.class, Shortsword.class, HandAxe.class, Spear.class, Quarterstaff.class, Dirk.class, Rapier.class,
			Sword.class, Mace.class, Scimitar.class, RoundShield.class, Sai.class, Whip.class,
			Longsword.class, Katana.class, BattleAxe.class, Flail.class, RunicBlade.class, AssassinsBlade.class, Crossbow.class,
			Greatsword.class, WarHammer.class, Glaive.class, Greataxe.class, Greatshield.class, Gauntlet.class, WarScythe.class
	};

	public static final Class<?>[] RINGS = {
			RingOfAccuracy.class, RingOfArcana.class, RingOfElements.class, RingOfEnergy.class, RingOfEvasion.class,
			RingOfForce.class, RingOfFuror.class, RingOfHaste.class, RingOfMight.class, RingOfSharpshooting.class,
			RingOfTenacity.class, RingOfVengeance.class, RingOfWealth.class
	};

	public static final Class<?>[] WANDS = {
			WandOfMagicMissile.class, WandOfLightning.class, WandOfDisintegration.class, WandOfFireblast.class,
			WandOfFrost.class, WandOfCorrosion.class, WandOfBlastWave.class, WandOfLivingEarth.class,
			WandOfTransfusion.class, WandOfPrismaticLight.class, WandOfWarding.class, WandOfRegrowth.class,
			WandOfCorruption.class, WandOfTranslocation.class
	};

	//the artifacts that work for any hero, the class specific ones and the key are left out
	public static final Class<?>[] ARTIFACTS = {
			AlchemistsToolkit.class, ChaliceOfBlood.class, EtherealChains.class, HornOfPlenty.class,
			MasterThievesArmband.class, SandalsOfNature.class, TalismanOfForesight.class,
			TimekeepersHourglass.class, UnstableSpellbook.class
	};

	public static boolean enabled(){
		return SPDSettings.loadout() && !Dungeon.daily;
	}

	//the saved choice, or the first entry of the list when nothing (valid) was chosen yet
	public static Class<?> weapon(){
		return find(WEAPONS, SPDSettings.loadoutWeapon(), Longsword.class);
	}

	public static Class<?> relic(){
		Class<?> cls = find(null, SPDSettings.loadoutRelic(), null);
		return cls != null ? cls : RingOfMight.class;
	}

	public static Class<?>[][] relicLists(){
		return new Class<?>[][]{ RINGS, WANDS, ARTIFACTS };
	}

	private static Class<?> find( Class<?>[] list, String name, Class<?> fallback ){
		Class<?>[][] lists = list == null ? relicLists() : new Class<?>[][]{ list };
		for (Class<?>[] l : lists){
			for (Class<?> cls : l){
				if (cls.getName().equals(name)) return cls;
			}
		}
		return fallback;
	}

	//gives the hero the chosen items, called once when the run is created, right after the class items
	public static void apply( Hero hero ){
		applyWeapon(hero);
		applyRelic(hero);

		//a ring of might may have raised the max HP
		hero.updateHT(false);
		hero.HP = hero.HT;
	}

	private static void applyWeapon( Hero hero ){
		MeleeWeapon weapon = (MeleeWeapon) Reflection.newInstance(weapon());
		weapon.level(WEAPON_LEVEL);
		weapon.identify();

		//the class weapon stays in the backpack, some heroes rely on it (the mage's staff holds their wand)
		KindOfWeapon old = hero.belongings.weapon;
		hero.belongings.weapon = weapon;
		weapon.activate(hero);
		if (old != null){
			old.collect();
		}
	}

	private static void applyRelic( Hero hero ){
		Item item = (Item) Reflection.newInstance(relic());
		if (item instanceof Ring || item instanceof Wand){
			item.level(RELIC_LEVEL);
		}
		item.identify();

		if (item instanceof Wand){
			((Wand) item).curCharges = ((Wand) item).maxCharges;
			item.collect();
		} else if (item instanceof Ring){
			Ring ring = (Ring) item;
			//rings go in the ring slot, then in the misc slot, and stay in the backpack if both are taken
			if (hero.belongings.ring == null){
				hero.belongings.ring = ring;
				ring.activate(hero);
			} else if (hero.belongings.misc == null){
				hero.belongings.misc = ring;
				ring.activate(hero);
			} else {
				ring.collect();
			}
		} else if (item instanceof Artifact){
			Artifact artifact = (Artifact) item;
			//artifacts must stay unique throughout the run
			Generator.removeArtifact(artifact.getClass());
			if (hero.belongings.artifact == null){
				hero.belongings.artifact = artifact;
				artifact.activate(hero);
			} else if (hero.belongings.misc == null){
				hero.belongings.misc = artifact;
				artifact.activate(hero);
			} else {
				artifact.collect();
			}
		}

		//items you can use go in the first free quickslot
		if (item instanceof Wand || item instanceof Artifact || item instanceof RingOfVengeance){
			for (int s = 0; s < QuickSlot.SIZE; s++){
				if (Dungeon.quickslot.getItem(s) == null){
					Dungeon.quickslot.setSlot(s, item);
					break;
				}
			}
		}
	}
}
