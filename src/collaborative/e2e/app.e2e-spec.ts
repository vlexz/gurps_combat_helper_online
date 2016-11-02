import { CollaborativePage } from './app.po';

describe('collaborative App', function() {
  let page: CollaborativePage;

  beforeEach(() => {
    page = new CollaborativePage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
