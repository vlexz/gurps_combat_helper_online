
export class SkillDescriptor {
  id: string;
  name: string;
}

export class LibrarySkill {
  ready: boolean;
  skill: Skill;

  static fromJson(json: any): LibrarySkill {
    let res = new LibrarySkill;
    res.ready = json.ready;
    res.skill = Skill.fromJson(json.skill);
    return res;
  }
}

export class Skill {
  name: string = '';
  skillString: string = '';
  attr: string = 'DX';
  diff: string = 'E';
  relLvl: number = 0;
  bonus: number = 0;
  lvl: number = 0; // calculated
  cp: number = 0; // calculated

  static fromJson(json: Object): Skill {
    let skill = new Skill;
    Object.assign(skill, json);
    return skill;
  }

  clone(modifier: Object = null): Skill {
    let skill = Skill.fromJson(JSON.parse(JSON.stringify(this)));
    if (modifier) {
      Object.assign(skill, modifier);
    }
    return skill;
  }
}
