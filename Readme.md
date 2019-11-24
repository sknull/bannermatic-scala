# Bannermatic

## Table of Contacts
1. [About](#1)
1. [Usage](#2)

## About<a name="1"></a>
For my current team at NA News Aktuell I had to learn the Scala language.
So why not take a fun approach and some nerdy stuff?
As I like colorful banner and back some time have developed a little python tool
to combine [pyfiglet](https://github.com/pwaller/pyfiglet) with ANSI banners generated from images I have ported that one into scala.
Hence I was of course not able to leverage pyfiglet this time I had to do a scala port for that as well.
Now there is a native Scala figlet along the way...

## Usage<a name="2"></a>

### Build
    
    mvn clean package -DskipTests
    
### Call help with

    java -jar target/bannermatic.jar --help
    
### Examples

     java -jar target/bannermatic.jar -i src/test/resources/images/rose-red.png -t "Hello World!" -a left -p outside -j right -w 55 -W 55