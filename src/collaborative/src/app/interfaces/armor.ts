
export class Protection {
    dr: number; // valid values >= 0
    ep: number; // valid values >= 0
    epi: number; // valid values >= 0
  }

export class Armor {
  name: string;
  carried: string; // valid strings: "Ready" "Equipped" "Combat" "Travel" "Stash"
  db: number; // valid values >= 0 & < 4
  protection: Protection;
  front: boolean;
  back: boolean;
  drType: string; // valid strings: "hard" "soft" "force field" "tough skin"
  locations: string[]; // valid strings:  "eyes" "skull" "face" "head" "neck" "right leg" "left leg" "legs" 
                      // "right arm" "left arm" "arms" "chest" "vitals" "abdomen" "groin" "torso" "hands" "left hand" 
                      // "right hand" "feet" "right foot" "left foot" "skin" "full body"
  hp: number; // valid values >= 0
  hpLeft: number; // valid values >= 0 & <= hp
  broken: boolean;
  lc: number; // valid values >= 0 & < 6
  tl: number; // valid values >= 0 & < 13
  notes: string;
  wt: number; // valid values >= 0
  cost: number; // valid values >= 0


  static fromJson(json: any) {
    let res = new Armor;
    Object.assign(res, json);
    return res;
  }

  clone(): Armor {
    let obj = JSON.parse(JSON.stringify(this));
    return Armor.fromJson(obj);
  }

};
