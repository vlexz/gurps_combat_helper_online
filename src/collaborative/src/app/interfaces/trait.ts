
export class Trait {
  public category: string;
  public name: string;
  public cp: number;
  public cpBase: number;
  public traitString: string;

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

export class TraitDescriptor {
  public id: string;
  public name: string;
}

export class LibraryTrait {
  public ready: boolean;
  public trait: Trait;

  static fromJson(json: any): LibraryTrait {
    let res = new LibraryTrait;
    res.ready = json.ready;
    res.trait = Trait.fromJson(json.traitt);
    return res;
  }

}
