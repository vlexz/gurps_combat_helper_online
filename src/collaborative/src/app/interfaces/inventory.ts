
export class InventoryItem {
  name: string; // "Holster",
  carried: string; // "Equipped", // valid strings: "Ready" "Equipped" "Combat" "Travel" "Stash"
  dr: number; // 1, // valid values >= 0
  hp: number; // 5, // valid values >= 0
  hpLeft: number; // 5, // valid values >= 0 & <= hp
  broken: boolean; // false,
  lc: number; // 5, // valid values >= 0 & < 6
  tl: number; // 5, // valid values >= 0 & < 13
  notes: string; // "",
  wt: number; // 0.5, // valid values >= 0
  cost: number; // 75, // valid values >= 0
  n: number; // 1, // valid values >= 0
  totalWt: number; // 0.5, // calculated field
  totalCost: number; // 75 // calculated field

  static fromJson(json: Object): InventoryItem {
    let res = new InventoryItem();
    Object.assign(res, json);
    return res;
  }
}
