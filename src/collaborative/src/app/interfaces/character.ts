
import { Trait } from './trait';

export class Stat {
  base: number;
  delta: number;
  bonus: number;
  cpMod: number;
  cp: number;
}

export class VariableStat extends Stat {
  lost: number;
  compromised: boolean;
  collapsing: boolean;
}

class Stats {
  st: Stat;
  dx: Stat;
  iq: Stat;
  ht: Stat;
  will: Stat;
  per: Stat;
  hp: VariableStat;
  fp: VariableStat;
  liftSt: Stat;
  strikeSt: Stat;
  basicSpeed: Stat;
  basicMove: Stat;
}

class Skill {
  skillString: string;
  attr: string;
  diff: string;
  relLvl: number;
  bonus: number;
  lvl: number; // calculated
  cp: number; // calculated
}

export class Character {
  _id: string;
  stats: Stats;
  traits: Trait[];
  skills: Skill[];

  get advatages(): Trait[] {
    return this.traits.filter(t => t.cp >= 0);
  }

  get disadvantages(): Trait[] {
    return this.traits.filter(t => t.cp < 0);
  }
}
