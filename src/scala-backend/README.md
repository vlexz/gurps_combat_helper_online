<h3>Scala backend for an online GURPS toolkit.</h3>

<p>/api/char
<br>POST — validates the charlist in request and saves it to the database under new id, returns recalculated charlist 
<br>GET — returns a new blank charlist</p>
<p>/api/chars GET — returns a list of charlists</p>
<p>/api/char/:id
<br>GET — returns the charlist strored under this id
<br>PUT — validates the charlist in request and updates the charlist stored under this id with recieved one, then returns recalculated charlist
<br>PATCH — validates the update JSON in request and updates the charlist stored under this id, then returns recalculated charlist
<br>DELETE — removes the charlist stored under this id from the database</p>
<p>/api/char/:id/pic
<br>GET — returns charlist portrait if it exists
<br>PUT — stores uploaded image as charlist portrair, overwriting any existing one</p>
<p>/api/char/[trait|skill|teq|weap|armor|item] GET — returns a charlist component template</p>
<p>/api/char/traits GET — returns the list of basic traits' names</p>
<p>/api/char/trait/:name GET — returns a trait by name from the list of basic traits</p>
<p>You can see a sample Charlist structure in <code>example.json</code>.</p>