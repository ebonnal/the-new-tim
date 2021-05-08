# TheNewTIM
[![Actions Status](https://github.com/enzobnl/the-new-tim/workflows/build/badge.svg)](https://github.com/enzobnl/flexible-memoization/actions)

## Overview
This is an adaptation of "The Incredible Machine" with increased multithreaded physical engine. About 40 levels available !

/!\ most of the code base is in french because this project has been developed during my early university years in France !

## Features
- Multithreaded physical engine
- Rotation taken into account during collisions
- Funny tools to solve puzzles : *Balloons*, *Walls* and *Ventilators* 
- 40 funny levels that might be hard to solve...
- Infinite number of ways to solve levels
- You can visualize solution if you are locked ! Or simply go to next level ;)
- You can create you own levels with the **EDITOR MODE** !

## Game Goal
Solve them all !
You have to put inside the bucket(s) all the *Balloons* involved in the level !

## Build

```bash
find -name '*.java' > sources.txt
mkdir output
javac -encoding ISO-8859-1 -d output @sources.txt
cp -r src/main/resources/* output
cd ./output
jar cmf ../manifest.mf thethetim.jar com data
```

## Run

```bash
cd ./output
java -jar thethetim.jar
```

## Screens
Tutorial
![tuto1](https://github.com/EnzoBnl/TheNewTIM/blob/master/src/main/resources/data/tuto1.png)
![tuto2](https://github.com/EnzoBnl/TheNewTIM/blob/master/src/main/resources/data/tuto2.png)

Example of levels
![screen11](https://github.com/EnzoBnl/TheNewTIM/blob/master/src/main/resources/data/screens/11.png)
![screen8](https://github.com/EnzoBnl/TheNewTIM/blob/master/src/main/resources/data/screens/8.png)
![screen7](https://github.com/EnzoBnl/TheNewTIM/blob/master/src/main/resources/data/screens/7.png)
