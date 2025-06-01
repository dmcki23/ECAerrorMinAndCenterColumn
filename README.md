# ECAminErrorFit
Daniel McKinley
ECAminErrorFit - project and repository name
Elementary Cellular Automata as Non-Cryptographic Hash Functions - paper name

Probably the most used entry points would be:

The LaTeX paper is ECAhashPaper/ECAhashPaper.pdf 

The Javadoc for the project is in /Javadoc

The best way to interact with this is through source code open in your favorite IDE. Every property 
mentioned in the paper is in one of these primary functions. There is a section of source code at the top of main() in the
Main class of examples commented out with single-line comments. Uncomment desired functions and change the filepath variable
to any 2 byte RGB code bitmap you might have. Command line options and a menu are in the works. 
There's Javadoc, inline comments, and the functions themselves produce notated output data displaying 
these properties.

Most Popular Functions:

Class Hash
is manager class for all hash code
initWolframs() this chain of functions initializes the truth tables

Class HashLogicOpTransform
testAllLogic()
verifyLogicOperationHash()

Class HashTruthTables
doAllRules() establishes the 2 subsets of 8 and the rest of the 256 ECA too
displays aggregate results

HashTwoDSingleBit
verifyInverseAndAvalanche(String filename) does what it says, the file has to be in ImagesProcessed/

HashTwoDhexadecimal
hashBitmap(String filename) does what it says, the file has to be in ImagesProcessed/ ,
the output is "filename" + postfixes + ".bmp". The bitmap has to be a 2 byte RGB code of some kind. This
one produces the gifs and database images because it's faster. It works for any size bitmaps but in 
practice I shrink the image because of the runtime.

HashTwoD has a set of its own inverse functions of its own. They operate on the exact same principles, 
the difference between HashTwoD and HashTwoDSingle bit 
is that HashTwoD breaks down the bitmap raster into hexadecimal and then starts hashing and 
HashTwoDSingleBit breaks down the raster into single bits then starts hashing, hexadecimal versus binary. 
There are several forks 
of properties of inverses that work differently with the difference. The inverses using the single bit 
version are 10 times less lossy than the hexadecimal version with a perfect set inverse combining 
all the individual rules at the same time.

HashCollisions, checkForTupleUniqueness() checks the size 4 truth tables for uniqueness at every point

