<h3>Scala backend for an online GURPS toolkit.</h3>

<p>/api/char
<br>OPTIONS — returns Allow header with implemented methods
<br>POST — validates the charlist in request and saves it to the database under new id, returns recalculated charlist 
<br>GET — returns a new blank charlist</p>
<p>/api/chars
<br>OPTIONS — returns Allow header with implemented methods
<br>GET — returns a list of charlists</p>
<p>/api/char/:id
<br>OPTIONS — returns Allow header with implemented methods
<br>GET — returns the charlist strored under this id
<br>PUT — validates the charlist in request and updates the charlist stored under this id with recieved one, then returns recalculated charlist
<br>PATCH — validates the update JSON in request and updates the charlist stored under this id, then returns recalculated charlist
<br>DELETE — removes the charlist stored under this id from the database</p>
<p>/api/char/:id/pic
<br>OPTIONS — returns Allow header with implemented methods
<br>GET — returns charlist portrait if it exists
<br>PUT — stores uploaded image as charlist portrair, overwriting any existing one</p>
<p>/api/chars/[trait|skill|teq|weap|armor|item]
<br>OPTIONS — returns Allow header with implemented methods
<br>GET — returns a charlist component template</p>