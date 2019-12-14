ČSLD
====

Larp database is an implementation of the portal for inserting Larp games and events and allowing the users to comment on them and rate them. 

### Set up for local Development

The whole application and its dependencies is packaged as the Docker container. First download the Docker for your platform. The information about how to start are available here: https://www.docker.com/get-started

#### Linux

The next step is in the terminal to download and start the image. It expects that you didn't do all the steps to access the docker from your current user. Following commands under the linux should solve this:

```
sudo docker pull jbalhar/csld
sudo docker run -it -p 8080:8080 --name csld jbalhar/csld:latest bash
```

At this moment in the terminal you are in the bash of the running container. To initialize and start the database run:

```
_csld init
```

Initialize the database by going to the page [http://localhost:8081/testDatabase]()

The database is then available on the page [http://localhost:8081]() 


#### Windows

Under the windows it is necessary to run the PowerShell as administrator to run the commands:

```
docker pull jbalhar/csld
docker run -it -p 8080:8080 --name csld jbalhar/csld:latest bash
```

Initialize the database by going to the page [http://10.0.75.2:8081/testDatabase]() 

The database is then available on the page [http://10.0.75.2:8081]() 

### Start the stopped container

#### Linux 

The following command will start the container, log you into the container and start the database. 

```
sudo docker start csld
sudo docker exec -it bash
_csld start 
```

#### Windows

The following command will start the container, log you into the container and start the database. 

```
docker start csld
docker exec -it bash
_csld start 
```

### Update the database to new version

The command relevant for updating the CSLD to new version is `_csld update`