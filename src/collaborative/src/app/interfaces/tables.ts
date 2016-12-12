export class ConstantTables {
  public attributes: string[] = [
    'ST',
    'DX',
    'IQ',
    'HT',
    'Will',
    'Per'
  ];

  public skillDifficulties: Object[] = [
    {val: 'E', name: 'Easy'},
    {val: 'A', name: 'Average'},
    {val: 'H', name: 'Hard'},
    {val: 'VH', name: 'Very Hard'},
    {val: 'W', name: 'Wildcard'},
  ];

  public techniqueDifficulties: Object[] = [
    {val: 'A', name: 'Average'},
    {val: 'H', name: 'Hard'},
  ];

  public armorLocations: string[] = [
    'eyes',
    'skull',
    'face',
    'head',
    'neck',
    'right leg',
    'left leg',
    'legs',
    'right arm',
    'left arm',
    'arms',
    'chest',
    'vitals',
    'abdomen',
    'groin',
    'torso',
    'hands',
    'left hand',
    'right hand',
    'feet',
    'right foot',
    'left foot',
    'skin',
    'full body'
  ];

}
