export class ConstantTables {
  public attributes: string[] = [
    'ST',
    'DX',
    'IQ',
    'HT',
    'Will',
    'Per'
  ];

  public skillDifficulties: any[] = [
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

  public armorLocations: any = {
    head: ['eyes', 'skull', 'face', 'head'],
    body: ['neck', 'chest', 'vitals', 'abdomen', 'groin', 'torso'],
    arms: ['right arm', 'left arm', 'arms', 'hands', 'left hand', 'right hand'],
    legs: ['right leg', 'left leg', 'legs', 'feet', 'right foot', 'left foot'],
    suit: ['skin', 'full body']
  };
}
