
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

  static fromJson(json: Object) {
    let char = new Character;
    Object.assign(char, json);
    return char;
  }

  get advantages(): Trait[] {
    return this.traits.filter(t => t.category === 'Advantage');
  }

  removeTrait(type: string, index: number) {
    let i = -1;
    this.traits = this.traits.filter(t => {
      if (t.category === type) {
        ++i;
      }
      if (i === index) {
        i = NaN;
        return false;
      }
      return true;
    });
  }

  get disadvantages(): Trait[] {
    return this.traits.filter(t => t.category === 'Disadvantage');
  }


}
