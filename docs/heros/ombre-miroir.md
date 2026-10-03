# L'Ombre-Miroir — Document de design de héros

> **Statut :** proposition de design (non implémentée dans le code du jeu).
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
| PV de départ | 18 | Guerrier 20, Mage/Voleur 20 → plus fragile |
| PV par niveau | +4 | Identique aux autres héros |
| Force de départ | 10 | Standard |
| Précision / Esquive | Standard / **+10 %** | Bonus d'esquive inné, compensé par les PV réduits |
| Arme de départ | **Éclat d'Obsidienne** (voir §4) | Arme de tier 1 |
| Armure de départ | Robe de tissu (tier 1) | Standard |
| Ressource unique | **Fêlures** (0 → 10) | Jauge visible dans la barre d'état |

### La ressource « Fêlures »

| Événement | Effet sur la jauge |
|---|---|
| Recevoir un coup physique | +1 Fêlure |
| Esquiver une attaque | +1 Fêlure (max. 1 par tour) |
| Dépenser une compétence de reflet | −X Fêlures (coût de la compétence) |
| 10 tours sans combat | −1 Fêlure (décroissance lente) |

> **Pourquoi ?** Le personnage est récompensé quand il *s'expose* au danger, ce qui pousse à un jeu tendu plutôt qu'à la fuite systématique.

---

## 3. Arbre de compétences (5 paliers)

L'arbre suit la structure de Shattered Pixel Dungeon : des points de talent sont gagnés en montant de niveau, chaque talent possède 2 ou 3 rangs. Le palier 3 débloque une **sous-classe**, le palier 4 une **capacité d'armure**.

### Palier 1 — « Le Reflet » (niveaux 1 à 5, 5 points)

| Talent | Rang 1 | Rang 2 | Contre-stratégie |
|---|---|---|---|
| **Verre Trempé** | Manger donne +1 Fêlure et 2 PV | +2 Fêlures et 3 PV | Inutile si la jauge est déjà pleine : à garder pour l'après-combat |
| **Éclat Révélateur** | Identifier une arme révèle aussi sa malédiction éventuelle | + révèle l'enchantement | Aucun gain en combat |
| **Pas Silencieux** | +1 tour avant que les monstres endormis ne se réveillent | +2 tours | Les monstres déjà éveillés (Gnolls chasseurs) l'ignorent |
| **Miroir Protecteur** | La 1ʳᵉ attaque reçue à chaque étage est réduite de 30 % | −50 % | Un seul usage par étage : ouvrir avec une attaque faible le « consomme » |

### Palier 2 — « La Fêlure » (niveaux 6 à 11, 6 points)

| Talent | Rang 1 | Rang 2 | Rang 3 | Contre-stratégie |
|---|---|---|---|---|
| **Riposte Spéculaire** | Après une esquive, la prochaine attaque inflige +15 % | +25 % | +35 % | Les ennemis à distance (archers squelettes) ne déclenchent pas la riposte au contact |
| **Absorption** | 1 Fêlure dépensée → soigne 1 PV (hors combat) | 2 PV | 3 PV | Impossible si un ennemi est visible |
| **Image Rémanente** | Laisse un leurre 2 tours en quittant une case | 3 tours | 4 tours | Les monstres « intelligents » (Mages Nains) ignorent le leurre |

### Palier 3 — « Le Double » (niveaux 12 à 19, 8 points) + choix de sous-classe

À la Tengu (étage 10), on utilise un **Éclat de Mémoire** pour choisir une sous-classe :

| Sous-classe | Concept | Mécanique principale | Faiblesse |
|---|---|---|---|
| **Le Reflet Vengeur** | Riposte | 25 % des dégâts reçus au corps-à-corps sont renvoyés à l'attaquant (max. 10 par coup) | Aucun renvoi contre la magie et les attaques à distance |
| **L'Ombre Jumelle** | Invocation | Peut dépenser 5 Fêlures pour créer un **Double** (30 % des PV du héros, copie l'arme équipée, dure 15 tours) | Le Double meurt en un coup d'AoE ; 1 seul Double à la fois |

| Talent palier 3 | Rang 1 | Rang 2 | Rang 3 | Contre-stratégie |
|---|---|---|---|---|
| **Éclats Tranchants** (Vengeur) | Le renvoi applique Saignement 2 | Saignement 3 | Saignement 4 | Les ennemis inorganiques (golems, élémentaires) ne saignent pas |
| **Glace sans Tain** (Vengeur) | +1 armure par tranche de 3 Fêlures | par tranche de 2 | + 1 armure fixe | Dépenser ses Fêlures fait chuter l'armure : dilemme constant |
| **Lien Spéculaire** (Jumelle) | Échanger sa position avec le Double (coût 2 Fêlures) | coût 1 | coût 0, 1 fois / 20 tours | Si le Double est encerclé, l'échange piège le héros |
| **Écho Partagé** (Jumelle) | Le Double hérite de 50 % de l'enchantement d'arme | 75 % | 100 % | Les malédictions sont aussi copiées |
| **Mémoire du Verre** (commun) | Voir la carte de l'étage précédent en entier | + objets visibles | + pièges visibles | Information uniquement, aucun gain de puissance |

### Palier 4 — « Le Miroir Noir » (niveaux 20 à 30, 11 points) + capacité d'armure

Débloqué via le **Casque de la Couronne Naine** (comme les autres héros). Coût : 35 charge d'armure par défaut.

| Capacité d'armure | Effet | Talents d'amélioration (3 rangs chacun) | Contre-stratégie |
|---|---|---|---|
| **Kaléidoscope** | Crée 3 reflets autour du héros pendant 5 tours ; chaque attaque ennemie a 1 chance sur 4 de toucher le vrai héros… mais les reflets éclatent au 1ᵉʳ coup | +1 reflet / +2 tours / les reflets éclatent en infligeant 5 dégâts | Les attaques de zone (bombes, souffle de Yog) détruisent tous les reflets d'un coup |
| **Inversion** | Échange les PV en % entre le héros et un ennemi ciblé (non-boss) | Portée +2 / coût −5 / utilisable sur les mini-boss à 50 % d'efficacité | Ne fonctionne pas sur les boss ; inutile si le héros est déjà en meilleure santé que la cible |
| **Prison de Verre** | Enferme un ennemi dans un miroir pendant 6 tours (paralysie + invulnérabilité) | +2 tours / la cible ressort Affaiblie / 2 cibles | La cible enfermée est aussi *intouchable* : outil de fuite, pas de mise à mort |

### Palier 5 — « Le Visage » (talent ultime, 1 point unique)

Un seul point, obtenu en ramassant l'**Amulette de Yendor** (étage 26). Il ne sert donc que pendant la remontée vers la surface : une récompense de fin de partie, pas un pic de puissance précoce.

| Talent ultime | Effet | Contre-stratégie |
|---|---|---|
| **Le Dernier Éclat** | Une fois par partie, à la mort, le héros revient avec 30 % PV et 0 Fêlure, en laissant un Double qui combat 5 tours | Ne se cumule pas avec un Ankh ; consommé en priorité, donc l'Ankh reste nécessaire pour une 2ᵉ chance |
| **Réflexion Parfaite** | Le 1ᵉʳ sort ennemi reçu par étage est renvoyé à son lanceur | Un seul sort par étage, et pas les sorts de boss |

---

## 4. Objets uniques exclusifs

| Objet | Type | Effet | Amélioration (+1) | Limite / contre-stratégie |
|---|---|---|---|---|
| **Éclat d'Obsidienne** | Arme de départ (tier 1, 1-6 dégâts, rapide) | Chaque coup critique (riposte) ajoute +1 Fêlure | +1 dégât max, +5 % chance de Fêlure bonus | Dégâts faibles : il faut changer d'arme vers l'étage 6-8 ; on peut **réinfuser** son effet dans une nouvelle arme avec un Parchemin d'Amélioration |
| **Miroir Fêlé** | Artéfact exclusif (ne peut pas être retiré, comme le Grimoire de la Clerc) | Action « Réfléchir » : dépense 3 Fêlures pour que le prochain projectile ennemi soit renvoyé | Le coût baisse de 1 tous les 3 niveaux d'artéfact (min. 1) ; niveau max +10 | Ne renvoie que les projectiles physiques (flèches, fléchettes, rochers) ; les sorts passent à travers |
| **Poussière de Tain** | Consommable (3 au départ, 1 trouvée par zone) | Jetée au sol : crée un nuage de 3×3 pendant 5 tours où les ennemis perdent leur cible (Aveuglement léger) | — (non améliorable) | Les ennemis sans vue (Larves de Yog, Spectres) ne sont pas affectés ; le nuage gêne aussi les alliés |
| **Larme de Verre** | Trinket exclusif (trouvé via l'Alchimie : Éclat d'Obsidienne + Pierre d'Énergie) | Les potions de soin donnent aussi +3 Fêlures, mais soignent 10 % de moins | +1 Fêlure par niveau | Coût réel en soin : mauvais choix sur une partie à défi « Pas de régénération » |

---

## 5. Résistances et faiblesses

| Élément / effet | Type | Valeur | Justification narrative | Comment l'exploiter (pour l'ennemi) / le gérer (pour le joueur) |
|---|---|---|---|---|
| **Charme** | Résistance | Durée −50 % | Elle ne voit que des reflets, pas des visages | Les Succubes restent dangereuses via leurs dégâts |
| **Terreur** | Résistance | Durée −50 % | Le reflet ne connaît pas la peur | — |
| **Ténèbres** (lumière basse) | Résistance | +2 de vision dans le noir | Créature d'ombre | Inutile dans les étages éclairés |
| **Éclairs** (Shamans, DM-100, Baguette de Foudre) | **Faiblesse** | +25 % dégâts | Le verre conduit la décharge | Combattre dans l'eau aggrave le risque : se placer hors des flaques |
| **Aveuglement** | **Faiblesse** | Durée ×2 | Un miroir sans lumière ne reflète rien | Garder une Potion de Purification ; la Poussière de Tain ne l'aveugle pas elle-même |
| **Coups écrasants** (Golems, Brutes, Arme « Lourde ») | **Faiblesse** | Chaque coup donne +2 Fêlures mais inflige +10 % dégâts | Le verre éclate sous les chocs | Paradoxalement, accélère le Miroir Brisé : risque/récompense |
| **Poison / Toxique** | Neutre | — | — | — |
| **Feu** | Neutre | — | — | — |

---

## 6. Conditions de déblocage

| Étape | Condition | Inspiration SPD |
|---|---|---|
| 1 | Avoir débloqué **au moins 4 héros** (Guerrier, Mage, Voleur, Chasseresse) | Progression standard |
| 2 | Battre le **Roi Nain** (étage 20) avec n'importe quel héros | Lien narratif avec le miroir du Roi |
| 3 | Lors de cette victoire, avoir **esquivé 50 attaques** sur la partie | Enseigne la mécanique d'esquive avant le déblocage |
| *Alternative* | Obtenir le badge **« Sans Reflet »** : atteindre l'étage 10 sans jamais avoir bu de Potion de Soin | Voie pour les joueurs prudents |

Badge de déblocage : **« L'Éclat Manquant »**. Message affiché : *« Dans les débris du trône, un fragment de verre noir vous rend votre regard… »*

---

## 7. Mécanique « Miroir Brisé » (mode rage)

### Déclenchement

| Condition | Valeur |
|---|---|
| Seuil de PV | **PV < 15 %** du maximum |
| Prérequis | Au moins **5 Fêlures** dans la jauge |
| Déclenchement | Automatique (pas d'action requise), ou manuel via le Miroir Fêlé dès 25 % PV pour 8 Fêlures |
| Temps de recharge | **150 tours** (affiché comme un debuff « Miroir Recomposé ») |

### Effets (durée : 6 tours + 1 tour par tranche de 2 Fêlures consommées, **max. 10 tours**)

| Effet | Valeur | Visuel pixel art |
|---|---|---|
| Toutes les Fêlures sont consommées | Jauge → 0 | Le sprite se fragmente en 4 morceaux qui flottent autour du héros |
| Dégâts infligés | **+40 %** | Particules de verre noir à chaque coup |
| Vitesse d'attaque | **+1 attaque gratuite tous les 3 tours** | Traînée violette (palette 4 couleurs) |
| Vol de vie | 15 % des dégâts infligés | Petites étincelles blanches |
| Renvoi de dégâts | 20 % des dégâts de mêlée reçus | Flash blanc d'1 frame |

### Contreparties (obligatoires, pour éviter l'abus)

| Malus | Valeur | Raison |
|---|---|---|
| Esquive | **−50 %** | Le héros ne se protège plus |
| Impossible de lire des parchemins ou d'utiliser des baguettes | Pendant toute la durée | Furie aveugle : pas de solution « miracle » |
| Soins reçus (potions, rosée) | **−50 %** | Empêche la boucle « rage + potion » |
| Fin du mode | **Affaibli 10 tours** + Faim +50 | Coût réel à l'issue du combat |
| Pas d'invincibilité | Le héros peut mourir normalement pendant le mode | Respect de la contrainte « pas d'invincibilité infinie » |

### Contre-stratégies côté monstres / joueur

| Situation | Pourquoi le Miroir Brisé échoue |
|---|---|
| Ennemis à distance (Archers, Warlocks) | Le renvoi ne s'applique qu'à la mêlée ; le héros doit traverser la salle |
| Ennemis qui fuient (Voleurs, Rats Albinos effrayés) | Le temps limité est perdu à poursuivre |
| Combat de boss avec phases d'invulnérabilité (Tengu, DM-300 surchargé) | Une rage mal synchronisée tombe dans le vide, et la recharge est longue |
| Aveuglement / Paralysie | Les tours passent sans agir, le mode est gaspillé |

### Exemple de calcul (niveau 12, 66 PV max, 8 Fêlures)

1. Un Gnoll Brute frappe : le héros passe de 12 PV à 9 PV (< 15 % de 66 = 9,9) → **Miroir Brisé**.
2. Durée : 6 + (8 ÷ 2) = **10 tours** (plafond atteint).
3. Le héros frappe avec une Épée (dégâts moyens 10) → 14 dégâts, soigne 2 PV.
4. À la fin : Affaibli 10 tours, recharge 150 tours. S'il reste un ennemi en vie, la situation est critique.

---

## 8. Notes d'équilibrage

| Risque identifié | Mesure prise |
|---|---|
| Boucle infinie Fêlures → Double → Fêlures | Le Double ne génère pas de Fêlures pour le héros |
| Kaléidoscope + Prison de Verre = fuite garantie | Les deux capacités sont mutuellement exclusives (choix d'armure unique) |
| Le Dernier Éclat = deuxième Ankh gratuit | Une seule fois par partie, et accessible uniquement au palier 5 (fin de partie) |
| Miroir Brisé déclenché volontairement en boucle | Recharge de 150 tours + coût de 5 Fêlures minimum + Affaibli |
| Courbe de difficulté | Plus fragile que les autres en début de partie (18 PV), puissance qui monte avec la maîtrise de la jauge |
