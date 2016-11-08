
export class Damage {
  attackType: string; //  "thr", valid strings: "thr" "sw" ""
  dmgDice: number; //  0, valid values >= 0
  dmgMod: number; ///  0,
  armorDiv: number; //  1, valid values: 0.1, 0.2, 0.5, 1, 2, 3, 5, 10, 100
  dmgType: string; // "cr", valid strings: "cr" "cr ex" "cut" "imp" "pi-" "pi" "pi+" "pi++" "burn" "burn ex" "tox" "cor" "aff" "fat" "spec."
  fragDice: number;
  dmgString: string; //  "1d cr" calculated field
}

export class Attack {
  name: string;
  skill: string;
  available: boolean;
  damage: Damage;
  linked: Damage[];
  followup: Damage[];
}

export class MeleeAttack extends Attack {
}

export class RangedAttack extends Attack {
}

export class Block {
}

export class Weapon {
  name: string;
  carried: string;
  attackMelee: MeleeAttack[];
  attacksRanged: RangedAttack[];
  blocks: Block[];
}
