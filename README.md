# Arem-Taller3

Este taller fue hecho con el objetivo de comprender la arquitectura de un framework de servidor web como spark por lo cual en el laboratorio se realizo una implementacion de un servidor web usando sockets y configurandolo para que reciba peticiones y devuelva recursos estaticos como archivos html,archivos txt,archivos js e imegenes en formato PNG Y JPG ,Porl ultimo se implementaron funciones lambda para la asignacion de endpoints para los recursos dianmicos,tal como en el framework spark y se logro realizar la coneccion de un en endpoint a una base de datos Mongo DB


# Pre-Requisitos

Para el uso de la aplicacion se requiere que el computador tenga instalados los siguientes prerequisitos:

   * Java 8
   * Maven
   * Git
   
# Despliegues

[![CircleCI](https://circleci.com/gh/fernando-b15/Arem-Taller3.svg?style=svg&circle-token=6dbdf7146391b3d5921881925c34523e20092bef)](https://app.circleci.com/pipelines/github/fernando-b15/Arem-Taller3/1/workflows/afeafaa5-537b-4c3d-a117-cc2ae1505cf1)

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://arem-taller3.herokuapp.com/)

# Instalacion

Para comenzar la instalacion porfavor copie el siguiente comando en su linea de comandos :

~~~
git clone https://github.com/fernando-b15/Arem-Taller3
~~~

Posteriormente desde linea comandos ingrese al directorio de la aplicacion con el siguinete comando :

![image3](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/clone.PNG)

~~~
cd Arem-Taller3
~~~

Ahora para realizar la compilacio y empaquetacion de la aplicacion ,ingrese el siguinete comando:

~~~
mvn package
~~~

![image1](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/package.PNG)

# Pruebas de Servidor

Acontinuacion se encuentran la ruta de los recusos estaticos y dinamicos del servidor

## Recursos Estaticos

  * / o /index.html
  ![image5](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/estatico1.PNG)
  * /bienvenida.txt
  ![image6](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/estatico2.PNG)
  * /perro.JPG
   ![image7](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/estatico5.PNG)
  * /nave.JPG
   ![image8](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/estatico3.PNG)
  * /eclipse.PNG
  ![image9](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/estatico4.PNG)

## Recursos Dinamicos 
Para el Acceso  a recursos dinamicos la ruta inicia con /Apps y el el servidor se encaraga de devolver el recurso correspondiente al EndPoint
  * /Apps/hello
  ![image10](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/dinamico1.PNG)
  * /Apps/home
  ![image11](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/dinamico2.PNG)
  * /Apps/registro
   ![image12](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/dinamico3.PNG)

# Documentacion

Para obtener la documentacion de la aplicacion , ingrese el siguinete codigo

~~~
mvn javadoc:javadoc
~~~

![image15](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/javadoc.PNG)

para acceder a la documentacion de la apliacacion ingrese al siguiente enlace [apidocs](https://github.com/fernando-b15/Arem-Taller2/tree/master/apidocs) 

# Ejecucion

Para la ejecucion del programa desde linea de comandos windows, ingrese el siguinete codigo:

~~~
java -cp target/classes;target/dependency/* edu.escuelaing.arem.SparkWebApp
~~~

El comando anterior iniciara el servidor web en puerto 36000 y realizara la coneccion a la base de datos Mongo DB

![image4](https://github.com/fernando-b15/Arem-Taller3/blob/master/img/run.PNG)


# Informe del taller

Para conocer mas a fondo el desarrollo del taller acceda al [Informe](https://github.com/fernando-b15/Arem-Taller3/blob/master/Arem_Taller3.pdf)

# Licencia

La aplicacion cuenta con la siguiente [MIT LICENCE](https://github.com/fernando-b15/Arem-Taller3/blob/master/LICENSE) 

# Autor

   * [Fernanado Barrera Barrera](https://github.com/fernando-b15) :guitar:

