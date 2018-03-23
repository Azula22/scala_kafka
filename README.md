* This is just a simple app for sign in with microservice structure.  
* Just for education purpose :ok_woman:

For running the app you need to have [postgreSQL](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-postgresql-on-ubuntu-16-04). Make sure your postgres USER password is 'postgres' and you have permission to connect from not local servers !!!

**1. Inside the app folder run:**   
```bash
chmod 755 runTests.sh
```
**2. Run tests:**   
```bash
./runTests.sh
```
**3. Create docker images:**   
```bash
sbt docker
```
**4. Run with docker :**   
```bash
docker-compose up
```

**Enjoy** :eyes:
