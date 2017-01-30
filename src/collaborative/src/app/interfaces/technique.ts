
export class Technique {
  // "name" : "Off-hand weapon training",
  skill: string; // "skill" : "Guns",
  // "spc" : "Pistol",
  tchString: string;
  diff: string;
  // "style" : "Trench Warfare",
  defLvl: number;
  // "maxLvl" : 0,
  // "notes" : "",
  cp: number;  // valid values >= 0 & <= max
  relLvl: number; // calculated field
  lvl: number; // " : 14 // calculated field

  static fromJson(json: Object): Technique {
    let tech = new Technique;
    Object.assign(tech, json);
    return tech;
  }

  clone(modifier: Object = null): Technique {
    let tech = Technique.fromJson(JSON.parse(JSON.stringify(this)));
    if (modifier) {
      Object.assign(tech, modifier);
    }
    return tech;
  }
}

export class LibraryTechnique {
  ready: boolean;
  technique: Technique;

  static fromJson(json: any): LibraryTechnique {
    let res: LibraryTechnique = new LibraryTechnique();
    res.ready = json.ready;
    res.technique = Technique.fromJson(json.technique);
    return res;
  }
}

