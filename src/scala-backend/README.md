<h3>Scala backend for an online GURPS toolkit.</h3>

<p>You can see a sample Charlist JSON structure in <code>example.json</code>.</p>
<p>/api/char
<br>POST — validates the charlist in request and saves it to the database under new id, returns recalculated charlist 
<br>GET — returns a new blank charlist</p>
<p>/api/chars GET — returns a list of charlists</p>
<p>/api/char/:id
<br>GET — returns the charlist strored under this id
<br>PUT — validates the charlist in request and updates the charlist stored under this id with recieved one, then 
returns recalculated charlist
<br>PATCH — validates the update JSON in request and updates the charlist stored under this id, then returns 
recalculated charlist
<br>DELETE — removes the charlist stored under this id from the database</p>
<p>/api/char/:id/pic
<br>GET — returns charlist portrait if it exists
<br>PUT — stores uploaded image as charlist portrair, overwriting any existing one</p>
<p>/api/trait
<br>POST — validates the trait in request and saves it to the database under new id, returns recalculated trait
<br>GET — returns default blank trait</p>
<p>/api/traits GET — returns the list of basic traits: [id, name]</p>
<p>/api/traits/search/:category?term=string GET — returns a list of basic traits of the category with 'string' in name: 
[id, name]</p> 
<p>/api/trait/:id GET — returns a trait by id from the list of basic traits</p>
<p>/api/skill
<br>POST — validates the skill in request and saves it to the database under new id, returns recalculated skill
<br>GET — returns default blank skill</p>
<p>/api/skills GET — returns the list of basic skills, [id, skillString]</p>
<p>/api/skills/search/?term=string GET — returns a list of basic skills with 'string' in name or specialization: [id, 
skillString]</p>
<p>/api/skill/:id GET — returns a skill by id from the list of basic skills</p>
<p>/api/armor
<br>POST — validates the armor piece in request and saves it to the database under new id, returns recalculated armor
piece
<br>GET — returns default armor piece</p>
<p>/api/armors GET — returns the list of basic armor, [id, name]</p>
<p>/api/armors/search/?term=string GET — returns a list of basic armor with 'string' in name: [id, name]</p>
<p>/api/armor/:id GET — returns an armor piece by id from the list of basic armor</p>
<p>/api/weap
<br>POST — validates the weapon in request and saves it to the database under new id, returns recalculated weapon
<br>GET — returns default weapon</p>
<p>/api/weaps GET — returns the list of basic weapons, [id, name]</p>
<p>/api/weaps/search/?term=string GET — returns a list of basic weapons with 'string' in name: [id, name]</p>
<p>/api/weap/:id GET — returns a weapon by id from the list of basic weapons</p>
<p>/api/item
<br>POST — validates the item in request and saves it to the database under new id, returns recalculated item
<br>GET — returns default template item</p>
<p>/api/items GET — returns the list of basic items, [id, name]</p>
<p>/api/items/search/?term=string GET — returns a list of basic items with 'string' in name: [id, name]</p>
<p>/api/item/:id GET — returns an item by id from the list of basic items</p>
<p>/api/tecn
<br>POST — validates the technique in request and saves it to the database under new id, returns recalculated technique
<br>GET — returns default template technique</p>
<p>/api/tecns GET — returns the list of basic techniques, [id, name]</p>
<p>/api/tecns/search/?term=string GET — returns a list of basic techniques with 'string' in 'tchString': [id, name]</p>
<p>/api/tecn/:id GET — returns a technique by id from the list of basic techniques</p>
