
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

  clone(modifier: Object = null) {
    let trait = Trait.fromJson(JSON.parse(JSON.stringify(this)));
    if (modifier) {
      Object.assign(trait, modifier);
    }
    return trait;
  }

}
