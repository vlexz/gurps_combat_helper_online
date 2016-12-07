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
}
