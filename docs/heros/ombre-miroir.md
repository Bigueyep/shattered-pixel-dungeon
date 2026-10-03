# L'Ombre-Miroir — Document de design de héros

> **Statut :** implémenté dans le jeu (classe `HeroClass.MIRRORSHADE`). Ce document décrit les valeurs réellement codées.
> **Rôle :** héros hybride « riposte / contrôle », entre le Voleur (furtivité) et la Duelliste (gestion de ressource active).
> **Difficulté conseillée :** ★★★★☆ — à débloquer après avoir maîtrisé les héros de base.

---

## 1. Histoire

On raconte qu'au cinquième étage des Cités Naines, le Roi Nain faisait polir un miroir d'obsidienne pour y enfermer les âmes de ses ennemis. Lorsque Yog-Dzewa s'éveilla, le miroir se fendit en mille éclats — et dans chacun d'eux, quelque chose continua de *regarder*. Personne ne sait si l'Ombre-Miroir est l'un de ces prisonniers, ou le reflet d'un aventurier mort qui a oublié de disparaître avec lui.

Elle ne projette aucune ombre, mais toutes les ombres du donjon semblent se tourner vers elle. Elle ne frappe pas vraiment : elle *renvoie*. Chaque coup qu'elle reçoit laisse une fêlure sur sa peau de verre noir, et l'on murmure qu'au jour où la dernière fêlure se refermera, elle verra enfin le visage de celui qu'elle reflète. D'ici là, elle descend — car au fond du donjon se trouve le seul éclat qui lui manque.

---

## 2. Fiche de base

| Statistique | Valeur | Comparaison |
|---|---|---|
| PV de départ | 18 | Autres héros : 20 → plus fragile |
| PV par niveau | +5 | Identique aux autres héros (18 + 5 × (niveau − 1)) |
| Force de départ | 10 | Standard |
| Esquive | **+10 %** | Bonus inné, compensé par les PV réduits |
| Arme de départ | **Éclat d'Obsidienne** (voir §4) | Arme de tier 1 rapide |
| Armure de départ | Armure en lin | Standard |
| Ressource unique | **Fêlures** (0 → 10) | Icône dans la barre d'état et compteur sur le miroir |
| Identifie d'office | Parchemin d'identification, Potion d'invisibilité, Parchemin d'image miroir | — |

### La ressource « Fêlures »

| Événement | Effet sur la jauge |
|---|---|
| Recevoir un coup d'un ennemi | +1 Fêlure (+2 contre Golems et Brutes) |
| Esquiver une attaque | +1 Fêlure (1 par tour au maximum) |
| Toucher avec l'Éclat d'Obsidienne | 25 % de chances (+5 % par niveau, 50 % au max.) de +1 Fêlure |
| Dépenser une action du Miroir Fêlé | −X Fêlures (coût de l'action) |
| 10 tours sans ennemi en vue | −1 Fêlure (décroissance lente) |

> **Pourquoi ?** Le personnage est récompensé quand il *s'expose* au danger, ce qui pousse à un jeu tendu plutôt qu'à la fuite systématique.

---

## 3. Arbre de compétences (5 paliers)

L'arbre suit la structure de Shattered Pixel Dungeon : paliers 1 à 4 de talents (débloqués aux niveaux 2, 7, 13 et 21), sous-classe au palier 3, capacité d'armure au palier 4. Le palier 5 est un passif de fin de partie.

### Palier 1 — « Le Reflet » (2 rangs par talent)

| Talent | Rang 1 | Rang 2 | Contre-stratégie |
|---|---|---|---|
| **Verre Trempé** | Manger : +1 Fêlure, +2 PV | +2 Fêlures, +3 PV | Inutile si la jauge est pleine : à garder pour l'après-combat |
| **Éclat Révélateur** | Ramasser une arme/armure révèle si elle est maudite | + anneaux et baguettes | Aucun gain en combat |
| **Pas Silencieux** | Repérée comme si elle était 1 case plus loin | 2 cases | Les ennemis déjà en chasse ne sont pas concernés |
| **Miroir Protecteur** | 1ʳᵉ attaque ennemie de chaque étage −30 % | −50 % | Un seul usage par étage : une attaque faible le « consomme » |

### Palier 2 — « La Fêlure » (2 rangs par talent)

| Talent | Rang 1 | Rang 2 | Contre-stratégie |
|---|---|---|---|
| **Breuvage de Tain** | Boire une potion : +1 Fêlure | +2 Fêlures | Coûte des potions |
| **Riposte Spéculaire** | Après une esquive, prochaine attaque de mêlée (5 tours) +20 % | +35 % | Les ennemis à distance ne se font pas punir au contact |
| **Absorption** | Sans ennemi en vue : 1 Fêlure → 1 PV tous les 3 tours | → 2 PV | Impossible en combat |
| **Image Rémanente** | Touchée sous 50 % PV : 15 % de chances de laisser une image miroir (1 fois / 30 tours) | 30 % | Les images meurent au premier coup |
| **Mémoire du Verre** | Nouvel étage : carte révélée dans un rayon de 5 cases | 8 cases | Information uniquement |

### Palier 3 — « Le Double » (3 rangs) + sous-classe (à Tengu, étage 10)

| Talent de classe | Rang 1 | Rang 2 | Rang 3 | Contre-stratégie |
|---|---|---|---|---|
| **Incassable** | Recharge du Miroir Brisé 125 tours | 100 | 75 | Reste longue : un seul Miroir Brisé par gros combat |
| **Point de Rupture** | Miroir Brisé sous 20 % PV | 25 % | 30 % | Se déclenche plus tôt, donc parfois au mauvais moment |

| Sous-classe | Mécanique principale | Faiblesse |
|---|---|---|
| **Reflet Vengeur** (`REFLECTOR`) | Renvoie 25 % des dégâts de mêlée reçus (10 max. par coup) | Aucun renvoi contre la magie et les attaques à distance |
| **Ombre Jumelle** (`TWIN`) | Action « Double » du miroir : invoque un Double (30 % des PV max, copie l'arme, 50 % des dégâts, 15 tours) | Un seul Double à la fois ; fragile face aux attaques de zone |

| Talent de sous-classe | Rang 1 | Rang 2 | Rang 3 | Contre-stratégie |
|---|---|---|---|---|
| **Éclats Tranchants** (Vengeur) | Le renvoi inflige Saignement 2 | 3 | 4 | Les ennemis immunisés au saignement l'ignorent |
| **Glace sans Tain** (Vengeur) | +1 armure par tranche de 3 Fêlures | par tranche de 2 | + 1 armure fixe | Dépenser ses Fêlures fait chuter l'armure |
| **Surface Polie** (Vengeur) | Renvoi 30 % (15 max.) | 35 % (20 max.) | 40 % (25 max.) | Toujours inutile contre le tir et la magie |
| **Lien Spéculaire** (Jumelle) | Échange de place avec le Double : 2 Fêlures | 1 | 0, 1 fois / 20 tours | Si le Double est encerclé, l'échange piège le héros |
| **Écho Partagé** (Jumelle) | Le Double inflige 60 % des dégâts | 70 % | 80 % | Les malédictions d'arme sont aussi copiées |
| **Résilience Jumelle** (Jumelle) | Double à 40 % PV, 20 tours | 50 %, 25 tours | 60 %, 30 tours | Les attaques de zone le détruisent toujours |

### Palier 4 — « Le Miroir Noir » (4 rangs) + capacité d'armure

Débloqué via la Couronne du Roi Nain. Coût : 35 de charge d'armure (réductible par Énergie Héroïque).

| Capacité | Effet | Talents (4 rangs) | Contre-stratégie |
|---|---|---|---|
| **Kaléidoscope** | 3 reflets pendant 5 tours ; chaque attaque n'a qu'1 chance sur (reflets + 1) de toucher le vrai héros, sinon elle brise un reflet | **Facettes Multiples** (+1 reflet/rang), **Prisme Persistant** (+2 tours/rang), **Éclats de Verre** (un reflet brisé inflige 3/5/7/9 dégâts à l'attaquant adjacent) | Gaz, explosions et effets de zone ignorent les reflets |
| **Inversion** | Échange les pourcentages de PV entre le héros et un ennemi à 3 cases max. | **Portée Inversée** (+1 case/rang), **Choc Inversé** (Affaiblissement 3/6/9/12 tours), **Souveraineté Inversée** (mini-boss à 25/50/75/100 %) | Jamais sur les boss ; inutile si le héros est plus en forme que sa cible |
| **Prison de Verre** | Enferme un ennemi 6 tours : il ne peut ni agir ni être blessé | **Verre Renforcé** (+2 tours/rang), **Libération Fragile** (Vulnérable 3/6/9/12 tours à la sortie), **Codétenus** (+1 à 4 ennemis adjacents, durée ÷ 2) | La cible est intouchable : outil de fuite, pas de mise à mort ; ni boss ni mini-boss |

### Palier 5 — « Le Visage » (passif de fin de partie)

| Passif | Effet | Contre-stratégie |
|---|---|---|
| **Le Dernier Éclat** | Une fois par partie, tant que le héros porte l'**Amulette de Yendor** : à la mort, il revient avec 30 % PV, 0 Fêlure, et un Double combat à ses côtés pendant 5 tours | Ne sert que pendant la remontée ; consommé avant un Ankh, qui reste nécessaire pour une 2ᵉ chance |

---

## 4. Objets exclusifs

| Objet | Type | Effet | Limite / contre-stratégie |
|---|---|---|---|
| **Éclat d'Obsidienne** | Arme de départ (tier 1, 1-6 dégâts, vitesse ×1,25) | Chaque coup a 25 % de chances (+5 %/niveau, max. 50 %) de donner 1 Fêlure | Dégâts faibles : à remplacer vers les étages 6-8 |
| **Miroir Fêlé** | Artéfact de départ (niveau max. +10, progresse en dépensant des Fêlures) | **Renvoyer** (3 Fêlures, −1 tous les 3 niveaux) : la prochaine attaque à distance est renvoyée à son auteur. **Briser** (8 Fêlures, sous 25 % PV) : Miroir Brisé volontaire. **Double** / **Échanger** pour l'Ombre Jumelle | Doit être équipé ; inutile contre la mêlée et les effets de zone |
| **Poussière de Tain** | Consommable à lancer (3 au départ) | Nuage de fumée 3×3 et Cécité 5 tours sur les ennemis touchés | Le nuage bloque aussi la vision des alliés |
| **Linceul argenté** | Armure de classe | Donne accès à la capacité d'armure | — |

---

## 5. Résistances et faiblesses

| Élément / effet | Type | Valeur | Justification | Gestion |
|---|---|---|---|---|
| **Charme**, **Terreur**, **Effroi** | Résistance | Durée −50 % | Le reflet ne connaît ni peur ni affection | Les Succubes restent dangereuses via leurs dégâts |
| **Électricité** (DM-100, Baguette de Foudre, enchantement Choc) | **Faiblesse** | +25 % dégâts | Le verre conduit la décharge | Rester hors de l'eau et des nuages électriques |
| **Cécité** | **Faiblesse** | Durée ×2 | Un miroir sans lumière ne reflète rien | Garder une Potion de Purification |
| **Coups écrasants** (Golems, Brutes) | **Faiblesse** | +10 % dégâts, mais +2 Fêlures | Le verre éclate sous les chocs | Accélère le Miroir Brisé : risque/récompense |

---

## 6. Conditions de déblocage

| Condition | Détail |
|---|---|
| Battre le **Roi Nain** (étage 20) | Avec n'importe quel héros |
| … après avoir **esquivé au moins 50 attaques** dans la même partie | Compteur `Statistics.dodges` |

Badge : **« Ombre-Miroir débloquée ! »**. Dans les versions *debug* (comme l'APK produit par la CI), tous les héros sont débloqués d'office.

---

## 7. Mécanique « Miroir Brisé » (mode rage)

### Déclenchement

| Condition | Valeur |
|---|---|
| Seuil de PV | **PV < 15 %** du maximum (jusqu'à 30 % avec Point de Rupture) |
| Prérequis | Au moins **5 Fêlures** |
| Déclenchement | Automatique après un dégât, ou manuel via le Miroir Fêlé (8 Fêlures, sous 25 % PV) |
| Temps de recharge | **150 tours** (125/100/75 avec Incassable), affiché comme « miroir en reformation » |
| Durée | 6 tours + 1 par tranche de 2 Fêlures consommées, **10 tours max.** ; toutes les Fêlures sont consommées |

### Effets et contreparties

| Bonus | Valeur | Malus | Valeur |
|---|---|---|---|
| Dégâts infligés | **+40 %** | Esquive | **−50 %** |
| Vitesse d'attaque | **+33 %** (délai × 0,75) | Soins reçus (potions, régénération de potion) | **−50 %** |
| Vol de vie | 15 % des dégâts infligés | Parchemins et baguettes | **Interdits** |
| Renvoi | 20 % des dégâts de mêlée reçus | À la fin | **Affaibli 10 tours** + Faim +50 |

Aucune invincibilité : le héros peut mourir pendant le mode.

### Contre-stratégies

| Situation | Pourquoi le Miroir Brisé échoue |
|---|---|
| Ennemis à distance | Le renvoi ne s'applique qu'à la mêlée ; il faut traverser la salle |
| Ennemis qui fuient | Le temps limité est perdu à poursuivre |
| Boss avec phases d'invulnérabilité | Une rage mal synchronisée tombe dans le vide, et la recharge est longue |
| Cécité / Paralysie | Les tours passent sans agir |

### Exemple (niveau 12 : 18 + 5 × 11 = 73 PV max, 8 Fêlures)

1. Un Gnoll Brute frappe : le héros passe de 12 à 9 PV (< 15 % de 73 = 10,95) → **Miroir Brisé**.
2. Durée : 6 + 8 ÷ 2 = **10 tours** (plafond atteint).
3. Une attaque à 10 dégâts en inflige 14 et soigne 2 PV.
4. À la fin : Affaibli 10 tours et recharge de 150 tours.

---

## 8. Notes d'équilibrage

| Risque identifié | Mesure prise |
|---|---|
| Boucle Fêlures → Double → Fêlures | Le Double et les images ne génèrent pas de Fêlures |
| Miroir Brisé déclenché en boucle | Recharge de 150 tours + 5 Fêlures minimum + Affaibli |
| Inversion = soin complet gratuit | Coût d'armure de 35, portée 3, ni boss ni mini-boss (sauf talent, partiel) |
| Le Dernier Éclat = Ankh gratuit | Une seule fois, uniquement avec l'Amulette (fin de partie) |
| Courbe de difficulté | Plus fragile en début de partie (18 PV), puissance qui monte avec la maîtrise de la jauge |

---

## 9. Fichiers du code

| Élément | Fichier |
|---|---|
| Classe, équipement de départ | `core/.../actors/hero/HeroClass.java` |
| Talents | `core/.../actors/hero/Talent.java` |
| Fêlures, renvoi, Dernier Éclat | `core/.../actors/buffs/MirrorCracks.java` |
| Miroir Brisé | `core/.../actors/buffs/BrokenMirror.java` |
| Capacités d'armure | `core/.../actors/hero/abilities/mirrorshade/` |
| Double | `core/.../actors/mobs/npcs/MirrorDouble.java` |
| Objets | `items/artifacts/CrackedMirror.java`, `items/weapon/melee/ObsidianShard.java`, `items/TainDust.java`, `items/armor/MirrorShadeArmor.java` |
| Textes | `core/src/main/assets/messages/*/*.properties` et `*_fr.properties` |
