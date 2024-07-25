# Echo my IP

A simple web server to return the IP address of the client.

I wanted 2 things with this project. I wanted a free unlimited "What is my IP?"
type of service. Second, I wanted to learn how to make and use a Ring server
with Reitit and Integrant.

# Using the server

```sh
curl DOMAIN:[PORT | 8080]/api
```

Or you can hit the root of the server through your web browser. The service is
simple. There is not much to explain.

# Run usage

```sh
# run the web server with clojure on port 8080
clojure -M -m main

# or
clojure -M -m main --port 3000
```

Use the compiled jar if you want to use this in production.

```sh
# built with java-21 (lts)
java -jar echo-my-ip-1.0.0.jar

# or
java -jar echo-my-ip-1.0.0.jar --port 3000
```

The web server does not support HTTPS so you will need a reverse proxy.

```sh
# example with Caddy
caddy reverse-proxy --to :8080
```

# Build

```sh
# build with clojure
clojure -T:build uber
# Building CSS

# Rebuilding...

# Done in 83ms.

# Writing target/com.mjhika/echo-my-ip-1.0.0.jar
```
