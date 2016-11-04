
export class Trait {
  public category: string;
  public name: string;
  public cp: number;

  static fromJson(json: Object) {
    let ret = new Trait;
    Object.assign(ret, json);
    return ret;
  }

  constructor(
  ) {
  }

  cloneWith(modifier) {
    let trait = Trait.fromJson(this);
    Object.assign(trait, modifier);
    return trait;
  }

}
