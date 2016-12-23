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
<p>/api/[skill|tecn|armor|weap|item]
<br>POST — validates the charlist component in request and saves it to the database under new id, returns recalculated 
charlist component
<br>GET — returns default blank charlist component template</p>
<p>/api/[skills|tecns|armors|weaps|items] GET — returns the list of basic charlist components, [id, name]</p>
<p>/api/[skills|tecns|armors|weaps|items]/search?term=string GET — returns a list of basic charlist components with 
'string' in name: [id, name]</p>
<p>/api/[skill|tecn|armor|weap|item]/:id GET — returns a charlist component by id from the list of basics</p>