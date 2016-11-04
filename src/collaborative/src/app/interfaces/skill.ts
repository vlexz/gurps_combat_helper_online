
export class Skill {
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
