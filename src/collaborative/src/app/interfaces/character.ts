
export interface Stat {
  base: number;
  delta: number;
  bonus: number;
  cpMod: number;
  cp: number;
}

export interface VariableStat extends Stat {
  lost: number;
  compromised: boolean;
  collapsing: boolean;
}

interface Stats {
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

export interface Character {
  _id: string;
  stats: Stats;
}
